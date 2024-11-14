package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.SpecSpecColorBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander(value = "SpecSpecColorServiceJob")
@Service
public class SpecSpecColorServiceJob extends IJobHandler {

    @Resource
    private SpecSpecColorBaseService baseService;

    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        int totalCount = baseService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
