package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class PicConfigRelationEntity implements Serializable {
    int userid;
    int itemid;
    int subitemid;
    int picid;
    int specid;
}
