package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class CarManuePriceEntity {
    int id;
    int brandId;
    String brandName;
    int seriesId;
    String seriesName;
}
