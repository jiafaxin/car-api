package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesNameInfo implements Serializable {

    private int id;

    private String name;

    private String eName;//englishName


}
