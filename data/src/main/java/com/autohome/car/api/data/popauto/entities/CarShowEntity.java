package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarShowEntity implements Serializable {

    private int id;

    private String picPath;

    private String sPicPath;

    private String fctName;

    private String seriesName;
}
