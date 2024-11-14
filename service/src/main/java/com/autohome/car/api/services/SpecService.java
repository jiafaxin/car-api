package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.car.ConfigListByYearIdRequest;
import autohome.rpc.car.car_api.v1.car.ConfigListByYearIdResponse;
import autohome.rpc.car.car_api.v1.pic.GetClassItemsBySpecIdRequest;
import autohome.rpc.car.car_api.v1.pic.GetClassItemsBySpecIdResponse;
import autohome.rpc.car.car_api.v1.spec.*;
import autohome.rpc.car.car_api.v2.spec.GetSpecDetailBySeriesIdRequest;
import autohome.rpc.car.car_api.v2.spec.GetSpecDetailBySeriesIdResponse;
import autohome.rpc.car.car_api.v2.spec.SpecPictureCountByConditionRequestV2;
import autohome.rpc.car.car_api.v2.spec.SpecPictureCountByConditionResponseV2;
import autohome.rpc.car.car_api.v2.spec.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.services.models.*;

import java.util.List;

public interface SpecService {

    /**
     * 根据多个车型id获取相关信息
     *
     * @param request
     * @return
     */
    ApiResult<SpecItems> getSpecInfoBySpecList(GetSpecInfoBySpecListRequest request);

    GetClassItemsBySpecIdResponse getClassItemsBySpecId(GetClassItemsBySpecIdRequest request);

    /**
     * 根据车系id获取电车的车型信息
     *
     * @param request
     * @return
     */
    ApiResult<ElectricSpecParam> getElectricSpecParamBySeriesId(GetElectricSpecParamBySeriesIdRequest request);

    /**
     * 根据车系id获取车型的参数信息
     *
     * @param request
     * @return
     */
    ApiResult<SpecDetailItems> getSpecParamBySeriesId(GetSpecParamBySeriesIdRequest request);

    GetSpecInfoBySpecIdResponse getSpecInfoBySpecId(GetSpecInfoBySpecIdRequest request);

    GetSpecDetailBySeriesIdResponse getSpecDetailBySeriesId(GetSpecDetailBySeriesIdRequest request);

    SpecDetailByYearIdResponse getSpecDetailByYearId(SpecDetailByYearIdRequest request);

    /**
     * 根据车系id获取车型详细信息(v1版本)
     */
    GetSpecDetailBySeriesIdV1Response getSpecDetailBySeriesIdV1(GetSpecDetailBySeriesIdRequest request);

    /**
     * 根据车系id获取车型详细信息(v2版本)
     */
    SpecGetSpecDetailBySeriesIdResponse getSpecDetailBySeriesIdV2(SpecGetSpecDetailBySeriesIdRequest request);

    /**
     * 根据多个车型id获取车型代表图
     */
    ApiResult<SpecLogoPage> getSpecLogoBySpecList(GetSpecLogoBySpecListRequest request);

    ApiResult<ParamTypeItemPage> getCarPriceSpecParamListBySpecListV1(GetSpecLogoBySpecListRequest request);

    ApiResult<SpecColorItemPage> getSpecColorBySpecIdV1(GetSpecInfoBySpecIdRequest request);

    GetCarPriceSpecInfoResponse getCarPriceSpecInfoBySeriesId(GetSpecDetailBySeriesIdRequest request);

    GetSpecListBySeriesResponse getSpecListBySeriesV1(GetElectricSpecParamBySeriesIdRequest request);

    SpecPictureCountByConditionResponse getSpecPictureCountByCondition(SpecPictureCountByConditionRequest request);

    Spec25PictureBySpecIdResponse getSpec25PictureBySpecId(Spec25PictureBySpecIdRequest request);

    SpecInnerColorBySpecIdResponse getSpecInnerColorBySpecId(SpecInnerColorBySpecIdRequest request);

    SpecInfoBySeriesIdResponse getSpecInfoBySeriesId(SpecInfoBySeriesIdRequest request);

    SpecPictureCountByConditionResponseV2 getSpecPictureCountByConditionV2(SpecPictureCountByConditionRequestV2 request);

    // GetSpecColorBySpecIdResponse getSpecInnerColorBySpecId(GetSpecInfoBySpecIdRequest request);


    /**
     * 获取多个车型id的车型基本信息
     */
    ApiResult<SpecBaseInfoItems> getSpecBaseInfoBySpecIds(GetSpecBaseInfoBySpecIdsRequest request);

    /**
     * 根据车系id获取车型内饰颜色列表
     */
    ApiResult<SpecColorListItems> getSpecInnerColorBySeriesId(GetSpecInnerColorBySeriesIdRequest request);

    /**
     * 根据车系id获取车型颜色列表
     */
    ApiResult<SpecColorListItems> getSpecSpecColorListBySeriesId(GetSpecSpecColorBySeriesIdRequest request);

    GetSpecNameResponse getSpecNameBySpecId(GetSpecInfoBySpecIdRequest request);

    GetSpecLogoResponse getSpecLogoBySpecId(GetSpecInfoBySpecIdRequest request);

    GetConfigListResponse getConfigListBySpecId(GetConfigListRequest request);

    ApiResult<ParamTypeItemPage> getSpecParamsBySeriesId(GetSpecInfoBySeriesIdRequest request);

    List<KeyValueDto<Integer,String>> getCar25PictureType();

    GetSpecStateCountBySeriesIdResponse getSpecStateCountBySeriesId(GetSpecStateCountBySeriesIdRequest request);

    GetSpecDetailBySpecListResponse getSpecDetailBySpecList(GetSpecDetailBySpecListRequest request);

    GetAppSpecParamBySpecListResponse getAppSpecParamBySpecList(GetSpecLogoBySpecListRequest request);

    List<ParamTypeItems> getParamTypeItems(String specList, boolean filterOpt);

    SpecBaseInfbySpecListResponse specBaseInfbySpecList(SpecBaseInfbySpecListRequest request);

    SpecAllSpecInfoResponse specAllSpecInfo(SpecAllSpecInfoRequest request);

    /**
     * 根据车系id获取各状态下车型数量
     * @param request
     * @return
     */
    ApiResult<SpecCountItem> getSpecCountBySeriesId(GetSpecCountBySeriesIdRequest request);

    GetCarSpecParamListByYearIdResponse getCarSpecParamListByYearId(GetCarSpecParamListByYearIdRequest request);

    GetCarPriceSpecParamListByYearIdResponse getCarPriceSpecParamListByYearId(GetCarPriceSpecParamListByYearIdRequest request);

    GetCarSpecPriceByYearIdResponse getCarSpecPriceByYearId(GetCarSpecPriceByYearIdRequest request);

//    GetCarSpecParamListBySeriesIdResponse getCarSpecParamListBySeriesId(GetCarSpecParamListBySeriesIdRequest request);

    GetCarSeriesNameByFctIdResponse getCarSeriesNameByFctId(GetCarSeriesNameByFctIdRequest request);

    GetLabelPicConfigListResponse getLabelPicConfigListBySpecId(GetSpecInfoBySpecIdRequest request);
    /**
     * 根据日期获取当天上传图片相关的车型列表。
     * @param request
     * @return
     */
    GetPicSpecListByDateResponse getPicSpecListByDate(GetPicSpecListByDateRequest request);

    ApiResult<ParamTypeItemPage> getSpecParamListBySpecId(GetSpecParamListBySpecIdRequest request);

    ConfigListByYearIdResponse getConfigListByYearId(ConfigListByYearIdRequest request);
}
