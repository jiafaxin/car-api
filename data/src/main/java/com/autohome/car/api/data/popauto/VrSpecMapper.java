package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.VrSpecEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VrSpecMapper {

    @Select("select distinct(Seriesid) from vr_spec")
    List<Integer> getVrAll();
    @Select("select distinct(Seriesid) from vr_spec where Seriesid = #{seriesId}")
    Integer getVrBySeriesId(int seriesId);

    @Select("select SeriesId,Specid,CoverUrl16_9,CoverUrl4_3,Panourl,coverUrl from [VR_Spec] with(nolock)")
    List<VrSpecEntity> getVrSpecAll();

}
