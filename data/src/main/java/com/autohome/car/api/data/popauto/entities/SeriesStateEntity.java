package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesStateEntity implements Serializable {

    private int seriesId;

    private int fctId;

    private int seriesFctMinPrice;

    private int seriesFctMaxPrice;

    private int rowIndex;
}
