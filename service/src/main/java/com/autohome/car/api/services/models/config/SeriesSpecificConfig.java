package com.autohome.car.api.services.models.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesSpecificConfig extends SpecificConfig implements Serializable {
    private int seriesid;
}
