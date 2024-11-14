package com.autohome.car.api.services;

import autohome.rpc.car.car_api.v1.javascript.SeriesByFactoryRequest;
import autohome.rpc.car.car_api.v1.javascript.SeriesByFactoryResponse;
import autohome.rpc.car.car_api.v1.series.*;
import autohome.rpc.car.car_api.v2.series.*;
import autohome.rpc.car.car_api.v3.series.ConfigWithAiVideoRequest;
import autohome.rpc.car.car_api.v3.series.ConfigWithAiVideoResponse;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.services.models.*;
import com.autohome.car.api.services.models.brand.BrandFactorySeriesItem;
import com.autohome.car.api.services.models.brand.BrandFctSeriesInfo;

import java.util.List;

public interface SeriesService {

    SeriesInfo getSeriesInfo(int seriesId,boolean dispqrcode,boolean needHtmlDecode);
    ApiResult<SeriesItems> getSeriesBaseInfoBySeriesList(GetBaseInfoBySeriesListRequest getBaseInfoBySeriesListRequest);

    /**
     * 获取车系的白底车图，最多支持50个车系同时查询
     */
    ApiResult<SeriesPhotoWhiteLogoPage> getSeriesPhotoWhiteLogoBySeriesId(SeriesIdRequest request);

    ApiResult<SeriesLogoPage> getSeriesLogoBySeriesList(GetSeriesLogoBySeriesListRequest request);

    /**
     * 根据多个车系id获取车系相关参数信息
     */
    ApiResult<List<SeriesParamItem>> getSeriesParamBySeriesListV2(SeriesIdRequest request);

    ApiResult<List<SeriesConfig>> getCarPriceSeriesParamBySeriesList(GetBaseInfoBySeriesListRequest request);

    GetBagInfoBySeriesIdResponse getBagInfoBySeriesIdV2(GetSeriesConfigRequestV2 request);

    GetSeriesByLevelIdResponse getSeriesByLevelId(GetSeriesByLevelIdRequest request);

    GetSeriesColorResponse getSeriesColorBySeriesId(autohome.rpc.car.car_api.v1.series.GetSeriesConfigRequest request);

    /**
     * 根据车系id获取车系相关参数信息
     * @param request
     * @return
     */
    //ApiResult<SeriesDetailItem> GetSeriesParamBySeriesId(GetSeriesParamBySeriesIdRequest request);

    SeriesClassPictureBySeriesIdResponse getSeriesClassPictureBySeriesId (SeriesClassPictureBySeriesIdRequest request);

    SeriesPngLogoBySeriesIdResponse getSeriesPngLogoBySeriesId (SeriesPngLogoBySeriesIdRequest request);

    /**
     *根据车系集合获取车系基础信息
     * @param request
     * @return
     */
    ApiResult<List<SeriesBaseItem>> getSeriesInfoBySeriesList(GetSeriesInfoBySeriesListRequest request);

    /**
     * 根据品牌ID和在售类型获取品牌下车系的报价信息
     * @param request
     * @return
     */
    ApiResult<BrandFctSeriesInfo> getSeriesMenuByBrandIdNew(GetSeriesMenuByBrandIdNewRequest request);

    GetSeriesLogoResponse getSeriesLogoBySeriesId(autohome.rpc.car.car_api.v1.series.GetSeriesConfigRequest request);

    GetSeriesBrandListByBrandIdsResponse getSeriesBrandListByBrandIds(GetSeriesBrandListByBrandIdsRequest request);

    GetSeriesInnerColorBySeriesIdResponse getSeriesInnerColorBySeriesId(GetSeriesInnerColorBySeriesIdRequest request);

    GetAllSeriesBaseInfoResponse getAllSeriesBaseInfo(GetAllSeriesBaseInfoRequest request);

    GetSeriesTagResponse getSeriesTagBySeriesIds(GetSeriesInfoBySeriesListRequest request);

    GetSeriesElectricListResponse getSeriesElectricList(GetSeriesElectricListRequest request);

    SeriesHaveCrashInfoResponse seriesHaveCrashInfo(SeriesHaveCrashInfoRequest request);

    GetSpecBySeriesResponse getSpecBySeries(GetSpecBySeriesRequest request);

    GetCrashTestBySeriesIdResponse getCrashTestBySeriesId(GetCrashTestBySeriesIdRequest request);

    GetAllSeriesResponse getAllSeries(GetAllSeriesRequest request);

    SeriesByFactoryResponse seriesByFactory(SeriesByFactoryRequest request);
    /**
     * 据品牌获取品牌下车系名称等信息
     * @param request
     * @return
     */
    ApiResult<BrandFactorySeriesItem> getSeriesNameByBrandId(GetSeriesNameByBrandIdRequest request);
    /**
     * 根据系列id获取系列名称
     * @param request
     * @return
     */
    GetSeriesNameBySeriesIdResponse getSeriesNameBySeriesId(GetSeriesNameBySeriesIdRequest request);

    /**
     * app接口需求 v8.8.5产品库源接口需求
     * 获取车系当前状态下最高配置的几项参数配置信息
     * @param request
     * @return
     */
    GetSeriesBaseParamBySeriesIdResponse getSeriesBaseParamBySeriesId(GetSeriesBaseParamBySeriesIdRequest request);

    /**
     * 根据品牌，销售状态，页数，页码 搜索车系数据
     * @param request
     * @return
     */
    GetSeriesByBrandAndStateResponse getSeriesByBrandAndState(GetSeriesByBrandAndStateRequest request);

    /**
     * 根据品牌id获取报价库车系菜单
     * @param request
     * @return
     */
    GetSeriesMenuByBrandIdResponse getSeriesMenuByBrandId(GetSeriesMenuByBrandIdRequest request);

    GetSeriesInfoByBrandIdResponse getSeriesInfoByBrandId(GetSeriesInfoByBrandIdRequest request);

    /**
     * 根据车系id配置选装包信息
     * @param request
     * @return
     */
    GetConfigBagBySeriesIdResponse getConfigBagBySeriesId(GetConfigBagBySeriesIdRequest request);

    /**
     * 根据车系名称获取车系ID
     * @param request
     * @return
     */
    GetSeriesIdBySeriesNameResponse getSeriesIdBySeriesName(GetSeriesIdBySeriesNameRequest request);

    GetGetSeriesStateInfoResponse getSeriesStateInfo(GetGetSeriesStateInfoRequest request);

    GetCrashTestSeriesRankResponse getCrashTestSeriesRank(GetCrashTestSeriesRankRequest request);
    GetSeriesHotResponse getSeriesHot(GetSeriesHotRequest request);

    Series25PointToVRResponse series25PointToVR(Series25PointToVRRequest request);

    CrashTestSeriesListResponse crashTestSeriesList(CrashTestSeriesListRequest request);
    GetSeriesAllBaseInfoResponse getSeriesAllBaseInfo(GetSeriesAllBaseInfoRequest request);

    SelectElectricListResponse getSelectElectricList(SelectElectricListRequest request);

    /**
     * APP参数配置页展示智能类视频
     * @param request
     * @return
     */
    ConfigWithAiVideoResponse getConfigWithAiVideoForApp(ConfigWithAiVideoRequest request);
}
