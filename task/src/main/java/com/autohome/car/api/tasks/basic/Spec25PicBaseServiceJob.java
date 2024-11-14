package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.Spec25PicBaseService;
import com.autohome.car.api.services.basic.SpecPictureStatisticsBaseService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "Spec25PicBaseServiceJob")
@Service
public class Spec25PicBaseServiceJob extends IJobHandler {

    @Autowired
    Spec25PicBaseService baseService;

    @Override
    public ReturnT<String> execute(String... strings) {
        int totalCount = baseService.refreshAll(x-> XxlJobLogger.log(x));
        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
    }
}
