package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.SeriesSpecYearService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("SeriesSpecYearServiceJob")
public class SeriesSpecYearServiceJob extends IJobHandler {

    @Resource
    private SeriesSpecYearService seriesSpecYearService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        seriesSpecYearService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"success");
    }
}
