package com.autohome.car.api.provider.services;

import com.autohome.car.api.services.LevelService;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v1.level.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@DubboService
public class LevelServiceGrpcImpl extends DubboLevelServiceTriple.LevelServiceImplBase {

    @Resource
    private LevelService levelService;
    /**
     * 获取级别信息
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/level_info.ashx")
    @Override
    public GetLevelInfoResponse getLevelInfo(GetLevelInfoRequest request) {
        return levelService.getLevelInfo(request);
    }

}