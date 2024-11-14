package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.CarManuePicMapper;
import com.autohome.car.api.services.basic.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CarManuePicService extends BaseService<List<Integer>> {

    @Autowired
    CarManuePicMapper carManuePicMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 30;
    }

    @Override
    protected boolean getFromDB() {
        return true;
    }

    @Override
    protected List<Integer> getData(Map<String, Object> params) {
        return carManuePicMapper.all();
    }
}
