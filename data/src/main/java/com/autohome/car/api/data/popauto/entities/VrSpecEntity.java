package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class VrSpecEntity implements Serializable {

    private int seriesId;

    private int specId;

    private String coverUrl16_9;

    private String coverUrl4_3;

    private String panoUrl;

    private String coverUrl;
}
