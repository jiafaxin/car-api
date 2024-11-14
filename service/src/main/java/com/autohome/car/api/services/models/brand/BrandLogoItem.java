package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandLogoItem implements Serializable {
    private int brandid;
    private String brandlogo;

}
