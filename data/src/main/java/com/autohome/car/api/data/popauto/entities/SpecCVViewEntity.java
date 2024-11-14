package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class SpecCVViewEntity {

    private int specId;
    //private String specName;
    //private int minPrice;
    //private int maxPrice;
    private int sYearId;
    private int sYear;
    private String brandFirstLetter;
    private String fctFirstLetter;
    //private int seriesId;
    private String seriesFirstLetter;
    private String specQuality;
    private int specState;
    int specIsImage;
    int specOrdercls;

    private int brandId;

    private String specDrivingMode;

    private int driveForm;

    private String seats;

}
