package com.autohome.car.api.tasks.series;

import com.autohome.car.api.services.basic.series.SeriesSearchService;
import com.autohome.car.api.services.basic.series.SeriesYearConfigService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SeriesYearConfigJob")
@Service
public class SeriesYearConfigJob extends IJobHandler {

    @Autowired
    SeriesYearConfigService service;

    @Override
    public ReturnT<String> execute(String... strings) {
        service.refreshAll(x -> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}
