package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecColorMapper;
import com.autohome.car.api.data.popauto.SpecPictureStatisticsMapper;
import com.autohome.car.api.data.popauto.entities.CarSpecPictureStatisticsEntity;
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
public class SpecPictureStatisticsBaseService extends BaseService<List<CarSpecPictureStatisticsEntity>> {

    @Autowired
    SeriesMapper seriesMapper;

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
    protected List<CarSpecPictureStatisticsEntity> getData(Map<String, Object> params) {
        int seriesid = (int) params.get("seriesid");
        return getData(seriesid);
    }

    public CompletableFuture<List<CarSpecPictureStatisticsEntity>> get(int seriesid) {
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid",seriesid);
        return getAsync(params);
    }

    List<CarSpecPictureStatisticsEntity> getData(int seriesid) {
        List<CarSpecPictureStatisticsEntity> list = specPictureStatisticsMapper.getSpecPictureStatisticsBySeriesId(seriesid);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                Map<String, Object> params = new HashMap<>();
                params.put("seriesid", seriesid);
                List<CarSpecPictureStatisticsEntity> list = specPictureStatisticsMapper.getSpecPictureStatisticsBySeriesId(seriesid);
                refresh(params, list);
            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
