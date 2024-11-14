package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.ConfigMapper;
import com.autohome.car.api.data.popauto.entities.ConfigItemEntity;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigItemBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class ConfigListService extends BaseService<List<ConfigTypeBaseInfo>> {

    @Autowired
    ConfigMapper configMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected boolean getFromDB(){
        return true;
    }

    public List<ConfigTypeBaseInfo> get(){
        return get(null);
    }

    public CompletableFuture<List<ConfigTypeBaseInfo>> getAsync() {
        return getAsync(null);
    }

    @Override
    protected List<ConfigTypeBaseInfo> getData(Map<String, Object> params) {
        List<ConfigItemEntity> list = configMapper.getAllConfig();
        return convert(list);
    }

    List<ConfigTypeBaseInfo> convert(List<ConfigItemEntity> list) {
        List<ConfigTypeBaseInfo> infos = new ArrayList<>();
        for (ConfigItemEntity item : list) {
            ConfigTypeBaseInfo baseInfo = infos.stream().filter(x -> x.getTypeId() == item.getTypeId()).findFirst().orElse(null);
            if (baseInfo == null) {
                baseInfo = new ConfigTypeBaseInfo();
                baseInfo.setItems(new ArrayList<>());
                baseInfo.setTypeId(item.getTypeId());
                baseInfo.setTypeName(item.getTypeName());
                infos.add(baseInfo);
            }
            baseInfo.getItems().add(new ConfigItemBaseInfo() {
                {
                    setItemId(item.getItemId());
                    setItemName(item.getItemName());
                    setDynamicShow(item.getDynamicShow());
                    setCVIsShow(item.getCVIsShow());
                    setIsShow(item.getIsShow());
                    setDisplayType(item.getDisplayType());
                }
            });
        }
        return infos;
    }


    public void refreshAll(Consumer<String> log){
        List<ConfigTypeBaseInfo> datas = getData(null);
        refresh(null,getData(null));
        log.accept("ConfigListService count:"+datas.size());
    }


}
