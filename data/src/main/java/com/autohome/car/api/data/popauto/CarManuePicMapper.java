package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.CarManueEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarManuePicMapper {
    @Select("select SeriesId from CarManuePic with(nolock)")
    List<Integer> all();

    @Select("SELECT DISTINCT BrandId,FirstLetter,BrandCount FROM CarManuePic WITH (NOLOCK) ORDER BY FirstLetter")
    List<CarManueEntity> getCarManuePicBrandAll();
}
