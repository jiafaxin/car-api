package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.SpecStateEnum;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.SeriesViewProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SeriesViewMapper {

    @Select("SELECT BrandId as brandId,FctId as fctId,SeriesId as seriesId,SeriesSpecNumSale as ssns \n" +
            "FROM SeriesView WITH(NOLOCK)\n" +
            "WHERE SeriesIsShow=1 AND BrandId=#{brandId}  AND SeriesIspublic=1 \n" +
            "ORDER BY  SeriesIsimport,FctName,SeriesRank DESC")
    List<BFSInfoEntity> getBFSInfoByBrandId(int brandId);

    @Select("<script>\n" +
            "SELECT BrandId as brandId,FctId as fctId,SeriesId as seriesId,SeriesSpecNumSale as ssns \n" +
            "FROM SeriesView WITH(NOLOCK)\n" +
            "WHERE SeriesIsShow=1 AND \n" +
            "BrandId in \n" +
            "<foreach collection='brandIds' item='brandId' open='(' separator=',' close=')'>\n" +
            "#{brandId}\n" +
            "</foreach>\n" +
            "AND SeriesIspublic=1 \n" +
            "ORDER BY  SeriesIsimport,FctName,SeriesRank DESC\n" +
            "</script>")
    List<BFSInfoEntity> getBFSInfoByBrandIds(List<Integer> brandIds);


    @Select("with specData as (\n" +
            "        select seriesid,fueltypedetail as fueltype from SpecView with(nolock)where brandid=#{brandId}\n" +
            "        union all \n" +
            "        select seriesid,fueltype  from cv_specview with(nolock)where brandid=#{brandId}\n" +
            "    ) \n" +
            "    select  A.brandId,A.brandFirstLetter,A.seriesId,A.SeriesState,case A.SeriesState \n" +
            "    when 10 then 1 when 20 then 1 when 30 then 1 when 40 then 2 when 0 then 2 end OrderSeriesState, A.seriesOrdercls,B.fueltype, E.scores_ranks as scoresRanks" +
            "    from SeriesView as A with(nolock) \n" +
            "    inner join specData as B with(nolock) on A.seriesId= B.seriesId \n" +
            "    left JOIN [Replication].dbo.dxp_CarBrandSeries_Ranks E WITH (NOLOCK) ON B.seriesid= E.series_id and E.isdelete=0 \n" +
            "    where A.seriesisnewenergy =1 and A.brandId = #{brandId}\n" +
            "    group by A.brandId,A.brandFirstLetter,\n" +
            "    A.seriesId,A.SeriesState,A.seriesOrdercls,B.fueltype,E.scores_ranks\n" +
            "    order by OrderSeriesState, E.scores_ranks")
    List<SeriesViewElectricEntity> getBrandEVSeriesList(int brandId);

    @Select("select distinct brandId from SeriesView with(nolock)")
    List<Integer> getAllBrandIdsFromSeriesView();
    @SelectProvider(value = SeriesViewProvider.class, method = "getSeriesInfoByLevelId")
    List<SeriesViewRankEntity> getSeriesInfoByLevelId(int levelId);

    @Select("select distinct LevelId from SeriesView with(nolock)")
    List<Integer> getAllLevelIdFromSeriesView();

    @Select("SELECT DISTINCT  seriesId,RowIndex FROM" +
            "(SELECT Row_Number() OVER(PARTITION BY levelid ORDER BY SeriesNewRank ASC) AS RowIndex,SeriesId " +
            "FROM SeriesView   WITH(NOLOCK) ) AS A " +
            "WHERE seriesId=#{seriesId}")
    SeriesLevelRankEntity getSeriesLevelRankById(int seriesId);

    @Select("SELECT DISTINCT  seriesId,RowIndex FROM" +
            "(SELECT Row_Number() OVER(PARTITION BY levelid ORDER BY SeriesNewRank ASC) AS RowIndex,SeriesId " +
            "FROM SeriesView   WITH(NOLOCK) ) AS A ")
    List<SeriesLevelRankEntity> getAllSeriesLevelRank();

    @Select("select brandid as [key],seriesId as [value] from SeriesView")
    List<KeyValueDto<Integer,Integer>> getSeriesIdsByBrandId();

    @Select("SELECT Row_Number() OVER(ORDER BY NewSeriesOrdercls ) AS RankIndex,BrandId,FactoryId,SeriesId,SFirstLetter,IsCV,SpecState,specIsImage,NewSeriesOrdercls as  SeriesOrder,seriesstate\n" +
            "                                            FROM (SELECT    sereies.SeriesId,sereies.BrandId,sereies.FctId AS FactoryId, sereies.seriesFirstLetter AS SFirstLetter,spec.SpecState,spec.IsCV,specIsImage,sereies.NewSeriesOrdercls ,sereies.seriesstate\n" +
            "\t\t\t\t                                            FROM(\tSELECT SeriesId,SpecState,1 AS IsCV,specIsImage FROM SpecView WITH(NOLOCK)\n" +
            "\t\t\t\t                                                            UNION\n" +
            "\t\t\t\t                                                            SELECT SeriesId,SpecState,2 AS IsCV,0 AS specIsImage FROM CV_SpecView WITH(NOLOCK)) AS spec \n" +
            "                                            INNER JOIN SeriesView AS  sereies WITH(NOLOCK) ON spec.SeriesId = sereies.seriesId) AS Temp")
    List<SeriesInfoEntity> getAllSeriesItems();


    @Select("SELECT count(seriesid) FROM (\n" +
            "          SELECT seriesid,dtime,row_number()OVER(PARTITION BY seriesid ORDER BY picid DESC) AS RN  FROM CarPhotoView WITH(NOLOCK) WHERE picclass=53 and DATEDIFF(DAY,dtime,getdate())<=7\n" +
            "        ) AS T WHERE rn =1 and seriesid = #{seriesId}")
    int getSeriesOfficialPicIsNewBySeriesId(int seriesId);

    @Select("SELECT DISTINCT seriesid as name FROM (\n" +
            "          SELECT seriesid,dtime,row_number()OVER(PARTITION BY seriesid ORDER BY picid DESC) AS RN  FROM CarPhotoView WITH(NOLOCK) WHERE picclass=53 and DATEDIFF(DAY,dtime,getdate())<=7\n" +
            "        ) AS T WHERE rn =1")
    List<Integer> getSeriesOfficialPicIsNewAll();

    @Select("SELECT brandId,fctId,seriesId,SeriesSpecNumSale as ssns \n" +
            "                                    FROM SeriesView WITH(NOLOCK)\n" +
            "                                    WHERE SeriesIsShow=1 AND FctId = #{fctId}  AND SeriesIspublic=1 \n" +
            "                                    ORDER BY  BrandId,SeriesIsimport,FctName,SeriesRank DESC")
    List<BFSInfoEntity> getBFSInfoByFctId(int fctId);

    @Select("SELECT seriesId,SeriesIspublic,SeriesState,FctId FROM SeriesView WITH(NOLOCK)\n" +
            "    WHERE FctId=#{fctId}\n" +
            "    ORDER BY SeriesIsimport,SeriesRank DESC")
    List<SeriesFctEntity> getSeriesByFctId(int fctId);

    @Select("WITH SeriesTemp\n" +
            "                                    AS(\tSELECT SeriesId,SeriesFctMinPrice,SeriesFctMaxPrice,ROW_NUMBER() OVER(ORDER BY  Id) AS RowIndex\n" +
            "\t                                    FROM (\tSELECT SeriesId,Id,SeriesFctMinPrice,SeriesFctMaxPrice,ROW_NUMBER() OVER(PARTITION BY SeriesId ORDER BY Id ) AS PartIndex \n" +
            "\t\t\t                                    FROM ${tableName} WITH(NOLOCK) WHERE BrandId = #{brandId}) AS A\n" +
            "\t                                    WHERE PartIndex=1 )\n" +
            "                                    SELECT SeriesTemp.SeriesId,SeriesView.FctId,SeriesTemp.SeriesFctMinPrice,SeriesTemp.SeriesFctMaxPrice,SeriesTemp.RowIndex FROM SeriesTemp WITH(NOLOCK)\n" +
            "                                    INNER JOIN SeriesView WITH(NOLOCK) ON SeriesTemp.SeriesId=SeriesView.SeriesId \n" +
            "                                    ORDER BY  SeriesView.seriesIsimport,SeriesView.fctName,SeriesTemp.RowIndex")
    List<SeriesStateEntity> getSeriesInfoByBrandIdAndState(String tableName,int brandId);

    @Select("SELECT A.SeriesId,A.BrandId,B.levelId,B.FctId,B.SeriesIsPublic,A.SeriesOrdercls FROM CarManuePrice AS A WITH(NOLOCK)\n" +
            "                                  JOIN SeriesView AS B WITH(NOLOCK) ON A.SeriesId = B.seriesId\n" +
            "                                  JOIN [group] AS C WITH(NOLOCK) ON A.BrandId = C.id\n" +
            "                                  JOIN BrandCountry AS D WITH(NOLOCK) ON C.Country = D.country")
    List<SeriesCountryEntity> getSeriesCountryAll();

    @Select("select seriesId, seriesState from seriesview with(nolock) where SeriesState =10")
    List<SeriesViewSimpInfo> getSeriesViewInfo();

    @SelectProvider(value = SeriesViewProvider.class, method = "getSeriesByPageLevelId")
    List<KeyValueDto<Integer, Integer>> getSeriesByPageLevelId(int levelId, SpecStateEnum state, int start, int end);

    @SelectProvider(value = SeriesViewProvider.class, method = "getSeriesCountByPageLevelId")
    int getSeriesCountByLevelId(int levelId, SpecStateEnum state);

    @SelectProvider(value = SeriesViewProvider.class,method = "getSeriesHotCount")
    int getSeriesHotCount(String seriesIspublic);

    @SelectProvider(value = SeriesViewProvider.class,method = "getSeriesHot")
    List<SeriesHotEntity> getSeriesHot(int start,int end,String seriesIspublic);

    @Select("select seriesId,seriesName,SeriesState,seriesRank from SeriesView with(nolock) where containelectriccar =1")
    @AutoCache(expireIn = 120)
    List<ElectricSeriesEntity> getElectricSeriesAll();
}
