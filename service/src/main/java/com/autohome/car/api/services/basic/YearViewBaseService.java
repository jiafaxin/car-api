package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.SYearViewMapper;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.SYearViewEntity;
import com.autohome.car.api.services.basic.models.YearViewBaseInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class YearViewBaseService extends BaseService<List<YearViewBaseInfo>> {

    @Resource
    private SYearViewMapper sYearViewMapper;

    @Resource
    SeriesMapper seriesMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60*24;
    }

    @Override
    protected List<YearViewBaseInfo> getData(Map<String, Object> params) {
        int seriesId = (int)params.get("seriesId");
        List<SYearViewEntity> yearViewEntities = sYearViewMapper.getSYearViewBySeriesId(seriesId);
        return convert(yearViewEntities);
    }

    /**
     * 外部使用
     * @param seriesId
     * @return
     */
    public CompletableFuture<List<YearViewBaseInfo>> get(int seriesId){
        CompletableFuture<List<YearViewBaseInfo>> completableFuture = getAsync(makeParams(seriesId));
        return completableFuture;
    }

    /**
     * 转换
     * @param yearViewEntities
     * @return
     */
    private List<YearViewBaseInfo> convert(List<SYearViewEntity> yearViewEntities){
        if(CollectionUtils.isEmpty(yearViewEntities)){
            return null;
        }
        List<YearViewBaseInfo> yearViewBaseInfos = new ArrayList<>();
        yearViewEntities.forEach(yearViewEntity->{
            YearViewBaseInfo yearViewBaseInfo = new YearViewBaseInfo();
            yearViewBaseInfo.setSeriesId(yearViewEntity.getSeriesId());
            yearViewBaseInfo.setSYearId(yearViewEntity.getSYearId());
            yearViewBaseInfo.setSYear(yearViewEntity.getSYear());
            yearViewBaseInfo.setSYearIsPublic(yearViewEntity.getSYearIsPublic());
            yearViewBaseInfo.setSYearState(yearViewEntity.getSYearState());
            yearViewBaseInfo.setSYearSpecNum(yearViewEntity.getSYearSpecNum());
            yearViewBaseInfo.setSYearSpecNumUnsold(yearViewEntity.getSYearSpecNumUnsold());
            yearViewBaseInfo.setSYearSpecNumSale(yearViewEntity.getSYearSpecNumSale());
            yearViewBaseInfo.setSYearSpecNumStop(yearViewEntity.getSYearSpecNumStop());
            yearViewBaseInfos.add(yearViewBaseInfo);
        });
        return yearViewBaseInfos;
    }
    Map<String,Object> makeParams(int seriesId){
        Map<String,Object> params = new HashMap<>();
        params.put("seriesId",seriesId);
        return params;
    }

    /**
     * 定时任务使用
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds().stream().sorted().collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(seriesIds)){
            List<List<Integer>> splitList = ToolUtils.splitList(seriesIds, 100);
            for(List<Integer> seriesIdList:splitList){
                List<SYearViewEntity> yearViewEntities = sYearViewMapper.getSYearViewBySeriesIds(seriesIdList);
                Map<Integer, List<SYearViewEntity>> csMap = yearViewEntities.stream().collect(Collectors.groupingBy(SYearViewEntity::getSeriesId));
                for(Map.Entry<Integer, List<SYearViewEntity>> configMap:csMap.entrySet()){
                    refresh(makeParams(configMap.getKey()),convert(configMap.getValue()));
                }
            }
            return seriesIds.size();
        }
        log.accept("yearViewBaseService yearViewBaseInfos is null!" );
        return 0;
    }
}
