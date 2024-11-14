package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ElectricSpecBaseEntity implements Serializable {

    private int fuelType;

    private int liCheng;

    private int seriesId;
}
