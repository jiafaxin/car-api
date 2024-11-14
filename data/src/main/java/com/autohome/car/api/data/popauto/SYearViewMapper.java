package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.SYearViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SYearViewMapper {

    @Select("SELECT seriesId, SyearId as sYearId,Syear as sYear,SyearIspublic as sYearIsPublic,SyearState as sYearState,syearSpecNum as sYearSpecNum," +
            "syearSpecNumUnsold as sYearSpecNumUnsold, syearSpecNumSale as sYearSpecNumSale, syearSpecNumStop as sYearSpecNumStop " +
            "FROM SyearView WITH(NOLOCK) WHERE seriesId= #{seriesId} ORDER BY Syear DESC")
    List<SYearViewEntity> getSYearViewBySeriesId(int seriesId);

    @Select("SELECT seriesId, SyearId as sYearId,Syear as sYear,SyearIspublic as sYearIsPublic,SyearState as sYearState,syearSpecNum as sYearSpecNum," +
            "syearSpecNumUnsold as sYearSpecNumUnsold, syearSpecNumSale as sYearSpecNumSale, syearSpecNumStop as sYearSpecNumStop " +
            "FROM SyearView WITH(NOLOCK) WHERE SyearId = #{SyearId} ORDER BY Syear DESC")
    List<SYearViewEntity> getSYearViewByYearId(int yearId);

    @Select("<script>\n" +
            "SELECT seriesId, SyearId as sYearId,Syear as sYear,SyearIspublic as sYearIsPublic,SyearState as sYearState,syearSpecNum as sYearSpecNum,\n" +
            "syearSpecNumUnsold as sYearSpecNumUnsold, syearSpecNumSale as sYearSpecNumSale, syearSpecNumStop as sYearSpecNumStop \n" +
            "FROM SyearView WITH(NOLOCK) \n" +
            "WHERE seriesId in\n" +
            "<foreach collection='seriesIds' item='seriesId' open='(' separator=',' close=')'>\n" +
            "#{seriesId}\n" +
            "</foreach>\n" +
            "ORDER BY Syear DESC\n" +
            "</script>")
    List<SYearViewEntity> getSYearViewBySeriesIds(List<Integer> seriesIds);

}
