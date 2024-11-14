package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.InnerFctColorMapper;
import com.autohome.car.api.data.popauto.entities.ColorInfoEntity;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class InnerColorBaseService extends BaseService<List<ColorBaseInfo>>  {

    @Autowired
    InnerFctColorMapper innerFctColorMapper;

    public ColorBaseInfo getColor(int colorId){
        return get(null).stream().filter(x->x.getId() == colorId).findFirst().orElse(null);
    }

    public List<ColorBaseInfo> getColorList(List<Integer> colorIds){
        return get(null).stream().filter(x->colorIds.contains(x.getId())).collect(Collectors.toList());
    }

    public Map<Integer,ColorBaseInfo> getColorMap(List<Integer> colorIds){
        Map<Integer,ColorBaseInfo> map = new LinkedHashMap<>();
        if(CollectionUtils.isEmpty(colorIds)){
            return map;
        }
        List<ColorBaseInfo> colorList = getColorList(colorIds);
        if(CollectionUtils.isEmpty(colorList)){
            return map;
        }
        for (ColorBaseInfo colorBaseInfo : colorList) {
            map.put(colorBaseInfo.getId(),colorBaseInfo);
        }
        return map;
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
    protected List<ColorBaseInfo> getData(Map<String, Object> params) {
        List<ColorInfoEntity> infos = innerFctColorMapper.getAllColorInfo();
        return infos.stream().map(x->{
            return new ColorBaseInfo(){{
                setId(x.getId());
                setName(x.getName());
                setValue(x.getValue());
            }};
        }).collect(Collectors.toList());
    }

    public int refreshAll(Consumer<String> log){
        List<ColorBaseInfo> datas = getData(null);
        refresh(null,datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }
}
