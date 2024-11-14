package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SYearViewMapper;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SYearViewEntity;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.data.popauto.entities.SpecStateEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class SpecListSameYearByYearService extends BaseService<List<SpecStateEntity>> {

    @Resource
    SeriesMapper seriesMapper;

    @Resource
    SpecViewMapper specViewMapper;

    @Resource
    SYearViewMapper sYearViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected List<SpecStateEntity> getData(Map<String, Object> params) {
        int seriesId = (int) params.get("seriesId");
        int yearId = (int) params.get("yearId");
        boolean isCv = (boolean) params.get("isCv");
        return getData(seriesId, yearId, isCv);
    }

    List<SpecStateEntity> getData(int seriesId, int year, boolean isCv) {
        List<SpecStateEntity> list = isCv
                ? specViewMapper.getCvSpecListByYear(seriesId, year)  //电动车
                : specViewMapper.getSpecListByYear(seriesId, year);   //燃油

        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public CompletableFuture<List<SpecStateEntity>> get(int seriesId, int yearId, boolean isCv){
        return getAsync(makeParams(seriesId, yearId, isCv));
    }

    Map<String,Object> makeParams(int seriesId, int yearId, boolean isCv){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesId",seriesId);
        params.put("yearId",yearId);
        params.put("isCv",isCv);
        return params;
    }

    public int refreshAll(Consumer<String> log) {
        //所有车系
        List<SeriesViewEntity> seriesLists = seriesMapper.getAllSeriesView();
        if(!CollectionUtils.isEmpty(seriesLists)){
            for(SeriesViewEntity seriesInfo : seriesLists){
                int seriesId = seriesInfo.getSeriesid();
                boolean isCv = Level.isCVLevel(seriesInfo.getLevelId());
                //车系对应的所有年代
                List<SYearViewEntity> sYears = sYearViewMapper.getSYearViewBySeriesId(seriesInfo.getSeriesid());
                //同年代款车型id
                for(SYearViewEntity sYear :sYears){
                    int sYearId = sYear.getSYearId();
                    List<SpecStateEntity> list = isCv
                            ? specViewMapper.getCvSpecListByYear(seriesId, sYearId)  //电动车
                            : specViewMapper.getSpecListByYear(seriesId, sYearId);   //燃油
                    refresh(makeParams(seriesId, sYearId, isCv), list);
                }

            }
            return seriesLists.size();
        }
        log.accept("SpecListSameYearBaseService SpecListSameYearIds is null!" );
        return 0;
    }
}
