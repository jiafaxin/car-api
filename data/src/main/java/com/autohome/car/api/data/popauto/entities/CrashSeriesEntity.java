package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CrashSeriesEntity implements Serializable {

    private int seriesid;

    private int itemid;

    private String crashvalue;
}
