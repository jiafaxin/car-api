package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesInfoEntity implements Serializable {

    int rankIndex;
    int brandId;
    int factoryId;
    int seriesId;
    String sFirstLetter;
    int isCV;
    int specState;
    int specIsImage;
    int seriesOrder;
    int seriesstate;

}
