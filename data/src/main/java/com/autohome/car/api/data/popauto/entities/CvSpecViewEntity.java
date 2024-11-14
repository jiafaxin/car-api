package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class CvSpecViewEntity {
    int specId;
    int syearId;
    int seriesId;
    int specState;
    int minPrice;
    int maxPrice;
    int specStructureType;
    int levelId;
    int specTransmissionType;
    float specDisplacement;
    int flowMode;
    int picNumber;
    int seriesIsShow;
}
