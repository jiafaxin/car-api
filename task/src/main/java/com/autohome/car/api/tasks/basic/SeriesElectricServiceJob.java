package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.SeriesElectricService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("SeriesElectricServiceJob")
public class SeriesElectricServiceJob extends IJobHandler {

    @Resource
    private SeriesElectricService seriesElectricService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        seriesElectricService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"success");
    }
}
