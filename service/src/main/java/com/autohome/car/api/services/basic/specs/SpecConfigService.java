package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SpecConfigMapper;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecConfigService  extends BaseService<List<SpecConfigEntity>> {

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
    protected boolean getFromDB(){
        return true;
    }

    @Override
    protected List<SpecConfigEntity> getData(Map<String, Object> params) {
        return specConfigMapper.getSpecConfigs(getSpecIdFromParams(params));
    }

    public Map<Integer, List<SpecConfigEntity>> getMap(List<Integer> ids) {
        List<List<SpecConfigEntity>> list = getList(ids);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(SpecConfigEntity::getSpecId));
    }

    public List<List<SpecConfigEntity>> getList(List<Integer> ids){
        if(CollectionUtils.isEmpty(ids))
            return new ArrayList<>();
        List<Map<String, Object>> params = ids.stream()
                .filter(Objects::nonNull)
                .map(this::makeParams)
                .collect(Collectors.toList());
        return mGet(params);
    }

    public List<SpecConfigEntity> get(int specId){
        return get(makeParams(specId));
    }

    final static String specIdParamName = "specId";

    int getSpecIdFromParams(Map<String, Object> params){
        return (int)params.get(specIdParamName);
    }

    Map<String, Object> makeParams(int specId){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put(specIdParamName,specId);
        return map;
    }

    public void refreshAll(Consumer<String> log){
        int i = 0;
        for (Integer id : specMapper.getAllIds()) {
            try {
                Map<String,Object> param = makeParams(id);
                refresh(param,getData(param));
                i++;
            }catch (Exception e){
                log.accept("error:"+id+":"+ ExceptionUtil.getStackTrace(e));
            }
        }
    }



}
