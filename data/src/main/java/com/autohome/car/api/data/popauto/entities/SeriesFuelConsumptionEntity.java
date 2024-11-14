package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeriesFuelConsumptionEntity {
    int seriesId;
    BigDecimal maxFuelConsumption;
    BigDecimal minFuelConsumption;
}
