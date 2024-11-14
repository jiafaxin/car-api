package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesBaseItem implements Serializable {

    private int seriesid;

    private String seriesname;

    private String seriesimage;

    private int isvr;

    private int minprice;

    private int maxprice;

    private String seriesplace;

}
