package com.autohome.car.api.services.models;

import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class CarPriceSpecInfo implements Serializable {

    private int id;
    private String name;
    private int minprice;
    private int maxprice;
    private String logo;
    private int yearid;
    private String yearname;
    private String quality;
    private String seat;

    public CarPriceSpecInfo(SpecViewEntity item, SpecBaseInfo specInfo) {
        this.id = item.getSpecId();
        this.name = specInfo.getSpecName();
        this.maxprice = item.getMaxPrice();
        this.minprice = item.getMinPrice();
        this.logo = ImageUtil.getFullImagePath(specInfo.getLogo()); //车型代表图
        this.yearid = item.getSyearId();
        this.yearname = item.getSyear() == 0 ? "" : item.getSyear() + "款";
        this.quality = StringUtils.isBlank(item.getQuality()) ? "" : item.getQuality();
        this.seat = item.getSeat();
    }

}
