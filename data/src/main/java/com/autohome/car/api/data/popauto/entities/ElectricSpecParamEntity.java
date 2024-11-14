package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ElectricSpecParamEntity implements Serializable {

    private String rongliang;

    private int gonglv;

    private int niuju;

    private int licheng;

    private int specId;

    private int fueltype;


}
