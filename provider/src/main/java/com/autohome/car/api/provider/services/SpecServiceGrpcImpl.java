package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.spec.*;
import autohome.rpc.car.car_api.v2.spec.GetSpecDetailBySeriesIdRequest;
import autohome.rpc.car.car_api.v1.spec.GetSpecElectricSubsidyBySpecListRequest;
import autohome.rpc.car.car_api.v1.spec.GetSpecElectricSubsidyBySpecListResponse;
import autohome.rpc.car.car_api.v3.spec.ConfigGetListBySpecListResponse;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.SpecMapper;
import com.autohome.car.api.data.popauto.entities.ElectricSpecParamEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigEntity;
import com.autohome.car.api.data.popauto.entities.SpecPriceViewEntity;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.SpecListConfigService;
import com.autohome.car.api.services.SpecService;
import com.autohome.car.api.services.basic.ConfigListService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SeriesSpecBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.specs.SpecConfigService;
import com.autohome.car.api.services.basic.specs.SpecParamService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@DubboService
@RestController
public class SpecServiceGrpcImpl extends DubboSpecServiceTriple.SpecServiceImplBase {

    @Autowired
    SpecService specService;

    @Autowired
    SpecParamService specParamService;

    @Autowired
    SpecListConfigService specListConfigService;

    @Autowired
    SpecConfigService specConfigService;

    @Autowired
    ConfigListService configListService;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SeriesSpecBaseService seriesSpecBaseService;

    @Autowired
    AutoCacheService autoCacheService;

    @Autowired
    SpecMapper specMapper;

    @Override
    @GetMapping({"/v1/carprice/spec_parambyspecid.ashx","/v1/carprice//spec_parambyspecid.ashx"})
    public GetSpecParamBySpecIdResponse getSpecParamBySpecId(GetSpecParamBySpecIdRequest request) {
        if (request.getSpecid() <= 0) {
            return GetSpecParamBySpecIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        SpecParam param = specParamService.get(request.getSpecid());
        GetSpecParamBySpecIdResponse.Result result;
        if (param != null) {
            if (param.getSeriesname() != null && request.getAppid().equals("app") || request.getAppid().equals("app.iphone")) {
                param.setSeriesname(HtmlUtils.decode(param.getSeriesname()));
            }
            if (request.getDispqrcode() != 1) {
                param.setQrcode("");
            }
            result = MessageUtil.toMessage(param, GetSpecParamBySpecIdResponse.Result.class);
        }else{
            result = GetSpecParamBySpecIdResponse.Result.newBuilder().setSpecid(request.getSpecid()).setSpecparamisshow(1).build();
        }
        GetSpecParamBySpecIdResponse.Builder response = GetSpecParamBySpecIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");
        if (result != null) {
            response.setResult(result);
        }

        return response.build();
    }

    /**
     * 根据多个车型id获取相关信息
     * @param request
     * @return
     */
    @Override
    @GetMapping("/v1/carprice/spec_infobyspeclist.ashx")
    public GetSpecInfoBySpecListResponse getSpecInfoBySpecList(GetSpecInfoBySpecListRequest request) {
        ApiResult<SpecItems> apiResult = specService.getSpecInfoBySpecList(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetSpecInfoBySpecListResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetSpecInfoBySpecListResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetSpecInfoBySpecListResponse.Result.class);
        return GetSpecInfoBySpecListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }
    /**
     * 根据车系id获取电车的车型信息
     * @param request
     * @return
     */
    @Override
    @GetMapping("/v1/App/Electric_SpecParamBySeriesId.ashx")
    public GetElectricSpecParamBySeriesIdResponse getElectricSpecParamBySeriesId(GetElectricSpecParamBySeriesIdRequest request) {
        ApiResult<ElectricSpecParam> apiResult = specService.getElectricSpecParamBySeriesId(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetElectricSpecParamBySeriesIdResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetElectricSpecParamBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetElectricSpecParamBySeriesIdResponse.Result.class);
        return GetElectricSpecParamBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    /**
     * 根据车系id获取车型的参数信息
     * @param request
     * @return
     */
    @Override
    @GetMapping("/v1/carprice/spec_parambyseriesId.ashx")
    public GetSpecParamBySeriesIdResponse getSpecParamBySeriesId(GetSpecParamBySeriesIdRequest request) {
        ApiResult<SpecDetailItems> apiResult = specService.getSpecParamBySeriesId(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetSpecParamBySeriesIdResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetSpecParamBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetSpecParamBySeriesIdResponse.Result.class);
        return GetSpecParamBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    @Override
    @GetMapping("/v1/carprice/spec_infobyspecid.ashx")
    public GetSpecInfoBySpecIdResponse getSpecInfoBySpecId(GetSpecInfoBySpecIdRequest request){
        return specService.getSpecInfoBySpecId(request);
    }
    /*
     * 根据车系id获取车型详细信息(v1版本)
     */
    @Override
    @GetMapping("/v1/carprice/spec_detailbyseriesId.ashx")
    public GetSpecDetailBySeriesIdV1Response getSpecDetailBySeriesIdV1(GetSpecDetailBySeriesIdRequest request) {
        return specService.getSpecDetailBySeriesIdV1(request);
    }

    /**
     * 根据多个车型id获取车型代表图(v1版本)
     */
    @Override
    @GetMapping("/v1/carprice/spec_logobyspeclist.ashx")
    public GetSpecLogoBySpecListResponse getSpecLogoBySpecList(GetSpecLogoBySpecListRequest request) {
        ApiResult<SpecLogoPage> apiResult = specService.getSpecLogoBySpecList(request);
        SpecLogoPage specLogoPage = apiResult.getResult();
        GetSpecLogoBySpecListResponse.Builder builder = GetSpecLogoBySpecListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(specLogoPage)) {
            builder.setResult(MessageUtil.toMessage(specLogoPage, GetSpecLogoBySpecListResponse.Result.class));
        }
        return builder.build();
    }

    @Override
    @GetMapping("/v1/carprice/spec_paramsinglebyspecid.ashx")
    public SpecParamSingleBySpecidResponse specParamSingleBySpecid(SpecParamSingleBySpecidRequest request) {
        SpecParamSingleBySpecidResponse.Builder builder = SpecParamSingleBySpecidResponse.newBuilder().setReturnCode(0);
        if( (request.getSpecid() <= 0 ) ){
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        SpecParamSingleBySpecidResponse.Result.Builder resultBuilder = SpecParamSingleBySpecidResponse.Result.newBuilder()
                .setSpecid(request.getSpecid());

        SpecBaseInfo specBaseInfo = specBaseService.get(request.getSpecid()).join();
        if(specBaseInfo==null || specBaseInfo.getIsSpecParamIsShow()!=1){
            return builder.setReturnMsg("成功").setResult(resultBuilder).build();
        }

        String lastItemType = null;//上一次参数类别名称
        String currentItemType = null;//当前参数类别名称
        String currentParamName = null;//当前参数名称

        List<SpecParamSingleBySpecidResponse.Result.Paramtypeitem> paramTypeItems = new ArrayList();//参数类型集合
        List<SpecParamSingleBySpecidResponse.Result.Paramtypeitem.Paramitem> paramItems = new ArrayList();//参数集合

        int iFuelTypeDetail = specBaseInfo.getFuelTypeDetail();

        //插电和油电没有“	系统综合扭矩(N·m)”时  要不要分开展示“发动机最大扭矩(N·m)”和“电动机总扭矩(N·m)”
        //true 时 基本参数大类下 最大功率不显示 分别显示
        boolean reWriteMaxKw = false;
        List<SpecConfigEntity> drMaxKw = null;
        List<SpecParamSingleBySpecidResponse.Result.Paramtypeitem.Paramitem> arrMaxkw = new ArrayList();

        boolean reWriteMaxTorque = false;
        List<SpecConfigEntity> drMaxTorque = null;
        List<SpecParamSingleBySpecidResponse.Result.Paramtypeitem.Paramitem> arrMaxTorque = new ArrayList();

        //是否有插电或油电混合的车型
        boolean fueltyp35Exist = (specBaseInfo.getFuelTypeDetail() == 3 || specBaseInfo.getFuelTypeDetail() == 5);

        List<SpecConfigEntity> dt = specConfigService.get(request.getSpecid());

        if (dt != null && dt.size() > 0){
            //系统综合功率(kW)
            if (fueltyp35Exist){
                reWriteMaxKw = dt.stream().anyMatch(dr -> dr.getName().equals("系统综合功率(kW)") && (dr.getItemValue().equals("") || dr.getItemValue().equals("0")));

                if (reWriteMaxKw){
                    drMaxKw = dt.stream().filter((dr -> ((dr.getItem().equals("发动机") && dr.getName().equals("最大功率(kW)")) || (dr.getItem().equals("电动机") && dr.getName().equals("电动机总功率(kW)"))))).collect(Collectors.toList());
                    for (SpecConfigEntity dr : drMaxKw) {
                        arrMaxkw.add(
                                SpecParamSingleBySpecidResponse.Result.Paramtypeitem.Paramitem.newBuilder()
                                        .setId(dr.getConfigid())
                                        .setName(dr.getName().equals("最大功率(kW)") ? "发动机最大功率(kW)" : dr.getName())
                                        .setValue(CommonFunction.getDefaultParamSign(dr.getItemValue())).build()
                        );
                    }
                }

                //系统综合扭矩(N·m)
                reWriteMaxTorque = dt.stream().anyMatch(dr -> (dr.getName().equals("系统综合扭矩(N·m)") && (dr.getItemValue().equals("") || dr.getItemValue().equals("0"))));
                if (reWriteMaxTorque){
                    drMaxTorque = dt.stream().filter(dr -> (
                            (dr.getItem().equals("发动机") && dr.getName().equals("最大扭矩(N·m)") || (dr.getItem().equals("电动机") && dr.getName().equals("电动机总扭矩(N·m)"))
                    ))).collect(Collectors.toList());

                    for (SpecConfigEntity dr : drMaxTorque)
                    {
                        arrMaxTorque.add(
                                SpecParamSingleBySpecidResponse.Result.Paramtypeitem.Paramitem.newBuilder()
                                        .setId(dr.getConfigid())
                                        .setName(dr.getName().equals("最大扭矩(N·m)") ? "发动机最大扭矩(N·m)" : dr.getName())
                                        .setValue(CommonFunction.getDefaultParamSign(dr.getItemValue())).build()
                        );
                    }
                }
            }
        }

        for (int i = 0, len = dt.size(); i < len; i++){
            SpecConfigEntity dr = dt.get(i);
            currentItemType = dr.getItem();
            //如果纯电动车型不显示发动机信息，其他插电、增程都显示发动机
            if (iFuelTypeDetail == 4 && currentItemType .equals("发动机"))
                continue;
            if (!currentItemType.equals(lastItemType)) {
                if (i > 0){
                    if (i < len - 1){
                        //判断中间有电动机的分类，如果没有电的参数，跳出大类。乘用车和商用车的大类排序不一致。此处理是判断乘用车
                        //汽油、柴油、48v轻混的都不显示电动机大类。
                        if (CommonFunction.OILFUELTYPELIST.contains(iFuelTypeDetail) && currentItemType.equals("电动机")){
                            continue;
                        }
                    }
                    paramTypeItems.add(
                            SpecParamSingleBySpecidResponse.Result.Paramtypeitem.newBuilder()
                                    .setName(lastItemType)
                                    .addAllParamitems(paramItems).build()
                    );
                    paramItems = new ArrayList();
                }
                lastItemType = currentItemType;
            }
            currentParamName = dr.getName();
            int configId = dr.getConfigid();

            //全都没值的项，如果在限制的参数项范围内不外显、解决前台大片空白参数项问题
            if (configId > 0){
                if (CommonFunction.DynamicDisplayParamItems.contains(configId) && CommonFunction.getDefaultParamSign(dr.getItemValue()).equals( "-")){
                    continue;
                }
            }

            if (currentItemType .equals( "基本参数")){
                //只有新能源车型才显示相关基本参数
                if (!Spec.arrNewEnergyFueltype.contains(iFuelTypeDetail) && Spec.listNewEnergyParam.contains(currentParamName)){
                    continue;
                }
                //纯电动不显示内容。
                if (iFuelTypeDetail == 4 && Spec.listNotDisPlayOfPEVCarParam.contains(currentParamName)){
                    continue;
                }
                //燃料形式是油的车型，不显电动机(Ps)这项基本参数
                if (CommonFunction.OILFUELTYPELIST.contains(iFuelTypeDetail) && currentParamName.equals("电动机(Ps)")){
                    continue;
                }
                //最大功率的特殊处理
                if (reWriteMaxKw && dr.getName().equals( "最大功率(kW)") && arrMaxkw != null && arrMaxkw.size() > 0){
                    paramItems.addAll(arrMaxkw);
                    reWriteMaxKw = false;
                    continue;
                }
                //最大扭矩(N·m)的特殊处理
                if (reWriteMaxTorque && dr.getName().equals("最大扭矩(N·m)") && arrMaxTorque != null && arrMaxTorque.size() > 0){
                    paramItems.addAll(arrMaxTorque);
                    reWriteMaxTorque = false;
                    continue;
                }

            }
            else if (currentItemType .equals("车身")){
                //纯电动不显示内容。
                if (iFuelTypeDetail == 4 && Spec.listNotDisPlayOfPEVCarParam.contains(currentParamName)){
                    continue;
                }
            }

            paramItems.add(
                    SpecParamSingleBySpecidResponse.Result.Paramtypeitem.Paramitem.newBuilder()
                            .setId(dr.getConfigid())
                            .setName(currentParamName)
                            .setValue(CommonFunction.getDefaultParamSign(dr.getItemValue())).build()
            );

            //判断末尾有电动机的分类，如果没有电的参数，跳出大类。乘用车和商用车的大类排序不一致。此处理是判断商用车
            //汽油、柴油、48v轻混的都不显示电动机大类。
            if (CommonFunction.OILFUELTYPELIST.contains(iFuelTypeDetail) && currentItemType .equals("电动机")){
                continue;
            }

            if (i == len - 1){
                paramTypeItems.add(
                        SpecParamSingleBySpecidResponse.Result.Paramtypeitem.newBuilder()
                                .setName(lastItemType)
                                .addAllParamitems(paramItems).build()
                );
            }
        }
        resultBuilder.addAllParamtypeitems(paramTypeItems);

        return builder.setReturnMsg("成功").setResult(resultBuilder).build();
    }

    /**
     * 二期开发
     */
    @Override
    @GetMapping("/v1/carprice/spec_paramlistbyspeclist.ashx")
    public GetCarpriceSpecParamListBySpecListResponse getSpecParamListBySpecList(GetSpecLogoBySpecListRequest request) {
        ApiResult<ParamTypeItemPage> apiResult = specService.getCarPriceSpecParamListBySpecListV1(request);
        ParamTypeItemPage result = apiResult.getResult();
        GetCarpriceSpecParamListBySpecListResponse.Builder builder = GetCarpriceSpecParamListBySpecListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(result)) {
            builder.setResult(MessageUtil.toMessage(result, GetCarpriceSpecParamListBySpecListResponse.Result.class));
        }
        return builder.build();
    }

    /**
     * 二期开发
     */
    @Override
    @GetMapping({"/v1/carprice/spec_infobyseriesId.ashx","//v1/carprice/spec_infobyseriesId.ashx"})
    public GetCarPriceSpecInfoResponse getCarPriceSpecInfoBySeriesId(GetSpecDetailBySeriesIdRequest request) {
        return specService.getCarPriceSpecInfoBySeriesId(request);
    }

    /**
     * 二期开发
     * 获取车型的颜色信息
     */
    @Override
    @GetMapping("/v1/carprice/spec_colorbyspecid.ashx")
    public GetSpecColorBySpecIdResponse getSpecColorBySpecId(GetSpecInfoBySpecIdRequest request) {
        ApiResult<SpecColorItemPage> apiResult = specService.getSpecColorBySpecIdV1(request);
        SpecColorItemPage specColorItemPage = apiResult.getResult();
        GetSpecColorBySpecIdResponse.Builder builder = GetSpecColorBySpecIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(specColorItemPage)) {
            builder.setResult(MessageUtil.toMessage(specColorItemPage, GetSpecColorBySpecIdResponse.Result.class));
        }
        return builder.build();
    }

    @GetMapping("/v1/car/Spec_ListOfBookedBySeries.ashx")
    @Override
    public GetSpecListBySeriesResponse getSpecListBySeries(GetElectricSpecParamBySeriesIdRequest request) {
        return specService.getSpecListBySeriesV1(request);
    }

    @Override
    @GetMapping("/v1/CarPic/Spec_PictureCountByCondition.ashx")
    public SpecPictureCountByConditionResponse getSpecPictureCountByCondition(SpecPictureCountByConditionRequest request) {
        return specService.getSpecPictureCountByCondition(request);
    }

    @Override
    @GetMapping("/v1/carpic/spec_25picturebyspecid.ashx")
    public Spec25PictureBySpecIdResponse getSpec25PictureBySpecId(Spec25PictureBySpecIdRequest request) {
        return specService.getSpec25PictureBySpecId(request);
    }

    @Override
    @GetMapping(value = {"/v1/carprice/spec_innercolorbyspecid.ashx","/v1/CarPrice/Spec_InnerColorBySpecId.ashx"})
    public SpecInnerColorBySpecIdResponse getSpecInnerColorBySpecId(SpecInnerColorBySpecIdRequest request) {
        return specService.getSpecInnerColorBySpecId(request);
    }

    @Override
    @GetMapping("/v1/Carprice/Spec_GetSpecInfoBySeriesId.ashx")
    public SpecInfoBySeriesIdResponse getSpecInfoBySeriesId (SpecInfoBySeriesIdRequest request){
        return specService.getSpecInfoBySeriesId(request);
    }


    @Override
    @GetMapping("/v1/carprice/spec_innercolorlistbyseriesid.ashx")
    public GetSpecInnerColorBySeriesIdResponse getSpecInnerColorBySeriesId(GetSpecInnerColorBySeriesIdRequest request) {
        ApiResult<SpecColorListItems> apiResult = specService.getSpecInnerColorBySeriesId(request);
        GetSpecInnerColorBySeriesIdResponse.Builder builder = GetSpecInnerColorBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || apiResult.getResult() == null) {
            return builder.build();
        }
        GetSpecInnerColorBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSpecInnerColorBySeriesIdResponse.Result.class);
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/carprice/spec_colorlistbyseriesid.ashx")
    public GetSpecSpecColorBySeriesIdResponse getSpecSpecColorBySeriesId(GetSpecSpecColorBySeriesIdRequest request) {
        ApiResult<SpecColorListItems> apiResult = specService.getSpecSpecColorListBySeriesId(request);
        GetSpecSpecColorBySeriesIdResponse.Builder builder = GetSpecSpecColorBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || apiResult.getResult() == null) {
            return builder.build();
        }
        GetSpecSpecColorBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSpecSpecColorBySeriesIdResponse.Result.class);
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/CarPrice/Spec_GetSpecNameBySpecId.ashx")
    public GetSpecNameResponse getSpecName(GetSpecInfoBySpecIdRequest request) {
        return specService.getSpecNameBySpecId(request);
    }

    @Override
    @GetMapping("/v1/carprice/spec_logobyspecid.ashx")
    public GetSpecLogoResponse getSpecLogo(GetSpecInfoBySpecIdRequest request) {
        return specService.getSpecLogoBySpecId(request);
    }
    @Override
    @GetMapping("/v1/car/Config_ListBySpecId.ashx")
    public GetConfigListResponse getConfigListBySpecId(GetConfigListRequest request) {
        return specService.getConfigListBySpecId(request);
    }

    @Override
    @GetMapping({"/v1/carprice/spec_paramlistbyseriesid.ashx", "/v1/carprice/Spec_ParamListBySeriesId.ashx"})
    public GetSpecParamsBySeriesIdResponse getSpecParamsBySeriesId(GetSpecInfoBySeriesIdRequest request) {
        ApiResult<ParamTypeItemPage> apiResult = specService.getSpecParamsBySeriesId(request);
        ParamTypeItemPage result = apiResult.getResult();
        GetSpecParamsBySeriesIdResponse.Builder builder = GetSpecParamsBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(result)) {
            builder.setResult(MessageUtil.toMessage(result, GetSpecParamsBySeriesIdResponse.Result.class));
        }
        return builder.build();
    }

    /**
     * 根据车型id+城市id获取补贴后售价
     * @param request
     * @return
     */
    @GetMapping("/v1/BuTie/Spec_BuTieBySpecIdAndCityId.ashx")
    @Override
    public GetSpecBuTieBySpecIdAndCityIdResponse getSpecBuTieBySpecIdAndCityId(GetSpecBuTieBySpecIdAndCityIdRequest request) {
        return GetSpecBuTieBySpecIdAndCityIdResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @Override
    @GetMapping(value = {"/v1/App/Spec_ParamListBySpecList.ashx"})
    public GetAppSpecParamBySpecListResponse getAppSpecParamBySpecList(GetSpecLogoBySpecListRequest request) {
        GetAppSpecParamBySpecListResponse response = specService.getAppSpecParamBySpecList(request);
        GetAppSpecParamBySpecListResponse.Result result = response.getResult();
        int returnCode = response.getReturnCode();
        GetAppSpecParamBySpecListResponse.Builder builder = null;
        if (returnCode == 0) {
            builder = GetAppSpecParamBySpecListResponse.newBuilder();
            GetAppSpecParamBySpecListResponse.Result.Builder finalResult = GetAppSpecParamBySpecListResponse.Result.newBuilder();
            List<ParamTypeItems> list = specService.getParamTypeItems(request.getSpeclist(), true);
            List<GetAppSpecParamBySpecListResponse.Result.Paramtypeitems> paramTypeItems = MessageUtil.toMessageList(list, GetAppSpecParamBySpecListResponse.Result.Paramtypeitems.class);
            finalResult.addAllSpecinfoitem(result.getSpecinfoitemList()).addAllParamtypeitems(paramTypeItems);
            builder.setResult(finalResult);
        }
        return Objects.isNull(builder) ? response : builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    @GetMapping("/v1/carprice/spec_detailbyyearId.ashx")
    public SpecDetailByYearIdResponse specDetailByYearId(SpecDetailByYearIdRequest request) {
        return specService.getSpecDetailByYearId(request);
    }

//    @GetMapping("/v1/CarPrice/Spec_CountBySeriesId.ashx")
//    @Override
//    public GetSpecStateCountBySeriesIdResponse getSpecStateCountBySeriesId(GetSpecStateCountBySeriesIdRequest request){
//        return specService.getSpecStateCountBySeriesId(request);
//    }

    @GetMapping("/v1/carprice/spec_detailbyspeclist.ashx")
    @Override
    public GetSpecDetailBySpecListResponse getSpecDetailBySpecList(GetSpecDetailBySpecListRequest request){
        return specService.getSpecDetailBySpecList(request);
    }

    @Override
    @GetMapping("/v1/CarPrice/Spec_BaseInfbySpecList.ashx")
    public SpecBaseInfbySpecListResponse specBaseInfbySpecList(SpecBaseInfbySpecListRequest request){
        return specService.specBaseInfbySpecList(request);
    }

    @Override
    @GetMapping("/v1/Rec/Spec_AllSpecInfo.ashx")
    public SpecAllSpecInfoResponse specAllSpecInfo(SpecAllSpecInfoRequest request){
        return specService.specAllSpecInfo(request);
    }


    /**
     * 根据车系id获取各状态下车型数量
     * @param request
     * @return
     */
    @GetMapping("/v1/CarPrice/Spec_CountBySeriesId.ashx")
    @Override
    public GetSpecCountBySeriesIdResponse getSpecCountBySeriesId(GetSpecCountBySeriesIdRequest request) {
        ApiResult<SpecCountItem> apiResult = specService.getSpecCountBySeriesId(request);
        GetSpecCountBySeriesIdResponse.Builder builder = GetSpecCountBySeriesIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetSpecCountBySeriesIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSpecCountBySeriesIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    @Override
    @GetMapping("/v1/car/spec_paramlistbyyearid.ashx")
    public GetCarSpecParamListByYearIdResponse getCarSpecParamListByYearId(GetCarSpecParamListByYearIdRequest request) {
        return specService.getCarSpecParamListByYearId(request);
    }

    @Override
    @GetMapping("/v1/carprice/spec_paramlistbyyearid.ashx")
    public GetCarPriceSpecParamListByYearIdResponse getCarPriceSpecParamListByYearId(GetCarPriceSpecParamListByYearIdRequest request) {
        return specService.getCarPriceSpecParamListByYearId(request);
    }

    @Override
    @GetMapping("/v1/Unify/Spec_ListByYearId.ashx")
    public GetCarSpecPriceByYearIdResponse getCarSpecPriceByYearId(GetCarSpecPriceByYearIdRequest request) {
        return specService.getCarSpecPriceByYearId(request);
    }

    @Override
    @GetMapping("/v1/carprice/Spec_ParamSingleBySpecIdItemId.ashx")
    public GetSpecParamSingleByItemResponse getSpecParamSingleByItem(GetSpecParamSingleByItemRequest request){
        if (request.getSpecid() == 0 || request.getItemid() == 0) {
            return GetSpecParamSingleByItemResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        GetSpecParamSingleByItemResponse.Builder resp = GetSpecParamSingleByItemResponse.newBuilder();
        GetSpecParamSingleByItemResponse.Result.Builder result = GetSpecParamSingleByItemResponse.Result.newBuilder();
        result.setSpecid(request.getSpecid());
        result.setItemid(request.getItemid());
        String itemName = CommonFunction.GetItemName(request.getItemid());
        result.setItemname(itemName);

        CompletableFuture<SpecBaseInfo> specInfoFuture = specBaseService.get(request.getSpecid());
        CompletableFuture<List<SpecConfigEntity>> specConfigsFuture = CompletableFuture.supplyAsync(() -> specConfigService.get(request.getSpecid()));
        CompletableFuture.allOf(specInfoFuture, specConfigsFuture).join();

        SpecBaseInfo specInfo = specInfoFuture.join();
        if(specInfo == null || specInfo.getIsSpecParamIsShow() != 1){
            result.addAllParamitems(Collections.emptyList());
            return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }
        List<SpecConfigEntity> specConfigs = specConfigsFuture.join();
        if(CollectionUtils.isEmpty(specConfigs)){
            result.addAllParamitems(Collections.emptyList());
            return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }

        for(SpecConfigEntity item : specConfigs){
            if(!itemName.equals(item.getItem())){
                continue;
            }
            String val = item.getItemValue();
            String valName = (val == null || val.trim().isEmpty() || val.trim().equals("0") || val.trim().equals("0.0")) ? "-" : val;
            result.addParamitems(GetSpecParamSingleByItemResponse.Result.ParamItem.newBuilder()
                    .setName(item.getName())
                    .setValue(valName));
        }

        return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    @GetMapping("/v1/App/Spec_SpeclistBySeriesIds.ashx")
    public GetSpecListBySeriesIdsResponse getSpecListBySeriesIds(GetSpecListBySeriesIdsRequest request){
        List<Integer> seriesIds = request.getSeriesidsList();
        if (seriesIds.size() == 0 || seriesIds.size() > 10) {
            return GetSpecListBySeriesIdsResponse.newBuilder().
                    setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SpecStateEnum specState = Spec.getSpecState(request.getState());
        GetSpecListBySeriesIdsResponse.Builder resp = GetSpecListBySeriesIdsResponse.newBuilder();

        Map<Integer, SeriesBaseInfo> seriesMap = seriesBaseService.getMap(seriesIds);
        for(Integer seriesId : seriesIds){
            GetSpecListBySeriesIdsResponse.SpecItems.Builder specItem = GetSpecListBySeriesIdsResponse.SpecItems.newBuilder();
            specItem.setSeriesid(seriesId);

            SeriesBaseInfo seriesBaseInfo = seriesMap.get(seriesId);
            if(seriesBaseInfo == null){
                specItem.addAllList(Collections.emptyList());
                resp.addResult(specItem);
                continue;
            }
            specItem.setSeriesname(seriesBaseInfo.getName());

            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            List<SpecViewEntity> specList = seriesSpecBaseService.get(seriesId, isCV).join();
            List<SpecViewEntity> filterList = new ArrayList<>(specList);
            switch (specState){
                case WAIT_SELL:
                    filterList = specList.stream().filter(x -> x.getSpecState() == 10).collect(Collectors.toList());
                    break;
                case SELL_12:
                    filterList = specList.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).collect(Collectors.toList());
                    break;
                case STOP_SELL:
                    filterList = specList.stream().filter(x -> x.getSpecState() == 40).collect(Collectors.toList());
                    break;
                default:
                    filterList = specList.stream().filter(x -> x.getSpecState() >= 10).collect(Collectors.toList());
                    break;
            }
            if(CollectionUtils.isEmpty(filterList)){
                specItem.addAllList(Collections.emptyList());
                resp.addResult(specItem);
                continue;
            }
            List<SpecViewEntity> sortedList = filterList.stream()
                    .sorted(Comparator.comparing(SpecViewEntity::getAppointOrder, Comparator.reverseOrder())
                            .thenComparing(SpecViewEntity::getSpecIsPublic)
                            .thenComparing(SpecViewEntity::getSpecOrdercls))
                    .collect(Collectors.toList());
            for(SpecViewEntity spec : sortedList){
                String img = "";
                if(spec.getSpecimg() != null){
                    img = spec.getSpecimg().replace("/l_", "/");
                }
                specItem.addList(GetSpecListBySeriesIdsResponse.SpecItems.SpecList.newBuilder()
                        .setSpecid(spec.getSpecId())
                        .setSpecname(spec.getSpecName())
                        .setMaxprice(spec.getMaxPrice())
                        .setMinprice(spec.getMinPrice())
                        .setImg(ImageUtil.getFullImagePath(img))
                        .setSpecstate(spec.getSpecState())
                );
            }
            resp.addResult(specItem);
        }
        return resp.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode()).
                setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    @GetMapping("/v1/App/Spec_InfoBySpecIds.ashx")
    public GetAppSpecInfoBySpecListResponse getAppSpecInfoBySpecList(GetAppSpecInfoBySpecListRequest request) {
        List<Integer> specIds = request.getSpeclistList();
        if(specIds.size() == 0 || specIds.stream().anyMatch(val -> val == 0)){
            return GetAppSpecInfoBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        GetAppSpecInfoBySpecListResponse.Builder resp = GetAppSpecInfoBySpecListResponse.newBuilder();

        List<SpecParam> specParamList = specParamService.getList(specIds);
        List<SpecParam> sortedList = specParamList.stream().sorted(Comparator.comparing(SpecParam::getSpecid)).collect(Collectors.toList());
        List< GetAppSpecInfoBySpecListResponse.SpecInfo> specList = new ArrayList<>();
        for(SpecParam specParam :  sortedList){
            specList.add(GetAppSpecInfoBySpecListResponse.SpecInfo.newBuilder()
                    .setSpecid(specParam.getSpecid())
                    .setSpecname(specParam.getSpecname())
                    .setSeriesid(specParam.getSeriesid())
                    .setSeriesname(specParam.getSeriesname())
                    .setSpecminprice(specParam.getSpecminprice())
                    .setSpecmaxprice(specParam.getSpecmaxprice())
                    .setSpecistaxexemption(specParam.getSpecistaxexemption())
                    .setSeatnum(specParam.getSpecstructureseat() != null ? specParam.getSpecstructureseat() : "")
                    .setDisplacement(String.valueOf(specParam.getSpecdisplacement()))
                    .setSeriesisimport(specParam.getSpecisimport())
                    .build()
            );
        }

        return resp.addAllResult(specList).setReturnCode(0).setReturnMsg("成功").build();
    }

//    @Override
//    @GetMapping("/v1/car/spec_paramlistbyseriesid.ashx")
//    public GetCarSpecParamListBySeriesIdResponse getCarSpecParamListBySeriesId(GetCarSpecParamListBySeriesIdRequest request) {
//        return specService.getCarSpecParamListBySeriesId(request);
//    }

    @Override
    @GetMapping("/v1/carprice/series_namebyfctid.ashx")
    public GetCarSeriesNameByFctIdResponse getCarSeriesNameByFctId(GetCarSeriesNameByFctIdRequest request) {
        return specService.getCarSeriesNameByFctId(request);
    }

    @Override
    @GetMapping("/v1/duibi/Config_DistinctListBySpecList.ashx")
    public ConfigDistinctListBySpecListResponse configDistinctListBySpecList(ConfigDistinctListBySpecListRequest request) {
        ConfigGetListBySpecListResponse base = specListConfigService.configGetListBySpecList(request.getSpeclistList());
        ConfigDistinctListBySpecListResponse.Builder builder = ConfigDistinctListBySpecListResponse.newBuilder();
        builder.setReturnCode(base.getReturnCode()).setReturnMsg(base.getReturnMsg()).build();
        if (base.getReturnCode() != 0) {
            return builder.build();
        }
        if (base.getResult() == null) {
            return builder.build();
        }
        List<Integer> arrDisplayNone = Arrays.asList( 5, 7, 11, 13, 22, 25, 27, 28, 29, 31, 32, 33, 35, 36, 37, 41, 43, 44, 48, 49, 50, 52, 53, 55, 56, 57, 58, 59, 65, 66, 69, 71, 72, 73, 74, 81, 84, 87, 88, 89, 90, 91, 92, 94, 96, 98, 99, 103, 113, 114, 122, 123, 124, 125, 126, 128, 129, 132, 135, 136, 137, 138, 139, 145, 151, 204, 140, 141 );
        ConfigDistinctListBySpecListResponse.Result.Builder result = ConfigDistinctListBySpecListResponse.Result.newBuilder();
        for (ConfigGetListBySpecListResponse.Result.Configtypeitem configtypeitem : base.getResult().getConfigtypeitemsList()) {
            List<ConfigDistinctListBySpecListResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();

            for (ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem configitem : configtypeitem.getConfigitemsList()) {
                if(arrDisplayNone.contains(configitem.getConfigid())) continue;
                if (configitem.getValueitemsCount() <= 1) { //一样
                    continue;
                }
                ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem first = configitem.getValueitems(0);
                int sameCount = 1;
                for (int i = 1; i < configitem.getValueitemsCount(); i++) {
                    if (isSame(first, configitem.getValueitems(i))) {
                        sameCount++;
                    }
                }
                if (sameCount == configitem.getValueitemsCount()) //一样
                    continue;

                ConfigDistinctListBySpecListResponse.Result.Configtypeitem.Configitem.Builder newItem = ConfigDistinctListBySpecListResponse.Result.Configtypeitem.Configitem.newBuilder();
                newItem.setName(configitem.getName());
                newItem.setDisptype(configitem.getDisptype());
                for (ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem valueitem : configitem.getValueitemsList()) {
                    ConfigDistinctListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.Builder newValueItem = ConfigDistinctListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.newBuilder();
                    newValueItem.setSpecid(valueitem.getSpecid()).setValue(valueitem.getValue());
                    if (valueitem.getSublistCount() > 0) {
                        for (ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem subItem : valueitem.getSublistList()) {
                            newValueItem.addSublist(
                                    ConfigDistinctListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.newBuilder()
                                            .setSubname(subItem.getSubname())
                                            .setSubvalue(subItem.getSubvalue())
                            );
                        }
                    }
                    newItem.addValueitems(newValueItem);
                }
                items.add(newItem.build());
            }
            if (items.size() > 0) {
                result.addConfig(
                        ConfigDistinctListBySpecListResponse.Result.Configtypeitem.newBuilder()
                                .setName(configtypeitem.getName())
                                .addAllConfigitems(items)
                );
            }
        }
        return builder.setResult(result).build();
    }

    boolean isSame(ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem s,ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem t){
        if(!s.getValue().equals(t.getValue())){
            return false;
        }
        if(s.getSublistCount() != t.getSublistCount())
            return false;

        for (int i = 0; i < s.getSublistCount(); i++) {
            ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem ss = s.getSublist(i);
            ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem ts = t.getSublist(i);
            if(!ss.getSubname().equals(ts.getSubname()))
                return false;
            if(ss.getSubvalue()!=(ts.getSubvalue()))
                return false;
        }
        return true;
    }


    @Override
    @GetMapping("/NewEnergy/Dingzhi_SpecMainParam.ashx")
    public GetDingZhiSpecMainParamResponse getDingZhiSpecMainParam(GetDingZhiSpecMainParamRequest request){
        GetDingZhiSpecMainParamResponse.Builder resp = GetDingZhiSpecMainParamResponse.newBuilder();
        if(request.getSpecid() == 0){
            return GetDingZhiSpecMainParamResponse.newBuilder().setReturnCode(101).setReturnMsg("缺少必要的请求参数").build();
        }
        List<ElectricSpecParamEntity> elecSpecs = autoCacheService.getEleSpecSpecViewById(request.getSpecid());
        if(!CollectionUtils.isEmpty(elecSpecs)){
            GetDingZhiSpecMainParamResponse.Result.Builder result = GetDingZhiSpecMainParamResponse.Result.newBuilder();
            result.setSpecid(request.getSpecid());
            SpecParam specParam = specParamService.get(request.getSpecid());
            if(specParam != null){
                result.setSpecname(specParam.getSpecname());
                result.setSeriesid(specParam.getSeriesid());
                result.setSpecname(specParam.getSeriesname());
                result.setFueltype(specParam.getFueltype());
                result.setSpecimg(specParam.getSpeclogo() != null ? specParam.getSpeclogo() : "");
            }
            result.setRongliang(elecSpecs.get(0).getRongliang() != null ? elecSpecs.get(0).getRongliang() : "0");
            result.setGonglv(String.valueOf(elecSpecs.get(0).getGonglv()));
            result.setLicheng(String.valueOf(elecSpecs.get(0).getLicheng()));
            result.setNiuju(String.valueOf(elecSpecs.get(0).getNiuju()));
            resp.setResult(result);
        }
        return resp.setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v1/labelpic/ConfigList_BySpecId.ashx")
    @Override
    public GetLabelPicConfigListResponse getLabelPicConfigListBySpecId(GetSpecInfoBySpecIdRequest request) {
        return specService.getLabelPicConfigListBySpecId(request);
    }
    /**
     * 根据日期获取当天上传图片相关的车型列表。
     *
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/Pic_SpecListByDate.ashx")
    @Override
    public GetPicSpecListByDateResponse getPicSpecListByDate(GetPicSpecListByDateRequest request) {
        return specService.getPicSpecListByDate(request);
    }
    @Override
    @GetMapping("/v1/Unify/Spec_ListBySeriesId.ashx")
    public GetUnifySpecListBySeriesResponse getUnifySpecListBySeries(GetUnifySpecListBySeriesRequest request) {
        GetUnifySpecListBySeriesResponse.Builder builder = GetUnifySpecListBySeriesResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        long startTime = System.currentTimeMillis();
        List<SpecPriceViewEntity> waitSellSpec = autoCacheService.carSpecPriceWaitSellBySeriesId(seriesId);
        List<SpecPriceViewEntity> sellSpec = autoCacheService.carSpecPriceSellBySeriesId(seriesId);
        List<Integer> newCarIds = autoCacheService.getNewCarSpecIds();
        List<GetUnifySpecListBySeriesResponse.WaitSpecItem> waitList = new ArrayList<>();
        List<GetUnifySpecListBySeriesResponse.SpecItem> sellList = new ArrayList<>();
        List<KeyValueDto<Integer, Integer>> jianshuis = specMapper.getSpecJianShui();
        Map<Integer,Integer> jss = jianshuis.stream().collect(Collectors.toMap(x->x.getKey(),x->x.getValue()));
        if (!CollectionUtils.isEmpty(waitSellSpec) || !CollectionUtils.isEmpty(sellSpec)) {
            List<SpecPriceViewEntity> copyWaitSellSpec = new ArrayList<>(waitSellSpec);
            List<SpecPriceViewEntity> copySellSpec = new ArrayList<>(sellSpec);
            if (request.getSyearid() != 0) {
                copyWaitSellSpec = copyWaitSellSpec.stream().filter(x -> x.getSyearid() == request.getSyearid()).collect(Collectors.toList());
                copySellSpec = copySellSpec.stream().filter(x -> x.getSyearid() == request.getSyearid()).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(copyWaitSellSpec)) {
                List<Integer> specIds = copyWaitSellSpec.stream().map(SpecPriceViewEntity::getSpecId).collect(Collectors.toList());
                Map<Integer, SpecBaseInfo> specMap = specBaseService.getMapBigData(specIds);
                Map<Integer, List<SpecPriceViewEntity>> bookedMap = copyWaitSellSpec.stream()
                        .collect(Collectors.groupingBy(x -> specMap.get(x.getSpecId()).getIsBooked()));
                for (Map.Entry<Integer, List<SpecPriceViewEntity>> entry : bookedMap.entrySet()) {
                    int isbooked = entry.getKey();
                    List<GetUnifySpecListBySeriesResponse.WaitSpecList> arrSpec = new ArrayList<>();
                    for (SpecPriceViewEntity dr : entry.getValue()) {
                        int jianshui = jss.containsKey(dr.getSpecId()) ? jss.get(dr.getSpecId()):0;
                        if(!specMap.containsKey(dr.getSpecId())) continue;
                        SpecBaseInfo spec = specMap.get(dr.getSpecId());
                        arrSpec.add(GetUnifySpecListBySeriesResponse.WaitSpecList.newBuilder()
                                .setSpecid(dr.getSpecId())
                                .setSpecname(spec.getSpecName())
                                .setSpecstate(spec.getSpecState())
                                .setMinprice(dr.getFctMinPrice())
                                .setMaxprice(dr.getFctMaxPrice())
                                .setFueltype(dr.getFuelType())
                                .setFueltypedetail(dr.getFueltypedetail())
                                .setDriveform(CommonFunction.driveMode(dr.getDriveForm()))
                                .setDrivetype(CommonFunction.DriveType(dr.getDriveType()))
                                .setGearbox(StringUtils.isNotBlank(spec.getGearBox())?spec.getGearBox():"")
                                .setEvflag(dr.getFuelType() == 4 ? "电动" : dr.getFuelType() == 3 ? "混动" : "")
                                .setNewcarflag(newCarIds.contains(dr.getSpecId()) ? "新车上市" : "")
                                .setSubsidy(jianshui == 1 ? "减税" : jianshui == 2 ? "免税" : "")
                                .setSyear(dr.getSyear())
                                .setParamisshow(spec.getIsSpecParamIsShow())
                                .build()
                        );
                    }
                    GetUnifySpecListBySeriesResponse.WaitSpecItem.Builder waitItem = GetUnifySpecListBySeriesResponse.WaitSpecItem.newBuilder();
                    waitItem.setName("车型").setIsbooked(isbooked).addAllSpeclist(arrSpec);
                    waitList.add(waitItem.build());
                }
            }

            if (!CollectionUtils.isEmpty(copySellSpec)) {
                List<Integer> specIds = copySellSpec.stream().map(SpecPriceViewEntity::getSpecId).collect(Collectors.toList());
                Map<Integer, SpecBaseInfo> specMap = specBaseService.getMapBigData(specIds);
                Map<Integer, List<SpecPriceViewEntity>> showMap = copySellSpec.stream()
                        .collect(Collectors.groupingBy(x -> specMap.get(x.getSpecId()).getIsSpecParamIsShow()));
                Map<Integer, List<SpecPriceViewEntity>> sortShowMap = new TreeMap<>(showMap);

                for (Map.Entry<Integer, List<SpecPriceViewEntity>> entry : sortShowMap.entrySet()) {
                    int isShow = entry.getKey();
                    //非外显
                    if (isShow == 0) {
                        List<GetUnifySpecListBySeriesResponse.SpecList> arrSpec = new ArrayList<>();
                        for (SpecPriceViewEntity dr : entry.getValue()) {
                            if(!specMap.containsKey(dr.getSpecId())) continue;
                            SpecBaseInfo spec = specMap.get(dr.getSpecId());
                            int jianshui = jss.containsKey(dr.getSpecId()) ? jss.get(dr.getSpecId()):0;
                            arrSpec.add(GetUnifySpecListBySeriesResponse.SpecList.newBuilder()
                                    .setSpecid(dr.getSpecId())
                                    .setSpecname(spec.getSpecName())
                                    .setSpecstate(spec.getSpecState())
                                    .setMinprice(dr.getFctMinPrice())
                                    .setMaxprice(dr.getFctMaxPrice())
                                    .setFueltype(dr.getFuelType())
                                    .setFueltypedetail(dr.getFueltypedetail())
                                    .setDriveform(CommonFunction.driveMode(dr.getDriveForm()))
                                    .setDrivetype(CommonFunction.DriveType(dr.getDriveType()))
                                    .setGearbox(StringUtils.isBlank(spec.getGearBox())?"":spec.getGearBox())
                                    .setEvflag(dr.getFuelType() == 4 ? "电动" : dr.getFuelType() == 3 ? "混动" : "")
                                    .setNewcarflag(newCarIds.contains(dr.getSpecId()) ? "新车上市" : "")
                                    .setSubsidy(jianshui == 1 ? "减税" : jianshui == 2 ? "免税" : "")
                                    .setSyear(dr.getSyear())
                                    .setParamisshow(spec.getIsSpecParamIsShow())
                                    .setEmissionstandards(StringUtils.isNotBlank(spec.getDicEmissionStandards())?spec.getDicEmissionStandards():"")
                                    .setGearboxabbreviation(CommonFunction.GetTransmissionType(dr.getTransmissionTypeId()))
                                    .setDeliverycapacity(Double.parseDouble(dr.getDeliveryCapacity()))
                                    .setFlowmode(dr.getFlowMode())
                                    .setEndurancemileage(dr.getEndurancemileage())
                                    .setElectrickw(dr.getElectricKW())
                                    .build()
                            );
                        }
                        GetUnifySpecListBySeriesResponse.SpecItem.Builder specItem = GetUnifySpecListBySeriesResponse.SpecItem.newBuilder();
                        specItem.setName("参数配置未公布").addAllSpeclist(arrSpec);
                        sellList.add(specItem.build());
                    } else {
                        List<SpecPriceViewEntity> showList = entry.getValue();
                        Map<Integer, List<SpecPriceViewEntity>> electricTypeGroups = showList.stream()
                                .filter(entity -> entity.getElectrictype() > 0)
                                .collect(Collectors.groupingBy(SpecPriceViewEntity::getElectrictype));
                        if (!CollectionUtils.isEmpty(electricTypeGroups)) {
                            TreeMap<Integer, List<SpecPriceViewEntity>> sortedElectricTypeGroups = new TreeMap<>(electricTypeGroups);
                            for (Map.Entry<Integer, List<SpecPriceViewEntity>> elecEntry : sortedElectricTypeGroups.entrySet()) {
                                Map<Double, List<SpecPriceViewEntity>> electricKwGroups = elecEntry.getValue().stream()
                                        .collect(Collectors.groupingBy(SpecPriceViewEntity::getElectricKW));
                                TreeMap<Double, List<SpecPriceViewEntity>> sortElectricKWG = electricKwGroups.entrySet().stream()
                                        .sorted(Map.Entry.<Double, List<SpecPriceViewEntity>>comparingByKey().reversed())
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                (oldValue, newValue) -> oldValue, // Merge Function: Keep existing value
                                                TreeMap::new // Supplier: Create a TreeMap
                                        ));
                                List<GetUnifySpecListBySeriesResponse.SpecItem> arrStopSellSpec = new ArrayList<>();
                                List<GetUnifySpecListBySeriesResponse.SpecItem> arrSellSpec = new ArrayList<>();
                                for (Map.Entry<Double, List<SpecPriceViewEntity>> kwEntry : sortElectricKWG.entrySet()) {
                                    boolean hasSell = false;
                                    String groupname = String.format("%s %s", elecEntry.getKey() == 1 ? "电动" : "增程式", kwEntry.getKey() > 0 ? (int) Math.round(kwEntry.getKey() * 1.36) + "马力" : "");
                                    List<GetUnifySpecListBySeriesResponse.SpecList> arrSpec = new ArrayList<>();
                                    for (SpecPriceViewEntity dr : kwEntry.getValue()) {
                                        if(!specMap.containsKey(dr.getSpecId())) continue;
                                        SpecBaseInfo spec = specMap.get(dr.getSpecId());
                                        int jianshui = jss.containsKey(dr.getSpecId()) ? jss.get(dr.getSpecId()):0;
                                        arrSpec.add(GetUnifySpecListBySeriesResponse.SpecList.newBuilder()
                                                .setSpecid(dr.getSpecId())
                                                .setSpecname(spec.getSpecName())
                                                .setSpecstate(spec.getSpecState())
                                                .setMinprice(dr.getFctMinPrice())
                                                .setMaxprice(dr.getFctMaxPrice())
                                                .setFueltype(dr.getFuelType())
                                                .setFueltypedetail(dr.getFueltypedetail())
                                                .setDriveform(CommonFunction.driveMode(dr.getDriveForm()))
                                                .setDrivetype(CommonFunction.DriveType(dr.getDriveType()))
                                                .setGearbox(spec.getGearBox())
                                                .setEvflag(dr.getFuelType() == 4 ? "电动" : dr.getFuelType() == 3 ? "混动" : "")
                                                .setNewcarflag(newCarIds.contains(dr.getSpecId()) ? "新车上市" : "")
                                                .setSubsidy(jianshui == 1 ? "减税" : jianshui == 2 ? "免税" : "")
                                                .setSyear(dr.getSyear())
                                                .setParamisshow(spec.getIsSpecParamIsShow())
                                                .setEmissionstandards(StringUtils.isNotBlank( spec.getDicEmissionStandards())?spec.getDicEmissionStandards():"")
                                                .setGearboxabbreviation(CommonFunction.GetTransmissionType(dr.getTransmissionTypeId()))
                                                .setDeliverycapacity(Double.parseDouble(dr.getDeliveryCapacity()))
                                                .setFlowmode(dr.getFlowMode())
                                                .setEndurancemileage(dr.getEndurancemileage())
                                                .setElectrickw(dr.getElectricKW())
                                                .build()
                                        );
                                        if (spec.getSpecState() == 20) {
                                            hasSell = true;
                                        }
                                    }
                                    GetUnifySpecListBySeriesResponse.SpecItem.Builder specItem = GetUnifySpecListBySeriesResponse.SpecItem.newBuilder();
                                    if (hasSell) {
                                        arrSellSpec.add(specItem.setName(groupname).addAllSpeclist(arrSpec).build());
                                    } else {
                                        arrStopSellSpec.add(specItem.setName(groupname).addAllSpeclist(arrSpec).build());
                                    }
                                }
                                sellList.addAll(arrSellSpec);
                                sellList.addAll(arrStopSellSpec);
                            }
                        }

                        //汽油机
                        Map<Integer, List<SpecPriceViewEntity>> classicTypeGroups = showList.stream()
                                .filter(entity -> entity.getElectrictype() == 0)
                                .collect(Collectors.groupingBy(SpecPriceViewEntity::getIsClassic));
                        for (Map.Entry<Integer, List<SpecPriceViewEntity>> elecType : classicTypeGroups.entrySet()) {
                            List<GetUnifySpecListBySeriesResponse.SpecItem> arrStopSellSpec = new ArrayList<>();
                            List<GetUnifySpecListBySeriesResponse.SpecItem> arrSellSpec = new ArrayList<>();
                            // 第一层分组和排序（按照 horsepower 进行分组，并在每层内按照 horsepower 进行排序）
                            TreeMap<Integer, List<SpecPriceViewEntity>> sortedHorseMap = elecType.getValue().stream()
                                    .collect(Collectors.groupingBy(SpecPriceViewEntity::getHorsepower,
                                            TreeMap::new, // 使用 TreeMap 进行 horsepower 的排序
                                            Collectors.toList()
                                    ));
                            for (Map.Entry<Integer, List<SpecPriceViewEntity>> horseEntry : sortedHorseMap.entrySet()) {
                                int horse = horseEntry.getKey();
                                List<SpecPriceViewEntity> valueHorse = horseEntry.getValue();
                                // 第二层按照 flowMode分组
                                TreeMap<Integer, List<SpecPriceViewEntity>> sortedFlowModeMap = valueHorse.stream()
                                        .collect(Collectors.groupingBy(SpecPriceViewEntity::getFlowMode,
                                                TreeMap::new,
                                                Collectors.toList()
                                        ));
                                for (Map.Entry<Integer, List<SpecPriceViewEntity>> flowEntry : sortedFlowModeMap.entrySet()) {
                                    int flow = flowEntry.getKey();
                                    List<SpecPriceViewEntity> valueFlow = flowEntry.getValue();
                                    String flowMode = CommonFunction.admissionMehtod(flow);
                                    // 第三层按照 DeliveryCapacity分组
                                    TreeMap<String, List<SpecPriceViewEntity>> sortedDeliveryMap = valueFlow.stream()
                                            .collect(Collectors.groupingBy(SpecPriceViewEntity::getDeliveryCapacity,
                                                    () -> new TreeMap<>(Comparator.naturalOrder()),
                                                    Collectors.toList()
                                            ));
                                    for (Map.Entry<String, List<SpecPriceViewEntity>> deliveryEntry : sortedDeliveryMap.entrySet()) {
                                        String delivery = deliveryEntry.getKey();
                                        List<SpecPriceViewEntity> valueDelivery = deliveryEntry.getValue();
                                        //分组名开头
                                        String deliveryKey = "";
                                        BigDecimal dcDecimal = new BigDecimal(delivery);
                                        if (dcDecimal.compareTo(BigDecimal.ZERO) == 0) {
                                            deliveryKey = valueDelivery.get(0).getFuelType() == 4 ? "电动" : "";
                                        } else {
                                            deliveryKey = dcDecimal.setScale(1, RoundingMode.HALF_UP) + "升";
                                        }

                                        // 第四层按照id排序后，按照electricKW分组
                                        TreeMap<Double, List<SpecPriceViewEntity>> sortedElectricMap = valueDelivery.stream()
                                                .sorted(Comparator.comparingInt(SpecPriceViewEntity::getId))
                                                .collect(Collectors.groupingBy(SpecPriceViewEntity::getElectricKW,
                                                        TreeMap::new,
                                                        Collectors.toList()
                                                ));

                                        for (Map.Entry<Double, List<SpecPriceViewEntity>> electricEntry : sortedElectricMap.entrySet()) {
                                            boolean hasSell = false;
                                            double electricKW = electricEntry.getKey();
                                            List<SpecPriceViewEntity> specs = electricEntry.getValue();

                                            StringBuilder groupTitle = new StringBuilder();
                                            groupTitle.append(deliveryKey);
                                            String flowKey = flow == 1 ? "" : electricKW > 0 ? "" : " " + flowMode;//进气形式
                                            groupTitle.append(flowKey);

                                            String enginePower = "";
                                            if (horse > 0 && electricKW <= 0 || delivery.equals("0")) {
                                                enginePower = " " + horse + "马力";
                                            } else if (electricKW > 0 && horse > 0) {
                                                enginePower = " 发动机:" + horse + "马力";
                                            }
                                            groupTitle.append(enginePower);

                                            String horsePower = "";
                                            if (electricKW > 0 && !delivery.equals("0")) {
                                                horsePower = " 电动机:" + (int) Math.round(electricKW * 1.36) + "马力";
                                            }
                                            groupTitle.append(horsePower);

                                            List<GetUnifySpecListBySeriesResponse.SpecList> arrSpec = new ArrayList<>();
                                            for (SpecPriceViewEntity dr : specs) {
                                                if(!specMap.containsKey(dr.getSpecId())) continue;
                                                SpecBaseInfo spec = specMap.get(dr.getSpecId());
                                                int jianshui = jss.containsKey(dr.getSpecId()) ? jss.get(dr.getSpecId()):0;
                                                arrSpec.add(GetUnifySpecListBySeriesResponse.SpecList.newBuilder()
                                                        .setSpecid(dr.getSpecId())
                                                        .setSpecname(spec.getSpecName())
                                                        .setSpecstate(spec.getSpecState())
                                                        .setMinprice(dr.getFctMinPrice())
                                                        .setMaxprice(dr.getFctMaxPrice())
                                                        .setFueltype(dr.getFuelType())
                                                        .setFueltypedetail(dr.getFueltypedetail())
                                                        .setDriveform(CommonFunction.driveMode(dr.getDriveForm()))
                                                        .setDrivetype(CommonFunction.DriveType(dr.getDriveType()))
                                                        .setGearbox(spec.getGearBox())
                                                        .setEvflag(dr.getFuelType() == 4 ? "电动" : dr.getFuelType() == 3 ? "混动" : "")
                                                        .setNewcarflag(newCarIds.contains(dr.getSpecId()) ? "新车上市" : "")
                                                        .setSubsidy(jianshui == 1 ? "减税" : jianshui == 2 ? "免税" : "")
                                                        .setSyear(dr.getSyear())
                                                        .setParamisshow(spec.getIsSpecParamIsShow())
                                                        .setEmissionstandards(StringUtils.isNotBlank(spec.getDicEmissionStandards())?spec.getDicEmissionStandards():"")
                                                        .setGearboxabbreviation(CommonFunction.GetTransmissionType(dr.getTransmissionTypeId()))
                                                        .setDeliverycapacity(Double.parseDouble(dr.getDeliveryCapacity()))
                                                        .setFlowmode(dr.getFlowMode())
                                                        .setEndurancemileage(dr.getEndurancemileage())
                                                        .setElectrickw(dr.getElectricKW())
                                                        .build()
                                                );
                                                if (spec.getSpecState() == 20) {
                                                    hasSell = true;
                                                }
                                            }
                                            GetUnifySpecListBySeriesResponse.SpecItem.Builder specItem = GetUnifySpecListBySeriesResponse.SpecItem.newBuilder();
                                            if (hasSell) {
                                                arrSellSpec.add(specItem.setName(groupTitle.toString()).addAllSpeclist(arrSpec).build());
                                            } else {
                                                arrStopSellSpec.add(specItem.setName(groupTitle.toString()).addAllSpeclist(arrSpec).build());
                                            }
                                        }
                                    }
                                }
                            }
                            sellList.addAll(arrSellSpec);
                            sellList.addAll(arrStopSellSpec);
                        }
                    }
                }
            }
        }
        GetUnifySpecListBySeriesResponse.Result.Builder result = GetUnifySpecListBySeriesResponse.Result.newBuilder();
        result.setTimer((int) (System.currentTimeMillis() - startTime)).addAllWaitselllist(waitList).addAllSelllist(sellList);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v1/duibi/Param_ListBySpecList.ashx")
    @Override
    public GetDuibiSpecParamBySpecListResponse getDuibiSpecParamBySpecList(GetDuibiSpecParamBySpecListRequest request){
        GetDuibiSpecParamBySpecListResponse.Builder builder = GetDuibiSpecParamBySpecListResponse.newBuilder();
        List<Integer> specIds = request.getSpeclistList();
        if(specIds.size() > 8){
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<SpecParam> specParamList = specParamService.getList(specIds);
        if(CollectionUtils.isEmpty(specParamList)){
            return builder.setReturnCode(0).setReturnMsg("成功").build();
        }
        GetDuibiSpecParamBySpecListResponse.Result.Builder result = GetDuibiSpecParamBySpecListResponse.Result.newBuilder();
        for(SpecParam item : specParamList){
            GetDuibiSpecParamBySpecListResponse.Param.Builder paramBuilder = GetDuibiSpecParamBySpecListResponse.Param.newBuilder();
            paramBuilder.setSpecid(item.getSpecid());
            paramBuilder.setSpecname(item.getSpecname());
            paramBuilder.setSpeclogo(item.getSpeclogo());
            paramBuilder.setSpecstate(item.getSpecstate());
            paramBuilder.setSeriesid(item.getSeriesid());
            paramBuilder.setSeriesname(item.getSeriesname());
            paramBuilder.setBrandid(item.getBrandid());
            paramBuilder.setLevelid(item.getLevelid());
            paramBuilder.setLevelname(item.getLevelname());
            paramBuilder.setMinprice(item.getSpecminprice());
            paramBuilder.setMaxprice(item.getSpecmaxprice());
            paramBuilder.setLength(item.getSpeclength());
            paramBuilder.setWidth(item.getSpecwidth());
            paramBuilder.setHeight(item.getSpecheight());
            paramBuilder.setWheelbase(item.getWheelbase());
            paramBuilder.setEnginepower(item.getSpecenginepower());
            paramBuilder.setEnginetorque(Strings.isNotBlank(item.getEnginetorque()) ? Double.parseDouble(item.getEnginetorque()) :0);
            paramBuilder.setSpeed(item.getSsuo());
            paramBuilder.setSpecmaxspeed(item.getSpecMaxspeed());
            paramBuilder.setOiloffical(item.getSpecoiloffical() != null ? item.getSpecoiloffical() : 0);
            paramBuilder.setDisplacement(item.getSpecdisplacement());
            paramBuilder.setSpectransmission(item.getSpectransmission() != null ? item.getSpectransmission() : "");
            paramBuilder.setFlowmode(item.getSpecflowmodeid());
            paramBuilder.setFlowmodename(item.getSpecflowmodename());
            paramBuilder.setFueltype(item.getFueltype());
            paramBuilder.setFueltypename(item.getFueltypename());
            paramBuilder.setEndurancemileage(!Objects.equals(item.getMile(), "") ? Integer.parseInt(item.getMile()) : 0);
            paramBuilder.setBatterycapacity(!Objects.equals(item.getBatterycapacity(), "") ? Double.parseDouble(item.getBatterycapacity()) : 0);
            paramBuilder.setFastchargebatterypercentage(!Objects.equals(item.getFastchargePercent(), "") ? Integer.parseInt(item.getFastchargePercent()) : 0);
            paramBuilder.setOfficialfastchargetime(!Objects.equals(item.getFastchargetime(), "") ? Double.parseDouble(item.getFastchargetime()): 0);
            paramBuilder.setOfficialslowchargetime(!Objects.equals(item.getSlowchargetime(), "") ? Double.parseDouble(item.getSlowchargetime()) : 0);
            result.addParam(paramBuilder);
        }
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v1/carprice/spec_paramlistbyspecId.ashx")
    @Override
    public GetSpecParamListBySpecIdResponse getSpecParamListBySpecId(GetSpecParamListBySpecIdRequest request) {
        ApiResult<ParamTypeItemPage> apiResult = specService.getSpecParamListBySpecId(request);
        ParamTypeItemPage result = apiResult.getResult();
        GetSpecParamListBySpecIdResponse.Builder builder = GetSpecParamListBySpecIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(result)) {
            builder.setResult(MessageUtil.toMessage(result, GetSpecParamListBySpecIdResponse.Result.class));
        }
        return builder.build();
    }

    @Override
    @GetMapping("/Verify/PE.ashx")
    public VerifyPEResponse verifyPE(VerifyPERequest request){
        VerifyPEResponse.Builder builder = VerifyPEResponse.newBuilder();
        VerifyPEResponse.Result.Builder result = VerifyPEResponse.Result.newBuilder();
        String source = request.getSpecids();
        String data = DESUtil.encrypt(null,source);
        try{
            data = URLEncoder.encode(data,"UTF-8");
        }catch (Exception ee){

        }
        result.setContent(data);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    /**
     * 根据车系id+城市id获取车型补贴后售价列表
     * @param request
     * @return
     */
    @GetMapping("/v1/BuTie/Spec_BuTieBySeriesIdAndCityId.ashx")
    @Override
    public GetSpecBuTieBySeriesIdAndCityIdResponse getSpecBuTieBySeriesIdAndCityId(GetSpecBuTieBySeriesIdAndCityIdRequest request) {
        return GetSpecBuTieBySeriesIdAndCityIdResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @GetMapping("/v1/carprice/spec_getallname.ashx")
    @Override
    public GetSpecAllNameResponse getSpecAllName(GetSpecAllNameRequest request) {
        GetSpecAllNameResponse.Builder builder = GetSpecAllNameResponse.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");
        List<KeyValueDto<Integer, String>> specList = specMapper.getSpecAllName();
        if(CollectionUtils.isEmpty(specList)){
            return builder.build();
        }
        GetSpecAllNameResponse.Result.Builder result = GetSpecAllNameResponse.Result.newBuilder();
        for (KeyValueDto<Integer, String> item : specList) {
            result.addSeriesitems(GetSpecAllNameResponse.Result.SpecItem.newBuilder()
                    .setId(item.getKey())
                    .setName(item.getValue()));
        }
        result.setTotal(specList.size());
        return builder.setResult(result).build();
    }

    /**
     * 根据多个车型id及城市id获取补贴金额
     * @param request
     * @return
     */
    @GetMapping("/v1/CarPrice/Spec_ElectricSubsidyBySpecList.ashx")
    @Override
    public GetSpecElectricSubsidyBySpecListResponse getSpecElectricSubsidyBySpecList(GetSpecElectricSubsidyBySpecListRequest request) {
        return GetSpecElectricSubsidyBySpecListResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
}