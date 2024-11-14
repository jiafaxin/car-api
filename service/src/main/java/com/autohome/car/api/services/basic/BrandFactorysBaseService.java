package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.FactoryMapper;
import com.autohome.car.api.data.popauto.entities.FactoryInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class BrandFactorysBaseService extends BaseService<List<FactoryInfoEntity>>{

    @Autowired
    FactoryMapper factoryMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    public CompletableFuture<List<FactoryInfoEntity>> get(int brandId) {
        Map<String, Object> params = new HashMap<>();
        params.put("brandId", brandId);
        return getAsync(params);
    }

    @Override
    protected List<FactoryInfoEntity> getData(Map<String, Object> params) {
        int brandId = (int)params.get("brandId");
        List<FactoryInfoEntity> list = factoryMapper.getFactoryInfos(brandId);
        return list;
    }

    /**
     * 全量刷数据到redis
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log) {
        List<FactoryInfoEntity> list = factoryMapper.getAllFactoryInfos();
        Map<Integer, List<FactoryInfoEntity>> map = list.stream().collect(Collectors.groupingBy(x -> x.getBrandId(), Collectors.toList()));
        for (Map.Entry<Integer,List<FactoryInfoEntity>> item:map.entrySet()) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("brandId", item.getKey());
                refresh(params, item.getValue());
            }catch (Exception e){
                log.accept("error：" + item.getKey() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        }
        return map.size();
    }
}
