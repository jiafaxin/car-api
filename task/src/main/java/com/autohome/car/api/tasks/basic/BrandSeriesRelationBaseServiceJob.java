package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.BrandSeriesRelationBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander(value = "BrandSeriesRelationBaseServiceJob")
@Service
public class BrandSeriesRelationBaseServiceJob extends IJobHandler {

    @Resource
    private BrandSeriesRelationBaseService brandSeriesBaseService;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalCount = brandSeriesBaseService.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
