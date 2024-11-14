package com.autohome.car.api.services.models;

import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParamTypeItems {

    private String name;

    private List<ParamItems> paramitems;

    @Data
    @Builder
    public static class ParamItems {
        private int id;

        private String name;

        private List<ValueItems> valueitems;
    }

    @Data
    @Builder
    public static class ValueItems {
        private int specid;

        private String value;

        private String url;

    }

    @Data
    @Builder
    public static class TempInfo {
        /**
         * 判断是否有新能源车型
         */
        private int newEnergyNum;
        /**
         * 计算燃料形式是油的个数据，用于判断不显电动机(Ps)这项基本参数
         */
        private int oilEnergyNum;
        /**
         * 计算纯电动车型数量,纯电
         */
        private int pevSpecNum;
        /**
         * 判断是否是纯电动车
         */
        private boolean allSpecIsPEV;

        public static TempInfo getTempInfo(List<Integer> specIdList, SpecBaseService specBaseService) {
            boolean allSpecIsPEV = false;
            int newEnergyNum = 0, oilEnergyNum = 0, pevSpecNum = 0;
            for (Integer specId : specIdList) {
                SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
                int fuelTypeDetail = specBaseInfo == null ? -1 : specBaseInfo.getFuelTypeDetail();
                if (Spec.ARR_NEW_ENERGY_FUEL_TYPE.contains(fuelTypeDetail)) {
                    newEnergyNum += 1;
                }
                if (fuelTypeDetail == 4) {
                    pevSpecNum += 1;
                }
                if (CommonFunction.OILFUELTYPELIST.contains(fuelTypeDetail)) {
                    oilEnergyNum += 1;
                }
            }
            if (pevSpecNum == specIdList.size()) {
                allSpecIsPEV = true;
            }
            return ParamTypeItems.TempInfo.builder().newEnergyNum(newEnergyNum).oilEnergyNum(oilEnergyNum).pevSpecNum(pevSpecNum).allSpecIsPEV(allSpecIsPEV).build();
        }
    }
}
