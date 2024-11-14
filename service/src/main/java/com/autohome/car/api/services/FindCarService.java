package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.car.GetFindCarSeriesInfoByConditionRequest;
import autohome.rpc.car.car_api.v1.car.GetFindCarSeriesInfoByConditionResponse;
import autohome.rpc.car.car_api.v1.sou.SeriesFindCarRequest;
import autohome.rpc.car.car_api.v1.sou.SeriesFindCarResponse;

public interface FindCarService {
    SeriesFindCarResponse finCar(SeriesFindCarRequest request);

    /**
     * pc 找车页面重构后源接口
     * @param request
     * @return
     */
    GetFindCarSeriesInfoByConditionResponse getFindCarSeriesInfoByCondition(GetFindCarSeriesInfoByConditionRequest request);
}
