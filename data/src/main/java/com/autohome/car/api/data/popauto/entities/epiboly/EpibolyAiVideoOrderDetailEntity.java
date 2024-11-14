package com.autohome.car.api.data.popauto.entities.epiboly;

import lombok.Data;

import java.io.Serializable;

@Data
public class EpibolyAiVideoOrderDetailEntity implements Serializable {

    private int orderId;

    private String sourceId;

    private int status;

    private int pointId;

    private String pointName;
}
