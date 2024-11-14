package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.OptimizeParamItemInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OptimizeParamItemInfoMapper {

    @Select("select  item, name,pordercls,ordercls from Optimize_ParamItem_Info with(nolock) order by pordercls,ordercls ;")
    @AutoCache(expireIn = 60,removeIn = 120)
    List<OptimizeParamItemInfoEntity> getOptimizeParamItemInfoEntity();
}
