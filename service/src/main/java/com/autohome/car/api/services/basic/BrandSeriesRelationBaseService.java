package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.SeriesViewMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class BrandSeriesRelationBaseService extends BaseService<Map<Integer, List<Integer>>>{


    @Resource
    private SeriesViewMapper seriesViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected Map<Integer, List<Integer>> getData(Map<String, Object> params) {
        List<KeyValueDto<Integer, Integer>> keyValueDtos = seriesViewMapper.getSeriesIdsByBrandId();
        Map<Integer, List<Integer>> brandSeriesMap = keyValueDtos.stream()
                .collect(Collectors.groupingBy(keyValueDto -> keyValueDto.getKey(),
                        Collectors.mapping(keyValueDto -> keyValueDto.getValue(), Collectors.toList())));
        return brandSeriesMap;
    }

    public List<Integer> getSeriesIds(int brandId){
        Map<Integer, List<Integer>> brandSeriesMap = get(null);
        if(!CollectionUtils.isEmpty(brandSeriesMap)){
            List<Integer> seriesIds = brandSeriesMap.get(brandId);
            return seriesIds;
        }
        return Collections.emptyList();
    }


    /**
     * 全量刷数据到redis
     * @param log
     * @return
     */
    public int refreshAll(Consumer<String> log) {
        List<KeyValueDto<Integer, Integer>> keyValueDtos = seriesViewMapper.getSeriesIdsByBrandId();
        Map<Integer, List<Integer>> brandSeriesMap = keyValueDtos.stream()
                .collect(Collectors.groupingBy(keyValueDto -> keyValueDto.getKey(),
                        Collectors.mapping(keyValueDto -> keyValueDto.getValue(), Collectors.toList())));
        refresh(null,brandSeriesMap);
        return brandSeriesMap.size();
    }
}
