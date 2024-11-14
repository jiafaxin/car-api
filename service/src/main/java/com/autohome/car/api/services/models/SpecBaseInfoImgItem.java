package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpecBaseInfoImgItem implements Serializable {
    private int picid;
    private String picurl;
}
