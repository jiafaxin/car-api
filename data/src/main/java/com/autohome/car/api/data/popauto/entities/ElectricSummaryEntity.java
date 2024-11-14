package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class ElectricSummaryEntity {
    int syearId;
    int seriesId;
    int specId;
    int specPrice;
    String seriesName;
    String levelName;
    String electricKW;
    String electricMotorMileage;
    int endurancemileage;
    int specState;
}
