package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class LevelSeriesEntity implements Serializable {

    private int seriesId;
    private int seriesState;
    private int levelId;
    private int bbsIsShow;
    private int userDcarState;
    private int brandId;
    private String brandFirstLetter;
    private int fctId;
    private double seriesPriceMin;
    private double seriesPriceMax;
    private int gearboxManual;
    private int gearboxAuto;
    private int countryId;
    private double minDisplacement;
    private double maxDisplacement;
    private String seriesPlace;
    private int qianQu;
    private int houQu;
    private int siQu;
    private int qiYou;
    private int chaiYou;
    private int youDianHunHe;
    private int dianDong;
    private int chaDianHunDong;
    private int zengcheng;
    private int qingranliao;
    private int qinghunsiba;
    private int qinghunersi;
    private int liangXiang;
    private int sanXiang;
    private int xianBei;
    private int lvXing;
    private int yingDingChangPeng;
    private int ruanDingChangPeng;
    private int yingDingPaoChe;
    private int keChe;
    private int huoChe;
    private int pika;
    private int mpv;
    private int suv;
    private int seat2;
    private int seat4;
    private int seat5;
    private int seat6;
    private int seat7;
    private int seat8;
    private double seriesRank;
    private int seriesIsImgSpec;
    private String config1;
    private String config2;
    private String config3;
    private String config4;
    private String config5;
    private String config6;
    private String config7;
    private String config8;
    private String config9;
    private String config10;
    private String config11;
    private String config12;
    private String config13;
    private String config14;
    private String config15;
    private String config16;
    private String config17;
    private String config18;
    private String config19;
    private String config20;
    private String config21;
    private int newSeriesOrderCls;
    private int isImport;
    private int kuajieSanXiang;
    private int kuajieLiangXiang;
    private int kuajieLvXing;
    private int kuajieSuv;


    public void genLevelSeriesEntity(LevelSeriesEntity other) {
        this.seriesId = other.seriesId;
        this.seriesState = other.seriesState;
        this.levelId = other.levelId;
        this.bbsIsShow = other.bbsIsShow;
        this.userDcarState = other.userDcarState;
        this.brandId = other.brandId;
        this.brandFirstLetter = other.brandFirstLetter;
        this.fctId = other.fctId;
        this.seriesPriceMin = other.seriesPriceMin;
        this.seriesPriceMax = other.seriesPriceMax;
        this.gearboxManual = other.gearboxManual;
        this.gearboxAuto = other.gearboxAuto;
        this.countryId = other.countryId;
        this.minDisplacement = other.minDisplacement;
        this.maxDisplacement = other.maxDisplacement;
        this.seriesPlace = other.seriesPlace;
        this.qianQu = other.qianQu;
        this.houQu = other.houQu;
        this.siQu = other.siQu;
        this.qiYou = other.qiYou;
        this.chaiYou = other.chaiYou;
        this.youDianHunHe = other.youDianHunHe;
        this.dianDong = other.dianDong;
        this.chaDianHunDong = other.chaDianHunDong;

        this.zengcheng = other.zengcheng;
        this.qingranliao = other.qingranliao;
        this.qinghunsiba = other.qinghunsiba;
        this.qinghunersi = other.qinghunersi;

        this.liangXiang = other.liangXiang;
        this.sanXiang = other.sanXiang;
        this.xianBei = other.xianBei;
        this.lvXing = other.lvXing;
        this.yingDingChangPeng = other.yingDingChangPeng;
        this.ruanDingChangPeng = other.ruanDingChangPeng;
        this.yingDingPaoChe = other.yingDingPaoChe;
        this.keChe = other.keChe;
        this.huoChe = other.huoChe;
        this.pika = other.pika;
        this.mpv = other.mpv;
        this.suv = other.suv;
        this.seat2 = other.seat2;
        this.seat4 = other.seat4;
        this.seat5 = other.seat5;
        this.seat6 = other.seat6;
        this.seat7 = other.seat7;
        this.seat8 = other.seat8;
        this.seriesRank = other.seriesRank;
        this.seriesIsImgSpec = other.seriesIsImgSpec;
        this.config1 = other.config1;
        this.config2 = other.config2;
        this.config3 = other.config3;
        this.config4 = other.config4;
        this.config5 = other.config5;
        this.config6 = other.config6;
        this.config7 = other.config7;
        this.config8 = other.config8;
        this.config9 = other.config9;
        this.config10 = other.config10;
        this.config11 = other.config11;
        this.config12 = other.config12;
        this.config13 = other.config13;
        this.config14 = other.config14;
        this.config15 = other.config15;
        this.config16 = other.config16;
        this.config17 = other.config17;
        this.config18 = other.config18;
        this.config19 = other.config19;
        this.config20 = other.config20;
        this.config21 = other.config21;
        this.newSeriesOrderCls = other.newSeriesOrderCls;
        this.isImport = other.isImport;
        this.kuajieSanXiang = other.kuajieSanXiang;
        this.kuajieLiangXiang = other.kuajieLiangXiang;
        this.kuajieLvXing = other.kuajieLvXing;
        this.kuajieSuv = other.kuajieSuv;
    }

}