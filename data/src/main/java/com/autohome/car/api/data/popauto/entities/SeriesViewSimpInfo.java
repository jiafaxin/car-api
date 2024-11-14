package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesViewSimpInfo implements Serializable {

    private int seriesId;

    private int seriesState;
}
