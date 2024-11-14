package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesCountryEntity implements Serializable {

    private int seriesId;

    //private String seriesName;

    private int brandId;

    //private String brandName;

    //private String brandFirstLetter;

    private int levelId;

    //private int country;

    //private int seriesPriceMax;

    //private int seriesPriceMin;

    private int fctId;

    //private String seriesFirstLetter;

    //private double seriesRank;

    private int seriesIsPublic;

    //private int seriesState;

    private int seriesOrdercls;
}
