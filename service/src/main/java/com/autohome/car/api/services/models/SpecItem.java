package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecItem implements Serializable {

    private int id;

    private String name;

    private int minprice;

    private int maxprice;

    private String logo;

    private int yearid;

    private int yearname;

    private int seriesid;

    private String seriesname;

    private String serieslogo;

    private String seriesofficialurl;

    private String seriesfirstletter;

    private int brandid;

    private String brandname;

    private String brandlogo;

    private String brandofficialurl;

    private String brandfirstletter;

    private  int fctid;

    private String fctname;

    private String fctlogo;

    private String fctofficialurl;

    private String fctfirstletter;

    private int levelid;

    private String levelname;

    private String specquality;

    private int state;

    private int paramisshow;

    private String timemarket;

    private String emissionstandards;

    private int specisbooked;

    private String dynamicprice;

    private int fueltypedetail;

}
