package com.autohome.car.api.provider.services;

import autohome.rpc.car.car_api.v1.series.*;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.entities.SeriesBaseEntity;
import com.autohome.car.api.data.popauto.entities.SeriesSearchEntity;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.SeriesService;
import com.autohome.car.api.services.basic.BrandBaseService;
import com.autohome.car.api.services.basic.FactoryBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.*;
import com.autohome.car.api.services.impls.AutoCacheServiceImpl;
import com.autohome.car.api.services.models.*;
import com.autohome.car.api.services.models.brand.BrandFactorySeriesItem;
import com.autohome.car.api.services.models.brand.BrandFctSeriesInfo;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@DubboService
@RestController
public class SeriesServiceGrpcImpl extends DubboSeriesServiceTriple.SeriesServiceImplBase {

    @Autowired
    private SeriesService seriesService;

    @Autowired
    SeriesInfoService seriesInfoService;

    @Autowired
    SeriesConfigService seriesConfigService;

    @Autowired
    CarManuePicService carManuePicService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SeriesSearchService seriesSearchService;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    AutoCacheServiceImpl autoCacheServiceImpl;

    @Autowired
    FactoryBaseService factoryBaseService;

    @Autowired
    BrandBaseService brandBaseService;

    @Autowired
    SeriesAllSearchService seriesAllSearchService;

    @Override
    @GetMapping("/v1/CarPrice/Series_BaseInfoBySeriesList.ashx")
    public GetBaseInfoBySeriesListResponse getSeriesBaseInfoBySeriesList(GetBaseInfoBySeriesListRequest request) {
        ApiResult<SeriesItems> apiResult = seriesService.getSeriesBaseInfoBySeriesList(request);
        if (ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()) {
            return GetBaseInfoBySeriesListResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetBaseInfoBySeriesListResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetBaseInfoBySeriesListResponse.Result.class);
        return GetBaseInfoBySeriesListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    @Override
    @GetMapping("/v1/carprice/series_infobyseriesid.ashx")
    public GetSeriesInfoResponse getSeriesInfo(GetSeriesInfoRequest request) {
        if (request.getSeriesid() <= 0) {
            return GetSeriesInfoResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        SeriesInfo seriesInfo = seriesInfoService.get(request.getSeriesid(), request.getDispqrcode() == 1, request.getAppid().equals("app") || request.getAppid().equals("app.iphone"));
        GetSeriesInfoResponse.Result result = MessageUtil.toMessage(seriesInfo, GetSeriesInfoResponse.Result.class);
        GetSeriesInfoResponse.Builder builder = GetSeriesInfoResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");

        if (result != null) {
            builder.setResult(result);
        }
        return builder.build();
    }

    @Override
    @GetMapping("/v1/carprice/series_parambyseriesid.ashx")
    public GetSeriesConfigResponse getSeriesConfig(GetSeriesConfigRequest request) {
        if (request.getSeriesid() <= 0) {
            return GetSeriesConfigResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        SeriesConfig seriesConfig = seriesConfigService.get(request.getSeriesid());
        GetSeriesConfigResponse.Result result = MessageUtil.toMessage(seriesConfig, GetSeriesConfigResponse.Result.class);

        GetSeriesConfigResponse.Builder responseBuilder = autohome.rpc.car.car_api.v1.series.GetSeriesConfigResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");
        if (result != null) {
            responseBuilder.setResult(result);
        }
        return responseBuilder.build();
    }

    /**
     * 根据多个车系id获取车系代表图
     */
    @Override
    @GetMapping("/v1/carprice/series_logobyserieslist.ashx")
    public GetSeriesLogoBySeriesListResponse getSeriesLogoBySeriesList(GetSeriesLogoBySeriesListRequest request) {
        ApiResult<SeriesLogoPage> apiResult = seriesService.getSeriesLogoBySeriesList(request);
        SeriesLogoPage seriesLogoPage = apiResult.getResult();

        GetSeriesLogoBySeriesListResponse.Builder builder = GetSeriesLogoBySeriesListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (Objects.nonNull(seriesLogoPage)) {
            builder.setResult(MessageUtil.toMessage(seriesLogoPage, GetSeriesLogoBySeriesListResponse.Result.class));
        }
        return builder.build();
    }

    /**
     * 二期开发
     */
    @Override
    @GetMapping("/v1/carprice/series_parambyserieslist.ashx")
    public GetSeriesParamBySeriesListResponse getSeriesParamBySeriesList(GetBaseInfoBySeriesListRequest request) {
        ApiResult<List<SeriesConfig>> apiResult = seriesService.getCarPriceSeriesParamBySeriesList(request);
        List<SeriesConfig> result = apiResult.getResult();
        GetSeriesParamBySeriesListResponse.Builder builder = GetSeriesParamBySeriesListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (CollectionUtils.isNotEmpty(result)) {
            builder.addAllResult(MessageUtil.toMessageList(result, GetSeriesParamBySeriesListResponse.Result.class));
        }
        return builder.build();
    }

    @Override
    @GetMapping("/v1/carprice/series_11infobylevelid.ashx")
    public GetSeriesByLevelIdResponse getSeriesByLevelId(GetSeriesByLevelIdRequest request) {
        return seriesService.getSeriesByLevelId(request);
    }

    @Override
    @GetMapping("/v1/carprice/series_colorbyseriesid.ashx")
    public GetSeriesColorResponse getSeriesColor(GetSeriesConfigRequest request) {
        return seriesService.getSeriesColorBySeriesId(request);
    }

    @Override
    @GetMapping("/v1/carpic/series_classpicturebyseriesId.ashx")
    public SeriesClassPictureBySeriesIdResponse getSeriesClassPictureBySeriesId(SeriesClassPictureBySeriesIdRequest request) {
        return seriesService.getSeriesClassPictureBySeriesId(request);
    }



    @Override
    @GetMapping("/v1/car/Series_ParamBySeriesId.ashx")
    public GetSeriesConfigByIdResponse getSeriesConfigById(GetSeriesConfigByIdRequest request) {
        if (request.getSeriesid() <= 0) {
            return GetSeriesConfigByIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        SeriesConfig seriesConfig = seriesConfigService.get(request.getSeriesid());
        GetSeriesConfigByIdResponse.Result result = MessageUtil.toMessage(seriesConfig, GetSeriesConfigByIdResponse.Result.class);
        GetSeriesConfigByIdResponse.Builder responseBuilder = GetSeriesConfigByIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");
        if (result != null) {
            GetSeriesConfigByIdResponse.Result.Builder resultBuilder = GetSeriesConfigByIdResponse.Result.newBuilder(result);
            resultBuilder.setClubisshow(seriesConfig.getBbsShow());
            resultBuilder.setLevelranknumber(seriesConfig.getLevelRank());
            resultBuilder.setMinprice(seriesConfig.getTempMinPrice());
            resultBuilder.setMaxprice(seriesConfig.getTempMaxPrice());
            if (!seriesConfig.getSeriesConfigFilePath().isEmpty()) {
                String encodedPath = UrlUtils.encode(seriesConfig.getSeriesConfigFilePath(), "gb2312");
                String url = String.format("http://dl.car.autohome.com.cn/download/SeriesConfigDownLoad.ashx?picpath=%s", encodedPath);
                resultBuilder.setParamdownloadurl(url);
            }
            responseBuilder.setResult(resultBuilder);
        }

        return responseBuilder.build();
    }

    @Override
    @GetMapping("/v1/carprice/series_parambyspecId.ashx")
    public GetSeriesConfigBySpecIdResponse getSeriesParamBySpecId(GetSeriesConfigBySpecIdRequest request){
        if (request.getSpecid() <= 0) {
            return GetSeriesConfigBySpecIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        GetSeriesConfigBySpecIdResponse.Builder responseBuilder = GetSeriesConfigBySpecIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功");
        SpecBaseInfo specInfo = specBaseService.get(request.getSpecid()).join();
        if(specInfo == null){
            return responseBuilder.build();
        }
        SeriesConfig seriesConfig = seriesConfigService.get(specInfo.getSeriesId());
        GetSeriesConfigBySpecIdResponse.Result result = MessageUtil.toMessage(seriesConfig, GetSeriesConfigBySpecIdResponse.Result.class);
        if (result != null) {
            GetSeriesConfigBySpecIdResponse.Result.Builder resultBuilder = GetSeriesConfigBySpecIdResponse.Result.newBuilder(result);
            resultBuilder.setMinprice(seriesConfig.getTempMinPrice());
            resultBuilder.setMaxprice(seriesConfig.getTempMaxPrice());
            resultBuilder.setSpecname(specInfo.getSpecName());
            resultBuilder.setState(seriesConfig.getTempState());
            resultBuilder.addAllTransmissionitems(seriesConfig.getTransmissionitems());
            responseBuilder.setResult(resultBuilder);
        }

        return responseBuilder.build();
    }

    @Override
    @GetMapping("/v1/CarPrice/Series_ParamConfigIsShow.ashx")
    public SeriesParamConfigIsShowResponse seriesParamConfigIsShow(SeriesParamConfigIsShowRequest request) {
        SeriesParamConfigIsShowResponse.Builder builder = SeriesParamConfigIsShowResponse.newBuilder();
        builder.setReturnCode(0);
        builder.setReturnMsg("成功");
        if (request.getSeriesid() <= 0) {
            return builder.setResult(
                    SeriesParamConfigIsShowResponse.Result.newBuilder()
                            .setIsshow(false)
                            .setUrl("")
            ).build();
        }

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(request.getSeriesid()).join();
        if(seriesBaseInfo==null){
            return builder.setResult(
                    SeriesParamConfigIsShowResponse.Result.newBuilder()
                            .setIsshow(false)
                            .setUrl("")
            ).build();
        }

        builder.setResult(
                SeriesParamConfigIsShowResponse.Result.newBuilder()
                        .setIsshow(seriesBaseInfo.getShowCount()>0)
                        .setUrl(String.format("http://car.autohome.com.cn/config/series/%s.html", request.getSeriesid()))
        );
        return builder.build();
    }

    @Override
    @GetMapping("/v1/CarPic/Series_PicIsShow.ashx")
    public SeriesPicIsShowResponse seriesPicIsShow(SeriesPicIsShowRequest request) {
        SeriesPicIsShowResponse.Builder builder = SeriesPicIsShowResponse.newBuilder();
        builder.setReturnCode(0);
        builder.setReturnMsg("成功");
        if (request.getSeriesid() <= 0) {
            return builder.setResult(
                    SeriesPicIsShowResponse.Result.newBuilder()
                            .setIsshow(false)
                            .setUrl("")
            ).build();
        }

        boolean isShow = carManuePicService.get(null).contains(request.getSeriesid());

        builder.setResult(
                SeriesPicIsShowResponse.Result.newBuilder()
                        .setIsshow(isShow)
                        .setUrl(isShow?String.format("http://car.autohome.com.cn/pic/series/%s.html", request.getSeriesid()):"")
        );
        return builder.build();
    }

    /**
     * 根据车系集合获取车系基础信息
     * @param request
     * @return
     */
    @GetMapping("/v1/App/Series_SeriesInfoBySeriesList.ashx")
    @Override
    public GetSeriesInfoBySeriesListResponse getSeriesInfoBySeriesList(GetSeriesInfoBySeriesListRequest request) {
        ApiResult<List<SeriesBaseItem>> apiResult = seriesService.getSeriesInfoBySeriesList(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetSeriesInfoBySeriesListResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        List<GetSeriesInfoBySeriesListResponse.Result> result = MessageUtil.toMessageList(apiResult.getResult(),GetSeriesInfoBySeriesListResponse.Result.class);
        return GetSeriesInfoBySeriesListResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .addAllResult(result)
                .build();
    }

    /**
     * 根据品牌ID和在售类型获取品牌下车系的报价信息
     * @param request
     * @return
     */
    @GetMapping("/v1/App/Series_MenuByBrandIdNew.ashx")
    @Override
    public GetSeriesMenuByBrandIdNewResponse getSeriesMenuByBrandIdNew(GetSeriesMenuByBrandIdNewRequest request) {
        ApiResult<BrandFctSeriesInfo> apiResult = seriesService.getSeriesMenuByBrandIdNew(request);
        if(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode()){
            return GetSeriesMenuByBrandIdNewResponse.newBuilder()
                    .setReturnCode(apiResult.getReturncode())
                    .setReturnMsg(apiResult.getMessage())
                    .build();
        }
        GetSeriesMenuByBrandIdNewResponse.Result result = MessageUtil.toMessage(apiResult.getResult(),GetSeriesMenuByBrandIdNewResponse.Result.class);
        return GetSeriesMenuByBrandIdNewResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage())
                .setResult(result)
                .build();
    }

    /**
     * 根据车系id获取车系代表图
     */
    @Override
    @GetMapping("/v1/carprice/series_logobyseriesid.ashx")
    public GetSeriesLogoResponse getSeriesLogoBySeriesId(GetSeriesConfigRequest request) {
        return seriesService.getSeriesLogoBySeriesId(request);
    }

    @Override
    @GetMapping("/v1/App/Series_SeriesListByBrandIds.ashx")
    public GetSeriesBrandListByBrandIdsResponse getSeriesBrandListByBrandIds(GetSeriesBrandListByBrandIdsRequest request) {
        return seriesService.getSeriesBrandListByBrandIds(request);
    }


    @Override
    @GetMapping("/v1/carprice/series_innercolorbyseriesid.ashx")
    public GetSeriesInnerColorBySeriesIdResponse getSeriesInnerColorBySeriesId(GetSeriesInnerColorBySeriesIdRequest request) {
        return seriesService.getSeriesInnerColorBySeriesId(request);
    }

    @Override
    @GetMapping("/v1/carprice/series_menubysearch.ashx")
    public SeriesMenuBySearchResponse seriesMenuBySearch(SeriesMenuBySearchRequest request) {
        SeriesMenuBySearchResponse.Builder builder = SeriesMenuBySearchResponse.newBuilder();

        int size = request.getSize() <= 0 ? 20 : request.getSize();
        int page = request.getPage() <= 0 ? 1 : request.getPage();

        List<SeriesSearchEntity> list = seriesSearchService.get();

        switch (request.getState().toUpperCase()) {
            case "0X000C":
                list = list.stream().filter(x -> x.getSeriesIsPublic() == 1).collect(Collectors.toList());
                break;
            case "0X0010":
                list = list.stream().filter(x -> x.getSeriesIsPublic() == 2).collect(Collectors.toList());
                break;
            case "0X000E":
                list = list.stream().filter(x -> x.getSeriesIsPublic() <= 1).collect(Collectors.toList());
                break;
            case "0X001C":
                list = list.stream().filter(x -> x.getSeriesIsPublic() >= 1).collect(Collectors.toList());
                break;
            case "0X001E":
                break;
            default:
                return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }

        if (request.getBrandid() > 0){
            list = list.stream().filter(x->x.getBrandId() == request.getBrandid()).collect(Collectors.toList());;
        }
        if (request.getFctid() > 0) {
            list = list.stream().filter(x -> x.getFctId() == request.getFctid()).collect(Collectors.toList());;
        }
        if (request.getMinprice() > 0 && request.getMaxprice() > 0) {
            list = list.stream().filter(x->
                            (x.getSeriesPriceMax() >= request.getMinprice() && x.getSeriesPriceMax() <= request.getMaxprice())
                    || (x.getSeriesPriceMin() >= request.getMinprice() && x.getSeriesPriceMin() <= request.getMaxprice())
                    || (x.getSeriesPriceMin() <= request.getMinprice() && x.getSeriesPriceMax() >= request.getMaxprice())
            ).collect(Collectors.toList());;
        }
        else if (request.getMinprice() > 0) {
            list = list.stream().filter(x->x.getSeriesPriceMax() >= request.getMinprice()).collect(Collectors.toList());;
        }
        else  if (request.getMinprice() > 0) {
            list = list.stream().filter(x->x.getSeriesPriceMin() <= request.getMaxprice()).collect(Collectors.toList());;
        }
        if (request.getLevelid() == 14 || request.getLevelid() == 15) {
            list = list.stream().filter(x->x.getLevelId() >=14 && x.getLevelId()<=15).collect(Collectors.toList());;
        }
        else if (request.getLevelid() == 9) {
            list = list.stream().filter(x->x.getLevelId() >=16 && x.getLevelId()<=20).collect(Collectors.toList());;
        }
        else if (request.getLevelid() > 0) {
            list = list.stream().filter(x->x.getLevelId() == request.getLevelid()).collect(Collectors.toList());;
        }
        if (request.getCountryid() > 0) {
            list = list.stream().filter(x->x.getCountry() == request.getCountryid()).collect(Collectors.toList());;
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            list = list.stream().filter(x->x.getSeriesName().indexOf(request.getKeyword())>=0).collect(Collectors.toList());;
        }
        if (StringUtils.isNotBlank(request.getFirstletter()) && request.getFirstletter().length() == 1){
            list = list.stream().filter(x->x.getSeriesFirstLetter().equals(request.getFirstletter())).collect(Collectors.toList());;
        }
        switch (request.getStore()) {
            //按人气降序排序
            case 1:
                list = list.stream().sorted(Comparator.comparing(SeriesSearchEntity::getSeriesRank).reversed()).collect(Collectors.toList());
                break;
            //价格升序
            case 2:
                list = list.stream().sorted(Comparator.comparing(SeriesSearchEntity::getSeriesPriceMin)).collect(Collectors.toList());
                break;
            //车系首字母升序
            case 3:
                list = list.stream().sorted(Comparator.comparing(SeriesSearchEntity::getSeriesFirstLetter)).collect(Collectors.toList());
                break;
            //价格降序
            case 4:
                list = list.stream().sorted(Comparator.comparing(SeriesSearchEntity::getSeriesPriceMin).reversed()).collect(Collectors.toList());
                break;
        }
        List<Integer> seriesIds = list.stream().map(x->x.getSeriesId()).collect(Collectors.toList());

        SeriesMenuBySearchResponse.Result.Builder result = SeriesMenuBySearchResponse.Result.newBuilder();
        result.setPageindex(page);
        result.setSize(size);
        result.setTotal(seriesIds.size());

        seriesIds = seriesIds.stream().skip((page -1)*size).limit(size).collect(Collectors.toList());
        Map<Integer,SeriesConfig> configs = seriesConfigService.getMap(seriesIds);

        for (Integer serisId : seriesIds) {
            SeriesConfig config = configs.get(serisId);
            if(config == null)
                continue;
            result.addSeriesitems(
                    SeriesMenuBySearchResponse.Result.Seriesitem.newBuilder()
                            .setId(config.getId())
                            .setName(config.getName())
                            .setLogo(config.getLogo())
                            .setPnglogo(config.getPnglogo())
                            .setBrandname(config.getBrandname())
                            .setFctname(config.getFctname())
                            .setLevelname(config.getLevelname())
                            .setMinprice(config.getMinprice())
                            .setMaxprice(config.getMaxprice())
                            .addAllGearbox(config.getTransmissionitems())
                            .addAllDisplacement(config.getDisplacementitems())
                            .addAllStructure(config.getStructitems())
                            .setSeriesstate(config.getState())
            );
        }

        return builder.setReturnCode(0).setReturnMsg("成功").setResult(result).build();
    }
    @Override
    @GetMapping("/v1/carprice/series_getallname.ashx")
    public GetAllSeriesBaseInfoResponse getAllSeriesBaseInfo(GetAllSeriesBaseInfoRequest request) {
        return seriesService.getAllSeriesBaseInfo(request);
    }
    @Override
    @GetMapping("/v1/App/AutoTag_SeriesTagBySeriesIds.ashx")
    public GetSeriesTagResponse getSeriesTagBySeriesIds(GetSeriesInfoBySeriesListRequest request) {
        return seriesService.getSeriesTagBySeriesIds(request);
    }
    @Override
    @GetMapping("/Crash/SeriesHaveCrashInfo.ashx")
    public SeriesHaveCrashInfoResponse seriesHaveCrashInfo(SeriesHaveCrashInfoRequest request){
        return seriesService.seriesHaveCrashInfo(request);
    }

    @Override
    @GetMapping("/Crash/CrashTest_BySeriesId.ashx")
    public GetCrashTestBySeriesIdResponse getCrashTestBySeriesId(GetCrashTestBySeriesIdRequest request){
        return seriesService.getCrashTestBySeriesId(request);
    }

    /**
     * 根据品牌获取品牌下车系名称等信息
     * @param request
     * @return
     */
    @GetMapping({"/v1/carprice/series_namebybrandid.ashx","//v1/carprice/series_namebybrandid.ashx"})
    @Override
    public GetSeriesNameByBrandIdResponse getSeriesNameByBrandId(GetSeriesNameByBrandIdRequest request) {
        ApiResult<BrandFactorySeriesItem> apiResult = seriesService.getSeriesNameByBrandId(request);
        GetSeriesNameByBrandIdResponse.Builder builder = GetSeriesNameByBrandIdResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if(Objects.nonNull(apiResult.getResult())){
            GetSeriesNameByBrandIdResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSeriesNameByBrandIdResponse.Result.class);
            builder.setResult(result);
        }
        return builder.build();
    }

    /**
     * 根据系列id获取系列名称
     * @param request
     * @return
     */
    @GetMapping("/v1/CarPrice/Series_GetSeriesNameBySeriesId.ashx")
    @Override
    public GetSeriesNameBySeriesIdResponse getSeriesNameBySeriesId(GetSeriesNameBySeriesIdRequest request) {
        return seriesService.getSeriesNameBySeriesId(request);
    }

    /**
     * app接口需求 v8.8.5产品库源接口需求
     * 获取车系当前状态下最高配置的几项参数配置信息
     * @param request
     * @return
     */
    @GetMapping("/v1/App/Series_BaseParamBySeriesId.ashx")
    @Override
    public GetSeriesBaseParamBySeriesIdResponse getSeriesBaseParamBySeriesId(GetSeriesBaseParamBySeriesIdRequest request) {
        return seriesService.getSeriesBaseParamBySeriesId(request);
    }

    /**
     * 根据品牌，销售状态，页数，页码 搜索车系数据
     * @param request
     * @return
     */
    @GetMapping("/v1/CarPrice/Series_ByBrand.ashx")
    @Override
    public GetSeriesByBrandAndStateResponse getSeriesByBrandAndState(GetSeriesByBrandAndStateRequest request) {
        return seriesService.getSeriesByBrandAndState(request);
    }

    /**
     * 根据品牌id获取报价库车系菜单
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/series_menubybrandid.ashx")
    @Override
    public GetSeriesMenuByBrandIdResponse getSeriesMenuByBrandId(GetSeriesMenuByBrandIdRequest request) {
        return seriesService.getSeriesMenuByBrandId(request);
    }

    /**
     *
     * @param request
     * @return
     */
    @GetMapping("/v1/Mweb/SeriesInfoByBrand.ashx")
    @Override
    public GetSeriesInfoByBrandIdResponse getSeriesInfoByBrandId(GetSeriesInfoByBrandIdRequest request) {
        return seriesService.getSeriesInfoByBrandId(request);
    }

    @GetMapping("/v1/carprice/SeriesUpcoming.ashx")
    @Override
    public GetGetSeriesStateInfoResponse getSeriesStateInfo(GetGetSeriesStateInfoRequest request) {
        return seriesService.getSeriesStateInfo(request);
    }

    /**
     * 根据车系id配置选装包信息
     * @param request
     * @return
     */
    @GetMapping("/v1/car/Config_BagBySeriesId.ashx")
    @Override
    public GetConfigBagBySeriesIdResponse getConfigBagBySeriesId(GetConfigBagBySeriesIdRequest request) {
        return seriesService.getConfigBagBySeriesId(request);
    }

    /**
     *根据车系名称获取车系ID
     * @param request
     * @return
     */
    @GetMapping("/v1/carprice/series_idbyseriesname.ashx")
    @Override
    public GetSeriesIdBySeriesNameResponse getSeriesIdBySeriesName(GetSeriesIdBySeriesNameRequest request) {
        return seriesService.getSeriesIdBySeriesName(request);
    }

    @GetMapping("/v1/carprice/series_infowithpagebylevelid.ashx")
    @Override
    public SeriesWithPageByLevelIdResponse seriesWithPageByLevelId(SeriesWithPageByLevelIdRequest request){
        SeriesWithPageByLevelIdResponse.Builder builder = SeriesWithPageByLevelIdResponse.newBuilder();
        int levelId = request.getLevelid();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        int page = request.getPage() == 0 ? 1 : request.getPage();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if (levelId == 0 || size < 1 || page < 1 || state == SpecStateEnum.NONE) {
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }

        SeriesWithPageByLevelIdResponse.Result.Builder result = SeriesWithPageByLevelIdResponse.Result.newBuilder();
        int start = (page - 1) * size + 1;
        int end = page * size;
        int total = autoCacheServiceImpl.getSeriesCountByLevelId(levelId, state);
        List<KeyValueDto<Integer, Integer>> seriesList = autoCacheServiceImpl.getSeriesByPageLevelId(levelId, state, start, end);
        if(!CollectionUtils.isEmpty(seriesList)){
            seriesList = seriesList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            List<Integer> seriesIds = seriesList.stream().map(KeyValueDto::getKey).collect(Collectors.toList());
            Map<Integer,SeriesConfig> seriesMap = seriesConfigService.getMap(seriesIds);
            for(KeyValueDto<Integer, Integer> item : seriesList){
                SeriesConfig series = seriesMap != null ? seriesMap.get(item.getKey()) : null;
                result.addSeriesitems(SeriesWithPageByLevelIdResponse.Result.Seriesitem.newBuilder()
                        .setId(item.getKey())
                        .setIspublic(item.getValue())
                        .setName(series != null && series.getName() != null ? series.getName() : "")
                        .setLogo(series != null && series.getLogo() != null ? series.getLogo() : "")
                        .setMinprice(series != null ? series.getTempMinPrice() : 0)
                        .setMaxprice(series != null ? series.getTempMaxPrice() : 0)
                        .setState(series != null ? series.getTempState() : 0)
                        .setPicnum(series != null ? series.getPicnum() : 0));
            }
        }
        result.setLevelid(levelId).setSize(size).setPageindex(page).setTotal(total);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v1/CarShow/show_seriespictureitemsbycondition.ashx")
    @Override
    public GetCarShowSeriesPicByConditionResponse getCarShowSeriesPicByCondition(GetCarShowSeriesPicByConditionRequest request){
        GetCarShowSeriesPicByConditionResponse.Builder builder = GetCarShowSeriesPicByConditionResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int showId = request.getShowid();
        int pageIndex = request.getPageindex() == 0 ? 1 : request.getPageindex();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        int maxpicid = request.getMaxpicid();

        if ((seriesId == 0 && showId == 0) || pageIndex < 1 || size < 0) {
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<GetCarShowSeriesPicByConditionResponse.Picitem> picItems = new ArrayList<>();
        int total = 0;
        SeriesConfig seriesConfig = seriesConfigService.get(seriesId);
        if(seriesConfig != null){
            int fctId = seriesId > 10000 ? seriesId / 10000 : seriesConfig.getFctid();
            FactoryBaseInfo fctInfo = factoryBaseService.getFactory(fctId);
            int brandId = seriesId > 10000 ? autoCacheServiceImpl.getFctGBrandsById(seriesId).getValue() : seriesConfig.getBrandid();
            BrandBaseInfo brandInfo  = brandBaseService.get(brandId).join();
            List<KeyValueDto<Integer,String>> showCars = autoCacheServiceImpl.getShowCarsImg(showId, seriesId);
            List<KeyValueDto<Integer,String>> copyShowCars = new ArrayList<>(showCars);
            if(maxpicid > 0){
                copyShowCars = copyShowCars.stream().filter(x -> x.getKey() < maxpicid).collect(Collectors.toList());
            }
            total = copyShowCars.size();// 图片数量
            int length = Math.min(pageIndex * size, total);
            for (int i = (pageIndex - 1) * size; i < length; i++){
                KeyValueDto<Integer,String> item = copyShowCars.get(i);
                String filepath = ImageUtil.getFullImagePath(item.getValue().replace("http://www.autoimg.cn", "").replace("http://img.autohome.com.cn", ""));
                picItems.add(GetCarShowSeriesPicByConditionResponse.Picitem.newBuilder()
                        .setId(item.getKey())
                        .setFilepath(filepath)
                        .setSeriesid(seriesId)
                        .setSeriesname(seriesConfig.getName())
                        .setFctid(fctId)
                        .setFctname(fctInfo != null && fctInfo.getName() != null ? fctInfo.getName() : "")
                        .setBrandid(brandId)
                        .setBrandname(brandInfo != null && brandInfo.getName() != null ? brandInfo.getName() : "")
                        .build()
                );
            }
        }

        GetCarShowSeriesPicByConditionResponse.Result.Builder result = GetCarShowSeriesPicByConditionResponse.Result.newBuilder();
        result.setShowid(showId).setPageindex(pageIndex).setSize(size).setTotal(total).addAllPicitems(picItems);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/Crash/CrashTest_SeriesRank.ashx")
    @Override
    public GetCrashTestSeriesRankResponse getCrashTestSeriesRank(GetCrashTestSeriesRankRequest request) {
        return seriesService.getCrashTestSeriesRank(request);
    }

    @Override
    @GetMapping("/v1/carprice/series_hot.ashx")
    public GetSeriesHotResponse getSeriesHot(GetSeriesHotRequest request){
        return  seriesService.getSeriesHot(request);
    }

    @Override
    @GetMapping("/v1/pointlocation/Series_25PointToVR.ashx")
    public Series25PointToVRResponse series25PointToVR(Series25PointToVRRequest request){
        return seriesService.series25PointToVR(request);
    }

    @Override
    @GetMapping("/Crash/CrashTest_SeriesList.ashx")
    public CrashTestSeriesListResponse crashTestSeriesList(CrashTestSeriesListRequest request){
        return seriesService.crashTestSeriesList(request);
    }


    /**
     * 获取车系最新更新图片列表
     * @param request
     * @return
     */
    @GetMapping("/v1/carpic/series_latestupdatelist.ashx")
    @Override
    public GetSeriesLatestUpdateListResponse getSeriesLatestUpdateList(GetSeriesLatestUpdateListRequest request) {
        return GetSeriesLatestUpdateListResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @GetMapping("/v1/carprice/series_getallprice.ashx")
    @Override
    public GetSeriesAllPriceResponse getSeriesAllPrice(GetSeriesAllPriceRequest request){
        GetSeriesAllPriceResponse.Builder builder = GetSeriesAllPriceResponse.newBuilder();
        List<SeriesBaseEntity> seriesList = seriesAllSearchService.get();
        if(CollectionUtils.isEmpty(seriesList)){
            return builder.setReturnCode(0).setReturnMsg("成功").build();
        }
        GetSeriesAllPriceResponse.Result.Builder result = GetSeriesAllPriceResponse.Result.newBuilder();
        for(SeriesBaseEntity item : seriesList){
            result.addSeriesitems(GetSeriesAllPriceResponse.SeriesItem.newBuilder()
                    .setId(item.getId())
                    .setMaxprice(item.getPriceMax())
                    .setMinprice(item.getPriceMin())
            );
        }
        result.setTotal(seriesList.size());
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @GetMapping("/v1/carprice/series_info.ashx")
    @Override
    public SeriesInfoByStateResponse seriesInfoByState(SeriesInfoByStateRequest request) {
        SeriesInfoByStateResponse.Builder builder = SeriesInfoByStateResponse.newBuilder();
        List<SeriesBaseEntity> seriesList = seriesAllSearchService.get();
        if (CollectionUtils.isEmpty(seriesList)) {
            return builder.setReturnCode(0).setReturnMsg("成功").build();
        }
        SpecStateEnum state = Spec.getSpecState(request.getState());
        if(state == SpecStateEnum.NONE){
            return builder.setReturnCode(102).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<SeriesBaseEntity> list = new ArrayList<>(seriesList);
        switch (state) {
            case SELL_12:
                list = list.stream().filter(x -> x.getIsPublic() == 1).collect(Collectors.toList());
                break;
            case STOP_SELL:
                list = list.stream().filter(x -> x.getIsPublic() == 2).collect(Collectors.toList());
                break;
            case SELL_15:
                list = list.stream().filter(x -> x.getIsPublic() <= 1).collect(Collectors.toList());
                break;
            case SELL_28:
                list = list.stream().filter(x -> x.getIsPublic() >= 1).collect(Collectors.toList());
                break;
            case SELL_31:
                break;
            default:
                return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SeriesInfoByStateResponse.Result.Builder result = SeriesInfoByStateResponse.Result.newBuilder();
        for(SeriesBaseEntity item : list){
            result.addSeriesitems(SeriesInfoByStateResponse.Result.Seriesitem.newBuilder()
                    .setId(item.getId())
                    .setName(null != item.getName() ? HtmlUtils.decode(item.getName()) : "")
                    .setLogo(Strings.isNotBlank(item.getImg()) ? ImageUtil.getFullImagePath(item.getImg()) : "")
            );
        }
        result.setTotal(list.size());
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }
}