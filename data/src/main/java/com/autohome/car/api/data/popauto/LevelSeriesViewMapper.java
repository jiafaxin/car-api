package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.LevelSeriesViewProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface LevelSeriesViewMapper {

    @SelectProvider(value = LevelSeriesViewProvider.class, method = "getSeriesInfoByLevelId")
    @AutoCache(expireIn = 30)
    List<LevelSeriesEntity> getSeriesInfoByLevelId(int levelId);

}
