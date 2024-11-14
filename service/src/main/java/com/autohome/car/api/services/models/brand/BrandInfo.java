package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandInfo implements Serializable {
    private int brandid;
    private String brandname;
    private String brandlogo;
    private String country;
    private String brandofficialurl;
    private String brandfirstletter;

}
