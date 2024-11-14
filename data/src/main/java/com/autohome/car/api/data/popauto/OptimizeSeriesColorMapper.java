package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.OptimizeSeriesColorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OptimizeSeriesColorMapper {

    @Select({
            "<script>",
            "SELECT seriesid, colorid, piccount, picfilepath",
            "FROM Optimize_Stats_SeriesSellExteriorColor WITH(NOLOCK)",
            "WHERE seriesid IN",
            "<foreach item='id' index='index' collection='seriesIds' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    @AutoCache(expireIn = 60,removeIn = 120)
    List<OptimizeSeriesColorEntity> getOptimizeSeriesColorEntity(List<Integer> seriesIds);
}
