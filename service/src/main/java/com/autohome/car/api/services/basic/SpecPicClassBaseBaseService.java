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

import javax.swing.text.StyledEditorKit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class SpecPicClassBaseBaseService extends BaseService<List<SpecPicColorStatisticsEntity>> {

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

    public Map<String,Object> makeParam(Pair<Integer, Boolean> param){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesid", param.getKey());
        params.put("iscv",param.getValue());
        return params;
    }

    public List<SpecPicColorStatisticsEntity> getList(List<Pair<Integer, Boolean>> seriesIds) {
        if(CollectionUtils.isEmpty(seriesIds)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> params = seriesIds.stream()
                .filter(Objects::nonNull)
                .map(this::makeParam)
                .collect(Collectors.toList());
        List<List<SpecPicColorStatisticsEntity>> data = mGet(params);
        return data == null ? new ArrayList<>() : data.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Map<Integer,List<SpecPicColorStatisticsEntity>> getMap(List<Pair<Integer, Boolean>> seriesIds) {
        if(CollectionUtils.isEmpty(seriesIds)) {
            return new LinkedHashMap<>();
        }
        List<SpecPicColorStatisticsEntity> list = getList(seriesIds);
        if(list.size() == 0)
            return new LinkedHashMap<>();

        return list.stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getSeriesId));
    }

    List<SpecPicColorStatisticsEntity> getData(int seriesid,boolean isCv) {
        return specPicClassStatisticsMapper.getSpecPicClassBaseBySeriesId(seriesid, isCv);
    }

    public int refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        seriesViews.forEach(item -> {
            try {
                int seriesid = item.getSeriesid();
                Map<String, Object> params = new HashMap<>();
                params.put("seriesid", seriesid);
                params.put("iscv",false);
                List<SpecPicColorStatisticsEntity> list = specPicClassStatisticsMapper.getSpecPicClassBaseBySeriesId(seriesid, false);
                refresh(params, list);

                params.put("iscv",true);
                list = specPicClassStatisticsMapper.getSpecPicClassBaseBySeriesId(seriesid, true);
                refresh(params, list);

            }catch (Exception e){
                log.accept("error >> " + item.getSeriesid() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return seriesViews.size();
    }


}
