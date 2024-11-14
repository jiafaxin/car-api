package com.autohome.car.api.tasks.basic;

import com.autohome.car.api.services.basic.epiboly.EpibolyAiVideoOrderDetailService;
import com.autohome.car.api.services.basic.epiboly.EpibolyAiVideoOrderService;
import com.autohome.car.api.services.basic.point.PointParamConfigService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@JobHander(value = "EpibolyAiVideoOrderJob")
@Service
public class EpibolyAiVideoOrderJob extends IJobHandler {

    @Resource
    private EpibolyAiVideoOrderDetailService epibolyAiVideoOrderDetailService;

    @Resource
    private EpibolyAiVideoOrderService epibolyAiVideoOrderService;

    @Resource
    private PointParamConfigService videoPointLocationService;


    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        int orderCount = epibolyAiVideoOrderService.refreshAll(x -> XxlJobLogger.log(x));
        int orderDetailCount = epibolyAiVideoOrderDetailService.refreshAll(x -> XxlJobLogger.log(x));
        int buIdCount = videoPointLocationService.refreshAll(x -> XxlJobLogger.log(x));

        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步订单数：" + orderCount +
                "订单详情共有订单数：" + orderDetailCount + "业务线数：" + buIdCount);
    }
}
