package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.ParamConfigModelService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@JobHander("ParamConfigModelServiceJob")
public class ParamConfigModelServiceJob extends IJobHandler {

    @Resource
    private ParamConfigModelService paramConfigModelService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        paramConfigModelService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"共同步：" + 0);
    }
}
