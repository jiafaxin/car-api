package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.SpecPriceViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SpecPriceStopSellViewMapper {

    @Select("SELECT top 1 SeriesId FROM SpecPriceStopSellView WITH(NOLOCK) where SeriesId = #{seriesId};")
    Integer carStateSeriesStopSell(int seriesId);

    @Select("SELECT DISTINCT SeriesId FROM SpecPriceStopSellView WITH(NOLOCK)")
    List<Integer> carStateSeriesStopSellAll();

    @Select("SELECT * FROM (\n" +
            "    SELECT id,specId,syearid,syear,TransmissionTypeId,StructId,FctMinPrice,FctMaxPrice,DeliveryCapacity,SeriesId,FctId,Horsepower,BrandId,GearBox,DriveForm,DriveType,FlowMode,fuelType,fueltypedetail,SpecState,IsClassic,electrictype,electricKW,endurancemileage\n" +
            "    FROM SpecPriceWaitSellView WITH(NOLOCK)  where seriesid = #{seriesId}\n" +
            "    UNION ALL \n" +
            "    SELECT id,specId,syearid,syear,TransmissionTypeId,StructId,FctMinPrice,FctMaxPrice,DeliveryCapacity,SeriesId,FctId,Horsepower,BrandId,GearBox,DriveForm,DriveType,FlowMode,fuelType,fueltypedetail,SpecState,IsClassic,electrictype,electricKW,endurancemileage\n" +
            "    FROM SpecPriceSellView WITH(NOLOCK) where seriesid = #{seriesId}\n" +
            "    UNION ALL \n" +
            "    SELECT id,specId,syearid,syear,TransmissionTypeId,StructId,FctMinPrice,FctMaxPrice,DeliveryCapacity,SeriesId,FctId,Horsepower,BrandId,GearBox,DriveForm,DriveType,FlowMode,fuelType,fueltypedetail,SpecState,IsClassic,electrictype,electricKW,endurancemileage \n" +
            "    FROM SpecPriceStopSellView WITH(NOLOCK)  where seriesid = #{seriesId}\n" +
            ") AS T ;")
    List<SpecPriceViewEntity> carSpecPriceBySeriesId(int seriesId);

    @Select("SELECT specId,TransmissionTypeId,StructId,FctMinPrice,FctMaxPrice,DeliveryCapacity,SeriesId,FctId,Horsepower,BrandId,GearBox,DriveForm,DriveType,FlowMode,fuelType,fueltypedetail,SpecState,IsClassic,electrictype,electricKW,syear,syearid,endurancemileage\n" +
            "                                                            FROM SpecPriceWaitSellView  WITH(NOLOCK) WHERE SeriesId=#{seriesId} order by id")
    List<SpecPriceViewEntity> carSpecPriceWaitSellBySeriesId(int seriesId);

    @Select("SELECT specId,TransmissionTypeId,StructId,FctMinPrice,FctMaxPrice,DeliveryCapacity,SeriesId,FctId,Horsepower,BrandId,GearBox,DriveForm,DriveType,FlowMode,fuelType,fueltypedetail,SpecState,IsClassic,electrictype,electricKW ,syear,syearid,endurancemileage\n" +
            "                                FROM SpecPriceSellView WITH(NOLOCK) WHERE SeriesId=#{seriesId} order by id")
    List<SpecPriceViewEntity> carSpecPriceSellBySeriesId(int seriesId);
}
