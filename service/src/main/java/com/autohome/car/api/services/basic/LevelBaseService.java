package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.CarSpecLevelMapper;
import com.autohome.car.api.data.popauto.entities.LevelEntity;
import com.autohome.car.api.services.basic.models.LevelBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class LevelBaseService extends BaseService<List<LevelBaseInfo>>  {

    @Autowired
    CarSpecLevelMapper carSpecLevelMapper;

    public String getName(int levelId) {
        if(levelId == 14 || levelId == 15){
            return "皮卡";
        }
        LevelBaseInfo info = getLevel(levelId);
        if (info == null)
            return "";
        return info.getName();
    }

    public List<LevelBaseInfo> getLevelAll(){
        List<LevelBaseInfo> levelBaseInfos = get(null);
        return levelBaseInfos;
    }

    public LevelBaseInfo getLevel(int levelId){
        List<LevelBaseInfo> infos = get(null);
        return infos.stream().filter(x->x.getId() == levelId).findFirst().orElse(null);
    }

    public List<LevelBaseInfo> getLevelList(List<Integer> levelIds){
        List<LevelBaseInfo> infos = get(null);
        return infos.stream().filter(x->levelIds.contains(x.getId())).collect(Collectors.toList());
    }

    public CompletableFuture<LevelBaseInfo> getLevelAsync(int levelId){
        return getAsync(null).thenApply(infos->{
            return infos.stream().filter(x->x.getId() == levelId).findFirst().orElse(null);
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
    protected List<LevelBaseInfo> getData(Map<String, Object> params) {
        List<LevelEntity> levelEntities = carSpecLevelMapper.getAllLevel();
        List<LevelBaseInfo> infos = new ArrayList<>();
        levelEntities.forEach(item->{
            infos.add(new LevelBaseInfo(){{
                setId(item.getId());
                setName(item.getName());
                setDir(item.getDir());
                setDescription(item.getDescription());
            }});
        });
        return infos;
    }

    public int refreshAll(Consumer<String> log) {
        List<LevelBaseInfo> datas = getData(null);
        refresh(null, datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
