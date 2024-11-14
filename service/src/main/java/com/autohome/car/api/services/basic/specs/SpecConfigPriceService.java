package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecConfigMapper;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigPriceEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecConfigPriceService extends BaseService<List<SpecConfigPriceEntity>> {

    @Autowired
    SpecConfigMapper specConfigMapper;

    @Autowired
    SpecMapper specMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<SpecConfigPriceEntity> getData(Map<String, Object> params) {
        int specId = (int)params.get("specId");
        return specConfigMapper.getSpecConfigPrice(specId);
    }

    public CompletableFuture<Map<Integer,List<SpecConfigPriceEntity>>> getList(List<Integer> specIds) {
        if(specIds==null||specIds.size()==0)
            return CompletableFuture.completedFuture(new LinkedHashMap<>());

        return CompletableFuture.supplyAsync(()->{
            List<Map<String,Object>> keys = specIds.stream().map(x->makeParam(x)).collect(Collectors.toList());
            List<List<SpecConfigPriceEntity>> list = mGet(keys);
            Map<Integer,List<SpecConfigPriceEntity>> map = new LinkedHashMap<>();
            for (List<SpecConfigPriceEntity> specConfigPriceEntities : list) {
                if(specConfigPriceEntities==null||specConfigPriceEntities.size()==0)
                    continue;
                map.put(specConfigPriceEntities.get(0).getSpecId(),specConfigPriceEntities);
            }
            return map;
        });
    }

    public void refreshAll(Consumer<String> log) {
        List<SpecConfigPriceEntity> datas = specConfigMapper.getAllSpecConfigPrice();
        Map<Integer, List<SpecConfigPriceEntity>> listMap = datas.stream().collect(Collectors.groupingBy(SpecConfigPriceEntity::getSpecId));
        listMap.forEach((k, v) -> {
            refresh(makeParam(k), v);
        });
        log.accept("count:" + datas.size());
    }

    Map<String,Object> makeParam(int specId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("specId", specId);
        return params;
    }

    public List<SpecConfigPriceEntity> getBySpecId(int specId) {
        List<SpecConfigPriceEntity> specConfigPriceEntities = get(makeParam(specId));
        if (CollectionUtils.isEmpty(specConfigPriceEntities)) {
            return Collections.emptyList();
        }
        return specConfigPriceEntities;
    }


}
