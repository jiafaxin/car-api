package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.SeriesSpecYearOrderYearService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("SeriesSpecYearOrderYearServiceJob")
public class SeriesSpecYearOrderYearServiceJob extends IJobHandler {

    @Resource
    private SeriesSpecYearOrderYearService seriesSpecYearOrderYearService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        seriesSpecYearOrderYearService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"success");
    }
}
