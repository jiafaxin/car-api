package com.autohome.car.api.data.popauto.entities;


import lombok.Data;

import java.io.Serializable;

@Data
public class EleSpecViewBaseEntity implements Serializable {

    private int seriesId;
    private String horsepower;
    private String mileage;

    private int specState;

    private int fuelType;
}
