package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.Car25PictureViewEntity;
import com.autohome.car.api.data.popauto.entities.CarSpecPictureStatisticsEntity;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.data.popauto.providers.SpecPicClassStatisticsProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SpecPictureStatisticsMapper {

    @Select("SELECT SpecId,SpecYear,CarState,PicNumber FROM  CarSpecPictureStatistics WITH(NOLOCK) WHERE PicNumber>=3 AND SeriesId=#{seriesId}")
    List<CarSpecPictureStatisticsEntity> getSpecPictureStatisticsBySeriesId(int seriesId);

    @Select("SELECT A.Id,A.SpecId,A.PicPath,A.PicId,A.Ordercls,A.TopId,Remark FROM  Car25PictureView  AS A WITH(NOLOCK)\n" +
            "                                        WHERE A.SpecId=#{specId} AND A.PicId>0 ORDER BY A.Ordercls")
    List<Car25PictureViewEntity> GetDicSpec25Pic(int specId);

}
