package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.ColorInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarFctColorMapper {

    @Select("select id, ColorName as [name],ColorValue as [value] from Car_Fct_Color with(nolock)")
    List<ColorInfoEntity> getAllColorInfo();

}
