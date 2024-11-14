package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.ElectricSpecViewMapper;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.EleSpecViewBaseEntity;
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
public class ElectricSpecViewBaseService extends BaseService<List<EleSpecViewBaseEntity>> {

    @Resource
    private ElectricSpecViewMapper electricSpecViewMapper;

    @Resource
    private SeriesMapper seriesMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }

    @Override
    protected List<EleSpecViewBaseEntity> getData(Map<String, Object> params) {
        List<EleSpecViewBaseEntity> eleSpecViewSimp = electricSpecViewMapper.getEleSpecViewSimp(getSeriesId(params));
        return CollectionUtils.isEmpty(eleSpecViewSimp) ? null : eleSpecViewSimp;
    }

    public CompletableFuture<List<EleSpecViewBaseEntity>> get(int seriesId) {
        return getAsync(makeParam(seriesId));
    }

    int getSeriesId(Map<String, Object> params) {
        return (int) params.get("seriesId");
    }

    private Map<String, Object> makeParam(int seriesId) {
        Map<String, Object> param = new HashMap<>();
        param.put("seriesId", seriesId);
        return param;
    }

    public int refreshAll(Consumer<String> log) {
        List<CompletableFuture<String>> tasks = new ArrayList<>();

        List<Integer> ids = seriesMapper.getAllSeriesIds().stream().sorted().collect(Collectors.toList());
        List<List<Integer>> lists = ToolUtils.splitList(ids, 20);

        for (List<Integer> list : lists) {
            for (Integer seriesId : list) {
                tasks.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        Map<String, Object> param = makeParam(seriesId);
                        refresh(param, getData(param));
                        return "now :" + seriesId;
                    } catch (Exception e) {
                        return "error " + seriesId + " >>>>" + ExceptionUtil.getStackTrace(e);
                    }
                }));
            }
            if (!CollectionUtils.isEmpty(tasks)) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                for (CompletableFuture<String> task : tasks) {
                    log.accept(task.join());
                }
                tasks = new ArrayList<>();
            }
        }

        return ids.size();
    }
}
