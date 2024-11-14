package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.SeriesSpecService;
import com.autohome.car.api.services.basic.series.SeriesSpecWithStateService;
import com.autohome.car.api.services.basic.specs.SpecConfigBagService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@JobHander(value = "SeriesSpecServiceJob")
@Service
public class SeriesSpecServiceJob extends IJobHandler {

    @Autowired
    SeriesSpecService service;

    @Autowired
    SeriesSpecWithStateService seriesSpecWithStateService;

    @Override
    public ReturnT<String> execute(String... strings) {
        seriesSpecWithStateService.refreshAll(x->XxlJobLogger.log(x));
        service.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}