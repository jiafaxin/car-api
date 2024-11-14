package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class Car25PictureViewEntity {
    int id;
    int specId;
    String picPath;
    int picId;
    int ordercls;
    int topId;
    String remark;

    private String itemName;

    private String specName;
}
