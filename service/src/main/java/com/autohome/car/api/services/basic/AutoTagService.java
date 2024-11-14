package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.AutoTagMapper;
import com.autohome.car.api.data.popauto.entities.AutoTagEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class AutoTagService extends BaseService<List<AutoTagEntity>> {
    @Resource
    private AutoTagMapper autoTagMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    @Override
    protected List<AutoTagEntity> getData(Map<String, Object> params) {
        return autoTagMapper.getAutoTagName();
    }

    @Override
    protected boolean canEhCache() {
        return false;
    }

    public List<AutoTagEntity> get() {
        return get(makeParam());
    }

    private Map<String, Object> makeParam() {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("tag", "tag");
        return param;
    }

    public void refreshAll(Consumer<String> log) {
        refresh(makeParam(), getData(makeParam()));
        log.accept("success");
    }

}
