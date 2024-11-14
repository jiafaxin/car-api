package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.SpecColorMapper;
import com.autohome.car.api.data.popauto.entities.SpecColorListEntity;
import com.autohome.car.api.services.basic.models.SpecColorListBaseInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecSpecColorBaseService extends BaseService<List<SpecColorListBaseInfo>> {

    @Resource
    SpecColorMapper specColorMapper;

    @Resource
    SeriesMapper seriesMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }

    @Override
    protected List<SpecColorListBaseInfo> getData(Map<String, Object> params) {
        int seriesId = (int) params.get("seriesId");
        List<SpecColorListEntity> infos = specColorMapper.getOnSoldSpecSpecColorList(seriesId);
        return infos.stream().map(x -> new SpecColorListBaseInfo() {{
            setSeriesId(x.getSeriesId());
            setSpecId(x.getSpecId());
            setColorId(x.getColorId());
            setPicNumber(x.getPicNumber());
            setClubPicNumber(x.getClubPicNumber());
            setPrice(x.getPrice());
            setRemarks(x.getRemarks());
        }}).collect(Collectors.toList());
    }

    /**
     * 外部使用
     *
     * @param seriesId
     * @return
     */
    public CompletableFuture<List<SpecColorListBaseInfo>> get(int seriesId) {
        Map<String, Object> params = new HashMap<>();
        params.put("seriesId", seriesId);
        return getAsync(params);
    }

    public int refreshAll(Consumer<String> log) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds().stream().sorted().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(seriesIds)) {
            seriesIds.forEach(seriesId -> {
                try {
                    Map<String, Object> params = new HashMap<>();
                    params.put("seriesId", seriesId);
                    refresh(params, getData(params));
                } catch (Exception e) {
                    log.accept("error：" + seriesId + " >> " + ExceptionUtil.getStackTrace(e));
                }
            });
            return seriesIds.size();
        }
        log.accept("SpecSpecColorBaseService SpecColor is null!");
        return 0;
    }
}
