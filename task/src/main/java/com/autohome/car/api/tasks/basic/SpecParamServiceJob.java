package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.specs.SpecParamService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SpecParamServiceJob")
@Service
public class SpecParamServiceJob extends IJobHandler {
    @Autowired
    SpecParamService specParamService;
    @Override
    public ReturnT<String> execute(String... strings)  {
        specParamService.refreshAll(logItem->{
            XxlJobLogger.log(logItem);
        });
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}
