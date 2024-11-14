package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ParamConfigModelDetailEntity implements Serializable {

    /**
     * paramconfigid
     */
    private int pCId;

    /**
     * paramconfigname
     */
    private String pcn;
}
