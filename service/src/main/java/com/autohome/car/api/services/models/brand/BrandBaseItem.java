package com.autohome.car.api.services.models.brand;

import lombok.Data;

import java.io.Serializable;
@Data
public class BrandBaseItem implements Serializable {

    private int brandid;

    private String brandname;

    private String brandofficialurl;

    private String country;

    private String brandfirstletter;

    private String brandlogo;

}
