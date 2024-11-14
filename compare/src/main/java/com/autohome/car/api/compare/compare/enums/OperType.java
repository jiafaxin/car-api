package com.autohome.car.api.compare.compare.enums;

import lombok.Getter;

@Getter
public enum OperType {

    /**
     * 厂商(0-10)
     */
    FCT_ID("fctid", "根据厂商id查询"),

    /**
     * 车系（11-20）
     */
    SERIES_ID("seriesid", "根据系列id查询"),
    SERIES_ID_List("seriesidlist", "根据系列id列表查询"),


    SERIES_List("speclist", "根据系列id列表查询"),

    /**
     * 车型（30-40）
     */
    SPEC_ID("specid","根据specId查询"),
    SPEC_ID_List("specidlist","根据specIdList查询"),

    /**
     * 品牌（41-50）
     */
    BRAND_ID("brandid","根据brandid查询"),

    ID("id","根据brandid查询"),

    BRAND_ID_LIST("brandlist", "根据brandlist查询"),

    /**
     * yearId(51-60)
     */
    YEAR_ID("yearid", "根据yearId查询"),

    NO_PARAM("", "没有参数，查询所有"),

    /**
     * color相关
     */
    COLOR_LIST("colorList", "根据color等字段查询"),

    SHOW_ID("showid", "车展id查询"),

    STATE("state", "状态"),


    ;
    private String field;

    private String describe;

    OperType(String field, String describe){
        this.field = field;
        this.describe = describe;
    }
}
