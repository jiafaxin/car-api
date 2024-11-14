package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.PicClassEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class PicClassBaseService extends BaseService<List<PicClassEntity>> {

    @Autowired
    PicClassMapper picClassMapper;

    public CompletableFuture<PicClassEntity> get(int classId) {
        return getAsync(null).thenApply(list -> {
            return list.stream().filter(x -> x.getId() == classId).findFirst().orElse(null);
        });
    }

    public Map<Integer,PicClassEntity> getList(List<Integer> ids){
        List<PicClassEntity> list = get(null).stream().filter(x->ids.contains(x.getId())).collect(Collectors.toList());
        Map<Integer,PicClassEntity> map = new LinkedHashMap<>();
        for (PicClassEntity picClassEntity : list) {
            map.put(picClassEntity.getId(),picClassEntity);
        }
        return map;
    }

    public CompletableFuture<List<PicClassEntity>> getAll(){
        return getAsync(null);
    }

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<PicClassEntity> getData(Map<String, Object> params) {
        return picClassMapper.getPicClassList();
    }

    public void refresh(Consumer<String> log){
        List<PicClassEntity> datas = getData(null);
        refresh(null,datas);
        log.accept("total:"+datas.size());
    }

}
