package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecConfigBagMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigBagEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecConfigBagNewService extends BaseService<List<SpecConfigBagEntity>> {

    @Autowired
    SpecConfigBagMapper specConfigBagMapper;


    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }


    protected boolean getFromDB() {
        return true;
    }



    @Override
    protected List<SpecConfigBagEntity> getData(Map<String, Object> params) {
        int specId = (Integer) params.get("specId");
        return specConfigBagMapper.getBags(specId);
    }

    public List<SpecConfigBagEntity> getBagList(List<Integer> specIds) {
        List<SpecConfigBagEntity> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(specIds)) {
            return result;
        }
        List<Map<String,Object>> params = specIds.stream().map(x->makeParam(x)).collect(Collectors.toList());
        List<List<SpecConfigBagEntity>> list = mGet(params);
        if(CollectionUtils.isEmpty(list))
            return result;

        for (List<SpecConfigBagEntity> item : list) {
            if(CollectionUtils.isEmpty(item)) {
                continue;
            }
            result.addAll(item);
        }
        return result;
    }

    public Map<Integer,List<SpecConfigBagEntity>> getList(List<Integer> specIds) {
        Map<Integer,List<SpecConfigBagEntity>> result = new LinkedHashMap<>();

        if(specIds==null||specIds.size()==0)
            return result;
        List<Map<String,Object>> params = specIds.stream().map(x->makeParam(x)).collect(Collectors.toList());
        List<List<SpecConfigBagEntity>> list = mGet(params);
        if(list==null||list.size()==0)
            return result;

        for (List<SpecConfigBagEntity> item : list) {
            if(item==null||item.size()==0)
                continue;
            result.put(item.get(0).getSpecId(),item);
        }

        return result;
    }

    Map<String, Object> makeParam(int specId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("specId", specId);
        return params;
    }

    public void refreshAll(Consumer<String> log) {

        Map<Integer, List<SpecConfigBagEntity>> map = new LinkedHashMap<>();
        for (SpecConfigBagEntity bag : specConfigBagMapper.getAllBags()) {
            if (!map.containsKey(bag.getSpecId())) {
                map.put(bag.getSpecId(), new ArrayList<>());
            }
            map.get(bag.getSpecId()).add(bag);
        }
        map.forEach((k, v) -> {
            refresh(makeParam(k), v);
        });
        log.accept("count:" + map.size());
    }
}
