package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
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
public class SeriesSpecBaseService extends BaseService<List<SpecViewEntity>> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecViewMapper specViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<SpecViewEntity> getData(Map<String, Object> params) {
        int seriesid = (int) params.get("seriesid");
        boolean iscv = (boolean) params.get("iscv");
        return getData(seriesid,iscv);
    }

    public CompletableFuture<List<SpecViewEntity>> get(int seriesid,boolean isCV) {
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid",seriesid);
        params.put("iscv",isCV);
        return getAsync(params);
    }

    List<SpecViewEntity> getData(int seriesid,boolean isCV) {
        List<SpecViewEntity> list = specViewMapper.getAllSpecBySeriesId(seriesid, isCV);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                Map<String, Object> params = new HashMap<>();
                boolean isCvSeries = Level.isCVLevel(item.getLevelId());
                //int isCvSeries = seriesMapper.getDicCvSeries(seriesid);
                List<SpecViewEntity> list = specViewMapper.getAllSpecBySeriesId(seriesid, isCvSeries);
                params.put("seriesid", seriesid);
                params.put("iscv", isCvSeries);
                refresh(params, list);
            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
