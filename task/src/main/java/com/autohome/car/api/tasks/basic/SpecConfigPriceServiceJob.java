package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.specs.SpecConfigPriceService;
import com.autohome.car.api.services.basic.specs.SpecConfigRelationService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@JobHander(value = "SpecConfigPriceServiceJob")
@Service
public class SpecConfigPriceServiceJob extends IJobHandler {

    @Autowired
    SpecConfigPriceService service;

    @Override
    public ReturnT<String> execute(String... strings) {
        service.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}