package com.autohome.car.api.services.models;

import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.entities.SeriesPictureEntity;
import com.autohome.car.api.data.popauto.entities.SpecColorEntity;
import com.autohome.car.api.services.basic.models.SpecColorInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SeriesConfig  implements Serializable {
    int id;//车系id
    int seriesRelationSeriesId;//关联车系id
    String name;//车系名称
    int maxprice;//指导价高价
    int minprice;//指导价低价
    int state;//状态
    String seriesOfficialUrl;//车系官网
    int fctid;//厂商Id
    String fctname;//厂商名称
    int brandid;//品牌id
    String brandname;//品牌名称
    List<String> structitems;//车身结构
    List<String> transmissionitems;//变速箱
    List<String> displacementitems;//排气量
    int levelid;//级别名称
    String levelname;//级别
    List<String> picitems;//图片列表
    List<SeriesLogo> picinfoitems;// 图片详细列表
    int specnum;//车型数量
    int sellspecnum;//在售车型数量
    int stopspecnum;//停售车型数量
    int waitspecnum;//待售车型数量
    int picnum;//图片数量
    int minfuelconsumption;//最小油耗
    int maxfuelconsumption;//最大油耗
    int isshow;//车系频道是否外显
    int paramisshow; //是否有车型参数外显
    int existmaintain;//车系是否有保养信息
    int showelectricparam;//是否显示电动信息
    List<Integer> electricmotormileage;//电动机续航里程
    List<Double> electricmotorkw;//电动机功率
    String electricchargetime;//电动机充电时间
    List<Double> electricrongliang;
    String createtime;//车系创建时间
    int containbookedspec;//车系是否包含预定车型。结果车系状态判断，待售车系如果包含，价格文案为：接受预定，否则为：预售价
    BigDecimal minoilwear;//最小油耗
    BigDecimal maxoilwear;//最大油耗
    int containstopspec;//是否包含停售车型 (可说明车系状为0，但是有停售车型的情况。（产品库中车系只有0和40状态车型时车系状态定义为0）);
    int newenergy;//是否新能源车系
    String logo;
    String pnglogo;//车系透明代表图，切白底图用
    String pricedescription;//车系指导价格说明
    String seriesplace;//合资 自主 独资 进口
    int picallnum; //车系全部图片数量
    private int tempMaxPrice;//指导价高价
    private int tempMinPrice;//指导价低价
    private int tempState;
    private List<SpecColorInfo> colorList;

    String seriesConfigFilePath;
    int bbsShow; //论坛显示
    int levelRank; //关注度排名

    //参数外显新字段，新逻辑
    private int paramnewisshow;


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
