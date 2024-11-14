package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.data.popauto.providers.CarSpecInnerColorStatisticsProvider;
import com.autohome.car.api.data.popauto.providers.CarSpecPicColorStatisticsProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CarSpecInnerColorStatisticsMapper {

    @SelectProvider(value = CarSpecInnerColorStatisticsProvider.class,method = "getSpecInnerColorStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getSpecInnerColorStatisticsBySeriesId(int seriesId);

    @SelectProvider(value = CarSpecInnerColorStatisticsProvider.class,method = "getAllSpecInnerColorStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getAllSpecInnerColorStatisticsBySeriesId();
}
