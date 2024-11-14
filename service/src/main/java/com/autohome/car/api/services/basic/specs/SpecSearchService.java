package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.data.popauto.SpecViewMapper;
import com.autohome.car.api.data.popauto.entities.SpecSearchEntity;
import com.autohome.car.api.services.basic.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
public class SpecSearchService extends BaseService<List<SpecSearchEntity>> {

    @Autowired
    SpecViewMapper specViewMapper;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.H_1;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    /**
     * eCache没有数据，查询sqlserver
     * @return
     */
    protected boolean getFromDB(){
        return true;
    }

    @Override
    protected List<SpecSearchEntity> getData(Map<String, Object> params) {
        List<SpecSearchEntity> allSpecBaseInfo = specViewMapper.getAllSpecBaseInfo();
        if(!CollectionUtils.isEmpty(allSpecBaseInfo)){
            allSpecBaseInfo.forEach(specSearchEntity -> {
                //皮卡特殊处理
                specSearchEntity.setLevelName(StringUtils.isNotBlank(specSearchEntity.getLevelName()) ?
                        (specSearchEntity.getLevelName().contains("皮卡") ? "皮卡" : specSearchEntity.getLevelName()) : "");
            });
        }
        return allSpecBaseInfo;
    }

    /**
     * 不查redis
     * @return
     */
    public List<SpecSearchEntity> get(){
        return getNew(null);
    }

}
