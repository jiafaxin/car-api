package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpecViewAndPicEntity {
    int specId;
    int syearId;
    int seriesId;
    int specState;
    int minPrice;
    int maxPrice;
    String specStructureType;
    int levelId;
    String specTransmissionType;
    BigDecimal specDisplacement;
    int flowMode;
    int picNumber;
    int seriesIsShow;
    int state;
    int specPrice;


}
