//package com.autohome.car.api.tasks.spec;
//
//import com.autohome.car.api.services.basic.specs.SpecSearchService;
//import com.autohome.job.core.biz.model.ReturnT;
//import com.autohome.job.core.handler.IJobHandler;
//import com.autohome.job.core.handler.annotation.JobHander;
//import com.autohome.job.core.log.XxlJobLogger;
//import com.autohome.job.core.util.ShardingUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@JobHander(value = "SpecSearchJob")
//@Service
//public class SpecSearchJob extends IJobHandler {
//
//    @Autowired
//    SpecSearchService baseService;
//
//    @Override
//    public ReturnT<String> execute(String... strings) {
//        int total = ShardingUtil.getShardingVo().getTotal();
//        int index = ShardingUtil.getShardingVo().getIndex();
//        System.out.println("total: " + total + "index :" + index);
//        int totalCount = baseService.refreshAll(x -> XxlJobLogger.log(x));
//        return new ReturnT(ReturnT.SUCCESS_CODE,"共同步：" + totalCount);
//    }
//}
