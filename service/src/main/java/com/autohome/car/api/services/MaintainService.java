package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.mtn.IsHaveMaintainRequest;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.IsHaveMaintain;

public interface MaintainService {

    ApiResult<IsHaveMaintain> isHaveMaintain(IsHaveMaintainRequest request);
}
