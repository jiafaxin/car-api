package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandNameItem implements Serializable {

    private int brandid;

    private String brandname;
}
