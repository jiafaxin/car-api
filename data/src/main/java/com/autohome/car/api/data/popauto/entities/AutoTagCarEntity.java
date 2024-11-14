package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class AutoTagCarEntity {

    int id;
    int seriesId;
    int specId;
    int fctMinPrice;
    int fctMaxPrice;
    int seriesFctMaxPrice;
    int seriesFctMinPrice;
    double deliveryCapacity;
    int horsepower;
    int place;
    int gearBox;
    int structId;
    int driveForm;
    int fuelType;
    int flowMode;
    int driveType;
    int electricType;
    int electricKW;
    int isclassic;
    int spceOrdercLs;
}
