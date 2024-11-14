package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ElectricSpecViewEntity implements Serializable {

    private int fctId;

    private int seriesId;

    private int seriesOrderCls;

    private int fuelType;

    private int specIsImage;

    private int specState;

}
