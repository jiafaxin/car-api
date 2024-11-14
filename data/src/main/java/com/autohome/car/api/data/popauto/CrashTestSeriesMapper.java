package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.CrashCnCapSeriesEntity;
import com.autohome.car.api.data.popauto.entities.CrashSeriesEntity;
import com.autohome.car.api.data.popauto.providers.CrashTestSeriesProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface CrashTestSeriesMapper {
    @SelectProvider(type = CrashTestSeriesProvider.class, method = "getDataSql")
    List<CrashSeriesEntity> getCrashTestData(int orderType, int standardId);


    @SelectProvider(type = CrashTestSeriesProvider.class, method = "getCrashCnCapTestData")
    List<CrashCnCapSeriesEntity> getCrashCnCapTestData();
}
