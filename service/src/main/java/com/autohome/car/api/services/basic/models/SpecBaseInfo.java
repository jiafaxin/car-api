package com.autohome.car.api.services.basic.models;

import com.autohome.car.api.data.popauto.entities.SpecBaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class SpecBaseInfo extends SpecBaseEntity implements Serializable {
    /**
     * 查询表 specconfig name="简称" and item='变速箱'
     */
    String gearBox;
    /**
     * 环境标准，查询表 specconfig name="环境标准"
     */
    String dicEmissionStandards;
    String oilLabe;
    int wheelBase;
    int OilBoxVolume;
    String engingKW;

    private int sYearId;
    private int sYear;
    private String brandFirstLetter;
    private String fctFirstLetter;
    private String seriesFirstLetter;
    private String specQuality;
    private int specState;
    /**
     * 燃油类型
     */
    int fuelType;
    int fuelTypeDetail;
    double displacement;
    String pricedescription;
    double electroTotalKW;
    String specLogoImg;
    //车型是否有保养
    int isHaveMaintains;
    String pngLogo;
    int specIsImage;
    int specOrdercls;
    private String speedUrl;

    private String sic;//specInnerColor列表

    private String sc ; //specColor列表

    private int horsepower;
    private int flowMode;

    private String innerColorIds;

    private String innerColorPrices;

    private String innerColorRemarks;

    private int opn; //officialpicisnew
    int isclassic;

    private int brandId;

    private int driveForm;

    private String specDrivingMode;

    private String seats;

    public int getSpecOrder(){
        if(getSpecState()>=20 && getSpecState() <= 30)
            return 9999;
        if(getSpecState()<=10)
            return 8888;
        return getSYear();
    }

    public int getSpecOrderForConfig(){
        if(getSpecState()==20 )
            return 1;
        if(getSpecState()==30)
            return 2;
        return 3;
    }

}
