package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.shuyu.GetBaiKeLinkForExplainRequest;
import autohome.rpc.car.car_api.v1.shuyu.GetBaiKeLinkForExplainResponse;

public interface ShuYuService {
    GetBaiKeLinkForExplainResponse baiKeLinkForExplain(GetBaiKeLinkForExplainRequest request);
}
