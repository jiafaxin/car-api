package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.*;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.SeriesOnlyElectricEntity;
import com.autohome.car.api.data.popauto.entities.SeriesViewRankEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class SeriesElectricService extends BaseService<byte[]> {

    private final static String SERIES_ELECTRIC = "series_electric";

    private final static int SLICE_NUM = 300;

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
    protected byte[] getData(Map<String, Object> params) {
        List<SeriesOnlyElectricEntity> electricSeriesList = seriesMapper.getAllElectricSeriesList();
        String s = JsonUtils.toString(electricSeriesList);
        return GZIPUtils.compress(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected boolean canEhCache() {
        return true;
    }

    public List<SeriesOnlyElectricEntity> get() {
        byte[] uncompress = GZIPUtils.uncompress(get(makeParam()));
        String json = new String(uncompress);
        return JsonUtils.toObjectList(json, SeriesOnlyElectricEntity.class);
    }

    @Override
    protected void setToRedis(String key, byte[] result) {
        super.setToRedis(key, result);
    }

    private Map<String, Object> makeParam() {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put(SERIES_ELECTRIC, SERIES_ELECTRIC);
        return param;
    }

    public void refreshAll(Consumer<String> log) {
        refresh(makeParam(), getData(makeParam()));
    }

}
