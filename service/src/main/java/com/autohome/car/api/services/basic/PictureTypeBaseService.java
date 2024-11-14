package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.PicClassMapper;
import com.autohome.car.api.data.popauto.entities.PicClassEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class PictureTypeBaseService extends BaseService<String> {

    @Autowired
    PicClassMapper picClassMapper;

    public CompletableFuture<String> get(int id) {
        Map<String,Object> params = new HashMap<>();
        params.put("id",id);
        return getAsync(params);
    }

    protected boolean getFromDB(){
        return true;
    }

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 60;
    }

    @Override
    protected String getData(Map<String, Object> params) {
        int specId = (int) params.get("id");
        return getData(specId);
    }

    String getData(int id) {
        List<KeyValueDto<Integer,String>> list = picClassMapper.getCar25PictureType();
        Optional<KeyValueDto<Integer,String>> info = list.stream().filter(s -> s.getKey() == id).findFirst();
        if(info.isPresent()){
            return info.get().getValue();
        }
        return "";
    }
}
