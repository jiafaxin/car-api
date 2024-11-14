package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.providers.CarSpecPhotoProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CarSpecPhotoMapper {

    /**
     * 获取车系对应的最新一张白底车图
     */
    @SelectProvider(value = CarSpecPhotoProvider.class, method = "getAllSeriesTypePhoto")
    List<KeyValueDto<Integer, String>> getAllSeriesTypePhoto();

    /**
     * 根据系列id获取车系对应的最新一张白底车图
     */
    @SelectProvider(value = CarSpecPhotoProvider.class, method = "getSeriesTypePhotoBySeriesId")
    KeyValueDto<Integer, String> getSeriesTypePhotoBySeriesId(Integer seriesId);

}