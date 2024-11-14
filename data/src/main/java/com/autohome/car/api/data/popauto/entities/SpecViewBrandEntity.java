package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecViewBrandEntity implements Serializable {

    private int brandId;

    private int fueltypedetail;

    private int specIspublic;

    private int endurancemileage;

    private double officialFastChargetime;
}
