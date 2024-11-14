package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.providers.SpecPhotoProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SpecPhotoMapper {

    @SelectProvider(value = SpecPhotoProvider.class,method = "getPngPhoto")
    KeyValueDto<Integer,String> getPngPhoto(int specId);


    @SelectProvider(value = SpecPhotoProvider.class,method = "getAllPngLogo")
    List<KeyValueDto<Integer,String>> getAllPngLogo();

}
