package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SeriesInfoAllEntity implements Serializable {

    private int id;

    private String name;

    private int fctId;

    private String url;

    private int jb;

    private int brandId;

    private String place;

    private Date editTime;

    private String firstLetter;

    private int seriesState;

    private int ssns;//seriesSpecNumSale

    private int priceMin;

    private int priceMax;

    private String logo;

    private String pngLogo;
}
