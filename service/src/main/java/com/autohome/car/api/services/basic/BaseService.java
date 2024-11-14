package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCache;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.JsonUtils;
import com.autohome.car.api.common.Md5Util;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseService<T> {

    @Autowired
    protected StringRedisTemplate redisTemplate;

    @Autowired
    protected RedisTemplate<String,byte[]> bytesRedisTemplate;

    @Autowired
    EhCache ehCache;

    @Value("${provider.can-from-db:false}")
    private boolean canfromdb;

    final static String BaseKey = "basic:service:v5:";

    /**
     * EhCache 缓存时间
     * @return
     */
    protected abstract EhCacheName getCacheName();

    /**
     * Redis 超时时间
     * @return
     */
    protected abstract Integer getRedisTimeoutMinutes();

    /**
     * 回源逻辑
     * @param params
     * @return
     */
    protected abstract T getData(Map<String,Object> params);

    protected boolean getFromDB(){
        return canfromdb;
    }

    protected boolean canEhCache(){
        return true;
    }

    protected String keyVersion(){
        return "";
    }

    public String getKey(Map<String,Object> params) {
        String key = params == null || params.size() == 0 ? "" : Md5Util.get(params);
        key = BaseKey + this.getClass().getSimpleName() + ":" + key;
        if (StringUtils.isNotBlank(keyVersion())) {
            key = key + keyVersion();
        }
        return key;
    }

    /**
     * @return key: redis key value: paramField对应的id值
     */
//    private List<String> getCacheKeys(List<Map<String,Object>> params) {
//        if (CollectionUtils.isEmpty(params)) {
//            return Collections.emptyList();
//        }
//        return params.stream().map(this::getKey).collect(Collectors.toList());
//    }

    private Map<String, Map<String, Object>> getCacheKeys(List<Map<String,Object>> params) {
        if (CollectionUtils.isEmpty(params)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, Object>> cacheMap = new HashMap<>(params.size());
        for (Map<String, Object> param : params) {
            String key = this.getKey(param);
            cacheMap.put(key, param);
        }
        return cacheMap;
    }

    /**
     * 刷新redis 缓存
     * @param params
     * @param data
     * @return
     */
    public T refresh(Map<String,Object> params,T data) {
        setToRedis(getKey(params), data);
        return data;
    }


    /**
     * 此方法不查redis，用的时候请注意
     * @param params
     * @return
     */
    public T getNew(Map<String,Object> params) {
        String key = getKey(params);
        String cacheName = getCacheName().toString();
        T result = null;
        if(canEhCache()){
            result = ehCache.get(cacheName, key);
            if (result != null) {
                return result;
            }
        }
        if (result == null && getFromDB()) {
            result = getData(params);
            if (result == null) {
                return null;
            }
        }
        if (result == null) {
            return null;
        }
        if(canEhCache()){
            ehCache.set(cacheName, key, result);
        }
        return result;
    }

    public CompletableFuture<T> getAsync(Map<String,Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            return get(params);
        });
    }

    public T get(Map<String,Object> params) {
        String key = getKey(params);
        String cacheName = getCacheName().toString();
        T result = null;
        if(canEhCache()){
            result = ehCache.get(cacheName, key);
            if (result != null) {
                return result;
            }
        }
        result = getFromRedis(key);
        if (result == null && getFromDB()) {
            result = getData(params);
            if (result == null)
                return null;
            setToRedis(key, result);
        }
        if (result == null) {
            return null;
        }
        if(canEhCache()){
            ehCache.set(cacheName, key, result);
        }
        return result;
    }

    /**
     * 更新单个数据
     * @param params
     */
    public void refreshRedis(Map<String,Object> params){
        T data = getData(params);
        if (data == null) {
            return ;
        }
        setToRedis(getKey(params), data);
    }

    /**
     * 此方法只用于运营清除本地缓存
     * @param params
     */
    public void delECache(Map<String,Object> params){
        String key = getKey(params);
        String cacheName = getCacheName().toString();
        T result = null;
        if(canEhCache()){
            result = ehCache.get(cacheName, key);
            if (result != null) {
                ehCache.delete(cacheName,key);
            }
        }
    }

    /**
     * @return key: paramField对应的值 ， value: 缓存
     */
    public List<T> mGet(List<Map<String,Object>> params) {
        List<T> resultList = new ArrayList<>(params.size());
        Map<String, Map<String, Object>> cacheKeyMaps = getCacheKeys(params);
        List<String> cacheKeys = new ArrayList<>(cacheKeyMaps.keySet());
        //cacheKeys = cacheKeys.stream().distinct().collect(Collectors.toList());
        //从一级缓存获取数据
        String cacheName = getCacheName().toString();
        Map<String, T> cacheMap = ehCache.mGet(cacheName, cacheKeys);
        if (CollectionUtils.isNotEmptyMap(cacheMap)) {
            resultList.addAll(cacheMap.values());
        }
        List<String> unExistEhCacheKey = this.filterKey(cacheMap, cacheKeys);
        if (CollectionUtils.isEmpty(unExistEhCacheKey)) {
            return resultList;
        }
       //从redis获取数据
        Map<String, T> redisMap = mGetFromRedis(unExistEhCacheKey);
        if (CollectionUtils.isNotEmptyMap(redisMap)) {
            resultList.addAll(redisMap.values());
            for (Map.Entry<String, T> entry : redisMap.entrySet()) {
                ehCache.set(cacheName, entry.getKey(), entry.getValue());
            }
        }
        List<String> unExistRedisKey = this.filterKey(redisMap, unExistEhCacheKey);
        if (CollectionUtils.isEmpty(unExistRedisKey)) {
            return resultList;
        }
        if (CollectionUtils.size(unExistRedisKey) > 0 && getFromDB()) {
            for (String key : unExistRedisKey) {
//                log.warn("回源: " + key);
                T obj = getData(cacheKeyMaps.get(key));
                if (obj == null) {
                    continue;
                }
                setToRedis(key, obj);
                ehCache.set(cacheName, key, obj);
                resultList.add(obj);
            }
        }
        return resultList;
    }

    private List<String> filterKey(Map<String,T> localMap, List<String> listKey) {
        if (CollectionUtils.isEmptyMap(localMap)) {
            return listKey;
        }
        return listKey.stream().filter(key -> !localMap.containsKey(key)).collect(Collectors.toList());
    }

    protected void setToRedis(String key,T result){
        redisTemplate.opsForValue().set(key, JsonUtils.toString(result), getRedisTimeoutMinutes(), TimeUnit.MINUTES);
    }

    protected T getFromRedis(String key){
        String json = redisTemplate.opsForValue().get(key);
        return getT(json);
    }

    private T getT(String json) {
        if(StringUtils.isBlank(json)){
            return null;
        }
        Type type = ((ParameterizedTypeImpl) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        TypeReference<T> typeReference= new TypeReference<T>() {
            @Override
            public Type getType(){
                return type;
            }
        };
        return JsonUtils.toObject(json, typeReference);
    }
    protected Map<String, T> mGetFromRedis(List<String> keys){
        List<String> stringList = redisTemplate.opsForValue().multiGet(keys);
        if(CollectionUtils.isEmpty(stringList)){
            return Collections.emptyMap();
        }
        Map<String, T> resultMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = stringList.get(i);
            if(StringUtils.isBlank(value)){
                continue;
            }
            try {
                T t = getT(value);
                if (t != null) {
                    resultMap.put(key, t);
                }
            }catch (Exception e) {
                log.error("反序列化失败", e);
            }
        }
        return resultMap;
    }
}
