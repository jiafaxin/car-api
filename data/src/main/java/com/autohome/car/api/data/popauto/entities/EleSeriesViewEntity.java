package com.autohome.car.api.data.popauto.entities;


import lombok.Data;

import java.io.Serializable;

@Data
public class EleSeriesViewEntity implements Serializable {

    private int seriesId;
    private int brandId;
    private int seriesState;
    private int seriesOrdercls;
    private int fuelType;
}
