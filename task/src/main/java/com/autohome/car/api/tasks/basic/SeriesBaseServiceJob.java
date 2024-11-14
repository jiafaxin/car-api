package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.LevelBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SeriesBaseServiceJob")
@Service
public class SeriesBaseServiceJob extends IJobHandler {

    @Autowired
    SeriesBaseService baseService;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalCount = baseService.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
