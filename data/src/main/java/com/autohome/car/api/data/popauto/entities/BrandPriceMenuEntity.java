package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class BrandPriceMenuEntity {
    int brandId;
    String firstLetter;
    int brandCount;
    int orders;
}
