package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecPictureStatisticsMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.Car25PictureViewEntity;
import com.autohome.car.api.data.popauto.entities.CarSpecPictureStatisticsEntity;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
public class Spec25PicBaseService extends BaseService<List<Car25PictureViewEntity>> {

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    SpecPictureStatisticsMapper specPictureStatisticsMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected boolean canEhCache() {
        return false;
    }

    @Override
    protected List<Car25PictureViewEntity> getData(Map<String, Object> params) {
        int specid = (int) params.get("specid");
        return getData(specid);
    }

    public CompletableFuture<List<Car25PictureViewEntity>> get(int specid) {
        Map<String,Object> params = new HashMap<>();
        params.put("specid",specid);
        return getAsync(params);
    }

    List<Car25PictureViewEntity> getData(int specid) {
        List<Car25PictureViewEntity> list = specPictureStatisticsMapper.GetDicSpec25Pic(specid);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SpecViewEntity> specViews =  specViewMapper.getAll();
        specViews.forEach(item -> {
            try {
                int specid = item.getSpecId();
                Map<String, Object> params = new HashMap<>();
                params.put("specid", specid);
                List<Car25PictureViewEntity> list = specPictureStatisticsMapper.GetDicSpec25Pic(specid);
                refresh(params, list);
            }catch (Exception e){
                log.accept("error >> " + item.getSpecId() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return specViews.size();
    }


}
