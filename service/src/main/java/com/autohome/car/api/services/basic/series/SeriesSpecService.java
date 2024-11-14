package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecBaseEntity;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SeriesSpecService extends BaseService<Map<Integer,List<Integer>>> {


    @Autowired
    SpecViewMapper specViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected boolean getFromDB(){
        return true;
    }

    public List<Integer> getSpecIds(int seriesId){
        Map<Integer,List<Integer>> list = get(null);
        return list.get(seriesId);
    }

    @Override
    protected Map<Integer, List<Integer>> getData(Map<String, Object> params) {
        List<SpecBaseEntity> baseEntity = specViewMapper.getAllBase();
        Map<Integer,List<Integer>> list = new LinkedHashMap<>();
        for (SpecBaseEntity specBaseEntity : baseEntity) {
            if(!list.containsKey(specBaseEntity.getSeriesId())){
                list.put(specBaseEntity.getSeriesId(),new ArrayList<>());
            }
            list.get(specBaseEntity.getSeriesId()).add(specBaseEntity.getId());
        }
        return list;
    }

    public void refreshAll(Consumer<String> log) {
        refresh(null, getData(null));
    }

}
