package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.ShowMapper;
import com.autohome.car.api.data.popauto.entities.PicClassEntity;
import com.autohome.car.api.services.basic.models.ColorBaseInfo;
import com.autohome.car.api.services.basic.models.ShowBaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ShowBaseService extends BaseService<List<ShowBaseInfo>> {

    @Autowired
    ShowMapper showMapper;

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

    public ShowBaseInfo get(int showId) {
        List<ShowBaseInfo> showBaseInfos = get(null);
        if (showBaseInfos == null)
            return null;
        return showBaseInfos.stream().filter(x -> x.getId() == showId).findFirst().orElse(null);
    }

    public Map<Integer, ShowBaseInfo> getList(List<Integer> ids){
        if(ids==null || ids.size() == 0)
            return new LinkedHashMap<>();
        List<ShowBaseInfo> datas = get(null);
        if(datas == null || datas.size() == 0)
            return new LinkedHashMap<>();
        List<ShowBaseInfo> list = datas.stream().filter(x->ids.contains(x.getId())).collect(Collectors.toList());
        Map<Integer,ShowBaseInfo> map = new LinkedHashMap<>();
        for (ShowBaseInfo item : list) {
            map.put(item.getId(),item);
        }
        return map;
    }

    public String getName(int showId){
        ShowBaseInfo showBaseInfo = get(showId);
        if(showBaseInfo==null)
            return "";
        return showBaseInfo.getName();
    }

    @Override
    protected List<ShowBaseInfo> getData(Map<String, Object> params) {
        return showMapper.getShowNames().stream().map(x -> {
            return new ShowBaseInfo() {{
                setId(x.getKey());
                setName(x.getValue());
            }};
        }).collect(Collectors.toList());
    }

    public int refreshAll(Consumer<String> log) {
        List<ShowBaseInfo> datas = getData(null);
        refresh(null, datas);
        log.accept("success：" + datas.size());
        return datas.size();
    }


}
