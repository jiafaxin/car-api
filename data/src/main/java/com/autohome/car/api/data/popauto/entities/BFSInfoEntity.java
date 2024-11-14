package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class BFSInfoEntity implements Serializable {

    private int brandId;

    private int fctId;

    private int seriesId;

    private int ssns;//seriesSpecNumSale

}
