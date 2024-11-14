package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.mtn.IsHaveMaintainRequest;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.services.MaintainService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.models.IsHaveMaintain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaintainServiceImpl implements MaintainService {

    @Autowired
    private SpecBaseService specBaseService;

    @Autowired
    private SeriesBaseService seriesBaseService;

    @Override
    public ApiResult<IsHaveMaintain> isHaveMaintain(IsHaveMaintainRequest request) {
        int specId = request.getSpecid();
        int seriesId = request.getSeriesid();
        if(specId > 0){
            SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
            IsHaveMaintain isHaveMaintain = new IsHaveMaintain(null != specBaseInfo ? specBaseInfo.getIsHaveMaintains() : 0);
            return new ApiResult<>(isHaveMaintain, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }else if(seriesId > 0){
            SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
            IsHaveMaintain isHaveMaintain = new IsHaveMaintain(null != seriesBaseInfo ? seriesBaseInfo.getEm() : 0);
            return new ApiResult<>(isHaveMaintain, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }else{
             return new ApiResult<>(null, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }
    }
}
