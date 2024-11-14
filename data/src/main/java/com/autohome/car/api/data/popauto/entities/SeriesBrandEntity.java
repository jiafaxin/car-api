package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesBrandEntity implements Serializable {

    private int seriesId;
    private String seriesName;
    private String seriesImg;
    private int brandId;
    private String brandName;
    private String brandFirstLetter;
    private int levelid;
    private String country;
    private int seriesPriceMin;
    private int seriesPriceMax;
    private int fctId;
    private String seriesFirstLetter;
    private int seriesRank;
    private int seriesIsPublic;
    private int seriesState;
    private int seriesOrdercls;
}
