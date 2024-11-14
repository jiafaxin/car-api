package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.providers.ParamSubItemProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ParamSubItemMapper {

    @SelectProvider(value = ParamSubItemProvider.class,method = "getSpecOilLabe")
    KeyValueDto<Integer,String> getSpecOilLabe(int specId);

    @SelectProvider(value = ParamSubItemProvider.class,method = "getAllSpecOilLabe")
    List<KeyValueDto<Integer,String>> getAllSpecOilLabe();
}
