package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class ElectricSpecEntity {
    int seriesid;
    int seriesrank;
    String zhengchezhibao;
    String dianchileixing;
    int mileage;
    String chongdianshijian;
    Double officialFastChargetime;
    Double officialSlowChargetime;
}
