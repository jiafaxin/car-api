package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecYearEntity implements Serializable {
    /**
     * specId
     */
    private int id;

    /**
     * SyearId
     */
    private int yId;

    /**
     * specState
     */
    private int state;

    /**
     * specIsImage
     */
    private int sImage;

    /**
     * rIndex
     */
    private int rInd;

    /**
     * syear
     */
    private int syear;
}
