package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class SpecItems implements Serializable {
    private int total;
    private List<SpecItem> specitems;
}
