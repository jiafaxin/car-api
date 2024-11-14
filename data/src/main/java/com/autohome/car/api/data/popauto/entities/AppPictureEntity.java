package com.autohome.car.api.data.popauto.entities;

import lombok.Data;

import java.util.Date;

@Data
public class AppPictureEntity {
    int id;
    int brandId;
    int seriesId;
    int specId;
    String title;
    Date publishTime;
    Date edittime;
    int looptype;
    String bigImg;
    int displayType;
    int picTypeId;
    String  picpath;
    int picId;
    int bigimgtype;

    public AppPictureEntity groupBy(){
        AppPictureEntity result = new AppPictureEntity();
        result.setBrandId(getBrandId());
        result.setSeriesId(getSeriesId());
        result.setSpecId(getSpecId());
        result.setLooptype(getLooptype());
        result.setBigImg(getBigImg());
        result.setDisplayType(getDisplayType());
        result.setBigimgtype(getBigimgtype());
        result.setPicTypeId(getPicTypeId());
        result.setPublishTime(getPublishTime());
        result.setTitle(getTitle());
        return result;
    }

    public AppPictureEntity groupByEdit(){
        AppPictureEntity result = new AppPictureEntity();
        result.setId(getId());
        result.setBrandId(getBrandId());
        result.setSeriesId(getSeriesId());
        result.setSpecId(getSpecId());
        result.setLooptype(getLooptype());
        result.setBigImg(getBigImg());
        result.setDisplayType(getDisplayType());
        result.setBigimgtype(getBigimgtype());
        result.setPublishTime(getPublishTime());
        result.setTitle(getTitle());
        result.setEdittime(getEdittime());
        return result;
    }

}
