package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandPicListEntity implements Serializable {

    private int brandId;

    private String brandName;

    private String firstLetter;

    private int brandCount;

    private int brandOrder;

    private String img;
}
