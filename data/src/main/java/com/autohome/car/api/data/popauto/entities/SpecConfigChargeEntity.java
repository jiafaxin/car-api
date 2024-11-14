package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecConfigChargeEntity implements Serializable {

    /**
     * 系列id seriesId
     */
    private int id;
    /**
     * specId
     */
    private int cid;
    /**
     * 充电时间（快充和慢充）
     */
    private String ct;

    /**
     * 名称：快充时间 和 慢充时间
     * name
     */
    private String nm;

}
