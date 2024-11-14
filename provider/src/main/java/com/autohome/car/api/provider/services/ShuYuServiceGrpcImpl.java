package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.shuyu.*;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.services.ShuYuService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@DubboService
@RestController
public class ShuYuServiceGrpcImpl extends DubboShuYuServiceTriple.ShuYuServiceImplBase {

    @Resource
    private ShuYuService shuYuService;
    @Override
    @GetMapping("/v1/shuyu/baike_linkforexplan.ashx")
    public GetBaiKeLinkForExplainResponse baiKeLinkForExplain(GetBaiKeLinkForExplainRequest request) {
        return shuYuService.baiKeLinkForExplain(request);
    }

    @GetMapping("/v1/ShuYu/baike_getSecondClassesByParentId.ashx")
    @Override
    public GetBaiKeSecondClassesResponse getBaiKeSecondClasses(GetBaiKeSecondClassesRequest request) {
        return GetBaiKeSecondClassesResponse.newBuilder()
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @GetMapping("/v1/shuyu/baike_pagelist.ashx")
    @Override
    public GetBaiKePageListResponse getBaiKePageList(GetBaiKePageListRequest request) {
        return GetBaiKePageListResponse.newBuilder()
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @GetMapping("/v1/shuyu/baike_Infobyid.ashx")
    @Override
    public GetBaiKeInfoByIdResponse getBaiKeInfoById(GetBaiKeInfoByIdRequest request) {
        return GetBaiKeInfoByIdResponse.newBuilder()
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
}
