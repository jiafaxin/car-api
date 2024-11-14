package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class InnerSpecColorPriceRemarkEntity implements Serializable {
    private int specid;
    private String innerColorIds;

    private String innerColorPrices;

    private String innerColorRemarks;
}
