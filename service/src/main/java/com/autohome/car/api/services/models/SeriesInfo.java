package com.autohome.car.api.services.models;

import com.autohome.car.api.data.popauto.entities.SpecColorEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.stream.Stream;

@Data
public class SeriesInfo implements Serializable {
    int seriesid;
    String seriesname;
    String serieslogo;//车系代表图
    String rawSeriesLogo; //车系图原地址
    String seriesofficialurl;//车系官网
    String seriesfirstletter;//车系首字母
    String seriesplace;//合资、自主、进口
    int countryid;//国别
    String countryname;//国别id
    int brandid;//品牌id
    String brandname;//品牌名称
    String brandlogo;//品牌代表图
    String brandofficialurl;//品牌官网
    String brandfirstletter;//品牌首字母
    int fctid;//厂商id
    String fctname;//厂商名称
    String fctlogo;//厂商代表图
    String fctofficialurl;//厂商官网
    String fctfirstletter;//厂商首字母
    int levelid;//级别id
    String levelname;//级别名称
    String containelectriccar;//是否包含是电动车  20200202改业务为是否新能源车系。
    String qrcode;
}
