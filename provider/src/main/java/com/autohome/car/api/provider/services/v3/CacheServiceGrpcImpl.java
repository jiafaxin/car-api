package com.autohome.car.api.provider.services.v3;

import com.autohome.car.api.services.CacheService;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v3.cache.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * 更新缓存服务
 */
@DubboService
@RestController
public class CacheServiceGrpcImpl extends DubboCacheServiceTriple.CacheServiceImplBase {


    @Resource
    private CacheService cacheService;


    /**
     * 更新redis缓存和本地缓存
     * @param request
     * @return
     */
    @GetMapping("/v3/cache/refreshCache")
    @Override
    public ResRefreshCacheResponse refreshCache(ReqRefreshCacheRequest request) {
        return cacheService.refreshCache(request);
    }

}