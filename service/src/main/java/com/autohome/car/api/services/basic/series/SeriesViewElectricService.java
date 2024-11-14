package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.SeriesViewMapper;
import com.autohome.car.api.data.popauto.entities.SeriesViewElectricEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class SeriesViewElectricService extends BaseService<List<SeriesViewElectricEntity>> {

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
    protected List<SeriesViewElectricEntity> getData(Map<String, Object> params) {
        return seriesViewMapper.getBrandEVSeriesList((int) params.get("branchId"));
    }

    public List<SeriesViewElectricEntity> get(int branchId) {
        return get(makeParam(branchId));
    }

    Map<String, Object> makeParam(int branchId) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("branchId", branchId);
        return param;
    }

    public int refreshAll(Consumer<String> log) {
        List<Integer> brandIds = seriesViewMapper.getAllBrandIdsFromSeriesView();

        List<CompletableFuture<String>> tasks = new ArrayList<>(20);
        List<List<Integer>> lists = ToolUtils.splitList(brandIds, 20);
        for (List<Integer> list : lists) {
            for (Integer brandId : list) {
                tasks.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        Map<String, Object> param = makeParam(brandId);
                        List<SeriesViewElectricEntity> data = getData(param);
                        if (!CollectionUtils.isEmpty(data)) {
                            refresh(param, data);
                            return "brandId :" + brandId;
                        } else {
                            return "brandId: " + brandId + " 为空";
                        }
                    } catch (Exception e) {
                        return "error " + brandId + " >>>>" + ExceptionUtil.getStackTrace(e);
                    }
                }));
            }
            if (!CollectionUtils.isEmpty(tasks)) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                for (CompletableFuture<String> task : tasks) {
                    String info = task.join();
                    if(info.startsWith("error")) {
                        log.accept(task.join());
                    }
                }
                tasks.clear();
            }
        }
        return brandIds.size();
    }
}
