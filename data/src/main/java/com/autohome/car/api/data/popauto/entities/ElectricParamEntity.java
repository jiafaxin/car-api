package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class ElectricParamEntity {
    int syearId;
    int seriesId;
    int specId;
    String chargeTime;
    String electricKW;
    String electricRONGLIANG;
    String electricMotorMileage;
    int specState;
    int state;
    int pureelectric;
}
