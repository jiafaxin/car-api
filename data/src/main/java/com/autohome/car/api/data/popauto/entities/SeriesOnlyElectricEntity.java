package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesOnlyElectricEntity implements Serializable {

    /**
     * seriesId
     */
    private int id;

    /**
     * seriesName
     */
    private String name;

    /**
     * seriesState
     */
    private int state;
}
