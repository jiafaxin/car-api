package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.level.GetLevelInfoRequest;
import autohome.rpc.car.car_api.v1.level.GetLevelInfoResponse;

public interface LevelService {
    /**
     * 获取级别信息
     * @param request
     * @return
     */
    GetLevelInfoResponse getLevelInfo(GetLevelInfoRequest request);
}
