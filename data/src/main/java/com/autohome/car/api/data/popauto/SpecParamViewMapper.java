package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.SpecConfigChargeEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigEntity;
import com.autohome.car.api.data.popauto.providers.SpecParamViewProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpecParamViewMapper {

    @SelectProvider(value = SpecParamViewProvider.class,method = "getGearBox")
    KeyValueDto<Integer,String> getGearBox(int specId);

    @SelectProvider(value = SpecParamViewProvider.class,method = "getAllGearBox")
    List<KeyValueDto<Integer,String>> getAllGearBox();

    @SelectProvider(value = SpecParamViewProvider.class,method = "getDicEmissionStandards")
    KeyValueDto<Integer,String> getDicEmissionStandards(int specId);

    @SelectProvider(value = SpecParamViewProvider.class,method = "getAllDicEmissionStandards")
    List<KeyValueDto<Integer,String>> getAllDicEmissionStandards();

    @SelectProvider(value = SpecParamViewProvider.class,method = "getOilBoxVolume")
    KeyValueDto<Integer,String> getOilBoxVolume(int specId);

    @SelectProvider(value = SpecParamViewProvider.class,method = "getAllOilBoxVolume")
    List<KeyValueDto<Integer,String>> getAllOilBoxVolume();

    @SelectProvider(value = SpecParamViewProvider.class,method = "getEngineKW")
    KeyValueDto<Integer,String> getEngineKW(int specId);

    @SelectProvider(value = SpecParamViewProvider.class,method = "getAllEngineKW")
    List<KeyValueDto<Integer,String>> getAllEngineKW();

    @SelectProvider(value = SpecParamViewProvider.class, method = "getSpecConfig")
    List<Map<String, Object>> getSpecConfig(boolean isVc, boolean unCv, List<Integer> specList);

    @SelectProvider(value = SpecParamViewProvider.class, method = "getAllChargeTime")
    List<SpecConfigChargeEntity> getAllChargeTime(int seriesId);

}
