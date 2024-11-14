package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.util.Date;

@Data
public class SeriesViewEntity {
    int seriesid;
    String seriesName;
    int levelId;
    String levelName;
    int brandId;
    String brandName;
    int fctId;
    String fctName;
    Date seriesCreateTime;
    String seriesImg;
    int SeriesState;
    int seriesPriceMin;
    int seriesPriceMax;
    int seriesisnewenergy;
    String pricedescription;
    String seriesplace;
    String brandFirstLetter;
    String fctFirstLetter;
    String seriesFirstLetter;
    int containelectriccar;
    private int seriesPhotoNum;
    String seriesConfigFilePath;
    private int seriesSpecNum;

    private int newSeriesOrderCls;

    private int seriesIsPublic;
    int seriesRank;

    int seriesNewRank;
}
