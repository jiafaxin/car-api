package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecSeriesYearEntity extends SpecYearEntity implements Serializable {
    /**
     * specId
     */
    private int specId;

}
