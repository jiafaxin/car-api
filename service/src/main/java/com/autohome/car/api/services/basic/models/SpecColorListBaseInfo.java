package com.autohome.car.api.services.basic.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecColorListBaseInfo implements Serializable {
    int seriesId;
    int specId;
    int colorId;
    int picNumber;
    int clubPicNumber;
    int price;
    String remarks;
}
