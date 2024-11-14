package com.autohome.car.api.provider.services;

import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v1.config.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.autohome.car.api.services.ConfigService;
import javax.annotation.Resource;

@RestController
@DubboService
public class ConfigServiceGrpcImpl extends DubboConfigServiceTriple.ConfigServiceImplBase {

    @Resource
    private ConfigService configService;

    /**
     * 获取配置项基本信息
     * @param request
     * @return
     */
    @GetMapping("/v1/app/Config_itemBaseInfo.ashx")
    @Override
    public GetConfigItemBaseInfoResponse getConfigItemBaseInfo(GetConfigItemBaseInfoRequest request) {
        return configService.getConfigItemBaseInfo(request);
    }

}