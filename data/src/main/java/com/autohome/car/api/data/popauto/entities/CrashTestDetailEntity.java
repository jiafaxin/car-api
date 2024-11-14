package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

@Data
public class CrashTestDetailEntity {

    private int articleid;
    private int typeid;

    private int itemid;

    private String itemname;

    private String remark;

    private int valuetype;

    private String crashvalue;

}
