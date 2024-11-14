package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesBaseEntity implements Serializable {

    private int id;
    private String name;
    private int levelId;
    private String levelName;
    private int brandId;
    private String brandName;

    private int fctId;
    private String fctName;

    private String place;

    private int state;

    private int priceMin;

    private int priceMax;

    private String img;

    private int newRank;

    private int isPublic;
}
