package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.SeriesBaseInfoEntity;
import com.autohome.car.api.services.models.SeriesNameInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class SeriesAllBaseInfoService extends BaseService<List<SeriesNameInfo>>  {

    @Autowired
    private SeriesMapper seriesMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_10;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected List<SeriesNameInfo> getData(Map<String, Object> params) {
        List<SeriesNameInfo> data = new ArrayList<>();
        List<SeriesBaseInfoEntity> rows = seriesMapper.getAllBase();
        if(CollectionUtils.isEmpty(rows)){
            return data;
        }
        for(SeriesBaseInfoEntity item : rows){
            SeriesNameInfo seriesNameInfo = new SeriesNameInfo();
            seriesNameInfo.setId(item.getId());
            seriesNameInfo.setName(item.getName());
            seriesNameInfo.setEName(item.getEName());
            data.add(seriesNameInfo);
        }
        return data;
    }

    public List<SeriesNameInfo> get() {
        return get(null);
    }

    public int refreshAll(Consumer<String> log) {
        try {
            refresh(null, getData(null));
            log.accept("success:");
        }catch (Exception e){
            log.accept("error >> " + ExceptionUtil.getStackTrace(e));
        }
        return 0;
    }

}
