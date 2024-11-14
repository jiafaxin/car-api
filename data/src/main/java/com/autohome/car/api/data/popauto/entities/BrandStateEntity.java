package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandStateEntity implements Serializable {

    private int rankIndex;
    private int brandId;
    private int specIsImage;
    private int specState;
    private int isCV;
    private String bFirstLetter;
}
