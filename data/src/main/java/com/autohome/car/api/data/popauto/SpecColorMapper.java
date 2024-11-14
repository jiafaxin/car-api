package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.entities.CarSpecColorEntity;
import com.autohome.car.api.data.popauto.entities.SpecColorEntity;
import com.autohome.car.api.data.popauto.entities.SpecColorListEntity;
import com.autohome.car.api.data.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.car.api.data.popauto.providers.SpecColorProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SpecColorMapper {

    /**
     * 获取车系下颜色信息
     * @param seriesId
     * @return
     */
    @SelectProvider(value = SpecColorProvider.class,method = "getSeriesColor")
    List<SpecColorEntity> getSeriesColor(int seriesId);


    @SelectProvider(value = SpecColorProvider.class,method = "getAllSeriesColor")
    List<SpecColorEntity> getAllSeriesColor(int seriesId);


    /**
     * 获取车系下内饰颜色信息
     * @param seriesId
     * @return
     */
    @SelectProvider(value = SpecColorProvider.class,method = "getSeriesInnerSpecColor")
    List<SpecColorEntity> getSeriesInnerSpecColor(int seriesId);


    @SelectProvider(value = SpecColorProvider.class,method = "getAllSeriesInnerSpecColor")
    List<SpecColorEntity> getAllSeriesInnerSpecColor();

    @SelectProvider(value = SpecColorProvider.class,method = "GetSpecPicColorStatisticsBySeriesId")
    List<SpecPicColorStatisticsEntity> getSpecPicColorStatisticsBySeriesId();
    @Select("SELECT specId as sId,seriesId as ssId, ColorId as cId, remarks as mark, price FROM Car_Spec_Color WITH(NOLOCK) WHERE SpecId=#{specId}")
    List<CarSpecColorEntity> getSpecColorBySpecId(int specId);

    @Select("SELECT specId as sId,seriesId as ssId,ColorId as cId, remarks as mark, price FROM innerSpecColor WITH(NOLOCK) WHERE SpecId=#{specId}")
    List<CarSpecColorEntity> getSpecInnerColorBySpecId(int specId);

    @Select("SELECT  A.SpecId as [key],A.ColorId as [value] \n" +
            "                                FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            "                                INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
            "                                WHERE B.SpecState>=10 AND B.SpecState<=40 AND  B.specIsshow=1 AND  B.specId = #{specId}")
    List<KeyValueDto<Integer,Integer>> getColorBySpecId(int specId);

    @Select("SELECT  A.SpecId as [key],A.ColorId as [value] \n" +
            "                                FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            "                                INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
            "                                WHERE B.SpecState>=10 AND B.SpecState<=40 AND  B.specIsshow=1 ")
    List<KeyValueDto<Integer,Integer>> getAllColor();

    @Select("SELECT  A.SpecId as [key],A.ColorId as [value]\n" +
            "                                FROM innerSpecColor AS A WITH(NOLOCK)\n" +
            "                                INNER JOIN spec_new AS B WITH(NOLOCK) ON A.SpecId=B.id\n" +
            "                                WHERE B.SpecState>=10 AND B.SpecState<=40 AND  B.isshow=1 AND  B.id = #{specId}")
    List<KeyValueDto<Integer,Integer>> getSpecInnerColorList(int specId);

    @Select("SELECT  A.SpecId as [key],A.ColorId as [value]\n" +
            "                                FROM innerSpecColor AS A WITH(NOLOCK)\n" +
            "                                INNER JOIN spec_new AS B WITH(NOLOCK) ON A.SpecId=B.id\n" +
            "                                WHERE B.SpecState>=10 AND B.SpecState<=40 AND  B.isshow=1")
    List<KeyValueDto<Integer,Integer>> getAllSpecInnerColorList();

    @Select("SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber\n" +
            "                                        FROM CarSpecPicColorStatistics AS A WITH(NOLOCK)\t\t\t\t\n" +
            "                                        INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\t\t\t\t\n" +
            "                                        WHERE B.SeriesId=#{seriesId} AND A.PicClass<200")
    List<SpecPicColorStatisticsEntity> getSpecColorPicNumBySeries(int seriesId);

    @Select("SELECT B.SeriesId,B.SpecId,B.SyearId,B.Syear,B.SpecState,A.ColorId,A.PicClass,A.PicNumber\n" +
            "                                        FROM CarSpecInnerColorStatistics AS A WITH(NOLOCK)\n" +
            "                                        INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId = B.SpecId\n" +
            "                                        WHERE B.SeriesId = #{seriesId} AND A.PicClass < 200; ")
    List<SpecPicColorStatisticsEntity> getSpecInnerColorPicNumBySeries(int seriesId);

    @SelectProvider(value = SpecColorProvider.class,method = "getSpecColorPicNumBySeriesCV")
    List<SpecPicColorStatisticsEntity> getSpecColorPicNumBySeriesCV(int seriesId,boolean iscv);

    @Select("SELECT A.seriesId,A.SpecId,A.ColorId,0 PicNumber,0 ClubPicNumber,isnull(price,0) as price,remarks \n" +
            " FROM innerSpecColor AS A WITH(NOLOCK)\n" +
            " INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=b.Id\n" +
            " WHERE B.SpecState>=10 AND  B.SpecState<=30 AND B.isshow=1 AND  B.parent=#{seriesId}")
    List<SpecColorListEntity> getOnSoldSpecInnerColorList(int seriesId);

    @Select("SELECT A.SpecId,A.ColorId,0 PicNumber,0 ClubPicNumber,isnull(price,0) as price,remarks\n" +
            "FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            "INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=b.Id\n" +
            "WHERE B.SpecState>=20 AND  B.SpecState<=30 AND B.IsImageSpec=0 AND  B.parent=#{seriesId}\n" +
            "UNION ALL\n" +
            "SELECT A.SpecId,A.ColorId,0 PicNumber,0 ClubPicNumber,isnull(price,0) as price,remarks\n" +
            "FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            "INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=b.Id\n" +
            "WHERE B.SpecState<=10 AND B.IsImageSpec=0  AND B.isshow=1 AND  B.parent=#{seriesId};")
    List<SpecColorListEntity> getOnSoldSpecSpecColorList(int seriesId);

    @Select("SELECT A.SeriesId as ssId,A.SpecId as sId,A.ColorId as cId,A.price as price,A.remarks as mark\n" +
            " FROM Car_Spec_Color AS A WITH(NOLOCK)\n" +
            " INNER JOIN SpecView AS B WITH(NOLOCK) ON A.SpecId=B.SpecId\n" +
            " WHERE B.SpecId=#{specId}")
    List<CarSpecColorEntity> getSpecColorBySpec(int specId);

    @Select("SELECT A.SeriesId as ssId,A.SpecId as sId,A.ColorId as cId ,A.price as price,A.remarks as mark\n" +
            " FROM innerSpecColor AS A WITH(NOLOCK)\n" +
            " INNER JOIN Spec_New AS B WITH(NOLOCK) ON A.SpecId=B.Id\n" +
            " WHERE B.Id=#{specId}")
    List<CarSpecColorEntity> getSpecInnerColorBySpec(int specId);

    @SelectProvider(value = SpecColorProvider.class,method = "getSpecColorPicByYearId")
    List<SpecYearColorEntity> getSpecColorPicByYearId(int yearId, boolean inner);
}
