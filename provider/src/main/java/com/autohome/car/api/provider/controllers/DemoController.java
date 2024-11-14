package com.autohome.car.api.provider.controllers;

import com.autohome.car.api.common.CacheKeys;
import com.autohome.car.api.data.popauto.entities.CarPhotoViewEntity;
import com.autohome.car.api.services.common.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DemoController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/hello")
    public String hello(){
        return "success";
    }

    @GetMapping("/index")
    public String index(){
        log.info("info");
        log.warn("warn");
        log.error("error");
        return "success";
    }


    @RequestMapping(value = {"/prestop"}, method = RequestMethod.GET, produces = "application/json")
    public void replylist() {
        try {
            Thread.currentThread().sleep(1000 * 300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.error("### pod prestop at " + new Date());
    }


    @RequestMapping("/deleteCacheTime")
    public String deleteCacheTime(){
        String cacheKey = CacheKeys.SpecTimeMarketCacheKey;
        redisUtil.del(cacheKey);
        return "成功删除！";
    }

}
