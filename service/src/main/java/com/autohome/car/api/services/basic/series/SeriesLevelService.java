package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesViewMapper;
import com.autohome.car.api.data.popauto.entities.SeriesViewRankEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SeriesLevelService extends BaseService<List<SeriesViewRankEntity>> {

    @Resource
    private SeriesViewMapper seriesViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }

    @Override
    protected List<SeriesViewRankEntity> getData(Map<String, Object> params) {
        return seriesViewMapper.getSeriesInfoByLevelId((int) params.get("levelId"));
    }

    public List<SeriesViewRankEntity> get(int levelId) {
        return get(makeParam(levelId));
    }

    private Map<String, Object> makeParam(int levelId) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("levelId", levelId);
        return param;
    }

    public void refreshAll(Consumer<String> log) {
        List<Integer> levelIds = seriesViewMapper.getAllLevelIdFromSeriesView();
        levelIds.add(8);
        levelIds.add(9);
        for (Integer levelId : levelIds) {
            try {
                refresh(makeParam(levelId), seriesViewMapper.getSeriesInfoByLevelId(levelId));
            } catch (Exception e) {
                log.accept("error:" + levelId + ">>>" + ExceptionUtil.getStackTrace(e));
            }
        }
    }
}
