package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.CarSpecInnerColorStatisticsMapper;
import com.autohome.car.api.data.popauto.CarSpecPicColorStatisticsMapper;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SeriesSpecPicInnerColorStatistics extends BaseService<List<SpecPicColorStatisticsEntity>> {

    @Autowired
    CarSpecInnerColorStatisticsMapper carSpecInnerColorStatisticsMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<SpecPicColorStatisticsEntity> getData(Map<String, Object> params) {
        return carSpecInnerColorStatisticsMapper.getSpecInnerColorStatisticsBySeriesId((int) params.get("seriesId"));
    }

    public List<SpecPicColorStatisticsEntity> get(int seriesId){
        return get(makeParam(seriesId));
    }

    Map<String, Object> makeParam(int seriesId) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("seriesId", seriesId);
        return param;
    }


    public void refreshAll(Consumer<String> log) {
        Map<Integer, List<SpecPicColorStatisticsEntity>> gls = carSpecInnerColorStatisticsMapper.getAllSpecInnerColorStatisticsBySeriesId().stream().collect(Collectors.groupingBy(SpecPicColorStatisticsEntity::getSeriesId));
        gls.forEach((seriesId, list) -> {
            try {
                refresh(makeParam(seriesId), list);
            } catch (Exception e) {
                log.accept("error:" + seriesId + ">>>" + ExceptionUtil.getStackTrace(e));
            }
        });
    }
}
