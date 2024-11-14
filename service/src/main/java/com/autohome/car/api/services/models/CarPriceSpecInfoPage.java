package com.autohome.car.api.services.models;

import lombok.Data;

import java.util.List;

@Data
public class CarPriceSpecInfoPage {
    private int seriesid;
    private int tatal;
    List<CarPriceSpecInfo> specitems;
}
