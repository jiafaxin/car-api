package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecBaseEntity;
import com.autohome.car.api.services.basic.BaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系下所有在售的车型
 */
@Service
public class SeriesSpecWithStateService extends BaseService<List<String>> {


    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected String keyVersion(){
        return ":v2";
    }

    @Override
    protected boolean getFromDB(){
        return true;
    }

    Map<String, Object> makeParam(int seriesId){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("seriesId",seriesId);
        return params;
    }

    public List<Integer> getSpecIds(int seriesId,List<Integer> states) {
        List<String> list = get(makeParam(seriesId));
        return list.stream().map(x -> x.split("-")).filter(x -> states.contains(Integer.parseInt(x[1]))).map(x -> Integer.parseInt(x[0])).collect(Collectors.toList());
    }

    @Override
    protected List<String> getData(Map<String, Object> params) {
        int seriesId = (int) params.get("seriesId");
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (seriesBaseInfo == null)
            return new ArrayList<>();

        List<SpecBaseEntity> baseEntity = specViewMapper.getBaseBySeriesId(seriesId, Level.isCVLevel(seriesBaseInfo.getLevelId()));
        return baseEntity.stream().map(specBaseEntity -> specBaseEntity.getId() + "-" + specBaseEntity.getSpecState()).collect(Collectors.toList());
    }

    public void refreshAll(Consumer<String> log) {
        List<SpecBaseEntity> list = specViewMapper.getAllBase();
        Map<Integer,List<SpecBaseEntity>> map = list.stream().collect(Collectors.groupingBy(SpecBaseEntity::getSeriesId));
        map.forEach((seriesId,baseEntity)->{
            refresh(makeParam(seriesId), baseEntity.stream().map(specBaseEntity->specBaseEntity.getId()+"-"+specBaseEntity.getSpecState()).collect(Collectors.toList()));
        });
    }

}
