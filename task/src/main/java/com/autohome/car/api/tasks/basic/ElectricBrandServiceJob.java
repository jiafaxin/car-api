package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.ElectricBrandService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@JobHander("ElectricBrandServiceJob")
public class ElectricBrandServiceJob extends IJobHandler {

    @Resource
    private ElectricBrandService electricBrandService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        electricBrandService.refreshAll(XxlJobLogger::log);
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"success");
    }
}
