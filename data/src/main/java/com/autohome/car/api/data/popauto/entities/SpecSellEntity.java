package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecSellEntity implements Serializable {

    private int seriesId;

    private int specId;

    private int taxType;
}
