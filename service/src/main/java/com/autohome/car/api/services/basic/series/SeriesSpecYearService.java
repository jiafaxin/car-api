package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecYearEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class SeriesSpecYearService extends BaseService<List<SpecYearEntity>> {

    @Resource
    private SpecViewMapper specViewMapper;

    @Resource
    private SeriesMapper seriesMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    @Override
    protected boolean canEhCache() {
        return false;
    }

    public List<SpecYearEntity> get(int seriesId) {
        List<SpecYearEntity> yearEntityList = get(makeParams(seriesId));
        if (CollectionUtils.isEmpty(yearEntityList)) {
            return Collections.emptyList();
        }
        return yearEntityList;
    }

    @Override
    protected List<SpecYearEntity> getData(Map<String, Object> params) {
        return specViewMapper.getSYearBySeriesId((int) params.get("seriesId"));
    }

    Map<String, Object> makeParams(int seriesId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("seriesId", seriesId);
        return params;
    }

    public void refreshAll(Consumer<String> log) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        List<CompletableFuture<String>> tasks = new ArrayList<>(20);
        List<List<Integer>> lists = ToolUtils.splitList(seriesIds, 20);
        for (List<Integer> list : lists) {
            for (Integer seriesId : list) {
                tasks.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        refresh(makeParams(seriesId), getData(makeParams(seriesId)));
                        return "seriesId :" + seriesId;
                    } catch (Exception e) {
                        return "error " + seriesId + " >>>>" + ExceptionUtil.getStackTrace(e);
                    }
                }));
            }
            if (!CollectionUtils.isEmpty(tasks)) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                for (CompletableFuture<String> task : tasks) {
                    String info = task.join();
                    if(info.startsWith("error")) {
                        log.accept(info);
                    }
                }
                tasks.clear();
            }
        }
    }

}
