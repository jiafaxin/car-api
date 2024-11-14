package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.OptParItemInfoService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("OptParItemInfoJob")
public class OptParItemInfoJob extends IJobHandler {

    @Resource
    private OptParItemInfoService optParItemInfoService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        int totalCount = optParItemInfoService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
