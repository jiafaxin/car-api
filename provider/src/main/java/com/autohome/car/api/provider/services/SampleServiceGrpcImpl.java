package com.autohome.car.api.provider.services;

import com.autohome.car.api.services.FindCarService;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v1.sou.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@DubboService
@RestController
public class SampleServiceGrpcImpl extends DubboSampleServiceTriple.SampleServiceImplBase {

    @Autowired
    FindCarService findCarService;

    @Override
    @GetMapping("/v1/sou/Series_FindCar.ashx")
    public SeriesFindCarResponse seriesFindCar(SeriesFindCarRequest request) {

        return findCarService.finCar(request);
    }
}