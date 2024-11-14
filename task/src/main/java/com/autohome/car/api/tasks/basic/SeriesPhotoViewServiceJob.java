package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.series.PhotosService;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHander(value = "SeriesPhotoViewServiceJob")
@Service
public class SeriesPhotoViewServiceJob extends IJobHandler {
    @Autowired
    PhotosService photosService;
    @Override
    public ReturnT<String> execute(String... strings)  {
        photosService.refreshAll(logItem->{
            XxlJobLogger.log(logItem);
        });
        return new ReturnT(ReturnT.SUCCESS_CODE,"success");
    }
}
