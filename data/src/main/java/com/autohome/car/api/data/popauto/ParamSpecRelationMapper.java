package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.providers.ParamSpecRelationProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ParamSpecRelationMapper {

    @SelectProvider(value = ParamSpecRelationProvider.class, method = "getSpecWheelBase")
    KeyValueDto<Integer, String> getSpecWheelBase(int specId);

    @SelectProvider(value = ParamSpecRelationProvider.class, method = "getAllSpecWheelBase")
    List<KeyValueDto<Integer, String>> getAllSpecWheelBase();
}
