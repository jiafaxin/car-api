package com.autohome.car.api.data.popauto.entities;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpecBaseEntity implements Serializable {
    int id;
    String specName;
    int seriesId;
    String logo;
    Date stopTime;
    Integer isSpecParamIsShow;
    Integer isPreferential;
    Integer specTaxType;
    Integer isBooked;

    int specMinPrice;
    int specMaxPrice;
    Date timeMarket;
    int isNew;
    int electrickw;
    int specState;
    int isclassic;

    private Date editTime;
}
