package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.basic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CarSpecColorBaseService extends BaseService<List<CarSpecColorEntity>> {

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    SpecColorMapper specColorMapper;

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
        return false;
    }

    @Override
    protected boolean canEhCache(){
        return false;
    }

    @Override
    protected List<CarSpecColorEntity> getData(Map<String, Object> params) {
        int specId = (int)params.get("specId");
        return specColorMapper.getSpecColorBySpec(specId);
    }

    public List<CarSpecColorEntity> get(int specId) {
        return get(makeParam(specId));
    }

    public Map<Integer,List<CarSpecColorEntity>> getMap(List<Integer> specId) {
        List<CarSpecColorEntity> list = getList(specId);
        return list.stream().collect(Collectors.groupingBy(CarSpecColorEntity::getSId));
    }

    public List<CarSpecColorEntity> getList(List<Integer> specId){
        List<CarSpecColorEntity> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(specId)) {
            return result;
        }
        List<Map<String,Object>> params = new ArrayList<>();
        for (Integer id : specId) {
            if(id==null)
                continue;
            params.add(makeParam(id));
        }
        List<List<CarSpecColorEntity>> data = mGet(params);
        if(CollectionUtils.isEmpty(data)){
            return result;
        }
        for(List<CarSpecColorEntity> item :data){
            if(!CollectionUtils.isEmpty(item)){
                result.addAll(item);
            }
        }
        return result;
    }

    public int refreshAll(Consumer<String> log) {
        List<SpecViewAndPicEntity> allSpecs = specViewMapper.getAllSpecs();
        allSpecs.forEach(specView -> {
            List<CarSpecColorEntity> colors = specColorMapper.getSpecColorBySpec(specView.getSpecId());
            try {
                refresh(makeParam(specView.getSpecId()),colors);
            }catch (Exception e){
                log.accept(ExceptionUtil.getStackTrace(e));
            }
        });
        return allSpecs.size();
    }

    Map<String, Object> makeParam(int specId){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("specId", specId);
        return params;
    }

}
