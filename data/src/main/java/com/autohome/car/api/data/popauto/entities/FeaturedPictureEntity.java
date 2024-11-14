package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FeaturedPictureEntity implements Serializable {

    private int id;

    private String st;//

    private String lt;//longTitle

    private int typeId;

    private Date pt;//publishTime

    private String tl;//threadLink

    private String iu;//imageUrl

    private String mst;//mShortTitle

}
