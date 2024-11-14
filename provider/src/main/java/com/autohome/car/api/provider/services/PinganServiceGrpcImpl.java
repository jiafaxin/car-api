package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.pingan.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.SpecStateEnum;
import com.autohome.car.api.data.popauto.entities.SpecSeriesYearEntity;
import com.autohome.car.api.services.PingAnService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesSpecYearOrderYearService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@DubboService
@RestController
public class PinganServiceGrpcImpl extends DubboPinganServiceTriple.PinganServiceImplBase {

    @Autowired
    private SeriesSpecYearOrderYearService seriesSpecYearOrdYearService;

    @Autowired
    private SpecBaseService specBaseService;

    @Resource
    private PingAnService pingAnService;

    @Override
    @GetMapping("/v1/pingan/SyearAndSpecBySeries.ashx")
    public SyearAndSpecBySeriesResponse syearAndSpecBySeries(SyearAndSpecBySeriesRequest request) {
        SyearAndSpecBySeriesResponse.Builder builder = SyearAndSpecBySeriesResponse.newBuilder();
        SyearAndSpecBySeriesResponse.Result.Builder result = SyearAndSpecBySeriesResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        SpecStateEnum specState = Spec.getSpecState(request.getState());
        if (seriesId == 0 ||specState == SpecStateEnum.NONE) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<SpecSeriesYearEntity> list = seriesSpecYearOrdYearService.get(seriesId);
        boolean flag = false;
        switch (specState) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 20 && s.getState() <= 30).collect(Collectors.toList());
                break;
            //0x000e
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 10 && s.getState() <= 30).collect(Collectors.toList());
                break;
            //0x001c
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 20).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getState() <= 30).collect(Collectors.toList());
                break;
            //(0x001e)
            case SELL_30:
                flag = true;
                list = list.stream().filter(s -> s.getState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if (!flag) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }

        if (request.getIsFilterSpecImage() == 1) {
            list = list.stream().filter(e -> e.getSImage() == 0).collect(Collectors.toList());
        }

        if (!CollectionUtils.isEmpty(list)) {
            List<Integer> specIds = list.stream().map(SpecSeriesYearEntity::getSpecId).distinct().collect(Collectors.toList());
            Map<Integer, SpecBaseInfo> specMap = specBaseService.getMap(specIds);
            Map<Map<String, Object>, List<SpecSeriesYearEntity>> data = new LinkedHashMap<>();
            for(SpecSeriesYearEntity item : list){
                Map<String, Object> key = new HashMap<>();
                key.put("id",item.getYId());
                key.put("name", item.getSyear() + "款");
                if(data.containsKey(key)){
                    data.get(key).add(item);
                }else{
                    List<SpecSeriesYearEntity> value = new ArrayList<>();
                    value.add(item);
                    data.put(key, value);
                }
            }
            for (Map.Entry<Map<String, Object>, List<SpecSeriesYearEntity>> entry : data.entrySet()) {
                Map<String, Object> key = entry.getKey();
                List<SpecSeriesYearEntity> value = entry.getValue();
                SyearAndSpecBySeriesResponse.YearItem.Builder yearItem = SyearAndSpecBySeriesResponse.YearItem.newBuilder();
                yearItem.setId((Integer) key.get("id"));
                yearItem.setName((String) key.get("name"));
                for(SpecSeriesYearEntity v : value){
                    SpecBaseInfo spec = specMap != null ? specMap.getOrDefault(v.getSpecId(), null) : null;
                    yearItem.addSpecitems(SyearAndSpecBySeriesResponse.YearItem.SpecItem.newBuilder()
                            .setId(v.getSpecId())
                            .setName(spec != null ? spec.getSpecName() : "")
                            .setSpeclogo(spec != null && spec.getLogo() != null ? ImageUtil.getFullImagePath(spec.getLogo()) : "")
                            .setState(v.getState())
                            .setMinprice(spec != null ? spec.getSpecMinPrice() : 0)
                            .setMaxprice(spec != null ? spec.getSpecMaxPrice() : 0));
                }
                result.addYearitems(yearItem);
            }
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    /**
     * 平安获取品牌信息
     * @param request
     * @return
     */
    @RequestMapping("/v1/pingan/getBrandInfoAll")
    @Override
    public BrandInfoResponse getBrandInfoAll(BrandInfoRequest request) {
        return pingAnService.getBrandInfoAll(request);
    }

    /**
     * 平安根据品牌id获取车系信息
     * @param request
     * @return
     */
    @RequestMapping("/v1/pingan/getSeriesInfoByBrandId")
    @Override
    public SeriesInfoResponse getSeriesInfoByBrandId(SeriesInfoRequest request) {
        return pingAnService.getSeriesInfoByBrandId(request);
    }

    /**
     * 平安根据车系id获取车型信息
     * @param request
     * @return
     */
    @RequestMapping("/v1/pingan/getSpecInfoBySeriesId")
    @Override
    public SpecInfoResponse getSpecInfoBySeriesId(SpecInfoRequest request) {
        return pingAnService.getSpecInfoBySeriesId(request);
    }
}