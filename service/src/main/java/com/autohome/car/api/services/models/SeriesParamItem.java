package com.autohome.car.api.services.models;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SeriesParamItem extends SeriesConfig {

    private double currentstateminoilwear;
    private double currentstatemaxoilwear;
    private int containtelectriccar;

    public SeriesParamItem() {}

    public SeriesParamItem(SeriesConfig seriesConfig, double currentStateMinOilWear, double currentStateMaxOilWear) {
        BeanUtils.copyProperties(seriesConfig, this);
        this.currentstatemaxoilwear = currentStateMaxOilWear;
        this.currentstateminoilwear = currentStateMinOilWear;
        this.containtelectriccar = super.newenergy;
        super.maxprice = super.getTempMaxPrice();
        super.minprice = super.getTempMinPrice();
        super.state = super.getTempState();
    }


}
