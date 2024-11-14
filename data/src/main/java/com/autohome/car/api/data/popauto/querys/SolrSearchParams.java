package com.autohome.car.api.data.popauto.querys;

import lombok.Data;
import java.util.List;

@Data
public class SolrSearchParams {
    // 价格
    private List<String> listPrice;
    // 级别
    private List<Integer> listLevel;
    // 品牌
    private List<Integer> listBrand;
    // 结构
    private List<Integer> listStruct;
    // 排量
    private List<String> listDcap;
    // 变速箱
    private List<Integer> listGearBox;
    // 国别
    private List<Integer> listCountry;
    // 生产方式
    private List<Integer> listIsImport;
    // 座位
    private List<Integer> listSeat;
    // 燃料形式能源
    private List<Integer> listFuelType;
    // 进气形式
    private List<Integer> listFlowMode;
    // 驱动方式
    private List<Integer> listDriveType;
    // 配置
    private List<Integer> listConfig;
    // 续航里程
    private List<String> listMileage;
    // 排序
    private int sortType;
    // 车系
    private int seriesId;

}
