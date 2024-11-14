package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandViewEntity implements Serializable {

    private int rankIndex;
    private int brandId;
    private String bFirstLetter;
    private int isCV;
    private int specState;
    private int specIsImage;
}
