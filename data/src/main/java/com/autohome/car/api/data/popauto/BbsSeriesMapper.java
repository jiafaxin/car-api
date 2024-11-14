package com.autohome.car.api.data.popauto;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BbsSeriesMapper {
    @Select("select seriesId from BbsSeries with(nolock) where IsShow=1")
    List<Integer> getAllBbsShowSeriesIds();

    @Select("select seriesId from BbsSeries with(nolock) where IsShow=1 and seriesId = #{seriesId}")
    Integer getBbsShowSeriesId(int seriesId);
}
