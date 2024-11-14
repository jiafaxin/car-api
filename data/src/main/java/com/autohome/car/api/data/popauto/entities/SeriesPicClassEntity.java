package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesPicClassEntity implements Serializable {
    int picId;
    int specId;
    String picfilepath;
    int picClass;
    int isHD;
    int syearId;
    int syear;
    int specState;
    int rn;
}
