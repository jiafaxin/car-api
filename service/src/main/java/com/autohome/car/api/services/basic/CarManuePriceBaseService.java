package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.CarManuePriceMapper;
import com.autohome.car.api.data.popauto.entities.CarManuePriceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CarManuePriceBaseService extends BaseService<List<Integer>>{

    @Autowired
    private CarManuePriceMapper carManuePriceMapper;

    @Override
    public EhCacheName getCacheName() {
        return EhCacheName.M_30;
    }

    @Override
    public Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    public List<Integer> get() {
        return get(null);
    }

    @Override
    public List<Integer> getData(Map<String, Object> params) {
        List<CarManuePriceEntity> carManuePriceEntities = carManuePriceMapper.getAllSeriesManueInfo();
        return convert(carManuePriceEntities);
    }

    public int refreshAll(Consumer<String> log) {
        List<Integer> data = getData(null);
        try {
            refresh(null, data);
        }catch (Exception e){
            log.accept("error：" + " >> " + ExceptionUtil.getStackTrace(e));
        }
        return data.size();
    }

    List<Integer> convert(List<CarManuePriceEntity> carManuePriceEntities){
        if(CollectionUtils.isEmpty(carManuePriceEntities)){
            return new ArrayList<>();
        }
        return carManuePriceEntities.stream()
                .map(CarManuePriceEntity::getSeriesId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

}
