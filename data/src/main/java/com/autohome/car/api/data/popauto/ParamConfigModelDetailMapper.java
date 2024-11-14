package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.ParamConfigModelDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ParamConfigModelDetailMapper {

    @Select("select paramconfigid as pCId,paramconfigname as pcn from ParamConfigModelDetail with(nolock) where modelid = #{modelId}")
    List<ParamConfigModelDetailEntity> getListByModelId(int modelId);
}
