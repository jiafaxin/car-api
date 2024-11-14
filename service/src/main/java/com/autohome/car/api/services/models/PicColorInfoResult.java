package com.autohome.car.api.services.models;

import lombok.Data;

import java.util.List;

@Data
public class PicColorInfoResult {
    int seriesid;
    int classid;
    List<PicColorInfo> coloritems;
}
