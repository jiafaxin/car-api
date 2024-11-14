package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarManueBaseEntity implements Serializable {

    private  int brandId;
    private String firstLetter;
    private int brandCount;
}
