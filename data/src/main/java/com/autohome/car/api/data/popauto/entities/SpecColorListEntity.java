package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecColorListEntity implements Serializable {
    int seriesId;
    int specId;
    int colorId;
    int picNumber;
    int clubPicNumber;
    int price;
    String remarks;
}
