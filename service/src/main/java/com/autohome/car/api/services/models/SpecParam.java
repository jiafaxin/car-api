package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SpecParam implements Serializable {
    int specid;
    String specname;
    int seriesid;
    String seriesname;//针对主软调用产品库车系名乱码处理
    int brandid;
    String brandname;//品牌名称
    int fctid ;
    String fctname;
    int levelid;
    String levelname;
    int specpicount;
    String speclogo;
    int specminprice;
    int specmaxprice;
    int specengineid;
    String specenginename;
    int specstructuredoor;
    String specstructureseat;
    String specstructuretypename;
    String spectransmission;
    int specstate;
    Double specoiloffical;
    int speclength;
    int specwidth;
    int specheight;
    int specweight;
    String specdrivingmodename;
    int specflowmodeid;
    String specflowmodename;
    double specdisplacement;
    int specenginepower;
    int specparamisshow;
    int specispreferential ;
    int specistaxrelief;
    int specistaxexemption;
    String specquality;
    String specisimport;
    boolean specisbooked;
    String dynamicprice;
    int oilboxvolume;
    int fueltype;
    String fastchargetime;
    String slowchargetime;
    String fastchargePercent;
    String batterycapacity;
    String mile;
    int fueltypedetail;
    String fueltypename;
    String greenstandards;
    String enginetorque;
    String engingkw;
    String qrcode;
    String pricedescription;
    String electricmotorgrosspower;
    String electricmotorgrosstorque;
    String oillabel;
    int wheelbase;
    private BigDecimal officalOil;
    private double ssuo; //specSpeedupOffical
    private int specMaxspeed;
}
