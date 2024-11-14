package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class PicPointLocationEntity implements Serializable {
    int specId;
    int picid;
    String picurl;
    int pointlocatinid;
    int pointlocationseteditor;
}
