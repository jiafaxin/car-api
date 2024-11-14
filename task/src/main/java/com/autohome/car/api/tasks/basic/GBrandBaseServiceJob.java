package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.GBrandBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander(value = "GBrandBaseServiceJob")
@Service
public class GBrandBaseServiceJob extends IJobHandler {

    @Resource
    private GBrandBaseService gBrandBaseService;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalCount = gBrandBaseService.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
