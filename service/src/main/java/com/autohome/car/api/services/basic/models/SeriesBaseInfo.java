package com.autohome.car.api.services.basic.models;

import com.autohome.car.api.data.popauto.entities.SeriesBaseInfoEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SeriesBaseInfo extends SeriesBaseInfoEntity implements Serializable {
    int em; //existmaintain
    int cb;  //containBookedSpec
    int cs;  //containstopspec
    private int spm;//seriesPhotoNum
    private String pp; //photoPath;
    private int fc;//isforeigncar
    int bbsShow;
    int levelRank;
    String brandName;
    String pricedescription;

    private int isVr;

    private int seriesSpecNum;

    private int newSeriesOrderCls;

    private int seriesIsPublic;
    int showCount;
    int seriesRank;

    private List<Integer> tag;
    int opin;//officialPicIsNew

    int ws;//SpecPriceWaitSellView

    int si;//SpecPriceSellView

    private String stopLogo;//

    private int ine;//isNewEnergy

    private String fctName;
}
