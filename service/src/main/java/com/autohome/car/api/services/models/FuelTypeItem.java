package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FuelTypeItem implements Serializable {

    private int seriesid;

    private List<String> fueltyplist;
}
