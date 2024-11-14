package com.autohome.car.api.common.cache;


import com.autohome.car.api.common.DateUtils;
import com.autohome.car.api.common.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * crated by shicuining 2021/1/13
 */

@Aspect
@Component
public class AutoCacheAspect{

    @Autowired
    StringRedisTemplate redisTemplate;

    final String cacheDateFormat = "yyyyMMddHHmmss";

    //拦截MethodCache，开启缓存
    @Around("@annotation(com.autohome.car.api.common.cache.AutoCache)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AutoCache cacheAnnotation = method.getAnnotation(AutoCache.class);
        MethodDetail methodDetail = new MethodDetail(method, joinPoint.getArgs());
        String key = methodDetail.instanceKey(cacheAnnotation);

        String dataJson = null;
        String jsonResult = redisTemplate.opsForValue().get(key);
        if (jsonResult != null && jsonResult.length() >= 14) {
            Date time = DateUtils.StringToDate(jsonResult.substring(0, 14), cacheDateFormat, new Date());
            dataJson = jsonResult.substring(14);

            //缓存没过期
            if (time.after(new Date())) {
                CompletableFuture.runAsync(() -> {
                    int refreshMinutes = cacheAnnotation.expireIn() / 2;
                    if (refreshMinutes < 1) {
                        return;
                    }
                    int refreshSeconds = refreshMinutes == 0 ? 30 : refreshMinutes * 60;
                    Date refreshTime = org.apache.commons.lang3.time.DateUtils.addSeconds(time, -refreshSeconds);
                    if (refreshTime.before(new Date())) {
                        //过期时间过半 ，异步回源
                        CompletableFuture.runAsync(() -> {
                            try {
                                proceed(joinPoint, null, method, key, cacheAnnotation, true);
                            } catch (Throwable e) {
                                //do nothing
                            }
                        });
                    }
                });
                Object obj = cacheResult(dataJson, method);
                return obj;
            }
        }
        //回源
        return proceed(joinPoint, dataJson, method, key, cacheAnnotation, false);
    }

    Object proceed(ProceedingJoinPoint joinPoint,String dataJson,Method method,String key,AutoCache cacheAnnotation,boolean refresh) throws Throwable {
        if(refresh){
            //刷新请求新增分布式锁，防止过期大量请求
            if(!redisTemplate.opsForValue().setIfAbsent(key+":asynclock","true",1,TimeUnit.MINUTES)){
                return null;
            }
        }

        Object sourceResult = null;

        try {
            sourceResult = joinPoint.proceed();
        } catch (Throwable ex) {
            //如果回源发生异常，而redis中仍有未物理过期的缓存，则直接返回缓存内容
            if (dataJson != null) {
                return cacheResult(dataJson,method);
            }
            throw ex;
        }
        //集合是否判断，集合不判断会导致大量空的集合写入redis,判断的话会导致穿透，每次都会请求数据库
        if(sourceResult!=null) {
            saveCacheData(key, sourceResult, cacheAnnotation);
        }
        return sourceResult;
    }

    Object cacheResult(String dataJson,Method method){
        Object cacheResult = JsonUtils.toObject(dataJson, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return method.getAnnotatedReturnType().getType();
            }
        });
        return cacheResult;
    }

    void saveCacheData(String key,Object data,AutoCache cacheAnnotation) {
        String expireIn = DateUtils.DateToString(new Date(new Date().getTime() + (long) cacheAnnotation.expireIn() * 60 * 1000), cacheDateFormat);
        int removeIn = cacheAnnotation.removeIn() < cacheAnnotation.expireIn()?cacheAnnotation.expireIn(): cacheAnnotation.removeIn();
        redisTemplate.opsForValue().set(key, expireIn + JsonUtils.toString(data), removeIn, TimeUnit.MINUTES);
    }
}