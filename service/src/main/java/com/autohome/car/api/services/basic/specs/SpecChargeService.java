package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecParamViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecConfigChargeEntity;
import com.autohome.car.api.services.basic.BaseService;
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
public class SpecChargeService extends BaseService<List<SpecConfigChargeEntity>> {

    @Resource
    private SpecParamViewMapper specParamViewMapper;

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
    protected List<SpecConfigChargeEntity> getData(Map<String, Object> params) {
        return specParamViewMapper.getAllChargeTime(getSeriesId(params));
    }

    public CompletableFuture<List<SpecConfigChargeEntity>> get(int seriesId) {
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
                    String info = task.join();
                    if(info.startsWith("error")) {
                        log.accept(task.join());
                    }
                }
                tasks = new ArrayList<>();
            }
        }

        return ids.size();
    }


}
