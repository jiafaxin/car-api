package com.autohome.car.api.services.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpecDetailItem implements Serializable {
    private int specid;
    private String specname;
    private int specpicount;
    private String speclogo;
    private int specminprice;
    private int specmaxprice;
    private int specengineid;
    private String specenginename;
    private int specstructuredoor;
    private String specstructureseat;
    private String specstructuretypename;
    private String spectransmission;
    private int specstate;
    private double specoiloffical;
    private int speclength;
    private int specwidth;
    private int specheight;
    private int specweight;
    private String specdrivingmodename;
    private int specflowmodeid;
    private String specflowmodename;
    private double specdisplacement;
    private int specenginepower;
    private int specispreferential;
    private int specistaxrelief;
    private int specistaxexemption;
    private String specquality;
    private int specorder;
    private int specyear;
    private int specparamisshow;
}
