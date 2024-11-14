package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecBaseInfoItem implements Serializable {

    private int id;
    private int brandid;
    private String brandname;
    private int fctid;
    private String fctname;
    private int seriesid;
    private String seriesname;
    private int levelid;
    private String levelname;
    private int specid;
    private String specname;
    private int specisstop;
    private String specimg;
    private String minprice;
    private String maxprice;
    private String url;
    private int ispevcar;
    List<SpecBaseInfoImgItem> imglist;
    private int classpicnum;

}
