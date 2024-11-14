package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.SpecViewBrandService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("SpecViewBrandServiceJob")
public class SpecViewBrandServiceJob extends IJobHandler {

    @Resource
    private SpecViewBrandService specViewBrandService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        specViewBrandService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "success");
    }
}
