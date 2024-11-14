package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ElectricSeriesEntity implements Serializable {

    private int seriesId;

    private String seriesName;

    private int seriesState;

    private double seriesRank;
}
