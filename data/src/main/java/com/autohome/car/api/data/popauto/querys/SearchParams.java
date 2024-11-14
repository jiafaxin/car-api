package com.autohome.car.api.data.popauto.querys;

import lombok.Data;

@Data
public class SearchParams {

    private String price;
    private String brand;
    private String fctid;
    private String seriesid;
    private String level;
    //关注度
    private String seriesrank;
    private String country;
    private String isImport;
    //排量
    private String dcap;
    private String horsepower;
    //自主合资进口
    private String place;
    //变速箱细分
    private String gearbox;
    private String fueltypedetail;
    //续航里程
    private String mileage;
    private String batterytype;
    //-燃油标号
    private String fuellabel;
    //-供油方式
    private String oilsupply;
    //进气形式
    private String flowmode;
    //百公里加速
    private String speedup;
    //气缸数
    private String cylindernum;
    private String struct;
    private String seats;
    //能源
    private String energytype;
    //驻车制动类型
    private String braketype;
    private String driveform;
    private String drivetype;
    //4驱形式
    private String fourdrivetype;
    //车体结构
    private String fourdrivebodystruct;
    //中央差速器结构
    private String centraldiffstruct;
    //环保标准
    private String greenstandards;
    private String mainconfig;
    private String subconfig;
    //车型标签
    private String autotag;
    //配置
    private String config;
    //排序类型：
    // 1：热度升序（越小越靠前）
    // 2：价格升序
    // 3：价格降序
    // 4：销量降序
    // 5：续航久  Endurancemileage
    // 6：油耗低

    private Integer sorttype;
    private Integer orderid;
    //起始索引
    private Integer pageindex;
    //取多少个
    private Integer pagesize;

    private String specstate;
    /**
     * 发动机布局：1：2：
     */
    private String enginelayout;

    /**
     * 车身长度 （区间值）
     */
    private String carlength;
    /**
     * 快充时间
     */
    private String fastchargetime;
    //电池能量(kWh)
    private String batterycapacity;

}
