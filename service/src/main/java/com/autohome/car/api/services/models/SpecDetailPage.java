package com.autohome.car.api.services.models;

import lombok.Data;

import java.util.List;

/**
 * 根据车系id获取车型详细信息
 */
@Data
public class SpecDetailPage {
    private int seriesid;
    private int total;
    private List<SpecDetail> specitems;
}
