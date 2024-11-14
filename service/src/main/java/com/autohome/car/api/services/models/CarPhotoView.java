package com.autohome.car.api.services.models;

import lombok.Data;

@Data
public class CarPhotoView {
    int id;
    String filepath;
    int ishd;
    int specid;
    String specname;
    int typeid;
    String typename;
    int colorid;
    String colorname;
    int memberid;
    String membername;
    int width;
    int height;
    int dealerid;
    int sixtypicsortid;
    int iswallpaper;
    int optional;
    int showid;
    String showname;

    int yearId;
    String yearName;
    int seriesId;
    String seriesName;
    int fctId;
    String fctName;
    int brandId;
    String brandName;
    int specState;
}
