package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.series.SeriesInfoService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SeriesInfoServiceJob")
@Service
public class SeriesInfoServiceJob extends IJobHandler {
    @Autowired
    SeriesInfoService seriesInfoService;

    @Override
    public ReturnT<String> execute(String... strings) {
        seriesInfoService.refreshAll(logItem -> {
            XxlJobLogger.log(logItem);
        });
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}
