package com.autohome.car.api.data.popauto.entities;

import com.google.type.Decimal;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpecPriceViewEntity implements Serializable {
    int id;
    int specId;
    int syearid;
    int syear;
    int transmissionTypeId;
    int structId;
    int fctMinPrice;
    int fctMaxPrice;
    String deliveryCapacity;
    int seriesId;
    int fctId;
    int horsepower;
    int brandId;
    String gearBox;
    int driveForm;
    int driveType;
    int flowMode;
    int fuelType;
    int fueltypedetail;
    int specState;
    int isClassic;
    int electrictype;
    double electricKW;
    int endurancemileage;
}
