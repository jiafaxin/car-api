package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConfItemEntity implements Serializable {

    private int typeId;
    private int itemId;
    private int displayType;
    private String name;
    private String specId;
    private int itemValueId;
    private String subItemId;
    private String subValue;
}
