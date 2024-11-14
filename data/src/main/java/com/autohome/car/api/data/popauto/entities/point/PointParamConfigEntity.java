package com.autohome.car.api.data.popauto.entities.point;

import lombok.Data;

import java.io.Serializable;

@Data
public class PointParamConfigEntity implements Serializable {

    private int buId;

    private int pointLocationId;

    private int dataType;

    private int paramConfigId;

    private String paramConfigName;


}
