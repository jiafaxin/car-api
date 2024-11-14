package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.PicClassMapper;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.ShowMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.basic.models.ShowBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class Series25PictureBaseService extends BaseService<List<PicInfoEntity>> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    PicClassMapper picClassMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<PicInfoEntity> getData(Map<String, Object> params) {
        int seriesid = (int) params.get("seriesid");
        return getData(seriesid);
    }

    public CompletableFuture<List<PicInfoEntity>> get(int seriesid) {
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid",seriesid);
        return getAsync(params);
    }

    List<PicInfoEntity> getData(int seriesid) {
        List<PicInfoEntity> list = picClassMapper.Get25PicInfo(seriesid);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                Map<String, Object> params = new HashMap<>();
                params.put("seriesid", seriesid);

                List<PicInfoEntity> list = picClassMapper.Get25PicInfo(seriesid);
                refresh(params, list);
            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
