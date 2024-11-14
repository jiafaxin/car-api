package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FactoryBaseEntity  implements Serializable {

    int id;
    String name;
    private String url;
    private String logo;
    private String firstletter;
    private String isimport;

    private Date createTime;

    private Date editTime;

}
