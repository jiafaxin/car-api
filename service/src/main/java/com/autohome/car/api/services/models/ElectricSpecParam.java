package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ElectricSpecParam implements Serializable {
    private int seriesid;
    private String seriesname;
    private List<SpecMainItem> specitems;
}
