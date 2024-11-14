package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class SpecSearchEntity {
    int seriesId;
    String seriesName;
    String seriesPlace;
    int specId;
    String specName;
    String specimg;
    int specState;
    int minPrice;
    int maxPrice;
    int levelId;
    String levelName;
    int fuelType;
}
