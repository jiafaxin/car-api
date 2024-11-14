package com.autohome.car.api.common;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EhCache {

    @Autowired
    CacheManager cacheManager;

    public void set(String cacheName,Object key,Object value){
        Element element = new Element(key,value);
        Cache cache = cacheManager.getCache(cacheName);
        cache.put(element);
    }

    public <T> T get(String cacheName,Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        Element element = cache.get(key);
        if(element == null)
            return null;
        return (T) element.getObjectValue();
    }

    public void delete(String cacheName,Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.remove(key);
    }

    public <T> Map<String, T> mGet(String cacheName, List<?> key) {
        Cache cache = cacheManager.getCache(cacheName);
        Map<Object, Element> map = cache.getAll(key);
        if(CollectionUtils.isEmptyMap(map))
            return Collections.emptyMap();
        Set<Object> existKeys = map.keySet();
        return existKeys.stream().filter(map::containsKey).filter(k -> Objects.nonNull(map.get(k))).collect(Collectors.toMap(String::valueOf, k -> (T) map.get(k).getObjectValue()));
    }


    @Scheduled(cron = "0 0 */1 * * ?")
    public void clearAll(){
        log.info("开始清理ehcache===================================================================================================");
        clearCache(EhCacheName.M_5.toString());
        clearCache(EhCacheName.M_10.toString());
        clearCache(EhCacheName.M_30.toString());
        clearCache(EhCacheName.H_1.toString());
        clearCache(EhCacheName.H_24.toString());
        log.info("结束清理ehcache===================================================================================================");
    }

    public void clearCache(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);
        cache.evictExpiredElements();
    }
}
