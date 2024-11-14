package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class FactoryInfoEntity implements Serializable {

    int rankIndex;
    int brandId;
    int factoryId;
    String factoryName;
    String fFirstLetter;
    int isCV;
    int specState;
    int specIsImage;

}
