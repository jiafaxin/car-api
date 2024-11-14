package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.CarManueBaseEntity;
import com.autohome.car.api.data.popauto.entities.CarManueEntity;
import com.autohome.car.api.data.popauto.entities.CarManuePriceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarManuePriceMapper {

    @Select("select id, brandId, brandName, seriesId, seriesName from CarManuePrice with(nolock)")
    List<CarManuePriceEntity> getAllSeriesManueInfo();

    @Select("SELECT DISTINCT BrandId,FirstLetter,BrandCount FROM CarManuePrice WITH (NOLOCK) ORDER BY FirstLetter")
    List<CarManueEntity> getAllCarManuePrice();

    @Select("SELECT BrandId,FirstLetter,min(BrandCount) AS BrandCount FROM CarManuePrice WITH(NOLOCK)  WHERE FirstLetter = #{firstLetter} GROUP BY BrandId,FirstLetter  ORDER BY MAX(SeriesOrdercls)")
    List<CarManueBaseEntity> getCarManuePriceByFirstLetter(String firstLetter);

}
