package com.autohome.car.api.tasks.controllers;

import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class CacheController {

    @Resource
    private CacheService cacheService;




    /**
     * 更新redis缓存和本地缓存
     * @param
     * @return
     */
    @GetMapping("/v3/cache/refreshCache")
    public ApiResult taskRefreshCache(@RequestParam(value = "locationid",defaultValue = "0") Integer locationId,
                                  @RequestParam(value = "seriesid",defaultValue = "0") Integer seriesId,
                                  @RequestParam(value = "specid",defaultValue = "0") Integer specId){
        ApiResult apiResult = cacheService.taskRefreshCache(locationId,seriesId,specId);
        return apiResult;
    }

}
