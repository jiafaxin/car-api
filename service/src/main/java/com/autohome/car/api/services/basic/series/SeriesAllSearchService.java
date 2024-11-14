package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.SeriesBaseEntity;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class SeriesAllSearchService extends BaseService<List<SeriesBaseEntity>> {

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
    protected List<SeriesBaseEntity> getData(Map<String, Object> params) {
        List<SeriesViewEntity> allSeriesView = seriesMapper.getAllSeriesView();
        List<SeriesBaseEntity> seriesBaseEntities = new ArrayList<>();
        if(!CollectionUtils.isEmpty(allSeriesView)){
            for(SeriesViewEntity seriesView : allSeriesView){
                SeriesBaseEntity seriesBaseEntity = new SeriesBaseEntity();
                seriesBaseEntity.setId(seriesView.getSeriesid());
                seriesBaseEntity.setName(StringUtils.isBlank(seriesView.getSeriesName()) ? "" : HtmlUtils.decode(seriesView.getSeriesName()));
                seriesBaseEntity.setBrandId(seriesView.getBrandId());
                seriesBaseEntity.setBrandName(StringUtils.isBlank(seriesView.getBrandName()) ? "" : HtmlUtils.decode(seriesView.getBrandName()));
                seriesBaseEntity.setLevelId(seriesView.getLevelId());
                seriesBaseEntity.setLevelName(StringUtils.isBlank(seriesView.getLevelName()) ? "" :
                        (seriesView.getLevelName().contains("皮卡") ? "皮卡" : seriesView.getLevelName()));
                seriesBaseEntity.setFctId(seriesView.getFctId());
                seriesBaseEntity.setFctName(StringUtils.isBlank(seriesView.getFctName()) ? "" : HtmlUtils.decode(seriesView.getFctName()));
                seriesBaseEntity.setPlace(seriesView.getSeriesplace());
                seriesBaseEntity.setState(seriesView.getSeriesState());
                seriesBaseEntity.setPriceMin(seriesView.getSeriesPriceMin());
                seriesBaseEntity.setPriceMax(seriesView.getSeriesPriceMax());
                seriesBaseEntity.setImg(seriesView.getSeriesImg());
                seriesBaseEntity.setNewRank(seriesView.getSeriesNewRank());
                seriesBaseEntity.setIsPublic(seriesView.getSeriesIsPublic());
                seriesBaseEntities.add(seriesBaseEntity);
            }
        }
        return seriesBaseEntities;
    }

    public List<SeriesBaseEntity> get(){
        return get(null);
    }

    public int refreshAll(Consumer<String> log){
        List<SeriesBaseEntity> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
