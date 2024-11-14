package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesStateBrandEntity implements Serializable {
    int brandId;
    String brandName;
    String bFirstLetter;
    String logo;
    int isCV;
    int specState;
    int specIsImage;
    int rankIndex;
}
