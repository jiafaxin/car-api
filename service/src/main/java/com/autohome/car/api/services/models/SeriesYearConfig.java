package com.autohome.car.api.services.models;

import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.entities.SeriesPictureEntity;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class SeriesYearConfig implements Serializable {
    int id;//车系id
    int yearid;
    List<String> picitems;//图片列表
    List<SeriesLogo> picinfoitems;// 图片详细列表
    int maxprice;//指导价高价
    int minprice;//指导价低价
    List<String> structitems;//车身结构
    List<String> transmissionitems;//变速箱
    List<String> displacementitems;//排气量
    int specnum;//车型数量
    int picnum;//图片数量
    int isshow;//车系频道是否外显
    int showelectricparam;//是否显示电动信息
    List<Integer> electricmotormileage;//电动机续航里程
    List<Double> electricmotorkw;//电动机功率
    String electricchargetime;//电动机充电时间

    @Data
    public static class SeriesLogo implements Serializable{
        int specid;
        int picid;
        String picpath;
        int specstate;
        public SeriesLogo(){}
        public SeriesLogo(SeriesPictureEntity entity,int specstate){
            setPicid(entity.getPhotoId());
            setSpecid(entity.getSpecid());
            setPicpath(ImageUtil.getFullImagePath(entity.getPhotoFilepath().replace("~","")));
            setSpecstate(specstate);
        }
    }
}
