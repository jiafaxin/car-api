package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecDetailItems implements Serializable {
    private int seriesid;
    private String seriesname;
    private int brandid;
    private String brandname;
    private int fctid;
    private String fctname;
    private int levelid;
    private String levelname;
    private String isimport;
    private int total;
    private List<SpecDetailItem> items;
}
