package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class PicInfoEntity implements Serializable {
    int id;
    int specid;
    String picpath;
    int picid;
    int ordercls;
    int topid;
}
