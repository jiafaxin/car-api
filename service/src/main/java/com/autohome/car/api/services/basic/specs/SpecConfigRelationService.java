package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SpecConfigMapper;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigRelationEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecConfigRelationService  extends BaseService<List<SpecConfigRelationEntity>> {

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
    protected List<SpecConfigRelationEntity> getData(Map<String, Object> params) {
        int specId = (int)params.get("specId");
        return specConfigMapper.getSpecConfigRelations(specId);
    }

    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("specId",specId);
        return params;
    }

    public List<SpecConfigRelationEntity> get(int specId){
        return get(makeParams(specId));
    }

    public Map<Integer,List<SpecConfigRelationEntity>> getList(List<Integer> ids) {
        List<Map<String,Object>> params = ids.stream().map(x->makeParams(x)).collect(Collectors.toList());
        List<List<SpecConfigRelationEntity>> list = mGet(params);
        Map<Integer,List<SpecConfigRelationEntity>> result = new LinkedHashMap<>();
        for (List<SpecConfigRelationEntity> item : list) {
            if(item==null||item.size()==0)
                continue;
            result.put(item.get(0).getSpecId(),item);
        }
        return result;
    }

    public void refreshAll(Consumer<String> log){
        int i = 0;
        for (Integer id : specMapper.getAllIds()) {
            try {
                Map<String,Object> params = makeParams(id);
                refresh(params,getData(params));
            }catch (Exception e){
                log.accept("error:"+id+" >>> " + ExceptionUtil.getStackTrace(e));
            }
        }
    }
}
