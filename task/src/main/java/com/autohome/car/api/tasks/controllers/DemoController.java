package com.autohome.car.api.tasks.controllers;

import com.autohome.car.api.common.CacheKeys;
import com.autohome.car.api.services.common.RedisUtil;
import com.autohome.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DemoController {

    @Autowired(required = false)
    Map<String, IJobHandler> jobMap;

    @GetMapping("/hello")
    public String hello(){
        return "success";
    }


    /**
     * 定时任务执行
     * @return
     */
    @GetMapping("/testJob")
    public String testJob() {
        List<String> jobList = new ArrayList<>();
//        jobList.add("userFromArticleHandler");
//        jobList.add("userFromChejiahaoHandler");
//        jobList.add("userFromHongRenHandler");
//        jobList.add("originalChannelFeedJobHandler");
        //jobList.add("seriesJobHandler");
        jobList.add("picPreHotServiceJob");

        jobList.forEach(job -> {
            try {
                IJobHandler jobHandler = this.jobMap.get(job);

                if (jobHandler != null) {
                    jobHandler.execute("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return "ok";
    }

}
