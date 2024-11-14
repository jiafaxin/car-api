package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class SeriesSearchEntity {
    int seriesId;
    String seriesName;
    int brandId;
    String brandName;
    String brandFirstLetter;
    int levelId;
    String levelName;
    int country;
    int seriesPriceMax;
    int seriesPriceMin;
    int fctId;
    String fctName;
    String seriesFirstLetter;
    int seriesRank;
    int seriesIsPublic;
    int seriesState;
    String seriesPlace;
    String logo;
    int seriesOrdercls;
}
