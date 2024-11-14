package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.CarShowEntity;
import com.autohome.car.api.data.popauto.entities.ShowCarsViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShowMapper {
    @Select("SELECT Id as [key],Name as [value] FROM Exposition.dbo.Shows WITH(NOLOCK)")
    List<KeyValueDto<Integer,String>> getShowNames();

    @Select("SELECT A.id,A.seriesId,A.sImg,A.c FROM [exposition].dbo.ShowCarsView AS A WITH(NOLOCK)\n" +
            "                                    INNER JOIN (SELECT B.BrandId,A.ParentId AS ShowId,B.PavilionId FROM [replication].dbo.Pavilion  AS A WITH(NOLOCK) INNER JOIN [replication].dbo.Pavilion_Brand AS B WITH(NOLOCK) ON A.ID=B.PavilionId AND A.IsDel=0 AND B.IsDel=0) AS B\n" +
            "                                    ON  A.showId=B.ShowId AND A.BrandId=B.BrandId \n" +
            "                                    WHERE A.ShowId = #{showId} AND B.PavilionId=#{pavilionId}")
    List<ShowCarsViewEntity> getShowCarsInfoByPavilionId(@Param("showId") int showId, @Param("pavilionId") int pavilionId);

    @Select("SELECT A.id,A.seriesId,A.sImg,A.c, A.brandId,A.levelId FROM [exposition].dbo.ShowCarsView AS A WITH(NOLOCK) WHERE A.ShowId = #{showId}")
    List<ShowCarsViewEntity> getShowCarsInfoByShowId(@Param("showId") int showId);

    @Select("SELECT A.id,A.seriesId,A.sImg,A.c, A.brandId FROM [exposition].dbo.ShowCarsView AS A WITH(NOLOCK) INNER JOIN [popauto].dbo.SeriesView AS B ON A.Seriesid = B.seriesId   WHERE  B.containelectriccar = 1 AND A.ShowId = #{showId}")
    List<ShowCarsViewEntity> getEvCarShowInfoByShowId(@Param("showId") int showId);

    @Select("SELECT A.Id,A.SeriesId,A.SImg,A.C ,A.brandId,A.levelId  FROM [exposition].dbo.ShowCarsView as  A WITH(NOLOCK) INNER JOIN [popauto].dbo.Brands AS B ON A.Seriesid = B.id \n" +
            "                                            WHERE B.IsNewenergy = 1 AND  A. ShowID= #{showId}")
    List<ShowCarsViewEntity> getEvShowCarsNewEnergyByShowId( int showId);

    @Select(" select ParentId as [key], ID as [value] from [replication].dbo.Pavilion")
    List<KeyValueDto<Integer, Integer>> getShowIdsAndPavilionIds();

    @Select("SELECT id as [key],img as [value] FROM Exposition.dbo.show_cars WITH(NOLOCK) WHERE ShowID=#{showId} AND Seriesid=#{seriesId} ORDER BY id DESC")
    List<KeyValueDto<Integer,String>> getShowCarsImg(int showId, int seriesId);

    @AutoCache(expireIn = 30,removeIn = 120)
    @Select("SELECT Id,REPLACE(Img,'car-','5car-') AS PicPath, sImg AS SPicPath,(SELECT name FROM popauto.dbo.manufactory AS A WITH(NOLOCK) WHERE A.id=Show_cars.brandid) AS FctName,\n" +
            "                                                (SELECT seriesName FROM popauto.dbo.SeriesView AS A WITH(NOLOCK)  WHERE A.seriesId=show_cars.seriesid) AS SeriesName\n" +
            "                                    FROM exposition.dbo.show_cars WITH(NOLOCK) \n" +
            "                                    WHERE  showid= #{showId}  AND [seriesid] = #{seriesId} \n" +
            "                                     ORDER BY id DESC")
    List<CarShowEntity> getShowPicInfoByShowIdSeriesId(int showId, int seriesId);

    @AutoCache(expireIn = 30,removeIn = 120)
    @Select("SELECT SeriesId as [key],SeriesName as [value] \n" +
            "FROM( SELECT Id AS SeriesId,Name AS SeriesName,FctId\n" +
            "FROM exposition.dbo.Gbrands WITH(NOLOCK)\n" +
            "WHERE isdel=0\n" +
            "UNION ALL\n" +
            "SELECT Id AS SeriesId,Name AS SeriesName,M AS FctId\n" +
            "FROM popauto.dbo.Brands AS A  WITH(NOLOCK)\n" +
            "WHERE EXISTS(SELECT 1 FROM exposition.dbo.Show_Cars WITH(NOLOCK) WHERE A.id=Show_Cars.Seriesid)) AS Temp\n" +
            "WHERE Temp.FctId= #{fctId}")
    List<KeyValueDto<Integer,String>> getShowSeriesByFctId(int fctId);

    @Select("select SeriesId as [key],showId as [value] from exposition.dbo.show_cars GROUP BY SeriesId,showId\n")
    List<KeyValueDto<Integer,Integer>> getSeriesIdShowIdAll();


}
