package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.data.popauto.FactoryMapper;
import com.autohome.car.api.data.popauto.entities.FactoryBaseEntity;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class FactoryBaseService extends BaseService<List<FactoryBaseInfo>> {


    @Autowired
    FactoryMapper factoryMapper;


    public String getName(int factoryId) {
        FactoryBaseInfo info = getFactory(factoryId);
        if (info == null)
            return "";
        return info.getName();
    }

    public FactoryBaseInfo getFactory(int factoryId){
        List<FactoryBaseInfo> infos = get(null);
        if(CollectionUtils.isEmpty(infos)){
            return null;
        }
        return infos.stream().filter(x->x.getId() == factoryId).findFirst().orElse(null);
    }

    public List<FactoryBaseInfo> getAllFactory(){
        return get(null);
    }

    public List<FactoryBaseInfo> getFactoryByIds(List<Integer> fctIds){
        if(CollectionUtils.isEmpty(fctIds)){
            return null;
        }
        List<FactoryBaseInfo> infos = get(null);
        if(CollectionUtils.isEmpty(infos)){
            return infos;
        }
        return infos.stream().filter(x->fctIds.contains(x.getId())).collect(Collectors.toList());
    }

    public List<FactoryBaseInfo> getFactoryAll(){
        List<FactoryBaseInfo> infos = get(null);
        return infos;
    }

    public CompletableFuture<FactoryBaseInfo> getFactoryAsync(int factoryId){
        return getAsync(null).thenApply(infos->{
            return infos.stream().filter(x->x.getId() == factoryId).findFirst().orElse(null);
        });
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
    protected List<FactoryBaseInfo> getData(Map<String, Object> params) {
        List<FactoryBaseEntity> factoryBaseEntities = factoryMapper.getAllFactoryNames();
        List<FactoryBaseInfo> infos = new ArrayList<>();
        for (FactoryBaseEntity factoryBaseEntity :factoryBaseEntities){
            FactoryBaseInfo factoryBaseInfo = new FactoryBaseInfo();
            factoryBaseInfo.setId(factoryBaseEntity.getId());
            factoryBaseInfo.setName(HtmlUtils.decode(factoryBaseEntity.getName()));
            factoryBaseInfo.setUrl(factoryBaseEntity.getUrl());
            factoryBaseInfo.setLogo(factoryBaseEntity.getLogo());
            factoryBaseInfo.setFirstletter(factoryBaseEntity.getFirstletter());
            factoryBaseInfo.setIsimport(factoryBaseEntity.getIsimport());
            factoryBaseInfo.setCreateTime(factoryBaseEntity.getCreateTime());
            factoryBaseInfo.setEditTime(factoryBaseEntity.getEditTime());
            infos.add(factoryBaseInfo);
        }
        return infos;
    }

    public int refreshAll(Consumer<String> log){
        List<FactoryBaseInfo> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
