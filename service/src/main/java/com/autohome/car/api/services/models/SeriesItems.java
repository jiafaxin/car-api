package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SeriesItems implements Serializable {

    private int rowcount;
    private List<SeriesItem> list;
}
