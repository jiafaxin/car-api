package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v3.cache.ReqRefreshCacheRequest;
import autohome.rpc.car.car_api.v3.cache.ResRefreshCacheResponse;
import com.autohome.car.api.common.ApiResult;

public interface CacheService {

    /**
     * 更新redis缓存和本地缓存
     * @param request
     * @return
     */
    ResRefreshCacheResponse refreshCache(ReqRefreshCacheRequest request);

    /**
     * task调用
     * @param locationId
     * @param seriesId
     * @param specId
     * @return
     */
    ApiResult taskRefreshCache(int locationId,int seriesId,int specId);
}
