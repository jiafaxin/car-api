package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.services.basic.ConfigItemValueService;
import com.autohome.car.api.services.basic.ConfigListService;
import com.autohome.car.api.services.basic.ConfigSubItemService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "ConfigJob")
@Service
public class ConfigJob extends IJobHandler {

    @Autowired
    ConfigListService configListService;

    @Autowired
    ConfigItemValueService configItemValueService;

    @Autowired
    ConfigSubItemService configSubItemService;


    @Override
    public ReturnT<String> execute(String... strings) {
        try {
            configListService.refreshAll(x -> XxlJobLogger.log(x));
        }catch (Exception e){
            XxlJobLogger.log("error:"+ ExceptionUtil.getStackTrace(e));
        }
        try {
            configItemValueService.refreshAll(x -> XxlJobLogger.log(x));
        }catch (Exception e){
            XxlJobLogger.log("error:"+ ExceptionUtil.getStackTrace(e));
        }
        try {
            configSubItemService.refreshAll(x -> XxlJobLogger.log(x));
        }catch (Exception e){
            XxlJobLogger.log("error:"+ ExceptionUtil.getStackTrace(e));
        }

        return new ReturnT(ReturnT.SUCCESS_CODE, "success");
    }
}
