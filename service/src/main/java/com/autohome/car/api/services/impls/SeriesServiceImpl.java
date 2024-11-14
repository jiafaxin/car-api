package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.javascript.SeriesByFactoryRequest;
import autohome.rpc.car.car_api.v1.javascript.SeriesByFactoryResponse;
import autohome.rpc.car.car_api.v1.series.*;
import autohome.rpc.car.car_api.v2.series.*;
import autohome.rpc.car.car_api.v3.series.ConfigWithAiVideoRequest;
import autohome.rpc.car.car_api.v3.series.ConfigWithAiVideoResponse;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.data.popauto.entities.epiboly.EpibolyAiVideoOrderDetailEntity;
import com.autohome.car.api.data.popauto.entities.epiboly.EpibolyAiVideoOrderEntity;
import com.autohome.car.api.data.popauto.entities.point.PointParamConfigEntity;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.SeriesService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.epiboly.EpibolyAiVideoOrderDetailService;
import com.autohome.car.api.services.basic.epiboly.EpibolyAiVideoOrderService;
import com.autohome.car.api.services.basic.models.*;
import com.autohome.car.api.services.basic.point.PointParamConfigService;
import com.autohome.car.api.services.basic.series.SeriesAllSearchService;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.series.SeriesElectricService;
import com.autohome.car.api.services.basic.series.SeriesLevelService;
import com.autohome.car.api.services.basic.specs.SpecConfigBagNewService;
import com.autohome.car.api.services.basic.specs.SpecConfigBagService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.*;
import com.autohome.car.api.services.models.brand.BrandFactorySeriesItem;
import com.autohome.car.api.services.models.brand.BrandFctSeriesInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.LocalDateUtils.DATE_TIME_PATTERN_TWO;
import static com.autohome.car.api.common.ReturnMessageEnum.*;


@Service
@Slf4j
public class SeriesServiceImpl implements SeriesService {

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;

    @Autowired
    SpecColorMapper specColorMapper;
    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    FactoryBaseService factoryBaseService;

    @Autowired
    BrandBaseService brandBaseService;

    @Autowired
    LevelBaseService levelBaseService;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    QRCodeService qrCodeService;

    @Autowired
    private SeriesConfigService seriesConfigService;

    @Autowired
    private SeriesSpecBaseService seriesSpecBaseService;
    @Resource
    private CommService commService;

    @Resource
    private SpecBaseService specBaseService;

    @Resource
    private SpecConfigBagService specConfigBagService;

    @Resource
    private SeriesLevelService seriesLevelService;

    @Autowired
    PicClassBaseService picClassBaseService;

    @Autowired
    PhotoViewClassPicTop10BaseService photoViewClassPicTop10BaseService;

    @Resource
    private BrandSeriesRelationBaseService brandSeriesRelationBaseService;

    @Autowired
    SpecInnerColorBaseService specInnerColorBaseService;

    @Autowired
    InnerColorBaseService innerColorBaseService;

    @Autowired
    InnerSpecColorMapper innerSpecColorMapper;

    @Resource
    CarManuePriceBaseService carManuePriceBaseService;

    @Autowired
    SeriesAllBaseInfoService seriesAllBaseInfoService;

    @Autowired
    private AutoTagService autoTagService;

    @Resource
    private SeriesElectricService seriesElectricService;

    @Autowired
    AutoCacheService autoCacheService;

    @Resource
    private SpecConfigBagNewService specConfigBagNewService;

    @Resource
    private GBrandBaseService gBrandBaseService;

    @Autowired
    private SeriesAllSearchService seriesAllSearchService;

    @Resource
    private SeriesViewMapper seriesViewMapper;

    @Resource
    private EpibolyAiVideoOrderService epibolyAiVideoOrderService;

    @Resource
    private EpibolyAiVideoOrderDetailService epibolyAiVideoOrderDetailService;

    @Resource
    private PointParamConfigService pointParamConfigService;

    /**
     * 车系ids列表获取相关基本信息
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<SeriesItems> getSeriesBaseInfoBySeriesList(GetBaseInfoBySeriesListRequest request) {
        ApiResult<SeriesItems> apiResult = new ApiResult<>();

        SeriesItems seriesItems = new SeriesItems();
        apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode());
        apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
        List<Integer> seriesIds = CommonFunction.getListFromStr(request.getSerieslist());
        if (seriesIds.size() == 0 || seriesIds.contains(0)) {
            apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode());
            apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return apiResult;
        }
        //车系信息
        Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);
        //品牌信息
        List<Integer> brandIds = seriesBaseInfoMap.values().stream().map(seriesBaseInfo -> seriesBaseInfo.getBrandId()).distinct().collect(Collectors.toList());
        Map<Integer, BrandBaseInfo> brandBaseInfoMap = commService.getBrandBaseInfo(brandIds);
        //厂商信息
        List<Integer> fctIds = seriesBaseInfoMap.values().stream().map(seriesBaseInfo -> seriesBaseInfo.getFactId()).distinct().collect(Collectors.toList());
        Map<Integer, FactoryBaseInfo> factoryBaseInfoMap = commService.getFactoryBaseInfo(fctIds);
        //级别信息
        List<Integer> levelIds = seriesBaseInfoMap.values().stream().map(seriesBaseInfo -> seriesBaseInfo.getLevelId()).distinct().collect(Collectors.toList());
        Map<Integer, LevelBaseInfo> levelBaseInfoMap = commService.getLevelBaseInfo(levelIds);
        List<SeriesItem> seriesItemList = new ArrayList<>();
        for (Integer seriesId : seriesIds) {
            SeriesItem seriesItem = new SeriesItem();
            SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(seriesId);
            if(seriesBaseInfo == null || StringUtils.isBlank(seriesBaseInfo.getName())){
                continue;
            }
            BrandBaseInfo brandBaseInfo = brandBaseInfoMap.get(seriesBaseInfo.getBrandId());
            int minPrice = seriesBaseInfo.getSeriesPriceMin();
            int maxPrice = seriesBaseInfo.getSeriesPriceMax();
            seriesItem.setSeriesid(seriesId);
            seriesItem.setName(seriesBaseInfo.getName());
            seriesItem.setBrandid(seriesBaseInfo.getBrandId());
            if(null != brandBaseInfo){
                seriesItem.setBrandname(brandBaseInfo.getName());
                seriesItem.setBrandlogo(ImageUtil.getFullImagePath(brandBaseInfo.getLogo()));
            }
            seriesItem.setFctid(seriesBaseInfo.getFactId());
            FactoryBaseInfo factoryBaseInfo = factoryBaseInfoMap.get(seriesBaseInfo.getFactId());
            seriesItem.setFctname(null != factoryBaseInfo ? factoryBaseInfo.getName():"");
            LevelBaseInfo levelBaseInfo = levelBaseInfoMap.get(seriesBaseInfo.getLevelId());

            seriesItem.setLevel(null != levelBaseInfo ? levelBaseInfo.getName():"");
            seriesItem.setPic(ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()));
            seriesItem.setMinprice(String.valueOf(minPrice));
            seriesItem.setMaxprice(String.valueOf(maxPrice));
            seriesItem.setSeriespnglogo(ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()));
            seriesItemList.add(seriesItem);
        }

        seriesItems.setRowcount(seriesItemList.size());
        seriesItems.setList(seriesItemList);
        apiResult.setResult(seriesItems);
        return apiResult;
    }
    /**
     * 获取车系的白底车图，最多支持50个车系同时查询
     */
    @Override
    public ApiResult<SeriesPhotoWhiteLogoPage> getSeriesPhotoWhiteLogoBySeriesId(SeriesIdRequest request) {
        List<Integer> seriesList = CommonFunction.getListFromStr(request.getSerieslist());
        if (CollectionUtils.isEmpty(seriesList) || CollectionUtils.size(seriesList) > 50){
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }

        SeriesPhotoWhiteLogoPage specDetailPage = new SeriesPhotoWhiteLogoPage();
        List<SeriesPhotoWhiteLogoItem> result = seriesList.stream()
                .map(seriesId -> {
                    Optional<SeriesBaseInfo> optional = Optional.ofNullable(seriesBaseService.get(seriesId).join());
                    return optional.map(seriesBaseInfo -> SeriesPhotoWhiteLogoItem.builder()
                                    .id(seriesId).picpath(ImageUtil.getFullImagePathNew(seriesBaseInfo.getPp(), Boolean.FALSE))
                                    .seriespnglogo(ImageUtil.getFullImagePathNew(seriesBaseInfo.getNoBgLogo(), Boolean.TRUE))
                                    .build())
                            .orElse(SeriesPhotoWhiteLogoItem.builder().id(seriesId).build());
                }).collect(Collectors.toList());
        specDetailPage.setSerieslist(result);
        return new ApiResult<>(specDetailPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ApiResult<SeriesLogoPage> getSeriesLogoBySeriesList(GetSeriesLogoBySeriesListRequest request) {
        List<Integer> serisIdList = CommonFunction.getListFromStr(request.getSerieslist());
        if (org.springframework.util.CollectionUtils.isEmpty(serisIdList)) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }


        SeriesLogoPage seriesLogoPage = new SeriesLogoPage();
        List<SeriesLogoItem> result = serisIdList.stream()
                .map(seriesId -> {
                    Optional<SeriesBaseInfo> optional = Optional.ofNullable(seriesBaseService.get(seriesId).join());
                    return optional.map(seriesBaseInfo -> SeriesLogoItem.builder()
                                    .id(seriesId).logo(ImageUtil.getFullImagePathNew(seriesBaseInfo.getLogo(), true))
                                    .piccount(seriesBaseInfo.getSpm())
                                    .build())
                            .orElse(SeriesLogoItem.builder().id(seriesId).build());
                }).collect(Collectors.toList());
        seriesLogoPage.setSeriesitems(result);
        return new ApiResult<>(seriesLogoPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ApiResult<List<SeriesParamItem>> getSeriesParamBySeriesListV2(SeriesIdRequest request) {
        List<Integer> serisIdList = CommonFunction.getListFromStr(request.getSerieslist());
        if (CollectionUtils.isEmpty(serisIdList) || serisIdList.size() > 50) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<SeriesParamItem> list = new ArrayList<>();
        for (Integer seriesId : serisIdList) {
            SeriesConfig seriesConfig = seriesConfigService.get(seriesId);
            if (Objects.isNull(seriesConfig)) {
                continue;
            }
            List<SpecViewEntity> specViewEntities = seriesSpecBaseService.get(seriesId, Level.isCVLevel(seriesConfig.getLevelid())).join();
            double currentStateMaxOilWear = 0.0;
            double currentStateMinOilWear = 0.0;
            if (CollectionUtils.isNotEmpty(specViewEntities)) {
                specViewEntities = specViewEntities.stream().filter(specViewEntity -> specViewEntity.getOfficalOil() > 0.0).collect(Collectors.toList());
                int state = seriesConfig.getState();
                specViewEntities = (state == 20 || state == 30) ?
                        specViewEntities.stream().filter(specViewEntity -> specViewEntity.getSpecState() >= 20 && specViewEntity.getSpecState() <= 30).collect(Collectors.toList()) :
                        specViewEntities.stream().filter(specViewEntity -> specViewEntity.getSpecState() == state).collect(Collectors.toList());
                currentStateMinOilWear = specViewEntities.stream().map(SpecViewEntity::getOfficalOil).filter(Objects::nonNull).min(Comparator.comparing(Double::doubleValue)).orElse(0.0);
                currentStateMaxOilWear = specViewEntities.stream().map(SpecViewEntity::getOfficalOil).filter(Objects::nonNull).max(Comparator.comparing(Double::doubleValue)).orElse(0.0);
            }
            list.add(new SeriesParamItem(seriesConfig, currentStateMinOilWear, currentStateMaxOilWear));
        }
        return new ApiResult<>(list, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ApiResult<List<SeriesConfig>> getCarPriceSeriesParamBySeriesList(GetBaseInfoBySeriesListRequest request) {
        List<Integer> serisIdList = CommonFunction.getListFromStr(request.getSerieslist());
        if (CollectionUtils.isEmpty(serisIdList) || serisIdList.size() > 50) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<SeriesConfig> list = new ArrayList<>();
        Map<Integer, SeriesConfig> configMap = seriesConfigService.getMap(serisIdList);
        if(CollectionUtils.isNotEmptyMap(configMap)){
            for(Integer seriesId : serisIdList){
                SeriesConfig seriesConfig = configMap.get(seriesId);
                if(null == seriesConfig){
                    continue;
                }
                SeriesConfig config = new SeriesConfig();
                BeanUtils.copyProperties(seriesConfig,config);
                config.setMaxprice(seriesConfig.getTempMaxPrice());
                config.setMinprice(seriesConfig.getTempMinPrice());
                config.setState(seriesConfig.getTempState());
                list.add(config);
            }
        }

//        List<SeriesConfig> list = serisIdList.stream().map(seriesConfigService::get).filter(Objects::nonNull).peek(seriesConfig -> {
//            seriesConfig.setMaxprice(seriesConfig.getTempMaxPrice());
//            seriesConfig.setMinprice(seriesConfig.getTempMinPrice());
//            seriesConfig.setState(seriesConfig.getTempState());
//        }).collect(Collectors.toList());
        return new ApiResult<>(list, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public GetBagInfoBySeriesIdResponse getBagInfoBySeriesIdV2(GetSeriesConfigRequestV2 request) {
        GetBagInfoBySeriesIdResponse.Builder builder = GetBagInfoBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }

        GetBagInfoBySeriesIdResponse.Result.Builder result = GetBagInfoBySeriesIdResponse.Result.newBuilder();
        List<Integer> list = this.getSpecListBySeriesId(seriesId, result);

        List<GetBagInfoBySeriesIdResponse.BagTypeItems.BagItems> bagItems = this.getGetConfigBagsBySpecList(list);
        result.setSeriesid(request.getSeriesid());
        List<GetBagInfoBySeriesIdResponse.BagTypeItems> bagItemBuilder = new ArrayList<>();
        bagItemBuilder.add(GetBagInfoBySeriesIdResponse.BagTypeItems.newBuilder()
                .setName("选装包")
                .addAllBagitems(bagItems).build());
        result.addAllBagtypeitems(bagItemBuilder);
        return builder.setResult(result).setReturnMsg("成功").build();
    }

    @Override
    public GetSeriesByLevelIdResponse getSeriesByLevelId(GetSeriesByLevelIdRequest request) {
        GetSeriesByLevelIdResponse.Builder builder = GetSeriesByLevelIdResponse.newBuilder();
        int levelId = request.getLevelid();
        if (levelId == 0) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        GetSeriesByLevelIdResponse.Result.Builder result = GetSeriesByLevelIdResponse.Result.newBuilder();
        List<SeriesViewRankEntity> seriesViewRankEntities = seriesLevelService.get(levelId);
        if (CollectionUtils.isNotEmpty(seriesViewRankEntities)) {
            for (SeriesViewRankEntity seriesViewRankEntity : seriesViewRankEntities) {
                int seriesId = seriesViewRankEntity.getSeriesId();
                SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
                result.addSeriesitems(GetSeriesByLevelIdResponse.Result.Seriesitems.newBuilder().setId(seriesId).
                        setName(seriesBaseInfo.getName()).
                        setLogo(ImageUtil.getFullImagePathNew(seriesBaseInfo.getLogo(), true)).
                        setPicnum(seriesViewRankEntity.getSeriesPhotoNum()).build());
            }
        }
        result.setTotal(result.getSeriesitemsCount());
        result.setLevelid(levelId);
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetSeriesColorResponse getSeriesColorBySeriesId(autohome.rpc.car.car_api.v1.series.GetSeriesConfigRequest request) {
        GetSeriesColorResponse.Builder builder = GetSeriesColorResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        GetSeriesColorResponse.Result.Builder result = GetSeriesColorResponse.Result.newBuilder();
        SeriesConfig seriesConfig = seriesConfigService.get(seriesId);
        if (Objects.nonNull(seriesConfig)) {
            List<SpecColorInfo> colorList = seriesConfig.getColorList();
            if (CollectionUtils.isNotEmpty(colorList)) {
                colorList.stream().filter(Objects::nonNull).forEach(specColorInfo ->
                        result.addColoritems(GetSeriesColorResponse.Result.Coloritems.newBuilder()
                                .setId(specColorInfo.getCi())
                                .setName(StringUtils.defaultString(specColorInfo.getCn()))
                                .setValue(StringUtils.defaultString(specColorInfo.getCv()))
                                .setPicnum(specColorInfo.getPn())
                                .setClubpicnum(specColorInfo.getCpn())
                                .build()));
            }
        }
        result.setSeriesid(seriesId);
        result.setTotal(result.getColoritemsCount());
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    private List<GetBagInfoBySeriesIdResponse.BagTypeItems.BagItems> getGetConfigBagsBySpecList(List<Integer> specIdList) {
        if (org.springframework.util.CollectionUtils.isEmpty(specIdList) || specIdList.stream().anyMatch(specId -> specId > 1000000)) {
            return Collections.emptyList();
        }
        Map<Integer, List<SpecConfigBagEntity>> map = specConfigBagNewService.getList(specIdList);

        List<GetBagInfoBySeriesIdResponse.BagTypeItems.BagItems> bagItems = new ArrayList<>();
        for (Integer specId : specIdList) {
            GetBagInfoBySeriesIdResponse.BagTypeItems.BagItems.Builder bagItemBuilder = GetBagInfoBySeriesIdResponse.BagTypeItems.BagItems.newBuilder();
            bagItemBuilder.setSpecid(specId);
            List<SpecConfigBagEntity> list = map.get(specId);
            if(CollectionUtils.isNotEmpty(list)) {
                list = list.stream().sorted(Comparator.comparing(SpecConfigBagEntity::getPrice)).collect(Collectors.toList());
                for (SpecConfigBagEntity valeItem : list) {
                    bagItemBuilder.addValueitems(
                            GetBagInfoBySeriesIdResponse.BagTypeItems.BagItems.ValueItems.newBuilder()
                                    .setBagid(valeItem.getBagId())
                                    .setName(valeItem.getBagName())
                                    .setDescription(valeItem.getDescrip())
                                    .setPrice(valeItem.getPrice())
                                    .setPricedesc(CommonFunction.getPriceDesc(valeItem.getPrice()))
                    );
                }
            }
            bagItems.add(bagItemBuilder.build());
        }

        return bagItems;

    }

    private List<Integer> getSpecListBySeriesId(int seriesId, GetBagInfoBySeriesIdResponse.Result.Builder result) {
        List<SpecViewEntity> specViewEntities = commService.getSpecViewEntities(seriesId, 1);
        if (CollectionUtils.isNotEmpty(specViewEntities)) {
            List<Integer> specIds = specViewEntities.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
            Map<Integer, SpecBaseInfo> baseInfoMap = specBaseService.getMap(specIds);
            for (SpecViewEntity specViewEntity : specViewEntities) {
                GetBagInfoBySeriesIdResponse.SpecList.Builder specList = GetBagInfoBySeriesIdResponse.SpecList.newBuilder();
                SpecBaseInfo specBaseInfo = baseInfoMap.get(specViewEntity.getSpecId());
                specList.setSpecid(specViewEntity.getSpecId()).setSpecstate(specViewEntity.getSpecState()).setShowstate(specViewEntity.getSpecState() == 10 ? specBaseInfo.getIsBooked() : -1);
                result.addSpeclist(specList);
            }
        }
        return result.getSpeclistList().stream().map(GetBagInfoBySeriesIdResponse.SpecList::getSpecid).collect(Collectors.toList());
    }

    public SeriesInfo getSeriesInfo(int seriesId,boolean dispqrcode,boolean needHtmlDecode) {
        SeriesInfo seriesInfo = new SeriesInfo();
        seriesInfo.setSeriesid(seriesId);

        AtomicReference<SeriesViewEntity> seriesViewAR = new AtomicReference<>();
        AtomicReference<String> qrCodeAR = new AtomicReference<>("");
        AtomicReference<SeriesBaseInfo> baseInfoAR = new AtomicReference<>();
        AtomicReference<FactoryBaseInfo> fctAR = new AtomicReference<>();
        AtomicReference<BrandBaseInfo> brandAR = new AtomicReference<>();
        AtomicReference<LevelBaseInfo> levelAR = new AtomicReference<>();
        AtomicReference<GroupEntity> groupAR = new AtomicReference<>();

        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(CompletableFuture.supplyAsync(() -> seriesMapper.getSeriesView(seriesId)).thenAccept(x ->
                seriesViewAR.set(x)
        ));
        tasks.add(seriesBaseService.get(seriesId).thenComposeAsync(x -> {
            if(x==null)
                return CompletableFuture.completedFuture(null);

            baseInfoAR.set(x);

            List<CompletableFuture> ctasks = new ArrayList<>();

            ctasks.add(factoryBaseService.getFactoryAsync(x.getFactId()).thenAccept(fct -> fctAR.set(fct)));
            ctasks.add(brandBaseService.get(x.getBrandId()).thenAccept(brand -> brandAR.set(brand)));
            ctasks.add(levelBaseService.getLevelAsync(x.getLevelId()).thenAccept(level -> levelAR.set(level)));

            ctasks.add(CompletableFuture.supplyAsync(() -> groupMapper.getGroup(x.getBrandId())).thenAccept(group -> groupAR.set(group)));

            return CompletableFuture.allOf(ctasks.toArray(new CompletableFuture[ctasks.size()]));
        }));

        if (dispqrcode) {
            tasks.add(qrCodeService.series(seriesId).thenAccept(x -> qrCodeAR.set(x)));
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        SeriesViewEntity seriesView = seriesViewAR.get();
        if (seriesView == null)
            return null;

        SeriesBaseInfo baseInfo = baseInfoAR.get();
        FactoryBaseInfo fct = fctAR.get();
        BrandBaseInfo brand = brandAR.get();
        LevelBaseInfo level = levelAR.get();
        GroupEntity group = groupAR.get();

        seriesInfo.setQrcode(qrCodeAR.get());
        seriesInfo.setSeriesplace(seriesView.getSeriesplace());
        seriesInfo.setContainelectriccar(seriesView.getContainelectriccar()+"");
        seriesInfo.setFctfirstletter(seriesView.getFctFirstLetter());

        seriesInfo.setSeriesname(needHtmlDecode ? HtmlUtils.decode(baseInfo.getName()) : baseInfo.getName());
        seriesInfo.setBrandid(baseInfo.getBrandId());
        seriesInfo.setFctid(baseInfo.getFactId());
        seriesInfo.setLevelid(baseInfo.getLevelId());
        seriesInfo.setSerieslogo(ImageUtil.getFullImagePath(baseInfo.getLogo()));
        seriesInfo.setSeriesofficialurl(baseInfo.getUrl());
        seriesInfo.setSeriesfirstletter(baseInfo.getFl());

        seriesInfo.setFctname(fct.getName());
        seriesInfo.setFctlogo(ImageUtil.getFullImagePath(fct.getLogo()));
        seriesInfo.setFctofficialurl(fct.getUrl());

        seriesInfo.setBrandname(brand.getName());
        seriesInfo.setBrandlogo(ImageUtil.getFullImagePath(brand.getLogo()));
        seriesInfo.setBrandofficialurl(brand.getUrl());
        seriesInfo.setBrandfirstletter(brand.getFirstLetter());

        seriesInfo.setLevelname(level.getName());

        seriesInfo.setCountryid(group.getCountryId());
        seriesInfo.setCountryname(group.getCountry());

        return seriesInfo;
    }

    @Override
    public SeriesClassPictureBySeriesIdResponse getSeriesClassPictureBySeriesId(SeriesClassPictureBySeriesIdRequest request){
        SeriesClassPictureBySeriesIdResponse.Builder builder = SeriesClassPictureBySeriesIdResponse.newBuilder();
        SeriesClassPictureBySeriesIdResponse.Result.Builder result = SeriesClassPictureBySeriesIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<CarPhotoViewEntity> list = photoViewClassPicTop10BaseService.get(seriesId).join();
//        List<CarPhotoViewEntity> list = carPhotoViewMapper.getPhotoViewClassPicTop10BySeriesId(seriesId);
        if(!CollectionUtils.isEmpty(list)){
            list = list.stream().filter(s -> s.getSpecPicNumber() > 2)
                    .sorted(Comparator.comparing(CarPhotoViewEntity::getClassOrder)
                            .thenComparing(CarPhotoViewEntity::getShowId,Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewEntity::getStateOrder)
                            .thenComparing(CarPhotoViewEntity::getIsclassic)
                            .thenComparing(CarPhotoViewEntity::getSourceTypeOrder)
                            .thenComparing(CarPhotoViewEntity::getDealerPicOrder)
                            .thenComparing(CarPhotoViewEntity::getSpecPicUploadTimeOrder,Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewEntity::getPicId,Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(list)){
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }

        List<Integer> picClassList = new ArrayList<>();
        List<Integer> specIds = new ArrayList<>();
        list.stream().forEach(v -> {
            if(!picClassList.contains(v.getPicClass())){
                picClassList.add(v.getPicClass());
            }
            specIds.add(v.getSpecId());
        });
        Map<Integer, List<CarPhotoViewEntity>> map = list.stream().collect(Collectors.groupingBy(CarPhotoViewEntity::getPicClass));
        List<SpecBaseInfo> specBaseList = specBaseService.getList(specIds.stream().distinct().collect(Collectors.toList()));
        Map<Integer,SpecBaseInfo> specMap = specBaseList.stream().collect(Collectors.toMap(SpecBaseInfo::getId,a -> a,(x,y)->x));
        Map<Integer,PicClassEntity> picClassMap = picClassBaseService.getList(picClassList);
        for (int id:picClassList) {
            List<CarPhotoViewEntity> item = map.get(id);
            SeriesClassPictureBySeriesIdResponse.Result.Typeitems.Builder typeitems = SeriesClassPictureBySeriesIdResponse.Result.Typeitems.newBuilder();
            typeitems.setId(id);
//            PicClassEntity picBase = picClassBaseService.get(id).join();
            PicClassEntity picBase = picClassMap.get(id);
            typeitems.setName(picBase == null?"":picBase.getName());
            typeitems.setPictotal(item.size());
            SeriesClassPictureBySeriesIdResponse.Result.Picitems.Builder picitems = SeriesClassPictureBySeriesIdResponse.Result.Picitems.newBuilder();
            for (CarPhotoViewEntity photo:item.stream().limit(5).collect(Collectors.toList())) {
//                SpecBaseInfo specBase = specBaseService.get(photo.getSpecId()).join();
                SpecBaseInfo specBase = specMap.get(photo.getSpecId());
                picitems.setId(photo.getPicId());
                picitems.setFilepath(ImageUtil.getFullImagePathWithoutReplace(photo.getPicFilePath()));
                picitems.setSpecid(photo.getSpecId());
                picitems.setSpecname(specBase == null?"":specBase.getSpecName());
                typeitems.addPicitems(picitems);
            }
            result.addTypeitems(typeitems);
        }
        result.setSeriesid(seriesId);
        SeriesBaseInfo seriesBase = seriesBaseService.get(seriesId).join();
        result.setSeriesname(seriesBase == null ? "":seriesBase.getName());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public SeriesPngLogoBySeriesIdResponse getSeriesPngLogoBySeriesId (SeriesPngLogoBySeriesIdRequest request){
        SeriesPngLogoBySeriesIdResponse.Builder builder = SeriesPngLogoBySeriesIdResponse.newBuilder();
        SeriesPngLogoBySeriesIdResponse.Result.Builder result = SeriesPngLogoBySeriesIdResponse.Result.newBuilder();
        String seriesString = request.getSerieslist();
        int[] seriesIds = StringIntegerUtils.convertToInt32(seriesString,",",0);
        if(seriesIds.length == 0 || Arrays.stream(seriesIds).anyMatch(item -> item == 0) || seriesIds.length > 50){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        for (int series:seriesIds) {
            SeriesPngLogoBySeriesIdResponse.Result.Serieslist.Builder serieslist = SeriesPngLogoBySeriesIdResponse.Result.Serieslist.newBuilder();
            SeriesBaseInfo baseInfo = seriesBaseService.get(series).join();
            if(baseInfo != null){
                serieslist.setId(series);
                serieslist.setName(baseInfo.getName());
                serieslist.setPnglogo(ImageUtil.getFullImagePath(baseInfo.getNoBgLogo()));
                result.addSerieslist(serieslist);
            }
            else {
                serieslist.setId(series);
                serieslist.setName("");
                serieslist.setPnglogo("");
                result.addSerieslist(serieslist);
            }
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    /**
     *根据车系集合获取车系基础信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<List<SeriesBaseItem>> getSeriesInfoBySeriesList(GetSeriesInfoBySeriesListRequest request) {
        List<Integer> seriesIds = CommonFunction.getListFromStr(request.getSeriesids());
        if(seriesIds.size() == 0 || seriesIds.size() > 30){
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);
        List<SeriesBaseItem> seriesBaseItems = new ArrayList<>();
        for(int seriesId : seriesIds){
            SeriesBaseItem seriesBaseItem = new SeriesBaseItem();
            seriesBaseItem.setSeriesid(seriesId);
            SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(seriesId);
            seriesBaseItem.setSeriesname(null != seriesBaseInfo ? seriesBaseInfo.getName():"");
            seriesBaseItem.setSeriesimage(null != seriesBaseInfo ? ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()):"");
            seriesBaseItem.setIsvr(null != seriesBaseInfo ? seriesBaseInfo.getIsVr() : 0);
            seriesBaseItem.setMinprice(null != seriesBaseInfo ? seriesBaseInfo.getSeriesPriceMin():0);
            seriesBaseItem.setMaxprice(null != seriesBaseInfo ? seriesBaseInfo.getSeriesPriceMax():0);
            seriesBaseItem.setSeriesplace(null != seriesBaseInfo ? seriesBaseInfo.getPlace():"");
            seriesBaseItems.add(seriesBaseItem);
        }
        return new ApiResult<>(seriesBaseItems, RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据品牌ID和在售类型获取品牌下车系的报价信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandFctSeriesInfo> getSeriesMenuByBrandIdNew(GetSeriesMenuByBrandIdNewRequest request) {
        SpecStateEnum specStateEnum = Spec.getSpecState(request.getState());
        int brandId = request.getBrandid();
        if(brandId == 0){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        boolean haveSpecState = isHaveSpecState(specStateEnum);
        if(!haveSpecState){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        List<Integer> seriesIds = brandSeriesRelationBaseService.getSeriesIds(brandId);
        List<SeriesBaseInfo> seriesBaseInfos = commService.getSeriesBaseInfoNoMap(seriesIds);
        //过滤排序
        seriesBaseInfos = filterSeriesInfos(seriesBaseInfos,specStateEnum);
        List<BrandFctSeriesInfo.FctInfo> fctInfos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(seriesBaseInfos)){
            //厂商
            List<Integer> fctIds = seriesBaseInfos.stream().map(SeriesBaseInfo::getFactId).distinct().collect(Collectors.toList());
            Map<Integer, FactoryBaseInfo> fctMap = commService.getFactoryBaseInfo(fctIds);
            //级别信息
            List<Integer> levelIds = seriesBaseInfos.stream().map(SeriesBaseInfo::getLevelId).distinct().collect(Collectors.toList());
            Map<Integer, LevelBaseInfo> levelBaseInfoMap = commService.getLevelBaseInfo(levelIds);
            //按照厂商分组 保持原来顺序
            Map<Integer, List<SeriesBaseInfo>> bfsMap  = seriesBaseInfos.stream().collect(Collectors.groupingBy(SeriesBaseInfo::getFactId,
                    LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for(Map.Entry<Integer, List<SeriesBaseInfo>> seriesMap:bfsMap.entrySet()){
                BrandFctSeriesInfo.FctInfo fctInfo = new BrandFctSeriesInfo.FctInfo();
                int fctId = seriesMap.getKey();
                fctInfo.setId(fctId);
                FactoryBaseInfo factoryBaseInfo = fctMap.get(fctId);
                fctInfo.setName(null != factoryBaseInfo ? factoryBaseInfo.getName() : "");
                fctInfo.setLogo(null != factoryBaseInfo ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "");
                //车系信息集合
                List<BrandFctSeriesInfo.SeriesInfo> seriesInfos = new ArrayList<>();
                List<SeriesBaseInfo> seriesBaseInfoList = seriesMap.getValue();
                seriesBaseInfoList.forEach(seriesBaseInfo -> {
                    BrandFctSeriesInfo.SeriesInfo seriesInfo = new BrandFctSeriesInfo.SeriesInfo();
                    seriesInfo.setId(seriesBaseInfo.getId());
                    seriesInfo.setName(seriesBaseInfo.getName());
                    seriesInfo.setLogo(ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()));
                    seriesInfo.setLevelid(seriesBaseInfo.getLevelId());
                    LevelBaseInfo levelBaseInfo = levelBaseInfoMap.get(seriesBaseInfo.getLevelId());
                    seriesInfo.setLevelname(null != levelBaseInfo ? levelBaseInfo.getName() : "");
                    seriesInfo.setMinprice(seriesBaseInfo.getSeriesPriceMin());
                    seriesInfo.setMaxprice(seriesBaseInfo.getSeriesPriceMax());
                    seriesInfo.setSeriesState(seriesBaseInfo.getSeriesState());
                    seriesInfo.setSeriesorders(seriesBaseInfo.getNewSeriesOrderCls());
                    seriesInfo.setIsvr(seriesBaseInfo.getIsVr());
                    seriesInfo.setPnglogo(ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()));
                    seriesInfo.setContainbookedspec(seriesBaseInfo.getCb());
                    seriesInfo.setRelationseriesid(seriesBaseInfo.getRId());
                    seriesInfos.add(seriesInfo);
                });
                fctInfo.setSerieslist(seriesInfos);
                //厂商信息
                fctInfos.add(fctInfo);
            }
        }
        BrandFctSeriesInfo brandFctSeriesInfo = new BrandFctSeriesInfo();
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        brandFctSeriesInfo.setId(brandId);
        brandFctSeriesInfo.setName(null != brandBaseInfo ? brandBaseInfo.getName():"");
        brandFctSeriesInfo.setLogo(null != brandBaseInfo ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()):"");
        brandFctSeriesInfo.setFctlist(fctInfos);
        return new ApiResult<>(brandFctSeriesInfo,RETURN_MESSAGE_ENUM0);
    }

    private List<SeriesBaseInfo> filterSeriesInfos(List<SeriesBaseInfo> seriesBaseInfos,SpecStateEnum specStateEnum){
        switch (specStateEnum) {
            //未上市(0X0001)
            case NO_SELL:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesState()== 0 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesState()== 10 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesState()== 20 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesState()== 30 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesIsPublic() == 1 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesIsPublic() == 2 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesIsPublic() <= 1 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo ->
                        seriesBaseInfo.getSeriesIsPublic() >= 1 && seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo -> seriesBaseInfo.getSeriesSpecNum()>0).
                        sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
                break;
        }
        return seriesBaseInfos;
    }

    private boolean isHaveSpecState(SpecStateEnum specStateEnum){
        boolean flag = true;
        switch (specStateEnum) {
            //未上市(0X0001)
            case NO_SELL:
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                break;
            //在产在售(0X0004)
            case SELL:
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                break;
            //在售(0X000C)
            case SELL_12:
                break;
            //停售(0X0010)
            case STOP_SELL:
                break;
            //未售+在售(0X000F)
            case SELL_15:
                break;
            //在售+停售(0X001C)
            case SELL_28:
                break;
            //全部(0X001F)
            case SELL_31:
                break;
            default: flag = false;
        }
        return flag;
    }


    @Override
    public GetSeriesLogoResponse getSeriesLogoBySeriesId(autohome.rpc.car.car_api.v1.series.GetSeriesConfigRequest request) {
        GetSeriesLogoResponse.Builder builder = GetSeriesLogoResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetSeriesLogoResponse.Result.Builder result = GetSeriesLogoResponse.Result.newBuilder();
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (Objects.nonNull(seriesBaseInfo)) {
            result.setSeriesname(seriesBaseInfo.getName());
            result.setSerieslogo(ImageUtil.getFullImagePathNew(seriesBaseInfo.getLogo(), true));
            result.setSeriespnglogo(ImageUtil.getFullImagePathNew(seriesBaseInfo.getNoBgLogo(), true));
        }
        result.setSeriesid(seriesId);
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetSeriesBrandListByBrandIdsResponse getSeriesBrandListByBrandIds(GetSeriesBrandListByBrandIdsRequest request){
        GetSeriesBrandListByBrandIdsResponse.Builder builder = GetSeriesBrandListByBrandIdsResponse.newBuilder();
        if(Objects.equals(request.getBrandids(), "") || Objects.equals(request.getState(), "")){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }

        List<Integer> brandIdList = CommonFunction.getListFromStr(request.getBrandids());
        if(brandIdList.isEmpty() || brandIdList.size() > 10){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }

        //品牌
        CompletableFuture<Map<Integer, BrandBaseInfo>> brandsInfosFuture =  CompletableFuture.supplyAsync(() -> commService.getBrandBaseInfo(brandIdList));
        //车系品牌
        CompletableFuture<Map<Integer, List<Integer>>> seriesBrandIdsMapFuture = CompletableFuture.supplyAsync(() -> commService.getSeriesIdListByBrands(brandIdList));
        //carMan
        CompletableFuture<List<Integer>> carSeriesListFuture = CompletableFuture.supplyAsync(() -> carManuePriceBaseService.get());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(brandsInfosFuture, seriesBrandIdsMapFuture, carSeriesListFuture);
        allFutures.join();

        Map<Integer, BrandBaseInfo> brandsInfos =  brandsInfosFuture.join();
        Map<Integer, List<Integer>> seriesBrandIdsMap = seriesBrandIdsMapFuture.join();
        List<Integer> carSeriesList = carSeriesListFuture.join();

        List<GetSeriesBrandListByBrandIdsResponse.Result> resultList = new ArrayList<>();
        for(Integer brandId : brandIdList){
            GetSeriesBrandListByBrandIdsResponse.Result.Builder item = GetSeriesBrandListByBrandIdsResponse.Result.newBuilder();
            item.setBrandid(brandId);
            item.setBrandname(brandsInfos != null ? (brandsInfos.get(brandId) != null?brandsInfos.get(brandId).getName():""):"");
            List<Integer> seriesIdList = seriesBrandIdsMap.get(brandId);
            List<SeriesBaseInfo> seriesList = commService.getSeriesBaseInfoNoMap(seriesIdList);
            if(seriesList.isEmpty()){
                item.addAllList(Collections.emptyList());
            }else{
                if(CollectionUtils.isEmpty(carSeriesList)){
                    return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                            .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                            .build();
                }
                seriesList = seriesList.stream()
                        .filter(x -> carSeriesList.contains(x.getId()))
                        .sorted(Comparator.comparing(SeriesBaseInfo::getId))
                        .collect(Collectors.toList());
                //匹配状态
                SpecStateEnum state = Spec.getSpecState(request.getState());
                List<SeriesBaseInfo> matchSeriesList = CommonFunction.filterSeriesViewList(state, seriesList);
                if(matchSeriesList.isEmpty()){
                    item.addAllList(Collections.emptyList());
                }else{
                    GetSeriesBrandListByBrandIdsResponse.Result.SeriesList.Builder seriesItem = GetSeriesBrandListByBrandIdsResponse.Result.SeriesList.newBuilder();
                    for(SeriesBaseInfo series: matchSeriesList){
                        if(Objects.equals(item.getBrandname(), "")){
                            item.setBrandname(series.getBrandName());
                        }
                        seriesItem.setSeriesid(series.getId())
                                .setSeriesname(series.getName())
                                .setImage(!Objects.equals(series.getLogo(), "") ? ImageUtil.getFullImagePath(series.getLogo()):"")
                                .setMinprice(series.getSeriesPriceMin())
                                .setMaxprice(series.getSeriesPriceMax())
                                .setSeriesstate(series.getSeriesState());
                        item.addList(seriesItem);
                    }
                }
            }
            resultList.add(item.build());
        }

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .addAllResult(resultList)
                .build();
    }
    @Override
    public GetSeriesInnerColorBySeriesIdResponse getSeriesInnerColorBySeriesId(GetSeriesInnerColorBySeriesIdRequest request){
        GetSeriesInnerColorBySeriesIdResponse.Builder builder = GetSeriesInnerColorBySeriesIdResponse.newBuilder();
        GetSeriesInnerColorBySeriesIdResponse.Result.Builder result = GetSeriesInnerColorBySeriesIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
//        List<SpecColorListBaseInfo> list = specInnerColorBaseService.get(seriesId).join();
//        Map<Integer, List<SpecColorListBaseInfo>> map = list.stream().collect(Collectors.groupingBy(SpecColorListBaseInfo::getColorId));
//        for (Map.Entry<Integer,List<SpecColorListBaseInfo>> item:map.entrySet()) {
//            GetSeriesInnerColorBySeriesIdResponse.Coloritems.Builder color = GetSeriesInnerColorBySeriesIdResponse.Coloritems.newBuilder();
//            int colorid = item.getKey();
//            ColorBaseInfo colorBase = innerColorBaseService.getColor(colorid);
//            color.setId(colorid);
//            color.setName(colorBase == null?"":colorBase.getName());
//            color.setValue(colorBase == null?"":colorBase.getValue());
//            color.setPicnum(item.getValue().stream().collect(Collectors.summingInt(SpecColorListBaseInfo::getPicNumber)));
//            color.setClubpicnum(item.getValue().stream().collect(Collectors.summingInt(SpecColorListBaseInfo::getClubPicNumber)));
//            result.addColoritems(color);
//        }
        SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
        List<SpecColorEntity> list = innerSpecColorMapper.getSeriesInnerColor(seriesId);
        if(baseInfo != null && (baseInfo.getSeriesState() == 20 || baseInfo.getSeriesState() == 30)){
            list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30)
                .sorted(Comparator.comparing(SpecColorEntity::getPicNum,Comparator.reverseOrder()))
                .collect(Collectors.toList());
        }
        else {
            list = list.stream().filter(s -> s.getSpecState() == baseInfo.getSeriesState())
                    .sorted(Comparator.comparing(SpecColorEntity::getPicNum,Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        for (SpecColorEntity item:list) {
            GetSeriesInnerColorBySeriesIdResponse.Coloritems.Builder color = GetSeriesInnerColorBySeriesIdResponse.Coloritems.newBuilder();
            color.setId(item.getColorId());
            color.setName(item.getColorName());
            color.setValue(item.getColorValue());
            color.setPicnum(item.getPicNum());
            color.setClubpicnum(item.getClubPicNum());
            result.addColoritems(color);
        }

        result.setSeriesid(seriesId);
        result.setTotal(result.getColoritemsCount());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetAllSeriesBaseInfoResponse getAllSeriesBaseInfo(GetAllSeriesBaseInfoRequest request){
        GetAllSeriesBaseInfoResponse.Builder builder = GetAllSeriesBaseInfoResponse.newBuilder();
        GetAllSeriesBaseInfoResponse.Result.Builder result = GetAllSeriesBaseInfoResponse.Result.newBuilder();
        List<SeriesNameInfo> seriesNameInfos = seriesAllBaseInfoService.get();
        if(!CollectionUtils.isEmpty(seriesNameInfos)){
            for (SeriesNameInfo seriesNameInfo: seriesNameInfos){
                GetAllSeriesBaseInfoResponse.Result.SeriesItem.Builder item = GetAllSeriesBaseInfoResponse.Result.SeriesItem.newBuilder();
                item.setId(seriesNameInfo.getId());
                item.setName(null != seriesNameInfo.getName() ? HtmlUtils.decode(seriesNameInfo.getName()) : "");
                item.setEnglishname(null != seriesNameInfo.getEName() ? HtmlUtils.decode(seriesNameInfo.getEName()) : "");
                result.addSeriesitems(item);
            }
            result.setTotal(seriesNameInfos.size());
        }

        builder.setResult(result);
        builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg());
        return builder.build();
    }

    @Override
    public GetSeriesTagResponse getSeriesTagBySeriesIds(GetSeriesInfoBySeriesListRequest request) {
        GetSeriesTagResponse.Builder builder = GetSeriesTagResponse.newBuilder();
        GetSeriesTagResponse.Result.Builder result = GetSeriesTagResponse.Result.newBuilder();
        String seriesIds = request.getSeriesids();
        List<Integer> seriesList = CommonFunction.getListFromStr(seriesIds);
        if (!CommonFunction.unDistinctCheck(seriesList)){
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<AutoTagEntity> tagName = autoTagService.get();
        Map<Integer, String> tagNameMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(tagName)) {
            tagNameMap = tagName.stream().filter(Objects::nonNull).collect(Collectors.toMap(AutoTagEntity::getId, AutoTagEntity::getName));
        }

        Map<Integer, SeriesBaseInfo> baseServiceMap = seriesBaseService.getMap(seriesList);
        for (Integer seriesId : seriesList) {
            SeriesBaseInfo seriesBaseInfo = baseServiceMap.get(seriesId);
            if (Objects.isNull(seriesBaseInfo)) {
                continue;
            }
            List<GetSeriesTagResponse.Result.List.Taglist> taglist = new ArrayList<>();
            List<Integer> tagIdsList = seriesBaseInfo.getTag();
            if(CollectionUtils.isNotEmpty(tagIdsList)) {
                for (Integer tagId : tagIdsList) {
                    taglist.add(GetSeriesTagResponse.Result.List.Taglist.newBuilder()
                            .setTagid(tagId)
                            .setTagname(StringUtils.defaultString(tagNameMap.get(tagId)))
                            .build());
                }
            }
            result.addList(GetSeriesTagResponse.Result.List.newBuilder()
                    .setSereisid(seriesId)
                    .setSeriesname(StringUtils.defaultString(seriesBaseInfo.getName()))
                    .addAllTaglist(taglist)
                    .build());

        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetSeriesElectricListResponse getSeriesElectricList(GetSeriesElectricListRequest request) {
        GetSeriesElectricListResponse.Builder builder = GetSeriesElectricListResponse.newBuilder();
        GetSeriesElectricListResponse.Result.Builder result = GetSeriesElectricListResponse.Result.newBuilder();
        List<SeriesOnlyElectricEntity> electricEntities = seriesElectricService.get();
        for (SeriesOnlyElectricEntity e : electricEntities) {
            result.addItems(GetSeriesElectricListResponse.Result.Item.newBuilder()
                    .setSeriesid(e.getId())
                    .setSeriesname(StringUtils.defaultString(e.getName()))
                    .setSeriesstate(e.getState()).build());
        }
        result.setCount(result.getItemsCount());
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public SeriesHaveCrashInfoResponse seriesHaveCrashInfo(SeriesHaveCrashInfoRequest request){
        SeriesHaveCrashInfoResponse.Builder builder = SeriesHaveCrashInfoResponse.newBuilder();
        SeriesHaveCrashInfoResponse.Result.Builder result = SeriesHaveCrashInfoResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<Integer> list = autoCacheService.getSeriesHaveCrashInfo(seriesId);
        if(!CollectionUtils.isEmpty(list)){
            result.setHavecrashinfo(1);
            result.addAllStandards(list);
        }
        result.setSeriesid(seriesId);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetSpecBySeriesResponse getSpecBySeries(GetSpecBySeriesRequest request){
        GetSpecBySeriesResponse.Builder builder = GetSpecBySeriesResponse.newBuilder();
        GetSpecBySeriesResponse.Result.Builder result = GetSpecBySeriesResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SeriesBaseInfo seriesBase = seriesBaseService.get(seriesId).join();

        builder.setResult(result);
        builder.setReturnMsg("成功");
        if(seriesBase==null){
            builder.setResult(result);
            return builder.build();
        }
        List<SpecViewEntity> list = autoCacheService.getSpecBySeries(seriesId,Level.isCVLevel(seriesBase.getLevelId()));
        if(!CollectionUtils.isEmpty(list) && seriesBase != null){
            list.forEach(x -> {
                int specid = x.getSpecId();
                GetSpecBySeriesResponse.Result.Specitem.Builder item = GetSpecBySeriesResponse.Result.Specitem.newBuilder();
                SpecBaseInfo specBase = specBaseService.get(specid).join();
                item.setBrandid(seriesBase.getBrandId());
                item.setBrandname(seriesBase.getBrandName());
                item.setFctid(seriesBase.getFactId());
                item.setFctname(factoryBaseService.getName(seriesBase.getFactId()));
                item.setSeriesid(seriesId);
                item.setSeriesname(seriesBase.getName());
                item.setLevelid(seriesBase.getLevelId());
                item.setLevelname(levelBaseService.getName(seriesBase.getLevelId()));
                item.setSpecid(specid);
                item.setSpecname(specBase == null?"":specBase.getSpecName());
                item.setSpecimg(specBase == null?"":ImageUtil.getFullImagePath(specBase.getSpecLogoImg()));
                item.setSpecstate(x.getSpecState());
                item.setYearid(x.getSyearId());
                item.setYearaliasname(x.getSyearName());
                item.setSpecprice(x.getMaxPrice());
                item.setSpecminprice(x.getMinPrice());
                result.addSpecitems(item);
            });
            result.setTotal(result.getSpecitemsCount());
            builder.setResult(result);
        }
        return builder.build();
    }

    @Override
    public GetCrashTestBySeriesIdResponse getCrashTestBySeriesId(GetCrashTestBySeriesIdRequest request){
        GetCrashTestBySeriesIdResponse.Builder builder = GetCrashTestBySeriesIdResponse.newBuilder();
        GetCrashTestBySeriesIdResponse.Result.Builder result = GetCrashTestBySeriesIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int standardId = request.getStandardid();
        if(standardId == 0){
            standardId = 1;
        }
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SeriesBaseInfo seriesBase = seriesBaseService.get(seriesId).join();
        if(seriesBase != null){
            result.setSeriesid(seriesId);
            result.setSeriesname(seriesBase.getName());
            result.setSerieslogo(ImageUtil.getFullImagePath(seriesBase.getNoBgLogo()).replace("https://","http://"));
        }
        List<Integer> seriesIds = autoCacheService.getAllSeriesHaveCrashInfo();
        if(seriesIds != null && seriesIds.contains(seriesId)){
            List<CrashTestDetailEntity> list = autoCacheService.getCrashTestBySeriesId(seriesId,standardId);
            if(!CollectionUtils.isEmpty(list)){
                int articleId = 0;
                Map<Integer,List<CrashTestDetailEntity>> map = list.stream().collect(Collectors.groupingBy(CrashTestDetailEntity::getTypeid));
                for (Map.Entry<Integer,List<CrashTestDetailEntity>> item:map.entrySet()) {
                    Integer typeId = item.getKey();
                    GetCrashTestBySeriesIdResponse.Detail.Builder detail = GetCrashTestBySeriesIdResponse.Detail.newBuilder();
                    detail.setTypeid(typeId);
                    detail.setTypename(GetCrashTypeName(standardId,typeId));
                    for (CrashTestDetailEntity crash:item.getValue()) {
                        GetCrashTestBySeriesIdResponse.DetailItem.Builder detailItem = GetCrashTestBySeriesIdResponse.DetailItem.newBuilder();
                        detailItem.setItemid(crash.getItemid());
                        detailItem.setItemname(crash.getItemname());
                        String crashValue = crash.getCrashvalue();
                        int valueType = crash.getValuetype();
                        String dispContent = "";
                        if(crash.getArticleid()!=0){
                            articleId = crash.getArticleid();
                        }
                        //项目的值类型：0为数值、1为选择(G优秀、A良好、M一般、P较差)   2为boolen值。4为选择车型 用于前台展示内容判断,  3 小视频内容。
                        switch (valueType)
                        {
                            case 0:
                            case 3:
                                dispContent = crashValue;
                                break;
                            case 1:
                                dispContent = crashValue.equals("0") ? "" : getCrashResultValue(crashValue);
                                break;
                            case 2:
                                dispContent = crashValue.equals("1") ? "是" : "否";
                                break;
                            case 4:
                                SpecBaseInfo specBase = specBaseService.get(StringIntegerUtils.getIntOrDefault(crashValue,0)).join();
                                dispContent = StringUtils.isEmpty(crashValue) ? "" : crashValue.equals("-1") ? "全系未装载" : specBase == null?"":specBase.getSpecName();
                                break;
                        }
                        detailItem.setDispcontent(dispContent);
                        detailItem.setCrashvalue(crash.getCrashvalue());
                        detailItem.setRemark(crash.getRemark());
                        detailItem.setValuetype(crash.getValuetype());
                        detail.addList(detailItem);
                    }
                    result.addDetail(detail);
                }
                result.setArticleid(articleId);
            }
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    public String GetCrashTypeName(int standardId,int typeId)
    {
        if (standardId == 1)
        {
            if(typeId == 1){
                return CrashTypeEnum.V1.getValue();
            }
            if(typeId == 2){
                return CrashTypeEnum.V2.getValue();
            }
            if(typeId == 3){
                return CrashTypeEnum.V3.getValue();
            }
            if(typeId == 4){
                return CrashTypeEnum.V4.getValue();
            }
        }else if (standardId == 2)
        {
            if(typeId == 5){
                return CrashTypeEnum2020.V5.getValue();
            }
            if(typeId == 6){
                return CrashTypeEnum2020.V6.getValue();
            }
            if(typeId == 7){
                return CrashTypeEnum2020.V7.getValue();
            }
            if(typeId == 8){
                return CrashTypeEnum2020.V8.getValue();
            }
        }
        return null;
    }

    public static String getCrashResultValue(String value){
        if(value == null){
            return "";
        }
        if(value.equals("1")){
            return "G优秀";
        }
        if(value.equals("2")){
            return "A良好";
        }
        if(value.equals("3")){
            return "M一般";
        }
        if(value.equals("4")){
            return "P较差";
        }
        return "";
    }

    @Override
    public GetAllSeriesResponse getAllSeries(GetAllSeriesRequest request){
        GetAllSeriesResponse.Builder builder = GetAllSeriesResponse.newBuilder();
        GetAllSeriesResponse.Result.Builder result = GetAllSeriesResponse.Result.newBuilder();
        byte[] seriesInfoAllByte = autoCacheService.getAllSeriesList();
        //解压
        String json = new String(GZIPUtils.uncompress(seriesInfoAllByte));
        //转Java对象
        List<SeriesInfoAllEntity> seriesInfoAllEntities = JsonUtils.toObjectList(json, SeriesInfoAllEntity.class);
        seriesInfoAllEntities.forEach(seriesInfoAllEntity -> {
            GetAllSeriesResponse.SeriesItem.Builder item = GetAllSeriesResponse.SeriesItem.newBuilder();
            item.setId(seriesInfoAllEntity.getId());
            item.setName(null != seriesInfoAllEntity.getName() ? HtmlUtils.decode(seriesInfoAllEntity.getName()) : "");
            item.setFctid(seriesInfoAllEntity.getFctId());
            item.setUrl(null != seriesInfoAllEntity.getUrl() ? seriesInfoAllEntity.getUrl() : "");
            item.setBrandid(seriesInfoAllEntity.getBrandId());
            item.setLevelid(seriesInfoAllEntity.getJb());
            item.setLevelname(levelBaseService.getName(seriesInfoAllEntity.getJb()));
            item.setPlace(null != seriesInfoAllEntity.getPlace() ? seriesInfoAllEntity.getPlace() : "");
            item.setEdittime(null != seriesInfoAllEntity.getEditTime() ?
                    LocalDateUtils.format(seriesInfoAllEntity.getEditTime(), DATE_TIME_PATTERN_TWO) : "");
            item.setFirstletter(null != seriesInfoAllEntity.getFirstLetter() ? seriesInfoAllEntity.getFirstLetter() :"");
            item.setLogo(null != seriesInfoAllEntity.getLogo() ? ImageUtil.getFullImagePath(seriesInfoAllEntity.getLogo()) : "");
            item.setSeriespnglogo(null != seriesInfoAllEntity.getPngLogo() ? ImageUtil.getFullImagePath(seriesInfoAllEntity.getPngLogo()) : "");
            item.setState(seriesInfoAllEntity.getSeriesState());
            item.setMinprice(seriesInfoAllEntity.getPriceMin());
            item.setMaxprice(seriesInfoAllEntity.getPriceMax());
            item.setSalespecnum(seriesInfoAllEntity.getSsns());
            result.addSeriesitems(item);
        });
        result.setTotal(seriesInfoAllEntities.size());
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }

    @Override
    public SeriesByFactoryResponse seriesByFactory(SeriesByFactoryRequest request){
        SeriesByFactoryResponse.Builder builder = SeriesByFactoryResponse.newBuilder();
        SeriesByFactoryResponse.Result.Builder result = SeriesByFactoryResponse.Result.newBuilder();
        int brandId = request.getBrandid();
        int factoryid = request.getFactoryid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        int typeId = request.getTypeid();
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault( request.getIsFilterSpecImage(),0);
        typeId = typeId > 2 ? 0 : typeId;
        if (factoryid == 0 || state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        List<SeriesInfoEntity> list = autoCacheService.getAllSeriesItems();
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case SELL_30:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(!flag){
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }
        if(brandId > 0){
            list = list.stream().filter(s -> s.getBrandId() == brandId).collect(Collectors.toList());
        }
        list = list.stream().filter(s -> s.getFactoryId() == factoryid).collect(Collectors.toList());
        if(typeId > 0){
            int finalTypeId = typeId;
            list = list.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
        }
        if(isFilterSpecImage == 1){
            list = list.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        list = list.stream().sorted(Comparator.comparing(SeriesInfoEntity::getRankIndex)).collect(Collectors.toList());
        for (SeriesInfoEntity item:list) {
            SeriesBaseInfo seriesBase = seriesBaseService.get(item.getSeriesId()).join();
            SeriesByFactoryResponse.SeriesItem.Builder seriesItem = SeriesByFactoryResponse.SeriesItem.newBuilder();
            seriesItem.setId(item.getSeriesId());
            seriesItem.setName(seriesBase == null?"":seriesBase.getName());
            seriesItem.setSfirstletter(item.getSFirstLetter());
            seriesItem.setSeriesstate(item.getSeriesstate());
            seriesItem.setSeriesorder(item.getSeriesOrder());
            result.addSeriesitems(seriesItem);
        }
        List<SeriesByFactoryResponse.SeriesItem> fcts = result.getSeriesitemsList().stream().distinct().collect(Collectors.toList());
        result.clearSeriesitems();
        fcts.forEach(x->{
            result.addSeriesitems(x);
        });
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }


    /**
     * 据品牌获取品牌下车系名称等信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<BrandFactorySeriesItem> getSeriesNameByBrandId(GetSeriesNameByBrandIdRequest request) {
        int brandId = request.getBrandid();
        if(brandId == 0){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        BrandFactorySeriesItem brandFactorySeriesItem = new BrandFactorySeriesItem();
        //品牌和车系关系
        List<Integer> seriesIds = brandSeriesRelationBaseService.getSeriesIds(brandId);
        //车系信息
        List<SeriesBaseInfo> seriesBaseInfos = commService.getSeriesBaseInfoNoMap(seriesIds);
        List<BrandFactorySeriesItem.FactoryItem> factoryItems = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(seriesBaseInfos)){
            //厂商
            List<Integer> facIds = seriesBaseInfos.stream().map(seriesBaseInfo -> seriesBaseInfo.getFactId()).distinct().collect(Collectors.toList());
            Map<Integer, FactoryBaseInfo> factoryBaseInfoMap = commService.getFactoryBaseInfo(facIds);
            //级别
            List<Integer> levelId = seriesBaseInfos.stream().map(seriesBaseInfo -> seriesBaseInfo.getLevelId()).distinct().collect(Collectors.toList());
            Map<Integer, LevelBaseInfo> levelBaseInfoMap = commService.getLevelBaseInfo(levelId);
            //先按newSeriesOrderCls排序,然后按照厂商分组 保持原来顺序
            Map<Integer, List<SeriesBaseInfo>> bfsMap  = seriesBaseInfos.stream().
                    sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.groupingBy(SeriesBaseInfo::getFactId,
                            LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

            for(Map.Entry<Integer, List<SeriesBaseInfo>> factorySeriesMap:bfsMap.entrySet()){
                BrandFactorySeriesItem.FactoryItem factoryItem = new BrandFactorySeriesItem.FactoryItem();
                int facId = factorySeriesMap.getKey();
                factoryItem.setId(facId);
                FactoryBaseInfo factoryBaseInfo = factoryBaseInfoMap.get(facId);
                factoryItem.setName(null != factoryBaseInfo ? factoryBaseInfo.getName() : "");
                //按照厂商分好组的车系集合
                List<SeriesBaseInfo> seriesBaseInfoList = factorySeriesMap.getValue();
                List<BrandFactorySeriesItem.SeriesItem> seriesItems = new ArrayList<>();
                seriesBaseInfoList.forEach(seriesBaseInfo -> {
                    BrandFactorySeriesItem.SeriesItem seriesItem = new BrandFactorySeriesItem.SeriesItem();
                    seriesItem.setId(seriesBaseInfo.getId());
                    seriesItem.setName(seriesBaseInfo.getName());
                    seriesItem.setLevelid(seriesBaseInfo.getLevelId());
                    LevelBaseInfo levelBaseInfo = levelBaseInfoMap.get(seriesBaseInfo.getLevelId());
                    seriesItem.setLevelname(null != levelBaseInfo ? levelBaseInfo.getName() : "");
                    seriesItem.setIspublic(seriesBaseInfo.getSeriesIsPublic());
                    seriesItem.setSeriesstate(seriesBaseInfo.getSeriesState());
                    seriesItems.add(seriesItem);
                });
                //把车系信息放到厂商中
                factoryItem.setSeriesitems(seriesItems);
                factoryItems.add(factoryItem);
            }
        }
        brandFactorySeriesItem.setId(brandId);
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        brandFactorySeriesItem.setName(null != brandBaseInfo ? brandBaseInfo.getName() : "");
        brandFactorySeriesItem.setLogo(null != brandBaseInfo ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
        brandFactorySeriesItem.setOfficialurl(null != brandBaseInfo ? brandBaseInfo.getUrl() : "");
        brandFactorySeriesItem.setFactoryitems(factoryItems);
        return new ApiResult<>(brandFactorySeriesItem,RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据系列id获取系列名称
     * @param request
     * @return
     */
    @Override
    public GetSeriesNameBySeriesIdResponse getSeriesNameBySeriesId(GetSeriesNameBySeriesIdRequest request) {
        GetSeriesNameBySeriesIdResponse.Builder builder = GetSeriesNameBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if(seriesId <= 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        GetSeriesNameBySeriesIdResponse.Result.Builder resultBuilder = GetSeriesNameBySeriesIdResponse.Result.newBuilder();
        resultBuilder.setSeriesid(seriesId);
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(null != seriesBaseInfo){
            resultBuilder.setSeriesname(StringUtils.isNotBlank(seriesBaseInfo.getName()) ? seriesBaseInfo.getName() : "");
            resultBuilder.setBrandid(seriesBaseInfo.getBrandId());
            resultBuilder.setBrandname(StringUtils.isNotBlank(seriesBaseInfo.getBrandName()) ? HtmlUtils.decode(seriesBaseInfo.getBrandName()) : "");
            resultBuilder.setFctid(seriesBaseInfo.getFactId());
            String factoryName = factoryBaseService.getName(seriesBaseInfo.getFactId());
            resultBuilder.setFctname(factoryName);
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }
    /**
     * app接口需求 v8.8.5产品库源接口需求
     * 获取车系当前状态下最高配置的几项参数配置信息
     * @param request
     * @return
     */
    @Override
    public GetSeriesBaseParamBySeriesIdResponse getSeriesBaseParamBySeriesId(GetSeriesBaseParamBySeriesIdRequest request) {
        GetSeriesBaseParamBySeriesIdResponse.Builder builder = GetSeriesBaseParamBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM101.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM101.getReturnMsg())
                    .build();
        }
        int state = 0;
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(null != seriesBaseInfo){
            if(seriesBaseInfo.getSi() == 1){
                state = 1;
            }else{
                if(seriesBaseInfo.getWs() == 1){
                    state = 0;
                }else{
                    state = 2;
                }
            }
        }
        boolean isCv = null != seriesBaseInfo ? Level.isCVLevel(seriesBaseInfo.getLevelId()) : false;
        List<SpecViewEntity> specViewEntities = seriesSpecBaseService.get(seriesId, isCv).join();
        GetSeriesBaseParamBySeriesIdResponse.Result.Builder resultBuilder = GetSeriesBaseParamBySeriesIdResponse.Result.newBuilder();
        if(CollectionUtils.isNotEmpty(specViewEntities)){
            switch (state){
                case 0 :
                    specViewEntities = specViewEntities.stream().filter(specViewEntity ->
                            specViewEntity.getSpecState() <= 10 && specViewEntity.getSpecIsshow() == 1).collect(Collectors.toList());
                    break;
                case 1 :
                    specViewEntities = specViewEntities.stream().filter(specViewEntity ->
                            specViewEntity.getSpecState() >= 20 && specViewEntity.getSpecState() <= 30 && specViewEntity.getSpecIsshow() == 1).collect(Collectors.toList());
                    break;
                case 2 :
                    specViewEntities = specViewEntities.stream().filter(specViewEntity ->
                            specViewEntity.getSpecState() == 40 && specViewEntity.getSpecIsshow() == 1).collect(Collectors.toList());
                    break;
            }
            specViewEntities = specViewEntities.stream().filter(specViewEntity -> specViewEntity.getMinPrice() > 0).
                    sorted(Comparator.comparing(SpecViewEntity::getMinPrice,Comparator.reverseOrder())).limit(1).collect(Collectors.toList());
            int levelId = null != seriesBaseInfo ? seriesBaseInfo.getLevelId() : 0;
            String levelName = levelBaseService.getName(levelId);
            //遍历
            for(SpecViewEntity specViewEntity : specViewEntities){
                GetSeriesBaseParamBySeriesIdResponse.SeriesItem.Builder seriesBuilder = GetSeriesBaseParamBySeriesIdResponse.SeriesItem.newBuilder();
                seriesBuilder.setSeriesid(seriesId);
                seriesBuilder.setSeriesname(null != seriesBaseInfo && StringUtils.isNotBlank(seriesBaseInfo.getName()) ? seriesBaseInfo.getName() : "");
                seriesBuilder.setLevelid(levelId);
                seriesBuilder.setLevelname(levelName);
                seriesBuilder.setSpecid(specViewEntity.getSpecId());
                seriesBuilder.setSpecname(StringUtils.isNotBlank(specViewEntity.getSpecName()) ? specViewEntity.getSpecName() : "");
                seriesBuilder.setLength(specViewEntity.getLength());
                seriesBuilder.setWidth(specViewEntity.getWidth());
                seriesBuilder.setHeight(specViewEntity.getHeight());
                seriesBuilder.setOfficaloil(specViewEntity.getOfficalOil());
                seriesBuilder.setSeats(StringUtils.isNotBlank(specViewEntity.getSeats()) ? specViewEntity.getSeats() : "");
                seriesBuilder.setDoors(specViewEntity.getDoors());
                seriesBuilder.setMileage(StringUtils.isNotBlank(specViewEntity.getEndurancemileage()) ? Integer.valueOf(specViewEntity.getEndurancemileage()) : 0);
                seriesBuilder.setAcceleratedspeed(specViewEntity.getSsuo());
                seriesBuilder.setHorsepower(specViewEntity.getSpecEnginePower());
                resultBuilder.addList(seriesBuilder);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 根据品牌，销售状态，页数，页码 搜索车系数据
     * @param request
     * @return
     */
    @Override
    public GetSeriesByBrandAndStateResponse getSeriesByBrandAndState(GetSeriesByBrandAndStateRequest request) {
        GetSeriesByBrandAndStateResponse.Builder builder = GetSeriesByBrandAndStateResponse.newBuilder();
        int state = CommonFunction.getStringToInt(request.getState(), 1);
        int page = request.getPage() == 0 ? 1 : request.getPage();
        int size = request.getSize() == 0 ? 20 : request.getSize();
        int brandId = request.getBrandid();
        if (brandId <= 0 || size < 1 || page < 1 || state > 2) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        int start = (page - 1) * size + 1;//起始索引
        int end = page * size;//结束索引
        String tableName = state == 1 ? "SpecPriceSellView" : state == 0 ? "SpecPriceWaitSellView" : state == 2 ? "SpecPriceStopSellView" : "SpecPriceSellView";
        List<SeriesStateEntity> seriesStateEntities = autoCacheService.getSeriesInfoByBrandIdAndState(tableName, brandId);
        int count = 0;
        GetSeriesByBrandAndStateResponse.Result.Builder resultBuilder = GetSeriesByBrandAndStateResponse.Result.newBuilder();
        if (CollectionUtils.isNotEmpty(seriesStateEntities)) {
            count = seriesStateEntities.size();
            List<SeriesStateEntity> stateEntities = seriesStateEntities.stream().filter(seriesStateEntity ->
                            seriesStateEntity.getRowIndex() >= start && seriesStateEntity.getRowIndex() <= end).
                    sorted(Comparator.comparing(SeriesStateEntity::getSeriesId)).collect(Collectors.toList());
            //车系
            List<Integer> seriesIds = stateEntities.stream().map(SeriesStateEntity::getSeriesId).distinct().collect(Collectors.toList());
            Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);
            //厂商
            List<Integer> fctIds = stateEntities.stream().map(SeriesStateEntity::getFctId).distinct().collect(Collectors.toList());
            Map<Integer, FactoryBaseInfo> factoryBaseInfoMap = commService.getFactoryBaseInfo(fctIds);

            List<KeyValueDto<Integer, String>> gBrandAll = gBrandBaseService.getAll();
            //分组
            LinkedHashMap<Integer, ArrayList<SeriesStateEntity>> seriesStateMap = stateEntities.stream().collect(Collectors.groupingBy(seriesStateEntity ->
                    seriesStateEntity.getFctId(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

            for (Map.Entry<Integer, ArrayList<SeriesStateEntity>> seriesStateEntityMap : seriesStateMap.entrySet()) {
                GetSeriesByBrandAndStateResponse.FctItem.Builder fctItem = GetSeriesByBrandAndStateResponse.FctItem.newBuilder();
                int fctId = seriesStateEntityMap.getKey();
                fctItem.setId(String.valueOf(fctId));
                FactoryBaseInfo factoryBaseInfo = factoryBaseInfoMap.get(fctId);
                fctItem.setName((null != factoryBaseInfo && null != factoryBaseInfo.getName()) ? factoryBaseInfo.getName() : "");
                seriesStateEntityMap.getValue().forEach(seriesStateEntity -> {
                    GetSeriesByBrandAndStateResponse.SeriesItem.Builder seriesItem = GetSeriesByBrandAndStateResponse.SeriesItem.newBuilder();
                    int seriesId = seriesStateEntity.getSeriesId();
                    seriesItem.setId(seriesId);
                    SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(seriesId);
                    if(null != seriesBaseInfo){
                        String seriesName = "";
                        if(seriesId < 10000){
                            seriesName = null != seriesBaseInfo.getName() ? seriesBaseInfo.getName() : "";
                        }else{
                            seriesName = gBrandAll.stream().filter(keyValueDto -> keyValueDto.getKey() == seriesId).
                                    findFirst().map(KeyValueDto::getValue).orElse("概念车");
                        }
                        seriesItem.setSeriesname(seriesName);

                        seriesItem.setLogo(state == 2 ? (null != seriesBaseInfo.getStopLogo() ? ImageUtil.getFullImagePath(seriesBaseInfo.getStopLogo()) : "") :
                                (null != seriesBaseInfo.getLogo() ? ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()) : ""));
                    }
                    seriesItem.setMinprice(String.valueOf(seriesStateEntity.getSeriesFctMinPrice()));
                    seriesItem.setMaxprice(seriesStateEntity.getSeriesFctMaxPrice());
                    seriesItem.setFctid(seriesStateEntity.getFctId());
                    seriesItem.setFctname((null != factoryBaseInfo && null != factoryBaseInfo.getName()) ? factoryBaseInfo.getName() : "");
                    fctItem.addSeriesitems(seriesItem);
                });
                resultBuilder.addFctitems(fctItem);
            }
        }
        resultBuilder.setPageindex(page)
                .setSize(size)
                .setTotal(count);

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 根据品牌id获取报价库车系菜单
     * @param request
     * @return
     */
    @Override
    public GetSeriesMenuByBrandIdResponse getSeriesMenuByBrandId(GetSeriesMenuByBrandIdRequest request) {
        GetSeriesMenuByBrandIdResponse.Builder builder = GetSeriesMenuByBrandIdResponse.newBuilder();
        SpecStateEnum stateEnum = Spec.getSpecState(request.getState());
        if(!(stateEnum == SpecStateEnum.SELL_12 || stateEnum == SpecStateEnum.STOP_SELL || stateEnum == SpecStateEnum.SELL_14
        || stateEnum == SpecStateEnum.SELL_28 || stateEnum == SpecStateEnum.SELL_30)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        int brandId = request.getBrandid();
        if(brandId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<SeriesCountryEntity> seriesCountryAll = autoCacheService.getSeriesCountryAll();
        GetSeriesMenuByBrandIdResponse.Result.Builder resultBuilder = GetSeriesMenuByBrandIdResponse.Result.newBuilder();
        if(CollectionUtils.isNotEmpty(seriesCountryAll)){
            switch (stateEnum) {
                case SELL_12:
                    seriesCountryAll = seriesCountryAll.stream().filter(seriesCountryEntity ->
                            seriesCountryEntity.getSeriesIsPublic() == 1 && seriesCountryEntity.getBrandId() == brandId).collect(Collectors.toList());
                    break;
                case STOP_SELL:
                    seriesCountryAll = seriesCountryAll.stream().filter(seriesCountryEntity ->
                            seriesCountryEntity.getSeriesIsPublic() == 2 && seriesCountryEntity.getBrandId() == brandId).collect(Collectors.toList());
                    break;
                case SELL_14:
                    seriesCountryAll = seriesCountryAll.stream().filter(seriesCountryEntity ->
                            seriesCountryEntity.getSeriesIsPublic() <= 1 && seriesCountryEntity.getBrandId() == brandId).collect(Collectors.toList());
                    break;
                case SELL_28:
                    seriesCountryAll = seriesCountryAll.stream().filter(seriesCountryEntity ->
                            seriesCountryEntity.getSeriesIsPublic() >= 1 && seriesCountryEntity.getBrandId() == brandId).collect(Collectors.toList());
                    break;
                case SELL_30:
                    seriesCountryAll = seriesCountryAll.stream().filter(seriesCountryEntity ->
                            seriesCountryEntity.getBrandId() == brandId).collect(Collectors.toList());
                    break;
            }
            //厂商
            List<Integer> fctIds = seriesCountryAll.stream().map(SeriesCountryEntity::getFctId).distinct().collect(Collectors.toList());
            Map<Integer, FactoryBaseInfo> factoryBaseInfoMap = commService.getFactoryBaseInfo(fctIds);
            //车系
            List<Integer> seriesIds = seriesCountryAll.stream().map(SeriesCountryEntity::getSeriesId).distinct().collect(Collectors.toList());
            Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);
            //级别
            List<Integer> levelIds = seriesCountryAll.stream().map(SeriesCountryEntity::getLevelId).distinct().collect(Collectors.toList());
            Map<Integer, LevelBaseInfo> levelBaseInfoMap = commService.getLevelBaseInfo(levelIds);
            List<SeriesConfig> seriesConfigList = seriesConfigService.getList(seriesIds);
            //分组
            LinkedHashMap<Integer, ArrayList<SeriesCountryEntity>> countryMap = seriesCountryAll.stream().sorted(Comparator.comparing(SeriesCountryEntity::getFctId).thenComparing(SeriesCountryEntity::getSeriesId)).
                    collect(Collectors.groupingBy(seriesCountryEntity ->
                            seriesCountryEntity.getFctId(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for(Map.Entry<Integer, ArrayList<SeriesCountryEntity>> seriesCountryMap : countryMap.entrySet()){
                GetSeriesMenuByBrandIdResponse.FctItem.Builder fctItem = GetSeriesMenuByBrandIdResponse.FctItem.newBuilder();
                Integer fctId = seriesCountryMap.getKey();
                fctItem.setId(fctId);
                FactoryBaseInfo factoryBaseInfo = factoryBaseInfoMap.get(fctId);
                fctItem.setName((null != factoryBaseInfo && null != factoryBaseInfo.getName()) ? factoryBaseInfo.getName() : "");
                fctItem.setLogo((null != factoryBaseInfo && null != factoryBaseInfo.getLogo()) ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "");
                //车系
                if(CollectionUtils.isNotEmpty(seriesCountryMap.getValue())){
                    seriesCountryMap.getValue().forEach(seriesCountryEntity -> {
                        GetSeriesMenuByBrandIdResponse.SeriesItem.Builder seriesItem = GetSeriesMenuByBrandIdResponse.SeriesItem.newBuilder();
                        int seriesId = seriesCountryEntity.getSeriesId();
                        seriesItem.setId(seriesId);
                        SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(seriesId);
                        if(null != seriesBaseInfo){
                            seriesItem.setName(null != seriesBaseInfo.getName() ? seriesBaseInfo.getName() : "");
                            seriesItem.setLogo(null != seriesBaseInfo.getLogo() ? ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()) : "");
                            seriesItem.setMinprice(seriesBaseInfo.getSeriesPriceMin());
                            seriesItem.setMaxprice(seriesBaseInfo.getSeriesPriceMax());
                        }
                        seriesItem.setLevelid(seriesCountryEntity.getLevelId());
                        LevelBaseInfo levelBaseInfo = levelBaseInfoMap.get(seriesCountryEntity.getLevelId());
                        seriesItem.setLevelname((null != levelBaseInfo && null != levelBaseInfo.getName()) ? levelBaseInfo.getName() : "");
                        SeriesConfig seriesConfig = seriesConfigList.stream().filter(x -> x.getId() == seriesId).findFirst().orElse(null);
                        seriesItem.addAllGearbox(null != seriesConfig ? seriesConfig.getTransmissionitems() : new ArrayList<>());
                        seriesItem.addAllDisplacement(null != seriesConfig ? seriesConfig.getDisplacementitems() : new ArrayList<>());
                        seriesItem.addAllStructure(null != seriesConfig ? seriesConfig.getStructitems() : new ArrayList<>());
                        seriesItem.setSeriesordercls(seriesCountryEntity.getSeriesOrdercls());
                        fctItem.addSeriesitems(seriesItem);
                    });
                }
                resultBuilder.addFctitems(fctItem);
            }
        }
        BrandBaseInfo brandBaseInfo = brandBaseService.get(brandId).join();
        resultBuilder.setId(brandId)
                .setName(null != brandBaseInfo ? brandBaseInfo.getName() : "")
                .setLogo(null != brandBaseInfo ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
        return builder.setResult(resultBuilder)
                .setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @Override
    public GetSeriesInfoByBrandIdResponse getSeriesInfoByBrandId(GetSeriesInfoByBrandIdRequest request) {
        GetSeriesInfoByBrandIdResponse.Builder builder = GetSeriesInfoByBrandIdResponse.newBuilder();
        int brandId = request.getBrandid();
        List<Integer> seriesIds = brandSeriesRelationBaseService.getSeriesIds(brandId);
        List<SeriesBaseInfo> seriesBaseInfos = commService.getSeriesBaseInfoNoMap(seriesIds);
        seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo -> seriesBaseInfo.getSeriesSpecNum() >0).
                sorted(Comparator.comparing(SeriesBaseInfo::getNewSeriesOrderCls)).collect(Collectors.toList());
        GetSeriesInfoByBrandIdResponse.Result.Builder resultBuilder = GetSeriesInfoByBrandIdResponse.Result.newBuilder();
        if(CollectionUtils.isNotEmpty(seriesBaseInfos)){
            //级别
            List<Integer> levelIds = seriesBaseInfos.stream().map(SeriesBaseInfo::getLevelId).distinct().collect(Collectors.toList());
            Map<Integer, LevelBaseInfo> levelBaseInfoMap = commService.getLevelBaseInfo(levelIds);
            //厂商
            List<Integer> fctIds = seriesBaseInfos.stream().map(SeriesBaseInfo::getFactId).distinct().collect(Collectors.toList());
            Map<Integer, FactoryBaseInfo> factoryBaseInfoMap = commService.getFactoryBaseInfo(fctIds);

            Map<Integer, List<SeriesBaseInfo>> seriesBaseMap = seriesBaseInfos.stream().collect(Collectors.groupingBy(seriesBaseInfo ->
                    seriesBaseInfo.getFactId(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for(Map.Entry<Integer, List<SeriesBaseInfo>> seriesMap : seriesBaseMap.entrySet()){
                GetSeriesInfoByBrandIdResponse.FctItem.Builder fctItem = GetSeriesInfoByBrandIdResponse.FctItem.newBuilder();
                GetSeriesInfoByBrandIdResponse.FctItem.Builder allFctItem = GetSeriesInfoByBrandIdResponse.FctItem.newBuilder();
                seriesMap.getValue().forEach(seriesBaseInfo -> {
                    GetSeriesInfoByBrandIdResponse.SeriesItem.Builder sellSeriesItem = GetSeriesInfoByBrandIdResponse.SeriesItem.newBuilder();
                    sellSeriesItem.setId(seriesBaseInfo.getId());
                    sellSeriesItem.setName(null != seriesBaseInfo.getName() ? seriesBaseInfo.getName() : "");
                    sellSeriesItem.setLevelid(seriesBaseInfo.getLevelId());
                    LevelBaseInfo levelBaseInfo = levelBaseInfoMap.get(seriesBaseInfo.getLevelId());
                    sellSeriesItem.setLevelname((null != levelBaseInfo && null != levelBaseInfo.getName()) ? levelBaseInfo.getName() : "");
                    sellSeriesItem.setSeriesstate(seriesBaseInfo.getSeriesState());
                    sellSeriesItem.setMaxprice(seriesBaseInfo.getSeriesPriceMax());
                    sellSeriesItem.setMinprice(seriesBaseInfo.getSeriesPriceMin());
                    sellSeriesItem.setSeriespicurl(null != seriesBaseInfo.getLogo() ? ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()) : "");
                    sellSeriesItem.setSeriespnglogo(null != seriesBaseInfo.getNoBgLogo() ? ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()) : "");
                    sellSeriesItem.setNewenergy(seriesBaseInfo.getIne());
                    if(seriesBaseInfo.getSeriesIsPublic() == 1){
                        fctItem.addSeriesitems(sellSeriesItem);
                    }
                    allFctItem.addSeriesitems(sellSeriesItem);
                });
                fctItem.setId(seriesMap.getKey());
                FactoryBaseInfo factoryBaseInfo = factoryBaseInfoMap.get(seriesMap.getKey());
                fctItem.setName(null != factoryBaseInfo && null != factoryBaseInfo.getName() ? factoryBaseInfo.getName() : "");

                allFctItem.setId(seriesMap.getKey());
                allFctItem.setName(null != factoryBaseInfo && null != factoryBaseInfo.getName() ? factoryBaseInfo.getName() : "");
                //赋值
                resultBuilder.addAllsellseries(allFctItem);
                //有数据添加
                if(fctItem.getSeriesitemsCount() > 0){
                    resultBuilder.addSellseries(fctItem);
                }

            }

        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder).build();
    }

    @Override
    public GetGetSeriesStateInfoResponse getSeriesStateInfo(GetGetSeriesStateInfoRequest request) {
        GetGetSeriesStateInfoResponse.Builder builder = GetGetSeriesStateInfoResponse.newBuilder();
        GetGetSeriesStateInfoResponse.Result.Builder result = GetGetSeriesStateInfoResponse.Result.newBuilder();
        List<SeriesViewSimpInfo> seriesViewInfo = autoCacheService.getSeriesViewInfo();
        List<Integer> allValidSeriesIds = autoCacheService.getAllValidSeriesIds();

        if (CollectionUtils.isEmpty(seriesViewInfo) || CollectionUtils.isEmpty(allValidSeriesIds)) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        for (SeriesViewSimpInfo seriesViewSimpInfo : seriesViewInfo) {
            if(allValidSeriesIds.contains(seriesViewSimpInfo.getSeriesId())){
                result.addSeriesitems(GetGetSeriesStateInfoResponse.Result.Seriesitem.newBuilder().setSeriesid(seriesViewSimpInfo.getSeriesId()).build());
            }
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetCrashTestSeriesRankResponse getCrashTestSeriesRank(GetCrashTestSeriesRankRequest request) {
        GetCrashTestSeriesRankResponse.Builder builder = GetCrashTestSeriesRankResponse.newBuilder();
        GetCrashTestSeriesRankResponse.Result.Builder result = GetCrashTestSeriesRankResponse.Result.newBuilder();
        int standardId = CommonFunction.getStringToInt(request.getStandardid(), 1);
        int orderType = request.getOrdertype();
        if (standardId > 3 || standardId < 1) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        if (standardId == 3) {
            return getCrashTestSeriesRankByStandId(request);
        }
        List<CrashSeriesEntity> crashSeriesEntityList = autoCacheService.getCrashTestData(orderType, standardId);
        if (CollectionUtils.isEmpty(crashSeriesEntityList)) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        List<Integer> seriesIds = crashSeriesEntityList.stream().map(CrashSeriesEntity::getSeriesid).distinct().collect(Collectors.toList());
        Map<Integer, List<CrashSeriesEntity>> crashSeriesGroupBy = crashSeriesEntityList.stream().collect(Collectors.groupingBy(CrashSeriesEntity::getSeriesid));
        Map<Integer, SeriesConfig> map = seriesConfigService.getMap(seriesIds);
        int num = 1;
        for (Integer seriesId : seriesIds) {
            SeriesConfig seriesConfig = CollectionUtils.isNotEmptyMap(map) ? map.get(seriesId) : null;
            if (Objects.isNull(seriesConfig)) {
                continue;
            }
            List<GetCrashTestSeriesRankResponse.Result.List.Itemlist> itemList = new ArrayList<>();
            List<CrashSeriesEntity> crashSeriesEntities = crashSeriesGroupBy.get(seriesId);
            for (CrashSeriesEntity crashSeriesEntity : crashSeriesEntities) {
                String crashValue = crashSeriesEntity.getCrashvalue();
                int value = StringUtils.isNotBlank(crashValue) ? Integer.parseInt(crashValue) : 0;
                int itemId = crashSeriesEntity.getItemid();
                if (standardId == 2) {
                    itemId = convertItemId2017(itemId);
                }

                itemList.add(GetCrashTestSeriesRankResponse.Result.List.Itemlist.newBuilder().setItemid(itemId).setTestvalue(value).build());
            }
            result.addList(GetCrashTestSeriesRankResponse.Result.List.newBuilder().setOrdernum(num++).
                    setSeriesid(seriesId).
                    setSeriesname(StringUtils.defaultString(seriesConfig.getName())).
                    setMinprice(seriesConfig.getTempMinPrice()).
                    setMaxprice(seriesConfig.getTempMaxPrice()).
                    setSeriespnglogo(StringUtils.defaultString(seriesConfig.getPnglogo())).addAllItemlist(itemList).
                    build());
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    public GetCrashTestSeriesRankResponse getCrashTestSeriesRankByStandId(GetCrashTestSeriesRankRequest request) {
        GetCrashTestSeriesRankResponse.Builder builder = GetCrashTestSeriesRankResponse.newBuilder();
        GetCrashTestSeriesRankResponse.Result.Builder result = GetCrashTestSeriesRankResponse.Result.newBuilder();
        List<CrashCnCapSeriesEntity> crashSeriesEntityList = autoCacheService.getCrashCnCapTestData();
        Map<Integer, SeriesConfig> map = seriesConfigService.getMap(crashSeriesEntityList.stream().map(CrashCnCapSeriesEntity::getSeriesid).collect(Collectors.toList()));
        int num = 1;
        for (CrashCnCapSeriesEntity entity : crashSeriesEntityList) {
            int seriesId = entity.getSeriesid();
            SeriesConfig seriesConfig = CollectionUtils.isNotEmptyMap(map) ? map.get(seriesId) : null;
            if (Objects.isNull(seriesConfig)) {
                continue;
            }
            result.addList(GetCrashTestSeriesRankResponse.Result.List.newBuilder().setOrdernum(num++).
                    setSeriesid(seriesId).
                    setSeriesname(StringUtils.defaultString(seriesConfig.getName())).
                    setMinprice(seriesConfig.getTempMinPrice()).
                    setMaxprice(seriesConfig.getTempMaxPrice()).
                    setSeriespnglogo(StringUtils.defaultString(seriesConfig.getPnglogo())).
                    setCompscore(StringUtils.defaultString(entity.getCompscore())).
                    setStarscore(StringUtils.defaultString(entity.getStarscore())).
                    build());
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }
    private int convertItemId2017(int itemId2020) {
        switch (itemId2020) {
            case 32:
                return 2;
            case 40:
                return 13;
            case 47:
                return 20;
            case 55:
                return 25;
            default:
                return 0;
        }
    }


    /**
     * 根据车系id配置选装包信息
     * @param request
     * @return
     */
    @Override
    public GetConfigBagBySeriesIdResponse getConfigBagBySeriesId(GetConfigBagBySeriesIdRequest request) {
        GetConfigBagBySeriesIdResponse.Builder builder = GetConfigBagBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        GetConfigBagBySeriesIdResponse.Result.Builder result = GetConfigBagBySeriesIdResponse.Result.newBuilder();
        //specItem设置
        List<Integer> specIds = this.getSpecList(seriesId, result);
        GetConfigBagBySeriesIdResponse.BagTypeItem.Builder bagTypeItem = GetConfigBagBySeriesIdResponse.BagTypeItem.newBuilder();
        if(CollectionUtils.isNotEmpty(specIds)){
            List<SpecConfigBagEntity> specConfigBagEntities = specConfigBagNewService.getBagList(specIds);
            if(CollectionUtils.isNotEmpty(specConfigBagEntities)){
                LinkedHashMap<Integer, ArrayList<SpecConfigBagEntity>> specBagMap = specConfigBagEntities.stream().
                        sorted(Comparator.comparing(SpecConfigBagEntity::getBagId, Comparator.reverseOrder())).
                        collect(Collectors.groupingBy(SpecConfigBagEntity::getBagId,
                                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                //bagItem设置
                for(Map.Entry<Integer, ArrayList<SpecConfigBagEntity>> bagMap : specBagMap.entrySet()){
                    GetConfigBagBySeriesIdResponse.BagItem.Builder bagItem = GetConfigBagBySeriesIdResponse.BagItem.newBuilder();
                    ArrayList<SpecConfigBagEntity> bagMapValue = bagMap.getValue();
                    for (Integer specId : specIds) {
                        GetConfigBagBySeriesIdResponse.ValueItem.Builder valueItem = GetConfigBagBySeriesIdResponse.ValueItem.newBuilder();
                        valueItem.setSpecid(specId);
                        List<Integer> specIdList = bagMapValue.stream().map(SpecConfigBagEntity::getSpecId).collect(Collectors.toList());
                        valueItem.setValue(specIdList.contains(specId) ? "○" : "-");
                        bagItem.addValueitems(valueItem);
                    }
                    bagItem.setId(bagMapValue.get(0).getBagId());
                    bagItem.setName(null != bagMapValue.get(0).getBagName() ? bagMapValue.get(0).getBagName() : "");
                    bagItem.setDescription(null != bagMapValue.get(0).getDescrip() ? bagMapValue.get(0).getDescrip() : "");
                    bagItem.setPrice(bagMapValue.get(0).getPrice());
                    bagTypeItem.addBagitems(bagItem);
                }
                if(bagTypeItem.getBagitemsCount() > 0){
                    bagTypeItem.setName("选装包");
                }
                result.addBagtypeitems(bagTypeItem);
            }
        }
        result.setSeriesid(seriesId);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }

    /**
     * 根据车系名称获取车系ID
     * @param request
     * @return
     */
    @Override
    public GetSeriesIdBySeriesNameResponse getSeriesIdBySeriesName(GetSeriesIdBySeriesNameRequest request) {
        GetSeriesIdBySeriesNameResponse.Builder builder = GetSeriesIdBySeriesNameResponse.newBuilder();
        String seriesName = request.getSeriesname();
        if(StringUtils.isBlank(seriesName)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        KeyValueDto<Integer,String> keyValueDto = autoCacheService.getSeriesIdBySeriesName(seriesName);
        GetSeriesIdBySeriesNameResponse.Result.Builder resultBuilder = GetSeriesIdBySeriesNameResponse.Result.newBuilder();
        resultBuilder.setSeriesname(seriesName);
        resultBuilder.setSeriesid(null != keyValueDto ? keyValueDto.getKey() : 0);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * specItem设置值，并返回过滤后的specIds
     * @param seriesId
     * @param result
     * @return
     */
    private List<Integer> getSpecList(int seriesId, GetConfigBagBySeriesIdResponse.Result.Builder result) {
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (Objects.isNull(seriesBaseInfo)) {
            return Collections.emptyList();
        }
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        List<SpecViewEntity> specViewEntities = seriesSpecBaseService.get(seriesId, isCV).join();
        if (CollectionUtils.isEmpty(specViewEntities)) {
            return Collections.emptyList();
        }
        if (isCV) {
            specViewEntities = specViewEntities.stream().filter(specViewEntity -> specViewEntity.getSpecState() >=10 && specViewEntity.getSpecState() <= 30)
                    .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).thenComparing(SpecViewEntity::getSpecOrdercls).thenComparing(Comparator.comparing(SpecViewEntity::getSpecId).reversed()))
                    .collect(Collectors.toList());
        } else {
            specViewEntities = specViewEntities.stream().filter(specViewEntity ->  specViewEntity.getSpecState() <= 30 &&specViewEntity.getSpecIsImage() == 0)
                    .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).thenComparing(SpecViewEntity::getIsclassic).thenComparing(SpecViewEntity::getSpecOrdercls).thenComparing(Comparator.comparing(SpecViewEntity::getSpecId).reversed()))
                    .collect(Collectors.toList());
        }
        List<Integer> specIds = specViewEntities.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);

        specViewEntities = specViewEntities.stream().filter(Objects::nonNull).filter(specViewEntity ->{
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specViewEntity.getSpecId());
            return (specBaseInfo != null && specBaseInfo.getIsSpecParamIsShow() == 1) || specViewEntity.getSpecState() == 40;
        }).collect(Collectors.toList());

        specViewEntities.forEach(specViewEntity -> {
            GetConfigBagBySeriesIdResponse.SpecItem.Builder specItem = GetConfigBagBySeriesIdResponse.SpecItem.newBuilder();
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specViewEntity.getSpecId());
            specItem.setSpecid(specViewEntity.getSpecId());
            specItem.setSpecstate(specViewEntity.getSpecState());
            int showstate = 0;
            //即将销售接受预定 obj.Key==1接受预订
            if (specViewEntity.getSpecState() == 10) {
                showstate = null != specBaseInfo && specBaseInfo.getIsBooked() == 1 ? 1 : 0;
            } else {
                showstate = -1;
            }
            specItem.setShowstate(showstate);
            result.addSpeclist(specItem);
        });
        return specViewEntities.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
    }

    @Override
    public GetSeriesHotResponse getSeriesHot(GetSeriesHotRequest request){
        GetSeriesHotResponse.Builder builder = GetSeriesHotResponse.newBuilder();
        GetSeriesHotResponse.Result.Builder result = GetSeriesHotResponse.Result.newBuilder();
        int size = request.getSize();
        int page = request.getPage();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        String seriesIspublic = "";
        if (size < 1 || page < 1 || state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        String where = " where 1=1 ";
        int start = (page - 1) * size + 1;//起始索引
        int end = page * size;//结束索引
        switch (state) {
            case STOP_SELL:
                where += "AND SeriesIspublic=2 ";
                break;
            case SELL_3:
                where += "AND SeriesIspublic=0 ";
                break;
            //在产在售(0X0004)
            case SELL_12:
                where += "AND SeriesIspublic=1 ";
                break;
            case SELL_28:
                where += "AND SeriesIspublic>=1 ";
                break;
            case SELL_15:
                where += "AND SeriesIspublic<=1 ";
                break;
            case SELL_31:
                break;
        }
        int total = autoCacheService.getSeriesHotCount(where);
        List<SeriesHotEntity> list = autoCacheService.getSeriesHot(start,end,where);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(x -> {
                GetSeriesHotResponse.SeriesItem.Builder item = GetSeriesHotResponse.SeriesItem.newBuilder();
                SeriesBaseInfo seriesBase = seriesBaseService.get(x.getSeriesId()).join();
                item.setId(x.getSeriesId());
                item.setName(seriesBase == null ? "" : seriesBase.getName());
                item.setLogo(seriesBase == null ? "" : ImageUtil.getFullImagePath(seriesBase.getLogo()));
                item.setMinprice(x.getSeriesPriceMin());
                item.setMaxprice(x.getSeriesPriceMax());
                item.setIspublic(x.getSeriesIspublic());
                result.addSeriesitems(item);
            });
        }
        result.setTotal(total);
        result.setSize(size);
        result.setPageindex(page);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public Series25PointToVRResponse series25PointToVR(Series25PointToVRRequest request){
        Series25PointToVRResponse.Builder builder = Series25PointToVRResponse.newBuilder();
        int seriesid = request.getSeriesid();
        if (seriesid == 0)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        Series25PointToVRResponse.Result.Builder result = Series25PointToVRResponse.Result.newBuilder();
        Map<Integer, String> map = CommonFunction.getDicPic25PointToVR();
        List<Integer> list25 = autoCacheService.getSeries25Pic(seriesid);
        if(list25.size()>0) {
            if (list25.size() == 25) {
                map.forEach((k,v)->{
                    Series25PointToVRResponse.ClassItem.Builder item = Series25PointToVRResponse.ClassItem.newBuilder();
                    item.setClassid(k);
                    item.setName(CommonFunction.getPic25PointLocation(k));
                    item.setFrameids(v);
                    result.addList(item);
                });
            } else {
                map.forEach((k,v)->{
                    if(!list25.contains(k))
                        return;
                    Series25PointToVRResponse.ClassItem.Builder item = Series25PointToVRResponse.ClassItem.newBuilder();
                    item.setClassid(k);
                    item.setName(CommonFunction.getPic25PointLocation(k));
                    item.setFrameids(v);
                    result.addList(item);
                });

//                list25 = list25.stream().filter(x -> map.containsKey(x)).collect(Collectors.toList());
//                list25.forEach(x -> {
//                    Series25PointToVRResponse.ClassItem.Builder item = Series25PointToVRResponse.ClassItem.newBuilder();
//                    item.setClassid(x);
//                    item.setName(CommonFunction.getPic25PointLocation(x));
//                    item.setFrameids(map.get(x));
//                    result.addList(item);
//                });
            }
            builder.setResult(result);
        }
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public CrashTestSeriesListResponse crashTestSeriesList(CrashTestSeriesListRequest request){
        CrashTestSeriesListResponse.Builder builder = CrashTestSeriesListResponse.newBuilder();
        List<Integer> list = autoCacheService.getCrashTestSeriesList();
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(x -> {
                SeriesBaseInfo seriesBase = seriesBaseService.get(x).join();
                CrashTestSeriesListResponse.Result.Builder item = CrashTestSeriesListResponse.Result.newBuilder();
                item.setId(x);
                item.setName(seriesBase == null ? "" : seriesBase.getName());
                builder.addResult(item);
            });
        }
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetSeriesAllBaseInfoResponse getSeriesAllBaseInfo(GetSeriesAllBaseInfoRequest request){
        GetSeriesAllBaseInfoResponse.Builder builder = GetSeriesAllBaseInfoResponse.newBuilder();
        List<SeriesBaseEntity> seriesList = seriesAllSearchService.get();
        if(CollectionUtils.isEmpty(seriesList)){
            return builder.setReturnCode(0).setReturnMsg("成功").build();
        }
        GetSeriesAllBaseInfoResponse.Result.Builder result = GetSeriesAllBaseInfoResponse.Result.newBuilder();
        for(SeriesBaseEntity item : seriesList){
            result.addSeriesitems(GetSeriesAllBaseInfoResponse.SeriesItem.newBuilder()
                    .setId(item.getId())
                    .setName(null != item.getName() ? item.getName() : "")
                    .setLevelid(item.getLevelId())
                    .setLevelname(null != item.getLevelName() ? item.getLevelName() : "")
                    .setBrandid(item.getBrandId())
                    .setBrandname(null != item.getBrandName() ? item.getBrandName() : "")
                    .setFactoryid(item.getFctId())
                    .setFactoryname(null != item.getFctName() ? item.getFctName() : "")
                    .setPlace(null != item.getPlace() ? item.getPlace() : "")
                    .setRank(item.getNewRank())
                    .setState(item.getState())
            );
        }
        result.setTotal(seriesList.size());
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    /**
     * 获取包含电动车车系列表 分类
     * @param request
     * @return
     */
    @Override
    public SelectElectricListResponse getSelectElectricList(SelectElectricListRequest request) {
        SelectElectricListResponse.Builder builder = SelectElectricListResponse.newBuilder();
        SelectElectricListResponse.Result.Builder result = SelectElectricListResponse.Result.newBuilder();
        AtomicReference<List<ElectricSeriesEntity>> electricSeriesTask = new AtomicReference<>();
        AtomicReference<List<ElectricSpecBaseEntity>> electricSpecBaseTask = new AtomicReference<>();
        AtomicReference<List<SpecSellEntity>> SpecSellTask = new AtomicReference<>();


        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(CompletableFuture.supplyAsync(()->seriesViewMapper.getElectricSeriesAll()).thenAccept(x->{
            electricSeriesTask.set(x);
        }));
        tasks.add(CompletableFuture.supplyAsync(()->specViewMapper.getElectricSpecBaseAll()).thenAccept(x->{
            electricSpecBaseTask.set(x);
        }));
        tasks.add(CompletableFuture.supplyAsync(()->specViewMapper.getElectricSellAll()).thenAccept(x->{
            SpecSellTask.set(x);
        }));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        List<ElectricSeriesEntity> electricSeriesEntities = electricSeriesTask.get();
        List<ElectricSpecBaseEntity> electricSpecBaseEntities = electricSpecBaseTask.get();
        List<SpecSellEntity> specSellEntities = SpecSellTask.get();
        Map<Integer,List<SpecSellEntity>> specSellMap = CollectionUtils.isNotEmpty(specSellEntities) ?
                specSellEntities.stream().collect(Collectors.groupingBy(SpecSellEntity::getSeriesId)) : new HashMap<>();
        if(CollectionUtils.isNotEmpty(electricSeriesEntities)){
            for(ElectricSeriesEntity electricSeriesEntity : electricSeriesEntities){
                if(CommonFunction.ELECTRIC_SERIESID.contains(electricSeriesEntity.getSeriesId())){
                    continue;
                }
                SelectElectricListResponse.SeriesItem.Builder seriesItem = SelectElectricListResponse.SeriesItem.newBuilder();
                List<ElectricSpecBaseEntity> specBaseEntityList = electricSpecBaseEntities.stream().filter(electricSpecBaseEntity -> electricSeriesEntity.getSeriesId() == electricSpecBaseEntity.getSeriesId()).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(specBaseEntityList)){
                    Map<Integer, ArrayList<ElectricSpecBaseEntity>> specBaseMap = specBaseEntityList.stream().sorted(Comparator.comparing(ElectricSpecBaseEntity::getLiCheng,Comparator.reverseOrder()))
                            .collect(Collectors.groupingBy(ElectricSpecBaseEntity::getFuelType, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                    for (Map.Entry<Integer, ArrayList<ElectricSpecBaseEntity>> electricSpecMap : specBaseMap.entrySet()) {
                        SelectElectricListResponse.ClassItem.Builder classItem = SelectElectricListResponse.ClassItem.newBuilder();
                        classItem.setTypeid(electricSpecMap.getKey());
                        classItem.setTypename(electricSpecMap.getKey() == 1 ? "纯电动" : electricSpecMap.getKey() == 2 ? "插电式混合动力" : "增程式");
                        classItem.setLicheng(null != electricSpecMap.getValue() ? String.valueOf(electricSpecMap.getValue().get(0).getLiCheng()) : "0");
                        seriesItem.addClassitems(classItem);
                    }
                }

                seriesItem.setSeriesid(electricSeriesEntity.getSeriesId());
                seriesItem.setSeriesname(electricSeriesEntity.getSeriesName());
                seriesItem.setSeriesrank(electricSeriesEntity.getSeriesRank());
                seriesItem.setState(electricSeriesEntity.getSeriesState());
                seriesItem.setElectricstate(electricSeriesEntity.getSeriesState());
                List<SpecSellEntity> sellEntities = specSellMap.get(electricSeriesEntity.getSeriesId());
                seriesItem.setIspreferential(CollectionUtils.isNotEmpty(sellEntities) ? (sellEntities.get(0).getTaxType() == 1 ? 1 : 0) : 0);
                seriesItem.setIstaxexemption(CollectionUtils.isNotEmpty(sellEntities) ? (sellEntities.get(0).getTaxType() == 2 ? 1 : 0) : 0);
                result.addItems(seriesItem);
            }
        }
        result.setCount(result.getItemsCount());
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result).build();
    }


    /**
     * APP参数配置页展示智能类视频
     * @param request
     * @return
     */
    @Override
    public ConfigWithAiVideoResponse getConfigWithAiVideoForApp(ConfigWithAiVideoRequest request) {
        ConfigWithAiVideoResponse.Builder builder = ConfigWithAiVideoResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId <= 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        //根据车系id获取审核通过的订单信息
        EpibolyAiVideoOrderEntity aiVideoOrderEntity = epibolyAiVideoOrderService.getBySeriesId(seriesId);
        if(null == aiVideoOrderEntity){
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode()).
                    setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
            return builder.build();
        }
        //获取智能类视频关联的配置信息
        List<PointParamConfigEntity> paramConfigEntities = pointParamConfigService.getByBuId(4);
        if(CollectionUtils.isEmpty(paramConfigEntities)){
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode()).
                    setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
            return builder.build();
        }
        //只要配置信息
        paramConfigEntities = paramConfigEntities.stream().filter(pointParamConfigEntity -> pointParamConfigEntity.getDataType() == 2).collect(Collectors.toList());
        //根据订单id订单的视频详情信息
        List<EpibolyAiVideoOrderDetailEntity> orderDetailEntityList = epibolyAiVideoOrderDetailService.getByOrderId(aiVideoOrderEntity.getOrderId());
        //返回信息组装
        for(PointParamConfigEntity pointParamConfigEntity : paramConfigEntities){
            ConfigWithAiVideoResponse.Result.Builder result = ConfigWithAiVideoResponse.Result.newBuilder();
            result.setSeriesid(seriesId);
            result.setSpecid(aiVideoOrderEntity.getSpecId());
            result.setConfigid(pointParamConfigEntity.getParamConfigId());
            result.setConfigname(null != pointParamConfigEntity.getParamConfigName() ? pointParamConfigEntity.getParamConfigName() : "");
            //获取当前配置项点位的智能视频信息
            EpibolyAiVideoOrderDetailEntity orderDetailEntity = CollectionUtils.isEmpty(orderDetailEntityList) ? null :
                    orderDetailEntityList.stream().filter(video -> video.getStatus() == 1 && video.getPointId() == pointParamConfigEntity.getPointLocationId()).findFirst().orElse(null);
            if(null == orderDetailEntity){
                continue;
            }else{
                result.setVideoid(orderDetailEntity.getSourceId());
            }
            builder.addResult(result);
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
}
