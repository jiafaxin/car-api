package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.specs.SpecSYearService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("specSYearServiceJob")
public class SpecSYearServiceJob extends IJobHandler {
    @Resource
    private SpecSYearService specSYearService;

    @Override
    public ReturnT execute(String... strings) throws Exception {
        specSYearService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
