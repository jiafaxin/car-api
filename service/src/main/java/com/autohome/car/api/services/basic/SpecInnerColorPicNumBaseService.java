package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecColorMapper;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
public class SpecInnerColorPicNumBaseService extends BaseService<List<SpecPicColorStatisticsEntity>> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecColorMapper specColorMapper;

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

    protected boolean getFromDB(){
        return true;
    }

    @Override
    protected List<SpecPicColorStatisticsEntity> getData(Map<String, Object> params) {
        int seriesid = (int) params.get("seriesid");
        return getData(seriesid);
    }

    public CompletableFuture<List<SpecPicColorStatisticsEntity>> get(int seriesid) {
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid",seriesid);
        return getAsync(params);
    }

    List<SpecPicColorStatisticsEntity> getData(int seriesid) {
        List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecInnerColorPicNumBySeries(seriesid);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                Map<String, Object> params = new HashMap<>();
                params.put("seriesid", seriesid);
                //全量查询非常慢（2分钟出不来结果），使用单个查询
                List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecInnerColorPicNumBySeries(seriesid);
                refresh(params, list);
            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
