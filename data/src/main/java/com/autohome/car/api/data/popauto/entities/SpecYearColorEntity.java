package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecYearColorEntity implements Serializable {
    int syearId;
    int specState;
    int colorId;
    String colorName;
    String colorValue;
    int picNum;
    int clubPicNum;
}
