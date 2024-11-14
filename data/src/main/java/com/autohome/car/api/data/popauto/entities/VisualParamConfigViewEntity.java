package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class VisualParamConfigViewEntity implements Serializable {

    private int seriesId;

    private int specId;

    private int dataType;

    private int itemId;

    private String itemName;

    private String value;

    private int subId;

    private String subName;

    private int picId;

    private String picUrl;

    private int specState;
}
