package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.Spec25PicBaseService;
import com.autohome.car.api.services.basic.SpecColorPicNumBaseService;
import com.autohome.car.api.services.basic.SpecInnerColorPicNumBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SpecColorPicNumBaseServiceJob")
@Service
public class SpecColorPicNumBaseServiceJob extends IJobHandler {

    @Autowired
    SpecColorPicNumBaseService baseService;

    @Autowired
    SpecInnerColorPicNumBaseService innerBaseService;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalCount = baseService.refreshAll(x-> XxlJobLogger.log(x));
        int total2Count = innerBaseService.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
