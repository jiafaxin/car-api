package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.VisualParamConfigViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VisualParamConfigViewMapper {

    @Select("select SeriesId,specid,DataType,itemid,ItemName,valu as value,SubItemId as subId,SubItemName as subName,picid,picurl,specState from \n" +
            "Visual_ParamconfigView with(nolock) where specid = #{specId}")
    List<VisualParamConfigViewEntity> getVisualParamConfigBySpecId(int specId);

    @Select("select DISTINCT specid from Visual_ParamconfigView  with(nolock)")
    List<Integer> getSpecIdAll();



}
