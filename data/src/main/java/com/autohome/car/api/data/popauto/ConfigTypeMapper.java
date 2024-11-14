package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConfigTypeMapper {

    @Select("SELECT Id as [key],Name as [value]  FROM ConfigType WITH(NOLOCK)")
    List<KeyValueDto<Integer,String>> getAllConfigType();
}
