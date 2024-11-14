package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.ColorBaseService;
import com.autohome.car.api.services.basic.PicClassBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "PicClassBaseServiceJob")
@Service
public class PicClassBaseServiceJob extends IJobHandler {

    @Autowired
    PicClassBaseService service;

    @Override
    public ReturnT<String> execute(String... strings) {
        service.refresh(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}
