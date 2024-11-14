package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesItem implements Serializable {

    private int seriesid;
    private String name;
    private int brandid;
    private String brandname;
    private String brandlogo;
    private int fctid;
    private String fctname;
    private String level;
    private String pic;
    private String minprice;
    private String maxprice;
    private String seriespnglogo;

}
