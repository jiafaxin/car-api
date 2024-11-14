package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.ColorInfoEntity;
import com.autohome.car.api.data.popauto.entities.InnerSpecColorPriceRemarkEntity;
import com.autohome.car.api.data.popauto.entities.SpecColorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InnerSpecColorMapper {

    @Select("SELECT SpecId as [key],STRING_AGG(ColorId, ',') as [value] FROM innerSpecColor WITH(NOLOCK) WHERE SpecId=#{specid} group by SpecId")
    KeyValueDto<Integer, String> getInnerSpecColor(int specid);

    @Select("SELECT SpecId as [key], STRING_AGG(ColorId, ',') as [value] FROM innerSpecColor GROUP BY SpecId")
    List<KeyValueDto<Integer, String>> getAllInnerSpecColor();

    @Select("SELECT SpecId,STRING_AGG(ColorId, ',') as innerColorIds,STRING_AGG(price, ',') as innerColorPrices,STRING_AGG(ISNULL(remarks,''), ',') as innerColorRemarks FROM innerSpecColor WITH(NOLOCK) WHERE SpecId=#{specid} group by SpecId")
    InnerSpecColorPriceRemarkEntity getInnerSpecColorPriceRemark(int specid);

    @Select("SELECT SpecId,STRING_AGG(ColorId, ',') as innerColorIds,STRING_AGG(price, ',') as innerColorPrices,STRING_AGG(ISNULL(remarks,''), ',') as innerColorRemarks FROM innerSpecColor WITH(NOLOCK) group by SpecId")
    List<InnerSpecColorPriceRemarkEntity> getAllInnerSpecColorPriceRemark();

    @Select("SELECT SeriesId,ColorId,ColorName,ColorValue,SUM(ISNULL(SpecColorPicNumber,0)) AS PicNum,SUM(ISNULL(ClubPicNum,0)) AS ClubPicNum,SpecState\n" +
            "FROM(\tSELECT D.SeriesId,A.ColorId,C.ColorName,C.ColorValue,B.SpecColorPicNumber,E.ClubPicNum,CASE D.SpecState WHEN 30 THEN 20 ELSE D.SpecState END AS SpecState\n" +
            "\t\tFROM innerSpecColor AS A WITH(NOLOCK)\n" +
            "\t\tLEFT JOIN (SELECT SpecId,ColorId,SUM(PicNumber)  AS SpecColorPicNumber FROM CarSpecInnerColorStatistics WITH(NOLOCK) where picclass<200 GROUP BY SpecId,ColorId) AS B ON A.SpecId=B.SpecId AND A.ColorId=B.ColorId\n" +
            "\t\tINNER JOIN InnerFctColor AS C WITH(NOLOCK) ON A.ColorId=C.Id\n" +
            "\t\tINNER JOIN SpecView AS D WITH(NOLOCK)  ON A.SpecId=D.SpecId\n" +
            "\t\tLEFT JOIN (SELECT SpecId,PicColorId,COUNT(1) AS ClubPicNum FROM CarPhotoView WITH(NOLOCK) WHERE IsClubPhoto=1 AND PicColorId>0 GROUP BY SpecId,PicColorId) AS E ON A.ColorId=E.PicColorId AND A.SpecId=E.SpecId\n" +
            ") AS Temp\n" +
            "GROUP BY SeriesId,ColorId,ColorName,ColorValue,SpecState")
    List<SpecColorEntity> getAllSeriesInnerColor();

    @Select("select * from \n" +
            "(\n" +
            "SELECT SeriesId,ColorId,ColorName,ColorValue,SUM(ISNULL(SpecColorPicNumber,0)) AS PicNum,SUM(ISNULL(ClubPicNum,0)) AS ClubPicNum,SpecState\n" +
            "FROM(\tSELECT D.SeriesId,A.ColorId,C.ColorName,C.ColorValue,B.SpecColorPicNumber,E.ClubPicNum,CASE D.SpecState WHEN 30 THEN 20 ELSE D.SpecState END AS SpecState\n" +
            "\t\tFROM innerSpecColor AS A WITH(NOLOCK)\n" +
            "\t\tLEFT JOIN (SELECT SpecId,ColorId,SUM(PicNumber)  AS SpecColorPicNumber FROM CarSpecInnerColorStatistics WITH(NOLOCK) where picclass<200 GROUP BY SpecId,ColorId) AS B ON A.SpecId=B.SpecId AND A.ColorId=B.ColorId\n" +
            "\t\tINNER JOIN InnerFctColor AS C WITH(NOLOCK) ON A.ColorId=C.Id\n" +
            "\t\tINNER JOIN SpecView AS D WITH(NOLOCK)  ON A.SpecId=D.SpecId\n" +
            "\t\tLEFT JOIN (SELECT SpecId,PicColorId,COUNT(1) AS ClubPicNum FROM CarPhotoView WITH(NOLOCK) WHERE IsClubPhoto=1 AND PicColorId>0 GROUP BY SpecId,PicColorId) AS E ON A.ColorId=E.PicColorId AND A.SpecId=E.SpecId\n" +
            ") AS Temp\n" +
            "GROUP BY SeriesId,ColorId,ColorName,ColorValue,SpecState\n" +
            ") as a where a.SeriesId = #{seriesid}")
    List<SpecColorEntity> getSeriesInnerColor(int seriesid);

}
