package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.providers.ElectricSpecViewProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface ElectricSpecViewMapper {


    @Select("SELECT seriesId,horsepower,mileage,specState,fuelType FROM Electric_SpecView WITH(NOLOCK) WHERE seriesId=#{seriesId}")
    List<EleSpecViewBaseEntity> getEleSpecViewSimp(int seriesId);

    @Select("select   \n" +
            " batteryCapacity as rongliang,\n" +
            " electricKw as gonglv,\n" +
            " torque as niuju,\n" +
            " Mileage as licheng,\n" +
            " specId,\n" +
            " fueltype\n" +
            "from Electric_SpecView a with(nolock) where specId=#{specId}")
    List<ElectricSpecParamEntity> getEleSpecSpecViewById(int specId);

    @Select("select distinct A.fctId,B.seriesId,B.SeriesOrdercls,fuelType,specIsImage,specState from Electric_SpecView as A with(nolock) inner join CarManueElectric as B with(nolock) \n" +
            " on A.seriesId = B.SeriesId where A.brandId = #{brandId} order by B.SeriesOrdercls ")
    List<ElectricSpecViewEntity> getElectricInfoByBrandId(int brandId);

    @SelectProvider(value = ElectricSpecViewProvider.class,method = "seriesSearchSeriesByPriceLevelKMFueltype")
    List<KeyValueDto<Integer,Double>> seriesSearchSeriesByPriceLevelKMFueltype(int topnum,String level,int fueltype,int minPrice,int maxPrice,int minKm,int maxKm);


    @SelectProvider(value = ElectricSpecViewProvider.class,method = "getSeriesRand_ElectricZengCheng")
    @AutoCache(expireIn = 120,removeIn = 240)
    List<ElectricSpecEntity> getSeriesRand_ElectricZengCheng(int type);

    @Select("select specId,seriesId,seriesName,specPrice,levelName,endurancemileage from specview \n" +
            "                            where ( specstate>=20 and SpecState<=30 ) and fueltypedetail in (${fuelType})\n" +
            "                            order by endurancemileage desc")
    @AutoCache(expireIn = 120,removeIn = 240)
    List<ElectricSummaryEntity> getElectricSummary(String fuelType);

    @Select("with  series as(\n" +
            "   select A.SeriesId,B.SeriesState,A.SeriesOrdercls from CarManueElectric as A with(Nolock) inner join SeriesView as B with(Nolock) on A.SeriesId =B.seriesId\n" +
            "   )\n" +
            "   select  distinct  brandId,B.seriesId,B.SeriesState,B.SeriesOrdercls,fuelType from Electric_SpecView as A with(nolock) inner join series as B with(nolock) on A.seriesId = B.SeriesId\n" +
            "   where A.fuelType>0\n" +
            "   order by brandId ,SeriesOrdercls;")
    @AutoCache(expireIn = 60)
    List<EleSeriesViewEntity> getAllElectricSeries();
}
