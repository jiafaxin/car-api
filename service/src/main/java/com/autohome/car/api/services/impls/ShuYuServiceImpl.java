package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.shuyu.GetBaiKeLinkForExplainRequest;
import autohome.rpc.car.car_api.v1.shuyu.GetBaiKeLinkForExplainResponse;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.services.ShuYuService;
import org.springframework.stereotype.Service;

@Service
public class ShuYuServiceImpl implements ShuYuService {
    @Override
    public GetBaiKeLinkForExplainResponse baiKeLinkForExplain(GetBaiKeLinkForExplainRequest request) {
        GetBaiKeLinkForExplainResponse.Builder builder = GetBaiKeLinkForExplainResponse.newBuilder();
        return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }
}
