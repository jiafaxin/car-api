package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CrashCnCapSeriesEntity implements Serializable {
    private int id;

    private int seriesid;

    private String compscore;

    private String starscore;

}
