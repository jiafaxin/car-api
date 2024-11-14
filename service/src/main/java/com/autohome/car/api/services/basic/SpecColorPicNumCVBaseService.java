package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.BaseConfig.Level;
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
public class SpecColorPicNumCVBaseService extends BaseService<List<SpecPicColorStatisticsEntity>> {

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
        List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecColorPicNumBySeriesCV(seriesid,false);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                boolean iscv = Level.isCVLevel(item.getLevelId());
                Map<String, Object> params = new HashMap<>();
                params.put("seriesid", seriesid);
                List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecColorPicNumBySeriesCV(seriesid,iscv );
                refresh(params, list);
            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
