package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class SpecColorEntity {
    int seriesId;
    int colorId;
    String colorName;
    String colorValue;
    int picNum;
    int clubPicNum;
    int SpecState;
}
