package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.BrandFactorysBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "BrandFactorysBaseServiceJob")
@Service
public class BrandFactorysBaseServiceJob extends IJobHandler {

    @Autowired
    BrandFactorysBaseService baseService;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalCount = baseService.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
