package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.data.popauto.providers.CarSpecPicColorStatisticsProvider;
import com.autohome.car.api.data.popauto.providers.SpecPicClassStatisticsProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SpecPicClassStatisticsMapper {

    @SelectProvider(value = SpecPicClassStatisticsProvider.class,method = "GetSpecPicClassStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getSpecPicClassStatisticsBySeriesId(int seriesId, boolean isCv);

    @SelectProvider(value = SpecPicClassStatisticsProvider.class,method = "GetSpecPicClassBaseBySeriesId")
    List<SpecPicColorStatisticsEntity> getSpecPicClassBaseBySeriesId(int seriesId, boolean isCv);
}
