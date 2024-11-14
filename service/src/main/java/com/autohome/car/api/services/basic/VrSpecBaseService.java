package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.VrSpecMapper;
import com.autohome.car.api.data.popauto.entities.VrSpecEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class VrSpecBaseService extends BaseService<List<VrSpecEntity>> {

    @Resource
    private VrSpecMapper vrSpecMapper;


    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    public List<VrSpecEntity> getDataBySpecId(int specId){
        List<VrSpecEntity> vrSpecEntities = get(null);
        if(CollectionUtils.isEmpty(vrSpecEntities)){
            return null;
        }
       return vrSpecEntities.stream().filter(vrSpecEntity -> vrSpecEntity.getSpecId() == specId).collect(Collectors.toList());
    }

    public List<VrSpecEntity> getDataBySeriesId(int seriesId){
        List<VrSpecEntity> vrSpecEntities = get(null);
        if(CollectionUtils.isEmpty(vrSpecEntities)){
            return null;
        }
        return vrSpecEntities.stream().filter(vrSpecEntity -> vrSpecEntity.getSeriesId() == seriesId).collect(Collectors.toList());
    }

    @Override
    protected List<VrSpecEntity> getData(Map<String, Object> params) {
        return vrSpecMapper.getVrSpecAll();
    }

    public int refreshAll(Consumer<String> log){
        List<VrSpecEntity> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
