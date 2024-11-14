package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesHotEntity implements Serializable {
    private int seriesId;
    private int seriesPriceMin;
    private int seriesPriceMax;
    private int seriesIspublic;
}
