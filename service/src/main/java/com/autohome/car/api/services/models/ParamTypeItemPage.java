package com.autohome.car.api.services.models;

import lombok.Data;

import java.util.List;

@Data
public class ParamTypeItemPage {

    private int seriesid;
    private int specid;
    private List<ParamTypeItems> paramtypeitems;
}
