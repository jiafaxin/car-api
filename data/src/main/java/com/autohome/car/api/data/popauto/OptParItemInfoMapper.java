package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.OptParItemInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OptParItemInfoMapper {

    @Select("Select itemid AS configId, item, name, pordercls, ordercls from Optimize_ParamItem_Info")
    List<OptParItemInfoEntity> getAll();
}
