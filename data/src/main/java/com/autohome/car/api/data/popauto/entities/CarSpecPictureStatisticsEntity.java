package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarSpecPictureStatisticsEntity implements Serializable {
    int specId;
    int specYear;
    int carState;
    int picNumber;
}
