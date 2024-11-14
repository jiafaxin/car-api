package com.autohome.car.api.services.models;

import autohome.rpc.car.car_api.v1.spec.GetSpecDetailBySpecListResponse;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;


/**
 * 根据车系id获取车型详细信息
 */
@Data
public class SpecDetail implements Serializable {
    private int id;
    private String name;
    private String logo;
    private int syearid;
    private int year;
    private int minprice;
    private int maxprice;
    private String transmission;
    private String gearbox;
    private int state;
    private String drivingmodename;
    private int flowmodeid;
    private String flowmodename;
    private double displacement;
    private int enginepower;
    private int ispreferential;
    private int istaxrelief;
    private int istaxexemption;
    private int order;
    private int specisimage;
    private int paramisshow;
    private int isclassic;
    private String structtype;
    private String fueltype;
    private int fueltypeid;
    private int electrictype;
    private double electrickw;
    private int isnewcar;
    private String emissionstandards;
    private String seat;

    public static autohome.rpc.car.car_api.v1.spec.GetSpecDetailBySeriesIdV1Response.SpecDetail buildSpecDetail(SpecViewEntity item, SpecBaseInfo specInfo, boolean isCV) {
        if (Objects.isNull(item) || Objects.isNull(specInfo)) {
            return null;
        }
        String emissionStandards = specInfo.getDicEmissionStandards();
        String structtype = isCV ? Spec.carBodyStruct(item.getStructType()) : item.getSpecStructureType() == null ? "" : item.getSpecStructureType();
        String drivingmoden = isCV ? Spec.DriveMode(item.getDriveForm()) : item.getSpecDrivingMode() == null ? "" : item.getSpecDrivingMode();
        int specIsImage = isCV ? 0 : item.getSpecIsImage();
        int isclassic = isCV ? 0 : item.getIsclassic();
        int fuletypeId = isCV ? item.getFuelType() : item.getFuelTypeDetail();
        return autohome.rpc.car.car_api.v1.spec.GetSpecDetailBySeriesIdV1Response.SpecDetail.newBuilder().
                setEmissionstandards(emissionStandards == null ? "" : emissionStandards).
                setStructtype(structtype).
                setDrivingmodename(drivingmoden).
                setDrivingmodename(drivingmoden).
                setSpecisimage(specIsImage).
                setIsclassic(isclassic).
                setFueltypeid(fuletypeId).
                setId(item.getSpecId()).//车型id
                setName(StringUtils.defaultString(specInfo.getSpecName(), "")).//车型名称
                setLogo(ImageUtil.getFullImagePath(specInfo.getLogo())). //车型代表图
                setSyearid(item.getSyearId()).//年贷款id
                setYear(item.getSyear()).//年代款---------有问题----------
                setMinprice(item.getMinPrice()).//minprice:车型指导价(低价)
                setMaxprice(item.getMaxPrice()).//maxprice:车型指导价(高价)
                setTransmission(StringUtils.defaultString(specInfo.getGearBox(), "")).//变速箱
                setGearbox(Spec.carGearbox(specInfo.getGearBox())).//gearbox:变速箱类型手动自动
                setState(item.getSpecState()).//state:车型状态
                setFlowmodeid(item.getFlowMode()).//进气形式id
                setFlowmodename(Spec.AdmissionMethod(item.getFlowMode())).//进气形式名称
                setDisplacement(item.getSpecDisplacement()).//排气量
                setEnginepower(item.getSpecEnginePower()).//马力
                setIspreferential(specInfo.getIsPreferential()).//是否惠民
                setIstaxrelief(specInfo.getSpecTaxType() == 1 ? 1 : 0).//是否减税
                setIstaxexemption(specInfo.getSpecTaxType() == 2 ? 1 : 0).//是否免税
                setOrder(item.getSpecOrdercls()).//车型排序
                setParamisshow((item.getSpecState() == 40 || item.getSpecState() >= 10 && specInfo.getIsSpecParamIsShow() == 1) ? 1 : 0).//是否参数外显
                setFueltype(CommonFunction.carFuel(fuletypeId)).//燃料形式
                setIsnewcar(specInfo.getIsNew()).//spectsate=20 上市30天内
                setSeat(String.valueOf(item.getSeat())).build();
    }

//    public SpecDetail(String emissionStandards, String structtype, String drivingmoden, int specIsImage, int isclassic, int fuletypeId, SpecViewEntity item, SpecBaseInfo specInfo) {
//        this.emissionstandards = emissionStandards == null ? "" : emissionStandards;
//        this.structtype = structtype;
//        this.drivingmodename = drivingmoden;
//        this.specisimage = specIsImage;
//        this.isclassic = isclassic;
//        this.fueltypeid = fuletypeId;
//        init(item, specInfo);
//    }
//
//    public void init(SpecViewEntity item, SpecBaseInfo specInfo) {
//        this.id = item.getSpecId();//车型id
//        this.name = StringUtils.defaultString(specInfo.getSpecName(), "");//车型名称
//        this.logo = ImageUtil.getFullImagePath(specInfo.getLogo()); //车型代表图
//        this.syearid = item.getSyearId();//年贷款id
//        this.year = item.getSyear();//年代款---------有问题----------
//        this.minprice = item.getMinPrice();//minprice:车型指导价(低价)
//        this.maxprice = item.getMaxPrice();//maxprice:车型指导价(高价)
//        this.transmission = StringUtils.defaultString(specInfo.getGearBox(), "");//变速箱
//        this.gearbox = Spec.carGearbox(specInfo.getGearBox());//gearbox:变速箱类型手动自动
//        this.state = item.getSpecState();//state:车型状态
//        this.flowmodeid = item.getFlowMode();//进气形式id
//        this.flowmodename = Spec.AdmissionMethod(item.getFlowMode());//进气形式名称
//        this.displacement = item.getSpecDisplacement();//排气量
//        this.enginepower = item.getSpecEnginePower();//马力
//        this.ispreferential = specInfo.getIsPreferential();//是否惠民
//        this.istaxrelief = specInfo.getSpecTaxType() == 1 ? 1 : 0;//是否减税
//        this.istaxexemption = specInfo.getSpecTaxType() == 2 ? 1 : 0;//是否免税
//        this.order = item.getSpecOrdercls();//车型排序
//        this.paramisshow = (item.getSpecState() == 40 || item.getSpecState() >= 10 && specInfo.getIsSpecParamIsShow() == 1) ? 1 : 0;//是否参数外显
//        this.fueltype = CommonFunction.carFuel(this.getFueltypeid());//燃料形式
//        this.isnewcar = specInfo.getIsNew();//spectsate=20 上市30天内
//        this.seat = String.valueOf(item.getSeat());
//    }
}
