package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpecConfigPriceEntity {
    int specId;
    int itemId;
    int subItemId;
    int price;
}
