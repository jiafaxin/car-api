package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecPicClassStatisticsMapper;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class SpecPicClassStatisticsBaseService extends BaseService<List<SpecPicColorStatisticsEntity>> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecPicClassStatisticsMapper specPicClassStatisticsMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    public List<SpecPicColorStatisticsEntity> getList(List<Pair<Integer, Boolean>> ids) {
        if (CollectionUtils.isEmpty(ids))
            return new ArrayList<>();
        List<Map<String, Object>> params = ids.stream()
                .filter(Objects::nonNull)
                .map(x -> makeParams(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
        List<List<SpecPicColorStatisticsEntity>> list = mGet(params);
        if (list == null || list.size() == 0)
            return new ArrayList<>();
        List<SpecPicColorStatisticsEntity> flatList = list.stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return flatList;
    }

    public Map<Integer,List<SpecPicColorStatisticsEntity>> getSpecMap(List<Pair<Integer, Boolean>> ids) {
        if(ids==null||ids.size() == 0)
            return new LinkedHashMap<>();

        List<SpecPicColorStatisticsEntity> list = getList(ids);
        if(list==null||list.size()==0)
            return new LinkedHashMap<>();

        return list.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getSpecId));
    }

    public Map<Integer,List<SpecPicColorStatisticsEntity>> getMap(List<Pair<Integer, Boolean>> ids) {
        if(ids==null||ids.size() == 0)
            return new LinkedHashMap<>();

        List<SpecPicColorStatisticsEntity> list = getList(ids);
        if(list==null||list.size()==0)
            return new LinkedHashMap<>();

        return list.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getSeriesId));
    }

    @Override
    protected List<SpecPicColorStatisticsEntity> getData(Map<String, Object> params) {
        int seriesid = (int) params.get("seriesid");
        boolean iscv = (boolean) params.get("iscv");
        return getData(seriesid,iscv);
    }

    public CompletableFuture<List<SpecPicColorStatisticsEntity>> get(int seriesid,boolean isCv) {
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid",seriesid);
        params.put("iscv",isCv);
        return getAsync(params);
    }

    Map<String,Object> makeParams(int seriesid,boolean isCv){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid",seriesid);
        params.put("iscv",isCv);
        return params;
    }

    List<SpecPicColorStatisticsEntity> getData(int seriesid,boolean isCv) {
        List<SpecPicColorStatisticsEntity> list = specPicClassStatisticsMapper.getSpecPicClassStatisticsBySeriesId(seriesid, isCv);
        return list;
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                Map<String, Object> params = new HashMap<>();
                params.put("seriesid", seriesid);
                params.put("iscv",false);
                List<SpecPicColorStatisticsEntity> list = specPicClassStatisticsMapper.getSpecPicClassStatisticsBySeriesId(seriesid, false);
                refresh(params, list);

                params.put("iscv",true);
                list = specPicClassStatisticsMapper.getSpecPicClassStatisticsBySeriesId(seriesid, true);
                refresh(params, list);

            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
