package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.specs.SpecConfigSubItemService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SpecConfigSubItemServiceJob")
@Service
public class SpecConfigSubItemServiceJob extends IJobHandler {

    @Autowired
    SpecConfigSubItemService service;

    @Override
    public ReturnT<String> execute(String... strings) {
        service.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}