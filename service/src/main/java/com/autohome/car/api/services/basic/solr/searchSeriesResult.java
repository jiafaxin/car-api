package com.autohome.car.api.services.basic.solr;

import com.google.type.Decimal;
import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

@Data
public class searchSeriesResult implements Serializable {
    @Field("SeriesId")
    private Integer seriesId;
    @Field("id")
    private Integer specId;
    @Field("FctMaxPrice")
    private Integer fctMaxPrice;
    @Field("FctMinPrice")
    private Integer fctMinPrice;

    @Field("SeriesFctMaxPrice")
    private int seriesFctMaxPrice;
    @Field("SeriesFctMinPrice")
    private int seriesFctMinPrice;
    @Field("DeliveryCapacity")
    private Decimal deliveryCapacity;
    @Field("Horsepower")
    private Integer horsepower;
    @Field("GearBox")
    private Integer gearBox;
    @Field("SeriesbbsShow")
    private Integer seriesbbsShow;
    @Field("StructId")
    private Integer structId;
    @Field("Country")
    private Integer country;
    @Field("DriveType")
    private Integer driveType;
    @Field("fuelType")
    private Integer fuelType;
    @Field("FlowMode")
    private Integer flowMode;
    @Field("SpecState")
    private Integer specState;
    @Field("Seat")
    private Integer seat;
    @Field("electrictype")
    private Integer electrictype;
    @Field("electricKW")
    private Integer electricKW;
    @Field("syear")
    private Integer syear;
    @Field("syearid")
    private Integer syearid;
    @Field("isimport")
    private Integer isimport;
    @Field("isclassic")
    private Integer isclassic;
    @Field("endurancemileage")
    private Integer endurancemileage;
    @Field("fueltypedetail")
    private Integer fueltypedetail;
    @Field("FctId")
    private Integer fctId;
    @Field("BrandId")
    private Integer brandId;
    @Field("LevelId")
    private Integer levelId;
}
