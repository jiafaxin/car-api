package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecConfigBagMapper;
import com.autohome.car.api.data.popauto.SpecConfigMapper;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigBagEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigPriceEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SpecConfigBagService extends BaseService<Map<Integer,List<SpecConfigBagEntity>>> {

    @Autowired
    SpecConfigBagMapper specConfigBagMapper;


    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }


    protected boolean getFromDB(){
        return true;
    }

    @Override
    protected Map<Integer,List<SpecConfigBagEntity>> getData(Map<String, Object> params) {
        Map<Integer,List<SpecConfigBagEntity>> map = new LinkedHashMap<>();
        for (SpecConfigBagEntity bag : specConfigBagMapper.getAllBags()) {
            if(!map.containsKey(bag.getSpecId())){
                map.put(bag.getSpecId(),new ArrayList<>());
            }
            map.get(bag.getSpecId()).add(bag);
        }
        return map;
    }

    public Map<Integer,List<SpecConfigBagEntity>> getAll(){
        return get(null);
    }

    public void refreshAll(Consumer<String> log){
        Map<Integer,List<SpecConfigBagEntity>> datas = getData(null);
        refresh(null,datas);
        log.accept("count:"+datas.size());
    }
}
