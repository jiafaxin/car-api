package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.SeriesSearchEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class SeriesSearchService  extends BaseService<List<SeriesSearchEntity>> {

    @Autowired
    SeriesMapper seriesMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.H_1;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<SeriesSearchEntity> getData(Map<String, Object> params) {
        return seriesMapper.getAllSeries();
    }

    public List<SeriesSearchEntity> get(){
        return get(null);
    }

    public int refreshAll(Consumer<String> log){
        List<SeriesSearchEntity> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
