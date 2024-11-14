package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewEnergyBrandEntity implements Serializable {

    /**
     * brandId
     */
    private int bId;

    /**
     * brandName
     */
    private String bn;

    /**
     *brandFirstLetter
     */
    private String bfl;

    /**
     * brandImg
     */
    private String bi;

    /**
     * havesale
     */
    private int hs;

    /**
     * 国家
     */
    private String country;
}
