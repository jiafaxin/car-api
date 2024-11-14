package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CarPhotoViewEntity implements Serializable {
    int id;
    int specId;
    int seriesId;
    int picClass;
    String picFilePath;
    int picId;
    int picColorId;
    int isHD;
    int isTitle;
    int specState;
    int syearId;
    int syear;
    int specPicNumber;
    int innerColorId;
    int stateOrder;
    int isClubPhoto;
    BigDecimal classOrder;
    int isclassic;
    int dealerPicOrder;
    int sourceTypeOrder;
    int specPicUploadTimeOrder;
    int width;
    int height;
    int dealerid;
    int pointlocatinid;
    int isWallPaper;
    int optional;
    int showId;

    String typename;
    String colorname;
    String specname;
    String showname;
    String seriesName;
    String innerColorName;
    //外观和细节合并重新计算排序字段
    int newPicOrder;
}
