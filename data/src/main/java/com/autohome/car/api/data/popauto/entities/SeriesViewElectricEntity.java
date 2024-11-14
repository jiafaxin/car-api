package com.autohome.car.api.data.popauto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class SeriesViewElectricEntity implements Serializable {

    /**
     * 品牌id brandId
     */
    private int brandId;
    /**
     * 品牌首字母  brandFirstLetter
     */
    private String brandFirstLetter;
    /**
     * 系列id seriesId
     */
    private int seriesId;
    /**
     * 系列状态 seriesState
     */
    private int seriesState;
    /**
     * orderSeriesState
     */
    private int orderSeriesState;
    /**
     * seriesOrderCls
     */
    private int seriesOrderCls;
    /**
     * fuelType
     */
    private int fuelType;

    /**
     * scoresRanks
     */
    private String scoresRanks;

}
