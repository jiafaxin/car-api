package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.SeriesProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 车系主表名：Brands 需要注意下
 */
@Mapper
public interface SeriesMapper {

    @SelectProvider(value = SeriesProvider.class,method = "getBase")
    SeriesBaseInfoEntity getBase(int seriesId);


    @SelectProvider(value = SeriesProvider.class,method = "getAllBase")
    List<SeriesBaseInfoEntity> getAllBase();

    @SelectProvider(value = SeriesProvider.class,method = "getSeriesView")
    SeriesViewEntity getSeriesView(int seriesId);

    @SelectProvider(value = SeriesProvider.class,method = "getAllSeriesView")
    List<SeriesViewEntity> getAllSeriesView();


    @Select("select DISTINCT id from brands with(nolock)")
    List<Integer> getAllSeriesIds();

    @SelectProvider(value = SeriesProvider.class,method = "getAllElectricSeriesList")
    List<SeriesOnlyElectricEntity> getAllElectricSeriesList();

    @Select("SELECT DISTINCT SeriesId FROM SpecPriceWaitSellView WITH(NOLOCK);")
    List<Integer> containsWaitSellSeriesIds();

    @Select("SELECT DISTINCT SeriesId FROM SpecPriceStopSellView WITH(NOLOCK);")
    List<Integer> containsStopSellSeriesIds();

    @Select("SELECT DISTINCT SeriesId FROM SpecPriceSellView WITH(NOLOCK)")
    List<Integer> containsSellInSeriesIds();

    @Select("SELECT count(1) FROM SpecPriceWaitSellView WITH(NOLOCK) WHERE SeriesId = #{seriesId};")
    int waitSellSeries(int seriesId);

    @Select("SELECT count(1) FROM SpecPriceStopSellView WITH(NOLOCK) WHERE SeriesId = #{seriesId};")
    int stopSellSeries(int seriesId);

    @Select("SELECT A.SeriesId,\n" +
            "    B.seriesName,\n" +
            "    A.BrandId,\n" +
            "    B.brandName,\n" +
            "    B.brandFirstLetter,\n" +
            "    B.levelId,\n" +
            "    B.levelName,\n" +
            "    D.id AS Country,\n" +
            "    B.seriesPriceMax,\n" +
            "    B.seriesPriceMin,\n" +
            "    B.FctId,\n" +
            "    B.FctName,\n" +
            "    B.SeriesFirstLetter,\n" +
            "    B.SeriesRank,\n" +
            "    B.SeriesIsPublic,\n" +
            "    B.SeriesState,\n" +
            "    B.SeriesPlace,\n" +
            "    REPLACE(B.seriesImg,'~','') as logo,\n" +
            "    A.SeriesOrdercls\n" +
            "FROM CarManuePrice AS A WITH (NOLOCK)\n" +
            "        JOIN SeriesView AS B WITH (NOLOCK) ON A.SeriesId = B.seriesId\n" +
            "        JOIN [group] AS C WITH (NOLOCK) ON A.BrandId = C.id\n" +
            "        JOIN BrandCountry AS D WITH (NOLOCK) ON C.Country = D.country")
    List<SeriesSearchEntity> getAllSeries();

    @Select("SELECT count(1) FROM SpecPriceSellView WITH(NOLOCK) WHERE SeriesId = #{seriesId}")
    int sellInSeries(int seriesId);

    @Select("select distinct(standard_id)  from crashtest_series cs with(nolock) where publishstate=10 and seriesid=#{seriesId}")
    List<Integer> getSeriesHaveCrashInfo(int seriesId);

    @Select("select distinct seriesid from crashtest_series with(Nolock) where standard_id = 1 and  publishstate=10")
    List<Integer> getCrashTestSeriesList();

    @Select("with series as \n" +
            "(\n" +
            "\tselect * from (\n" +
            "\tselect id,seriesid,[description],create_time,ROW_NUMBER()over(partition by seriesid order by id desc) as RN from crashtest_series   with(nolock) where standard_id = #{standardId} and  seriesid=#{seriesId} and publishstate = 10  \n" +
            "\t) as t  where  RN=1\n" +
            ")\n" +
            "select C.id as articleid, A.typeid, \n" +
            "A.id as itemid,\n" +
            "A.name as itemname,\n" +
            "A.remark,\n" +
            "A.valuetype,\n" +
            "B.crashvalue\n" +
            "from crashtest_item as A  with(nolock)  inner join crashtest_detail as B  with(nolock)  on A.id = B.itemid\n" +
            "inner join series as C  with(nolock)  on C.id = B.parentid\n" +
            "inner join crashtest_type as D  with(nolock)  on D.id =B.typeid order by A.sortid")
    List<CrashTestDetailEntity> getCrashTestBySeriesId(int seriesId,int standardId);

    @Select("select A.id,A.M fctid,A.url,A.jb,A.newFctid brandid,A.Place,A.EditTime,A.firstletter,A.name,REPLACE(A.nobgcolorpicurl,'~','') as pngLogo" +
            ",B.SeriesState,B.seriesSpecNumSale as ssns,B.seriesPriceMin as priceMin,B.seriesPriceMax as priceMax,REPLACE(B.seriesImg,'~','') as logo  " +
            "from brands  as A WITH (NOLOCK) " +
            "inner join SeriesView as B with(nolock) on A.id = B.seriesId")
    List<SeriesInfoAllEntity> getAllSeriesList();

    @Select("SELECT id as [key], jb AS [value] FROM Brands WITH(NOLOCK)")
    List<KeyValueDto<Integer,Integer>> getAllSeriesLevelId();

    @Select("SELECT Id as [key],name as [value] FROM [Group] WITH (NOLOCK)")
    List<KeyValueDto<Integer,String>> getAllSeriesName();

    @Select(" SELECT seriesid AS name FROM (\n" +
            "            SELECT id,seriesid,ROW_NUMBER()OVER(PARTITION by seriesid order by id desc) as RN from crashtest_series   with(nolock) where  publishstate = 10  \n" +
            "            ) AS t  where  RN=1")
    List<Integer> getAllSeriesHaveCrashInfo();

    @Select("SELECT id, name, newFctId  FROM exposition.dbo.Gbrands with(nolock) where isdel=0")
    List<GBrandEntity> getGBrandsAll();

    @Select("SELECT id FROM [Brands] WITH(NOLOCK) WHERE isdel = 0")
    List<Integer> getAllValidSeriesIds();

    @Select("select id, newfctid AS name from exposition.dbo.Gbrands with(nolock) where isdel=0 and id=#{seriesId}")
    KeyValueDto<Integer,Integer> getFctGBrandsById(int seriesId);

}
