package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.ConfigSubItemValueRelationService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander(value = "SpecConfigSubItemValueServiceJob")
@Service
public class SpecConfigSubItemValueServiceJob extends IJobHandler {

    @Resource
    private ConfigSubItemValueRelationService configSubItemValueRelationService;
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        int count = configSubItemValueRelationService.refreshAll(x -> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"一共有" + count + "数据");
    }
}
