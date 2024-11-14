package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.NewEnergyBrandMapper;
import com.autohome.car.api.data.popauto.entities.NewEnergyBrandEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class ElectricBrandService extends BaseService<List<NewEnergyBrandEntity>> {

    @Resource
    private NewEnergyBrandMapper newEnergyBrandMapper;

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

    @Override
    protected List<NewEnergyBrandEntity> getData(Map<String, Object> params) {
        return newEnergyBrandMapper.getAllList();
    }

    public List<NewEnergyBrandEntity> getAll() {
        return get(makeParams());
    }

    Map<String, Object> makeParams() {
        return new HashMap<>();
    }

    public void refreshAll(Consumer<String> log) {
        try {
            List<NewEnergyBrandEntity> data = getData(makeParams());
            refresh(makeParams(), data);
        } catch (Exception e) {
            log.accept("error:" + e);
        }
    }
}
