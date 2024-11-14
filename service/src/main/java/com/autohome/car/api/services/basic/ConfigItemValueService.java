package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.ConfigMapper;
import com.autohome.car.api.data.popauto.entities.ConfigItemEntity;
import com.autohome.car.api.services.basic.models.ConfigItemBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class ConfigItemValueService extends BaseService<Map<Integer,String>> {

    @Autowired
    ConfigMapper configMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24 * 60;
    }

    public Map<Integer, String> get() {
        return get(null);
    }

    public CompletableFuture<Map<Integer, String>> getAsync() {
        return getAsync(null);
    }

    @Override
    protected Map<Integer, String> getData(Map<String, Object> params) {
        List<KeyValueDto<Integer, String>> list = configMapper.getAllConfigItemValues();
        return convert(list);
    }

    Map<Integer, String> convert(List<KeyValueDto<Integer, String>> list) {
        Map<Integer, String> result = new LinkedHashMap<>();
        for (KeyValueDto<Integer, String> item : list) {
            String value = item.getValue().contains("/") && ( item.getValue().contains("-") || item.getValue().contains("●") || item.getValue().contains("○") )
                    ? item.getValue().replace("/","&nbsp;/&nbsp;") : item.getValue();
            result.put(item.getKey(), value);
        }
        return result;
    }

    public void refreshAll(Consumer<String> log){
        Map<Integer, String> datas = getData(null);
        refresh(null,datas);
        log.accept("ConfigItemValueService count:"+datas.size());
    }
}
