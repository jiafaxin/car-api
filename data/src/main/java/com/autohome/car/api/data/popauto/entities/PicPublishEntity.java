package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PicPublishEntity implements Serializable {

    private int picId;

    private int specId;

    private String specName;

    private int typeId;

    private String filePath;

    private int isHD;

    private int seriesId;

    private String seriesName;

    private int colorId;

    private int sYearId;

    private int sYear;

    private int specState;

    private Date publishTime;

    private int fctId;

    private String fctName;

    private int brandId;

    private String brandName;
}
