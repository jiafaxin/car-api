package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SpecColorMapper;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.CarSpecColorEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class SpecColorService extends BaseService<List<CarSpecColorEntity>> {

    private final SpecColorMapper specColorMapper;

    private final SpecMapper specMapper;

    public SpecColorService(SpecColorMapper specColorMapper, SpecMapper specMapper) {
        this.specColorMapper = specColorMapper;
        this.specMapper = specMapper;
    }

    @Override
    protected String keyVersion() {
        return "v1";
    }

    private final static String specIdParamName = "specId";

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60 * 24;
    }

    @Override
    protected List<CarSpecColorEntity> getData(Map<String, Object> params) {
        return specColorMapper.getSpecColorBySpecId(getSpecIdFromParams(params));
    }

    public CompletableFuture<List<CarSpecColorEntity>> get(int specId) {
        return getAsync(makeParams(specId));
    }


    private int getSpecIdFromParams(Map<String, Object> params) {
        return (int) params.get(specIdParamName);
    }

    private Map<String, Object> makeParams(int specId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(specIdParamName, specId);
        return map;
    }

    public int refreshAll(Consumer<String> log) {
        List<Integer> allIds = specMapper.getAllIds();
        for (Integer specId : allIds) {
            try {
                Map<String, Object> param = makeParams(specId);
                refresh(param, getData(param));
            } catch (Exception e) {
                log.accept("error:" + specId + ":" + ExceptionUtil.getStackTrace(e));
            }
        }
        return allIds.size();
    }
}
