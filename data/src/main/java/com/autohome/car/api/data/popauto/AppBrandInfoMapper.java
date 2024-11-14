package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.AppBrandInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppBrandInfoMapper {

    @Select("select title,brandDescription as description from AppBrandInfo where brandId = #{brandId}")
    List<AppBrandInfoEntity> getAppBrandInfoByBrandId(int brandId);


}
