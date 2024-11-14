package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BrandBaseEntity  implements Serializable {

    private int id;
    private String name;
    private String logo;
    private String url;
    private String country;
    private String firstLetter;
    private int countryId;

    private Date createTime;

    private Date editTime;
}
