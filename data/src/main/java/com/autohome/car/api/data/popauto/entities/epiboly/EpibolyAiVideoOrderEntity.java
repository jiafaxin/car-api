package com.autohome.car.api.data.popauto.entities.epiboly;

import lombok.Data;

import java.io.Serializable;

@Data
public class EpibolyAiVideoOrderEntity implements Serializable {

    private int orderId;

    private int orderStatus;

    private int taskId;

    private int brandId;

    private int seriesId;

    private int specId;

    private int taskStatus;
}
