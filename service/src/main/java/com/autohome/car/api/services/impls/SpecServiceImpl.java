package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.car.ConfigListByYearIdRequest;
import autohome.rpc.car.car_api.v1.car.ConfigListByYearIdResponse;
import autohome.rpc.car.car_api.v1.car.ConfigTypeItem;
import autohome.rpc.car.car_api.v1.common.CarPhotoViewItemMessage;
import autohome.rpc.car.car_api.v1.pic.GetClassItemsBySpecIdRequest;
import autohome.rpc.car.car_api.v1.pic.GetClassItemsBySpecIdResponse;
import autohome.rpc.car.car_api.v1.spec.*;
import autohome.rpc.car.car_api.v2.spec.*;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CarPhotoService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.SpecService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.*;
import com.autohome.car.api.services.basic.series.*;
import com.autohome.car.api.services.basic.specs.*;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.common.SpecElectric;
import com.autohome.car.api.services.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.LocalDateUtils.DATE_TIME_PATTERN_TWO;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
@Slf4j
public class SpecServiceImpl implements SpecService {


    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    SpecPictureStatisticsMapper specPictureStatisticsMapper;

    @Autowired
    SpecColorMapper specColorMapper;

    @Autowired
    PicClassMapper picClassMapper;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    FactoryBaseService factoryInfoService;

    @Autowired
    LevelBaseService levelBaseInfoService;

    @Autowired
    QRCodeService qrCodeService;

    @Autowired
    private BrandBaseService brandBaseService;

    @Autowired
    PicClassBaseService picClassBaseService;

    @Autowired
    SpecPicClassStatisticsMapper specPicClassStatisticsMapper;

    @Autowired
    SpecParamViewMapper specParamViewMapper;

    @Autowired
    SpecPicClassStatisticsBaseService specPicClassStatisticsBaseService;

    @Autowired
    SeriesSpecBaseService seriesSpecBaseService;

    @Autowired
    SpecPicColorStatisticsBaseService specPicColorStatisticsBaseService;

    @Autowired
    SeriesSpecPicInnerColorStatistics seriesSpecPicInnerColorStatistics;

    @Autowired
    SeriesSpecPicColorStatistics seriesSpecPicColorStatistics;

    @Autowired
    ColorBaseService colorBaseService;

    @Resource
    private InnerColorBaseService innerColorBaseService;

    @Autowired
    private SpecConfigService specConfigService;

    @Autowired
    private OptParItemInfoService optParItemInfoService;

    @Autowired
    private SpecColorService specColorService;

    @Autowired
    private PhotosService photosService;

    @Autowired
    private CarPhotoService carPhotoService;

    @Autowired
    private SeriesConfigService seriesConfigService;

    @Autowired
    private SpecInnerColorBaseService specInnerColorBaseService;

    @Autowired
    SpecSpecColorBaseService specSpecColorBaseService;

    @Resource
    private CommService commService;

    @Resource
    private SeriesSpecService seriesSpecService;

    @Autowired
    ElectricSpecViewBaseService electricSpecViewBaseService;

    @Autowired
    SpecPictureStatisticsBaseService specPictureStatisticsBaseService;

    @Autowired
    Spec25PicBaseService spec25PicBaseService;

    @Autowired
    SpecColorPicNumBaseService specColorPicNumBaseService;

    @Autowired
    SpecInnerColorPicNumBaseService specInnerColorPicNumBaseService;

    @Autowired
    SpecColorPicNumCVBaseService specColorPicNumCVBaseService;

    @Autowired
    SeriesSpecInfoBaseService seriesSpecInfoBaseService;

    @Autowired
    AutoCacheService autoCacheService;

    @Resource
    private SeriesInfoService seriesInfoService;

    @Resource
    private ConfigListService configListService;

    @Resource
    private SpecConfigRelationService specConfigRelationService;

    @Resource
    private SpecConfigPriceService specConfigPriceService;

    @Resource
    private ConfigItemValueService configItemValueService;

    @Resource
    private SpecConfigSubItemService specConfigSubItemService;

    @Resource
    private ConfigSubItemService configSubItemService;

    @Autowired
    PictureTypeBaseService pictureTypeBaseService;

    @Resource
    SpecListSameYearByYearService specListSameYearByYearService;

    @Resource
    ParamConfigModelService paramConfigModelService;

    @Resource
    private SpecListSameYearBaseService specListSameYearBaseService;

    @Autowired
    private SpecSearchService specSearchService;

    @Override
    @AutoCache(expireIn = 24*60)
    public List<KeyValueDto<Integer,String>> getCar25PictureType(){
        return picClassMapper.getCar25PictureType();
    }

    /**
     * 根据车型id 获取该车型对应年代款的所有车型id列表
     *
     * @param specId
     * @return 获取该车型对应年代款的所有车型id列表
     */
    public List<Integer> getSpecListBySpecId(int specId) {
        List<SpecStateEntity> list = Spec.isCvSpec(specId)
                ? specViewMapper.getCvSpecListBySpecId(specId)  //电动车
                : specViewMapper.getSpecListBySpecId(specId);   //燃油

        if (list == null || list.size() == 0)
            return new ArrayList<>();

        return list.stream().filter(x -> {
            //todo : specParamNotShow 改到了specBaseService
            //specParamNotShow.isShow(specId) || x.getSpecState() == 40
            return true;
        }).map(x -> x.getSpecId()).collect(Collectors.toList());
    }


    /**
     * 根据多个车型id获取相关信息
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<SpecItems> getSpecInfoBySpecList(GetSpecInfoBySpecListRequest request) {
        List<Integer> specIdList = CommonFunction.getListFromStr(request.getSpeclist());
        //返回对象
        ApiResult<SpecItems> apiResult = new ApiResult<>();
        SpecItems specItems = new SpecItems();
        apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode());
        apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
        //不满足条件
        if (specIdList.size() > 100 || specIdList.size() == 0) {
            apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode());
            apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return apiResult;
        }
        int isconfig = request.getIsconfig();
        isconfig = isconfig > 1 ? 0 : isconfig;
        //不满足条件
        if (isconfig < 0 || specIdList.contains(0)) {
            apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode());
            apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return apiResult;
        }
        List<SpecItem> specItemList = new ArrayList<>();
        int specId = 0, seriesId = 0, brandId = 0, fctId = 0, levelId = 0, specState = 0;
        //车型信息
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIdList);
        //车系信息
        List<Integer> seriesIds = specBaseInfoMap.values().stream().map(specBaseInfo -> specBaseInfo.getSeriesId()).distinct().collect(Collectors.toList());
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
        //组装返回信息
        for (Integer strSpecId : specIdList) {
            SpecItem specItem = new SpecItem();
            specId = strSpecId;
            //价格描述
            String dynamicPrice = "";
            //从缓存或者数据库获取数据--车型基本信息
            //车型
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specId);
            if (Objects.isNull(specBaseInfo)) {
                continue;
            }
            specState = specBaseInfo.getSpecState();
            if (isconfig == 0 && specState < 20) {
                continue;
            }
            int minPrice = StringUtils.isNotBlank(String.valueOf(specBaseInfo.getSpecMinPrice())) ? specBaseInfo.getSpecMinPrice() : 0;
            int maxPrice = StringUtils.isNotBlank(String.valueOf(specBaseInfo.getSpecMaxPrice())) ? specBaseInfo.getSpecMaxPrice() : 0;

            //车系
            SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(specBaseInfo.getSeriesId());
            //品牌
            BrandBaseInfo brandBaseInfo = null;
            if (specState == 10) {
                dynamicPrice = specBaseInfo.getIsBooked() == 1 ? "订金:" + PriceUtils.getStrPrice(minPrice, maxPrice) : "预售价:" + PriceUtils.getStrPrice(minPrice, maxPrice);
            } else {
                dynamicPrice = "指导价:" + PriceUtils.getStrPrice(minPrice, maxPrice);
            }
            seriesId = specBaseInfo.getSeriesId();

            if (null != seriesBaseInfo) {
                brandBaseInfo = brandBaseInfoMap.get(seriesBaseInfo.getBrandId());
                brandId = seriesBaseInfo.getBrandId();
                fctId = seriesBaseInfo.getFactId();
                levelId = seriesBaseInfo.getLevelId();
            }
            //车型信息
            specItem.setId(specId);
            specItem.setName(specBaseInfo.getSpecName());
            specItem.setMinprice(minPrice);
            specItem.setMaxprice(maxPrice);
            specItem.setLogo(ImageUtil.getFullImagePath(specBaseInfo.getLogo()));
            specItem.setYearid(specBaseInfo.getSYearId());
            specItem.setYearname(specBaseInfo.getSYear());
            //车系信息
            specItem.setSeriesid(seriesId);
            specItem.setSeriesname(null != seriesBaseInfo ? seriesBaseInfo.getName() : "");
            specItem.setSerieslogo(null != seriesBaseInfo ? ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()) : "");
            specItem.setSeriesofficialurl(null != seriesBaseInfo ? seriesBaseInfo.getUrl() : "");
            specItem.setSeriesfirstletter(specBaseInfo.getSeriesFirstLetter());
            //品牌信息
            specItem.setBrandid(brandId);
            specItem.setBrandname(null != brandBaseInfo ? brandBaseInfo.getName() : "");
            specItem.setBrandlogo(null != brandBaseInfo ? ImageUtil.getFullImagePath(brandBaseInfo.getLogo()) : "");
            specItem.setBrandofficialurl(null != brandBaseInfo ? brandBaseInfo.getUrl() : "");
            specItem.setBrandfirstletter(null != brandBaseInfo ? brandBaseInfo.getFirstLetter() : "");
            //厂商信息
            FactoryBaseInfo factoryBaseInfo = factoryBaseInfoMap.get(fctId);
            specItem.setFctid(fctId);
            specItem.setFctname(null != factoryBaseInfo ? factoryBaseInfo.getName() : "");
            specItem.setFctlogo(null != factoryBaseInfo ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "");
            specItem.setFctofficialurl(null != factoryBaseInfo ? factoryBaseInfo.getUrl() : "");
            specItem.setFctfirstletter(specBaseInfo.getFctFirstLetter());
            //其他信息
            LevelBaseInfo levelBaseInfo = levelBaseInfoMap.get(levelId);
            specItem.setLevelid(levelId);
            specItem.setLevelname(null != levelBaseInfo ? levelBaseInfo.getName() : "");
            specItem.setSpecquality(specBaseInfo.getSpecQuality());
            specItem.setState(specState);
            specItem.setParamisshow((specState == 40 || specBaseInfo.getIsSpecParamIsShow() == 1) ? 1 : 0);
            //从缓存或者数据库获取数据--获取车型上市时间
            specItem.setTimemarket(null != specBaseInfo.getTimeMarket() ?
                    LocalDateUtils.format(specBaseInfo.getTimeMarket(), DATE_TIME_PATTERN_TWO) : null);
            specItem.setEmissionstandards(specBaseInfo.getDicEmissionStandards());
            specItem.setSpecisbooked(specBaseInfo.getIsBooked());
            specItem.setDynamicprice(dynamicPrice);
            //20230915添加fueltypedetail字段
            specItem.setFueltypedetail(specBaseInfo.getFuelTypeDetail());
            specItemList.add(specItem);
        }
        //返回参数赋值
        specItems.setTotal(specItemList.size());
        specItems.setSpecitems(specItemList);
        apiResult.setResult(specItems);
        return apiResult;
    }

    /**
     * 根据车系id获取电车的车型信息
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<ElectricSpecParam> getElectricSpecParamBySeriesId(GetElectricSpecParamBySeriesIdRequest request) {
        ApiResult<ElectricSpecParam> apiResult = new ApiResult<>();
        ElectricSpecParam electricSpecParam = new ElectricSpecParam();
        apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode());
        apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode());
            apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return apiResult;
        }
        List<SpecMainItem> specItems = new ArrayList<>();
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (null != seriesBaseInfo) {
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            List<SpecViewEntity> viewEntities = seriesSpecBaseService.get(seriesId, isCV).join();
            if (!CollectionUtils.isEmpty(viewEntities)) {
                //过滤出来电车
                viewEntities = viewEntities.stream().filter(viewEntity ->
                        isCV ? (viewEntity.getFuelType() >= 4 && viewEntity.getFuelType() <= 7) :
                                (viewEntity.getFuelTypeDetail() >= 4 && viewEntity.getFuelTypeDetail() <= 7)).collect(Collectors.toList());
                //获取所有的车型id
                List<Integer> specIds = viewEntities.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
                //异步批量获取车型信息
                Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);
                for (SpecViewEntity specViewEntity : viewEntities) {
                    //对象赋值
                    SpecMainItem specMainItem = new SpecMainItem();
                    specMainItem.setSpecid(specViewEntity.getSpecId());
                    SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specViewEntity.getSpecId());
                    specMainItem.setSpecname(null != specBaseInfo ? specBaseInfo.getSpecName() : "");
                    specMainItem.setSpecstate(specViewEntity.getSpecState());
                    specMainItem.setMinprice(specViewEntity.getMinPrice());
                    specMainItem.setMaxprice(specViewEntity.getMaxPrice());
                    specMainItem.setHorsepower(specViewEntity.getSpecEnginePower());
                    specMainItem.setMileage(Integer.parseInt(specViewEntity.getEndurancemileage()));
                    specMainItem.setOfficialfastchargetime(specViewEntity.getOfficialFastChargetime());
                    specMainItem.setOfficialslowchargetime(specViewEntity.getOfficialSlowChargetime());
                    specMainItem.setBatterycapacity(specViewEntity.getBatteryCapacity());
                    specMainItem.setParamisshow(specViewEntity.getSpecIsshow());
                    specMainItem.setFueltypedetail(isCV ? specViewEntity.getFuelType() : specViewEntity.getFuelTypeDetail());
                    specItems.add(specMainItem);
                }
            }
        }
        //返回参数赋值
        electricSpecParam.setSeriesid(seriesId);
        if (null != seriesBaseInfo && StringUtils.isNotBlank(seriesBaseInfo.getName())) {
            electricSpecParam.setSeriesname(seriesBaseInfo.getName());
        }
        electricSpecParam.setSpecitems(specItems);
        apiResult.setResult(electricSpecParam);
        return apiResult;
    }

    /**
     * 根据车系id获取车型的参数信息
     *
     * @param request
     * @return
     */
    @Override
    public ApiResult<SpecDetailItems> getSpecParamBySeriesId(GetSpecParamBySeriesIdRequest request) {
        ApiResult<SpecDetailItems> apiResult = new ApiResult<>();

        SpecDetailItems specDetailItems = new SpecDetailItems();
        apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode());
        apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
        int seriesId = request.getSeriesid();
        SpecStateEnum specStateEnum = Spec.getSpecState(request.getState());
        if (seriesId == 0 || specStateEnum == SpecStateEnum.NONE) {
            apiResult.setReturncode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode());
            apiResult.setMessage(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return apiResult;
        }
        List<SpecDetailItem> items = new ArrayList<>();
        List<SpecViewEntity> viewEntities = null;
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (null != seriesBaseInfo) {
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            viewEntities = seriesSpecBaseService.get(seriesId, isCV).join();
            if (!CollectionUtils.isEmpty(viewEntities)) {
                //即将上市
                if (specStateEnum == SpecStateEnum.WAIT_SELL) {
                    viewEntities = viewEntities.stream().filter(viewEntity -> viewEntity.getSpecState() == 10).collect(Collectors.toList());
                } else if (specStateEnum == SpecStateEnum.SELL_12) {
                    //在售
                    viewEntities = viewEntities.stream().filter(viewEntity -> viewEntity.getSpecState() >= 20 && viewEntity.getSpecState() <= 30).collect(Collectors.toList());
                } else if (specStateEnum == SpecStateEnum.STOP_SELL) {
                    //停售
                    viewEntities = viewEntities.stream().filter(viewEntity -> viewEntity.getSpecState() == 40).collect(Collectors.toList());
                }
                //车型信息
                List<Integer> specIds = viewEntities.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
                Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);
                for (SpecViewEntity specViewEntity : viewEntities) {
                    SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specViewEntity.getSpecId());
                    SpecDetailItem specDetailItem = new SpecDetailItem();
                    int isSpecParamIsShow = 0;
                    if (null != specBaseInfo) {
                        specDetailItem.setSpecname(specBaseInfo.getSpecName());
                        specDetailItem.setSpeclogo(ImageUtil.getFullImagePath(specBaseInfo.getLogo()));
                        specDetailItem.setSpecminprice(specBaseInfo.getSpecMinPrice());
                        specDetailItem.setSpecmaxprice(specBaseInfo.getSpecMaxPrice());
                        specDetailItem.setSpectransmission(specBaseInfo.getGearBox());
                        specDetailItem.setSpecispreferential(specBaseInfo.getIsPreferential());
                        specDetailItem.setSpecistaxrelief(specBaseInfo.getSpecTaxType() == 1 ? 1 : 0);
                        specDetailItem.setSpecistaxexemption(specBaseInfo.getSpecTaxType() == 2 ? 1 : 0);
                        isSpecParamIsShow = specBaseInfo.getIsSpecParamIsShow();
                    }

                    specDetailItem.setSpecid(specViewEntity.getSpecId());
                    specDetailItem.setSpecpicount(specViewEntity.getSpecPicNum());
                    specDetailItem.setSpecengineid(specViewEntity.getEngineId());
                    specDetailItem.setSpecenginename(specViewEntity.getEngineName());
                    specDetailItem.setSpecstructuredoor(specViewEntity.getDoors());
                    specDetailItem.setSpecstructureseat(specViewEntity.getSeats());
                    specDetailItem.setSpecstructuretypename(isCV ? Spec.carBodyStruct(specViewEntity.getStructType()) :
                            specViewEntity.getSpecStructureType() == null ? "" : specViewEntity.getSpecStructureType());

                    specDetailItem.setSpecstate(specViewEntity.getSpecState());
                    specDetailItem.setSpecoiloffical(specViewEntity.getOfficalOil());
                    specDetailItem.setSpeclength(specViewEntity.getLength());
                    specDetailItem.setSpecwidth(specViewEntity.getWidth());
                    specDetailItem.setSpecheight(specViewEntity.getHeight());
                    specDetailItem.setSpecweight(specViewEntity.getWeightkg());
                    specDetailItem.setSpecparamisshow((specViewEntity.getSpecState() == 40 || specViewEntity.getSpecState() >= 10 && isSpecParamIsShow == 1) ? 1 : 0);
                    //cvSpecView和SpecView表的数据不一样，SpecView表中的specDrivingMode就是名称，而cvSpecView表中的DriveForm是id
                    specDetailItem.setSpecdrivingmodename(isCV ? Spec.DriveMode(specViewEntity.getDriveForm()) :
                            specViewEntity.getSpecDrivingMode() == null ? "" : specViewEntity.getSpecDrivingMode());
                    specDetailItem.setSpecflowmodeid(specViewEntity.getFlowMode());
                    specDetailItem.setSpecflowmodename(Spec.AdmissionMethod(specViewEntity.getFlowMode()));
                    specDetailItem.setSpecdisplacement(specViewEntity.getSpecDisplacement());
                    specDetailItem.setSpecenginepower(specViewEntity.getSpecEnginePower());
                    specDetailItem.setSpecquality(specViewEntity.getQuality());
                    specDetailItem.setSpecorder(specViewEntity.getSpecOrdercls());
                    specDetailItem.setSpecyear(specViewEntity.getSyear());
                    items.add(specDetailItem);
                }
            }
        }
        //返回参数赋值
        specDetailItems.setSeriesid(seriesId);
        if (null != seriesBaseInfo) {
            specDetailItems.setSeriesname(seriesBaseInfo.getName());
            specDetailItems.setFctid(seriesBaseInfo.getFactId());
            String factoryName = factoryInfoService.getName(seriesBaseInfo.getFactId());
            specDetailItems.setFctname(factoryName);
            specDetailItems.setLevelid(seriesBaseInfo.getLevelId());
            specDetailItems.setLevelname(levelBaseInfoService.getName(seriesBaseInfo.getLevelId()));
            BrandBaseInfo brandBaseInfo = brandBaseService.get(seriesBaseInfo.getBrandId()).join();
            specDetailItems.setBrandid(seriesBaseInfo.getBrandId());
            if (null != brandBaseInfo) {
                specDetailItems.setBrandname(brandBaseInfo.getName());
            }
        }
        specDetailItems.setIsimport(!CollectionUtils.isEmpty(viewEntities) ?
                (StringUtils.isNotBlank(viewEntities.get(0).getSeriesIsImport()) ?
                        viewEntities.get(0).getSeriesIsImport() : (viewEntities.get(0).getSeriesIsImportNum() == 1 ? "进口" : "国产")
                ) : "");
        specDetailItems.setTotal(items.size());
        specDetailItems.setItems(items);
        apiResult.setResult(specDetailItems);
        return apiResult;
    }

    /**
     * 根据车型id,颜色id获取图片类别数量
     *
     * @param request
     * @return
     */
    @Override
    public GetClassItemsBySpecIdResponse getClassItemsBySpecId(GetClassItemsBySpecIdRequest request) {
        GetClassItemsBySpecIdResponse.Builder builder = GetClassItemsBySpecIdResponse.newBuilder();
        if (request.getSpecid() == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        GetClassItemsBySpecIdResponse.Result.Builder result = GetClassItemsBySpecIdResponse.Result.newBuilder();
        SpecBaseInfo specInfo = specBaseService.get(request.getSpecid()).join();
        if (specInfo != null) {
            int seriesId = specInfo.getSeriesId();
            boolean isCv = false;
            if (request.getSpecid() > 1000000) {
                isCv = true;
            }
            if (request.getColorid() == 0) {
                List<SpecPicColorStatisticsEntity> list = specPicClassStatisticsBaseService.get(seriesId, isCv).join();
                if (CollectionUtils.isEmpty(list)) {
                    builder.setReturnMsg("成功");
                    return builder.build();
                }
                list = list.stream().filter(s -> s.getSpecId() == request.getSpecid()).collect(Collectors.toList());
                {
                    list.forEach(v -> {
                        PicClassEntity cls = picClassBaseService.get(v.getPicClass()).join();
                        GetClassItemsBySpecIdResponse.ClassItem item = GetClassItemsBySpecIdResponse.ClassItem.newBuilder()
                                .setId(v.getPicClass())
                                .setName(cls == null ? "" : cls.getName())
                                .setPiccount(v.getPicNumber())
                                .setClubpiccount(v.getClubPicNumber())
                                .build();

                        result.addClassitems(item);
                    });
                }
                result.setSpecid(request.getSpecid());
                result.setColorid(request.getColorid());
                builder.setResult(result);
            } else {

                List<SpecPicColorStatisticsEntity> list = seriesSpecPicColorStatistics.get(seriesId);
                if (CollectionUtils.isEmpty(list)) {
                    builder.setReturnMsg("成功");
                    return builder.build();
                }
                list = list.stream().filter(s -> s.getSpecId() == request.getSpecid() && s.getColorId() == request.getColorid()).collect(Collectors.toList());
                {
                    list.forEach(v -> {
                        PicClassEntity cls = picClassBaseService.get(v.getPicClass()).join();
                        GetClassItemsBySpecIdResponse.ClassItem item = GetClassItemsBySpecIdResponse.ClassItem.newBuilder()
                                .setId(v.getPicClass())
                                .setName(cls == null ? "" : cls.getName())
                                .setPiccount(v.getPicNumber())
                                .setClubpiccount(v.getClubPicNumber())
                                .build();

                        result.addClassitems(item);
                    });
                }
                //车型不存在情况下，返回null节点和逻辑更相符
                if (list.size() > 0) {
                    result.setSpecid(request.getSpecid());
                    result.setColorid(request.getColorid());
                    builder.setResult(result);
                }
            }
        }
        builder.setReturnMsg("成功");
        return builder.build();
    }

    /**
     * 获取车型信息(如厂商信息,品牌信息,车系信息及年代款)
     *
     * @param request
     * @return
     */
    @Override
    public GetSpecInfoBySpecIdResponse getSpecInfoBySpecId(GetSpecInfoBySpecIdRequest request) {
        GetSpecInfoBySpecIdResponse.Builder builder = GetSpecInfoBySpecIdResponse.newBuilder();
        int specid = request.getSpecid();
        if (specid == 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }

        GetSpecInfoBySpecIdResponse.Result.Builder result = GetSpecInfoBySpecIdResponse.Result.newBuilder();
        SpecBaseInfo specInfo = specBaseService.get(specid).join();
        if (specInfo != null) {
            SeriesBaseInfo seriesinfo = seriesBaseService.get(specInfo.getSeriesId()).join();
            if (seriesinfo == null) {
                log.info("getSpecInfoBySpecId seriesinfo is null,id:" + specInfo.getSeriesId());
            } else {
                BrandBaseInfo brandinfo = brandBaseService.get(seriesinfo.getBrandId()).join();
                FactoryBaseInfo factoryinfo = factoryInfoService.getFactory(seriesinfo.getFactId());
                if (brandinfo == null) {
                    log.info("getSpecInfoBySpecId brandinfo is null,id:" + seriesinfo.getBrandId());
                    brandinfo = new BrandBaseInfo();
                }
                if (factoryinfo == null) {
                    log.info("getSpecInfoBySpecId factoryinfo is null,id:" + specInfo.getSeriesId());
                    factoryinfo = new FactoryBaseInfo();
                }
                result.setId(specid);
                result.setName(specInfo.getSpecName());
                result.setMinprice(specInfo.getSpecMinPrice());
                result.setMaxprice(specInfo.getSpecMaxPrice());
                result.setLogo(StringUtils.defaultString(ImageUtil.getFullImagePath(specInfo.getLogo()), ""));
                result.setYearid(specInfo.getSYearId());
                result.setYearname(specInfo.getSYear() + "款");
                result.setSeriesid(specInfo.getSeriesId());
                result.setSeriesname(seriesinfo.getName());
                result.setSerieslogo(StringUtils.defaultString(ImageUtil.getFullImagePath(seriesinfo.getLogo()), ""));
                result.setSeriesofficialurl(StringUtils.defaultString(seriesinfo.getUrl(), ""));
                result.setSeriesfirstletter(seriesinfo.getFl() == null ? "" : seriesinfo.getFl());
                result.setBrandid(brandinfo.getId());
                result.setBrandname(brandinfo.getName() == null ? "" : brandinfo.getName());
                result.setBrandlogo(StringUtils.defaultString(ImageUtil.getFullImagePath(brandinfo.getLogo()), ""));
                result.setBrandofficialurl(StringUtils.defaultString(brandinfo.getUrl(), ""));
                result.setBrandfirstletter(brandinfo.getFirstLetter() == null ? "" : brandinfo.getFirstLetter());
                result.setFctid(factoryinfo.getId());
                result.setFctname(factoryinfo.getName() == null ? "" : factoryinfo.getName());
                result.setFctlogo(StringUtils.defaultString(ImageUtil.getFullImagePath(factoryinfo.getLogo()), ""));
                result.setFctofficialurl(StringUtils.defaultString(factoryinfo.getUrl(), ""));
                result.setFctfirstletter(factoryinfo.getFirstletter() == null ? "" : factoryinfo.getFirstletter());
                result.setLevelid(seriesinfo.getLevelId());
                result.setLevelname(levelBaseInfoService.getLevel(seriesinfo.getLevelId()).getName());
                result.setState(specInfo.getSpecState());
                result.setParamisshow(specInfo.getIsSpecParamIsShow());
                result.setSpecquality(specInfo.getSpecQuality() == null ? "" : specInfo.getSpecQuality());// info.getSpecQuality()==null?"":info.getSpecQuality());
                result.setFueltype(specInfo.getFuelType());
                result.setDisplacement(new BigDecimal(String.valueOf(specInfo.getDisplacement())).stripTrailingZeros().toPlainString());
                result.setTimemarket(null != specInfo.getTimeMarket() ?
                        LocalDateUtils.format(specInfo.getTimeMarket(), DATE_TIME_PATTERN_TWO) : "");
                result.setRanliaoxingshi(specInfo.getFuelTypeDetail());
                result.setPricedescription(StringUtils.defaultString(specInfo.getPricedescription(), ""));
                result.setBooked(specInfo.getIsBooked());
                builder.setResult(result);
            }
        } else {
            log.info("getSpecInfoBySpecId specInfo is null,id:" + specid);
        }
        builder.setReturnMsg("成功");
        return builder.build();
    }

    /**
     * 根据车系id获取车型详细信息
     *
     * @param request
     * @return
     */
    @Override
    public GetSpecDetailBySeriesIdResponse getSpecDetailBySeriesId(GetSpecDetailBySeriesIdRequest request) {
        GetSpecDetailBySeriesIdResponse.Builder builder = GetSpecDetailBySeriesIdResponse.newBuilder();
        GetSpecDetailBySeriesIdResponse.Result.Builder result = GetSpecDetailBySeriesIdResponse.Result.newBuilder();
        int seriesid = request.getSeriesid();
        if (seriesid == 0 || StringUtils.isBlank(request.getState())) {
            builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        result.setSeriesid(seriesid);
        SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesid).join();
        if (seriesInfo != null) {
            boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
            List<SpecViewEntity> list = seriesSpecBaseService.get(seriesid, isCV).join();
            SpecStateEnum state = Spec.getSpecState(request.getState());
            KeyValueDto<Boolean, List<SpecViewEntity>> ret = CommonFunction.filterSpecViewList(state, list);
            if (!ret.getKey()) {
                builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg());
                return builder.build();
            }
            list = ret.getValue();
            if (CollectionUtils.isEmpty(list)) {
                result.setSeriesid(seriesid);
                builder.setReturnMsg("成功");
                builder.setResult(result);
                return builder.build();
            }
            List<Integer> specIds = new ArrayList<>();
            list.forEach(x -> {
                specIds.add(x.getSpecId());
            });
            List<SpecBaseInfo> specBaseList = specBaseService.getList(specIds);
            Map<Integer,SpecBaseInfo> map = specBaseList.stream().collect(Collectors.toMap(SpecBaseInfo::getId,a -> a,(x,y)->x));
            for (SpecViewEntity item : list) {
//                SpecBaseInfo specInfo = specBaseService.get(item.getSpecId()).join();
                SpecBaseInfo specInfo = map.get(item.getSpecId());
                if (specInfo != null) {
                    String emissionStandards = specInfo.getDicEmissionStandards();
                    String structtype = "";
                    String drivingmoden = "";
                    int specIsImage = 0;
                    int isclassic = 0;
                    int electricType = 0;
                    int paramisshow = 0;
                    int fuletypeId = 0;
                    String dp = item.getSpecState() == 10
                            ? (specInfo.getIsBooked() == 1 ? "订金:" + PriceUtils.getStrPrice(item.getMinPrice(), item.getMaxPrice()) : "预售价:" + PriceUtils.getStrPrice(item.getMinPrice(), item.getMaxPrice()))
                            : "指导价:" + PriceUtils.getStrPrice(item.getMinPrice(), item.getMaxPrice());
                    if (!isCV) {
                        drivingmoden = item.getSpecDrivingMode() == null ? "" : item.getSpecDrivingMode();
                        structtype = item.getSpecStructureType() == null ? "" : item.getSpecStructureType();
                        specIsImage = item.getSpecIsImage();
                        isclassic = item.getIsclassic();
                        int fueltypeDetail = item.getFuelTypeDetail();  //1、汽油、2柴油、3油电混合、4纯电、5插电、6增程
                        if (fueltypeDetail == 4)  //纯电为1
                        {
                            electricType = 1;
                        } else if (fueltypeDetail == 6) //增程是2
                        {
                            electricType = 2;
                        }
                        paramisshow = item.getSpecIsImage() == 1 ? 0 : item.getSpecIsshow();//paramisshow:是否参数外显
                        fuletypeId = fueltypeDetail;
                    } else {
                        drivingmoden = Spec.DriveMode(item.getDriveForm());
                        structtype = Spec.carBodyStruct(item.getStructType());
                        electricType = item.getFuelType() == 4 ? 1 : 0;
                        paramisshow = (item.getSpecState() == 40 || item.getSpecState() >= 10 && specInfo.getIsSpecParamIsShow() == 1) ? 1 : 0; //paramisshow:是否参数外显
                        fuletypeId = item.getFuelType();
                    }
                    GetSpecDetailBySeriesIdResponse.SpecList spec = GetSpecDetailBySeriesIdResponse.SpecList.newBuilder()
                            .setId(item.getSpecId())
                            .setName(StringUtils.defaultString(specInfo.getSpecName(), ""))
                            .setLogo(StringUtils.defaultString(ImageUtil.getFullImagePath(specInfo.getLogo()), ""))
                            .setSyearid(item.getSyearId())
                            .setYear(item.getSyear())
                            .setMinprice(item.getMinPrice())
                            .setMaxprice(item.getMaxPrice())
                            .setTransmission(StringUtils.defaultString(specInfo.getGearBox(), ""))
                            .setGearbox(Spec.carGearbox(specInfo.getGearBox()))
                            .setState(item.getSpecState())
                            .setDrivingmodename(drivingmoden)
                            .setFlowmodeid(item.getFlowMode())
                            .setFlowmodename(Spec.AdmissionMethod(item.getFlowMode()))
                            .setDisplacement(item.getSpecDisplacement())
                            .setEnginepower(item.getSpecEnginePower())
                            .setIspreferential(specInfo.getIsPreferential())
                            .setIstaxrelief(specInfo.getSpecTaxType() == 1 ? 1 : 0)
                            .setIstaxexemption(specInfo.getSpecTaxType() == 2 ? 1 : 0)
                            .setOrder(item.getSpecOrdercls())
                            .setSpecisimage(specIsImage)
                            .setParamisshow(paramisshow)
                            .setIsclassic(isclassic)
                            .setStructtype(structtype)
                            .setFueltype(CommonFunction.carFuel(fuletypeId))
                            .setFueltypeid(fuletypeId)
                            .setIsnewcar(specInfo.getIsNew())
                            .setEmissionstandards(emissionStandards == null ? "" : emissionStandards)
                            .setSeat(String.valueOf(item.getSeat()))
                            .setDynamicprice(dp)
                            .setElectrickw(specInfo.getElectroTotalKW())
                            .setEndurancemileage(Integer.parseInt(item.getEndurancemileage()))
                            .setElectrictype(electricType)   //电动机类型： 1为纯电动，2 为增程式)
                            .build();

                    result.addSpecitems(spec);
                } else {
                    log.info("getSpecDetailBySeriesId specInfo is null,id:" + item.getSpecId());
                }
            }
            result.setTotal(list.size());

        } else {
            log.info("getSpecDetailBySeriesId seriesInfo is null,id:" + seriesid);
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }



    /**
     * 根据车系id获取车型详细信息
     *
     * @param request
     * @return
     */
    @Override
    public SpecDetailByYearIdResponse getSpecDetailByYearId(SpecDetailByYearIdRequest request) {
        SpecDetailByYearIdResponse.Builder builder = SpecDetailByYearIdResponse.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");
        SpecDetailByYearIdResponse.Result.Builder result = SpecDetailByYearIdResponse.Result.newBuilder();
        int seriesid = request.getSeriesid();
        if (seriesid == 0 || StringUtils.isBlank(request.getState()) || request.getYearid() <= 0) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        result.setSeriesid(seriesid);
        result.setYearid(request.getYearid());
        SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesid).join();
        if (seriesInfo != null) {
            boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
            List<SpecViewEntity> list = seriesSpecBaseService.get(seriesid, isCV).join();
            list = list.stream().filter(x->x.getSyearId() == request.getYearid()).collect(Collectors.toList());
            SpecStateEnum state = Spec.getSpecState(request.getState());
            KeyValueDto<Boolean, List<SpecViewEntity>> ret = CommonFunction.filterSpecViewList(state, list);
            if (!ret.getKey()) {
                builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
                return builder.build();
            }
            list = ret.getValue();
            if (CollectionUtils.isEmpty(list)) {
                result.setSeriesid(seriesid);
                builder.setReturnMsg("成功");
                builder.setResult(result);
                return builder.build();
            }

            list.sort(Comparator.comparing(SpecViewEntity::getSpecId));


            List<Integer> specIDs = list.stream().map(x->x.getSpecId()).distinct().collect(Collectors.toList());
            if(specIDs.size() == 0){
                return builder.build();
            }

            Map<Integer,SpecBaseInfo> specs = specBaseService.getMap(specIDs);


            for (SpecViewEntity item : list) {
                if(!specs.containsKey(item.getSpecId()))
                    continue;
                SpecBaseInfo specInfo = specs.get(item.getSpecId());
                if (specInfo != null) {
                    String structtype = "";
                    String drivingmoden = "";
                    int isclassic = 0;
                    int paramisshow = item.getSpecState() == 10 ? (specInfo.getIsSpecParamIsShow() == 1?1:0):1;
                    if (!isCV) {
                        drivingmoden = item.getSpecDrivingMode() == null ? "" : item.getSpecDrivingMode();
                        structtype = item.getSpecStructureType() == null ? "" : item.getSpecStructureType();
                        isclassic = item.getIsclassic();
                    } else {
                        drivingmoden = Spec.DriveMode(item.getDriveForm());
                        structtype = Spec.carBodyStruct(item.getStructType());
                    }
                    SpecDetailByYearIdResponse.Result.Specitem spec = SpecDetailByYearIdResponse.Result.Specitem.newBuilder()
                            .setId(item.getSpecId())
                            .setName(StringUtils.defaultString(specInfo.getSpecName(), ""))
                            .setLogo(StringUtils.defaultString(ImageUtil.getFullImagePath(specInfo.getLogo()), ""))
                            .setYear(item.getSyear())
                            .setMinprice(item.getMinPrice())
                            .setMaxprice(item.getMaxPrice())
                            .setTransmission(StringUtils.defaultString(specInfo.getGearBox(), ""))
                            .setState(item.getSpecState())
                            .setDrivingmodename(drivingmoden)
                            .setFlowmodeid(item.getFlowMode())
                            .setFlowmodename(Spec.AdmissionMethod(item.getFlowMode()))
                            .setDisplacement(item.getSpecDisplacement())
                            .setEnginepower(item.getSpecEnginePower())
                            .setIspreferential(specInfo.getIsPreferential())
                            .setIstaxrelief(specInfo.getSpecTaxType() == 1 ? 1 : 0)
                            .setIstaxexemption(specInfo.getSpecTaxType() == 2 ? 1 : 0) //
                            .setOrder(item.getSpecOrdercls())
                            .setParamisshow(paramisshow)
                            .setIsclassic(isclassic)
                            .setStructtype(structtype)
                            .build();

                    result.addSpecitems(spec);
                } else {
                    log.info("getSpecDetailBySeriesId specInfo is null,id:" + item.getSpecId());
                }
            }
            result.setTotal(list.size());

        } else {
            log.info("getSpecDetailBySeriesId seriesInfo is null,id:" + seriesid);
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetSpecDetailBySeriesIdV1Response getSpecDetailBySeriesIdV1(GetSpecDetailBySeriesIdRequest request) {
        GetSpecDetailBySeriesIdV1Response.Builder builder = GetSpecDetailBySeriesIdV1Response.newBuilder();
        GetSpecDetailBySeriesIdV1Response.Result.Builder result = GetSpecDetailBySeriesIdV1Response.Result.newBuilder();
        int seriesId = request.getSeriesid();
        SeriesBaseInfo seriesInfo;
        result.setSeriesid(seriesId);
        if (seriesId == 0 || Objects.isNull(seriesInfo = seriesBaseService.get(seriesId).join())) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }

        boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
        List<SpecViewEntity> list = seriesSpecBaseService.get(seriesId, isCV).join();
        KeyValueDto<Boolean, List<SpecViewEntity>> keyValueDto = CommonFunction.filterSpecViewList(Spec.getSpecState(request.getState()), list);
        if (!keyValueDto.getKey()) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        list = keyValueDto.getValue();
        if (CollectionUtils.isEmpty(list)) {
            result.setSeriesid(seriesId);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();        }

        List<Integer> specIds = list.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> serviceMap = specBaseService.getMap(specIds);
        for (SpecViewEntity item : list) {
            autohome.rpc.car.car_api.v1.spec.GetSpecDetailBySeriesIdV1Response.SpecDetail spec = SpecDetail.buildSpecDetail(item, serviceMap.get(item.getSpecId()), isCV);
            if (Objects.nonNull(spec)) {
                result.addSpecitems(spec);
            }
        }
        result.setTotal(result.getSpecitemsCount());
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }


    /**
     * 根据车系id获取车型详细信息V2
     *
     * @param request SpecGetSpecDetailBySeriesIdRequest
     * @return SpecGetSpecDetailBySeriesIdResponse
     */
    @Override
    public SpecGetSpecDetailBySeriesIdResponse getSpecDetailBySeriesIdV2(SpecGetSpecDetailBySeriesIdRequest request) {
        SpecGetSpecDetailBySeriesIdResponse.Builder builder = SpecGetSpecDetailBySeriesIdResponse.newBuilder();
        SpecGetSpecDetailBySeriesIdResponse.Result.Builder result = SpecGetSpecDetailBySeriesIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0 || StringUtils.isBlank(request.getState())) {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        result.setSeriesid(seriesId);
        SeriesBaseInfo seriesInfo = seriesBaseService.get(seriesId).join();
        if (seriesInfo != null) {
            result.setSeriesname(seriesInfo.getName());
            result.setLevelid(seriesInfo.getLevelId());
            result.setLevelname(levelBaseInfoService.getName(seriesInfo.getLevelId()));
            boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
            List<SpecViewEntity> list = seriesSpecBaseService.get(seriesId, isCV).join();
            SpecStateEnum state = Spec.getSpecState(request.getState());
            KeyValueDto<Boolean, List<SpecViewEntity>> ret = CommonFunction.filterSpecViewList(state, list);
            if (!ret.getKey()) {
                builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
                return builder.build();
            }
            list = ret.getValue();
            if (CollectionUtils.isEmpty(list)) {
                result.setSeriesid(seriesId);
                builder.setReturnMsg("成功");
                builder.setResult(result);
                return builder.build();
            }
            int total = 0;
            for (SpecViewEntity item : list) {
                SpecBaseInfo specInfo = specBaseService.get(item.getSpecId()).join();
                if (specInfo != null) {
                    int paramisshow = 0;
                    paramisshow = (item.getSpecState() == 40 || item.getSpecState() >= 10 && specInfo.getIsSpecParamIsShow() == 1) ? 1 : 0; //paramisshow:是否参数外显

                    int fuelTypeId = item.getFuelType();
                    int fuelTypedetailId = 0;
                    if (!isCV) {
                        fuelTypedetailId = item.getFuelTypeDetail();  //1、汽油、2柴油、3油电混合、4纯电、5插电、6增程
                    } else {
                        fuelTypedetailId = fuelTypeId;
                    }
                    String stopTime = "";
                    if (specInfo.getStopTime() != null) {
                        stopTime = LocalDateUtils.format(specInfo.getStopTime(), DATE_TIME_PATTERN_TWO);
                    }
                    SpecGetSpecDetailBySeriesIdResponse.SpecList spec = SpecGetSpecDetailBySeriesIdResponse.SpecList.newBuilder()
                            .setId(item.getSpecId())
                            .setName(StringUtils.defaultString(specInfo.getSpecName(), ""))
                            .setLogo(StringUtils.defaultString(ImageUtil.getFullImagePath(specInfo.getLogo()), ""))
                            .setSyearid(item.getSyearId())
                            .setYear(item.getSyear())
                            .setMinprice(specInfo.getSpecMinPrice())
                            .setMaxprice(specInfo.getSpecMaxPrice())
                            .setState(item.getSpecState())
                            .setOrder(item.getSpecOrdercls())
                            .setParamisshow(paramisshow)
                            .setFueltype(CommonFunction.carFuel(fuelTypeId))
                            .setFueltypeid(fuelTypeId)
                            .setFuletypedetailid(fuelTypedetailId)
                            .setFueltypedetail(CommonFunction.carFuel(fuelTypedetailId))
                            .setStoptime(stopTime)
                            .build();

                    result.addSpecitems(spec);
                    total++;
                } else {
                    log.info("specGetSpecDetailBySeriesId specInfo is null,id:" + item.getSpecId());
                }
            }
            result.setTotal(total);

        } else {
            log.info("specGetSpecDetailBySeriesId seriesInfo is null,id:" + seriesId);
        }
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public ApiResult<SpecLogoPage> getSpecLogoBySpecList(GetSpecLogoBySpecListRequest request) {
        List<Integer> specIdList = CommonFunction.getListFromStr(request.getSpeclist());
        if (CollectionUtils.isEmpty(specIdList)) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        SpecLogoPage specLogoPage = new SpecLogoPage();
        List<SpecLogoItem> result = specIdList.stream()
                .map(specId -> {
                    Optional<SpecBaseInfo> optional = Optional.ofNullable(specBaseService.get(specId).join());
                    return optional.map(specBaseInfo -> SpecLogoItem.builder()
                                    .id(specId).logo(ImageUtil.getFullImagePathNew(specBaseInfo.getSpecLogoImg(), false)).build())
                            .orElse(SpecLogoItem.builder().id(specId).build());
                }).collect(Collectors.toList());
        specLogoPage.setSpecitems(result);
        return new ApiResult<>(specLogoPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ApiResult<ParamTypeItemPage> getCarPriceSpecParamListBySpecListV1(GetSpecLogoBySpecListRequest request) {
        List<Integer> specIdList = CommonFunction.getListFromStr(request.getSpeclist());
        if (!CommonFunction.check(specIdList) || CommonFunction.checkSpecParamIsShow(specIdList, specBaseService)) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(specIdList, true);

        ParamTypeItemPage paramTypeItemPage = new ParamTypeItemPage();
        paramTypeItemPage.setParamtypeitems(paramTypeItems.orElse(Collections.emptyList()));
        return new ApiResult<>(paramTypeItemPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public GetCarPriceSpecInfoResponse getCarPriceSpecInfoBySeriesId(GetSpecDetailBySeriesIdRequest request) {
        GetCarPriceSpecInfoResponse.Builder builder = GetCarPriceSpecInfoResponse.newBuilder();
        GetCarPriceSpecInfoResponse.Result.Builder result = GetCarPriceSpecInfoResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SeriesBaseInfo seriesInfo;
        result.setSeriesid(seriesId);
        if (Objects.isNull(seriesInfo = seriesBaseService.get(seriesId).join())) {
            return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }

        boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
        List<SpecViewEntity> list = seriesSpecBaseService.get(seriesId, isCV).join();
        KeyValueDto<Boolean, List<SpecViewEntity>> keyValueDto = CommonFunction.filterSpecViewList(Spec.getSpecState(request.getState()), list);
        if (!keyValueDto.getKey()) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        list = keyValueDto.getValue();
        if (CollectionUtils.isEmpty(list)) {
            return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        list = CommonFunction.sort(Spec.getSpecState(request.getState()), list, isCV);
        List<Integer> specIds = list.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> serviceMap = specBaseService.getMap(specIds);
        for (SpecViewEntity item : list) {
            if (Objects.isNull(item)) {
                continue;
            }
            SpecBaseInfo specBaseInfo = serviceMap.get(item.getSpecId());
            if (specBaseInfo == null) {
                continue;
            }
            GetCarPriceSpecInfoResponse.SpecInfoDetail.Builder itemResult = GetCarPriceSpecInfoResponse.SpecInfoDetail.newBuilder();
            itemResult.setId(item.getSpecId());
            itemResult.setName(specBaseInfo.getSpecName());
            itemResult.setMaxprice(item.getMaxPrice());
            itemResult.setMinprice(item.getMinPrice());
            itemResult.setLogo(ImageUtil.getFullImagePath(specBaseInfo.getLogo()));
            itemResult.setYearid(item.getSyearId());
            itemResult.setYearname(item.getSyear() == 0 ? "" : item.getSyear() + "款");
            itemResult.setQuality(StringUtils.isBlank(item.getQuality()) ? "" : item.getQuality());
            itemResult.setSeat(item.getSeat());
            result.addSpecitems(itemResult);
        }
        result.setTatal(result.getSpecitemsCount());
        return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetSpecListBySeriesResponse getSpecListBySeriesV1(GetElectricSpecParamBySeriesIdRequest request) {
        GetSpecListBySeriesResponse.Builder builder = GetSpecListBySeriesResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        List<Integer> specIds = seriesSpecService.getSpecIds(seriesId);
        if (!CollectionUtils.isEmpty(specIds)) {
            List<SpecBaseInfo> list = specBaseService.getList(specIds);
            specIds = list.stream().filter(specBaseInfo -> specBaseInfo.getIsBooked() == 1).map(SpecBaseInfo::getId).sorted().collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(specIds)) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        GetSpecListBySeriesResponse.Result.Builder result = GetSpecListBySeriesResponse.Result.newBuilder();
        result.addAllSpeclist(specIds);
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public ApiResult<SpecColorItemPage> getSpecColorBySpecIdV1(GetSpecInfoBySpecIdRequest request) {
        int specId = request.getSpecid();
        if (!CommonFunction.check(Collections.singletonList(specId))) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<CarSpecColorEntity> colorEntities = specColorService.get(specId).join();

        if (CollectionUtils.isEmpty(colorEntities)) {
            return new ApiResult<>(SpecColorItemPage.builder().total(0).specid(specId).coloritems(Collections.emptyList()).build(), RETURN_MESSAGE_ENUM0);
        }

        SpecBaseInfo join = specBaseService.get(specId).join();
        int seriesId = join != null ? join.getSeriesId() : 0;

        List<SpecPicColorStatisticsEntity> statisticsEntities = specPicColorStatisticsBaseService.get(seriesId).join();
        if (!CollectionUtils.isEmpty(statisticsEntities)) {
            statisticsEntities = statisticsEntities.stream().filter(s -> Objects.equals(specId, s.getSpecId())).collect(Collectors.toList());
        }

        List<SpecColorItemPage.ColorItem> colorItems = new ArrayList<>(colorEntities.size());
        Map<Integer, ColorBaseInfo> colorMap = colorBaseService.getColorMap(colorEntities.stream().map(CarSpecColorEntity::getCId).collect(Collectors.toList()));
        for (CarSpecColorEntity colorEntity : colorEntities) {
            int colorId = colorEntity.getCId();
            ColorBaseInfo colorBaseInfo = colorMap != null ? colorMap.get(colorId) : null;
            if (Objects.isNull(colorBaseInfo)) {
                continue;
            }
            List<SpecPicColorStatisticsEntity> entities = Collections.emptyList();
            if (!CollectionUtils.isEmpty(statisticsEntities)) {
                entities = statisticsEntities.stream().filter(s -> Objects.equals(s.getColorId(), colorId)).collect(Collectors.toList());
            }
            colorItems.add(SpecColorItemPage.ColorItem.builder().
                    picnum(CollectionUtils.isEmpty(entities) ? 0 : entities.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum()).
                    clubpicnum(CollectionUtils.isEmpty(entities) ? 0 : entities.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum()).
                    value(colorBaseInfo.getValue()).name(colorBaseInfo.getName()).id(colorId).
                    price(colorEntity.getPrice()).remark(StringUtils.defaultString(colorEntity.getMark())).
                    build());
        }
        colorItems = colorItems.stream().sorted((o1, o2) -> o2.getPicnum() - o1.getPicnum()).collect(Collectors.toList());
        return new ApiResult<>(SpecColorItemPage.builder().total(colorItems.size()).specid(specId).coloritems(colorItems).build(), RETURN_MESSAGE_ENUM0);
    }

    private Optional<List<ParamTypeItems>> getParamTypeItems(List<Integer> specIdList, boolean filterOpt) {
        if (CollectionUtils.isEmpty(specIdList)) {
            return Optional.empty();
        }
        KeyValueDto<Boolean, Boolean> keyValueDto = CommonFunction.getCvType(specIdList);
        List<Map<String, Object>> specConfigMaps = this.getSpecConfigMaps(keyValueDto.getKey(), keyValueDto.getValue(), specIdList, filterOpt);

        if (CollectionUtils.isEmpty(specConfigMaps)) {
            return Optional.empty();
        }

        ParamTypeItems.TempInfo tempInfo = ParamTypeItems.TempInfo.getTempInfo(specIdList, specBaseService);
        //插电和油电没有“	系统综合扭矩(N·m)”时  要不要分开展示“发动机最大扭矩(N·m)”和“电动机总扭矩(N·m)”
        //true 时 基本参数大类下 最大功率不显示 分别显示
        boolean reWriteMaxKw = isReWriteMaxKw(specIdList, specConfigMaps);
        List<ParamTypeItems.ParamItems> arrMaxKw = getList(specIdList, specConfigMaps, reWriteMaxKw, "最大功率(kW)", "电动机总功率(kW)", "发动机最大功率(kW)");

        boolean reWriteMaxTorque = isReWriteMaxTorque(specIdList, specConfigMaps);
        List<ParamTypeItems.ParamItems> arrMaxTorque = getList(specIdList, specConfigMaps, reWriteMaxTorque, "最大扭矩(N·m)", "电动机总扭矩(N·m)", "发动机最大扭矩(N·m)");
        List<ParamTypeItems> paramTypeItems = getParamTypeItems(specIdList, specConfigMaps, tempInfo, reWriteMaxKw, arrMaxKw, reWriteMaxTorque, arrMaxTorque);
        return Optional.of(paramTypeItems);
    }

    private List<ParamTypeItems> getParamTypeItems(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps, ParamTypeItems.TempInfo tempInfo, boolean reWriteMaxKw, List<ParamTypeItems.ParamItems> arrMaxKw, boolean reWriteMaxTorque, List<ParamTypeItems.ParamItems> arrMaxTorque) {
        String lastItemType = null;//上一次参数类别名称
        String currentItemType = null;//当前参数类别名称
        List<ParamTypeItems> paramTypeItems = new ArrayList<>();
        List<ParamTypeItems.ParamItems> paramItems = new ArrayList<>();
        for (int i = 0, len = specConfigMaps.size(); i < len; i++) {
            Map<String, Object> map = specConfigMaps.get(i);
            if (map == null || map.size() == 0) {
                continue;
            }
            int currentParamValueEqualNullNum = 0;
            currentItemType = (String) map.get("item");
            //如果全是纯电动车型不显示发动机信息
            if (tempInfo.isAllSpecIsPEV() && StringUtils.equals(currentItemType, "发动机")) {
                continue;
            }
            if (!Objects.equals(currentItemType, lastItemType)) {
                if (i > 0) {
                    //汽油、柴油、48v轻混的都不显示电动机大类。
                    //判断中间有电动机的分类，如果没有电的参数，跳出大类。乘用车和商用车的大类排序不一致。此处理是判断乘用车
                    if (i < len - 1) {
                        if (tempInfo.getOilEnergyNum() == specIdList.size() && StringUtils.equals(currentItemType, "电动机")) {
                            continue;
                        }
                    }
                    paramTypeItems.add(ParamTypeItems.builder().name(lastItemType).paramitems(paramItems).build());
                    paramItems = new ArrayList<>();
                }
                lastItemType = currentItemType;
            }
            List<ParamTypeItems.ValueItems> specValueItems = new ArrayList<>();//车型参数值集合
            int configId = Integer.parseInt(map.get("configId").toString());//参数项id
            for (Integer specId : specIdList) {
                String strValue = CommonFunction.getDefaultParamSign(map.get(specId.toString()) + "");
                String url = "";
                if (Spec.REAL_TEST_ITEMS.contains(configId)) {
                    SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
                    url = specBaseInfo != null ? (StringUtils.isBlank(specBaseInfo.getSpeedUrl()) ? "" : specBaseInfo.getSpeedUrl()) : "";
                }
                specValueItems.add(ParamTypeItems.ValueItems.builder().specid(specId).value(strValue).url(url).build());
                if (StringUtils.equals(strValue, "-")) {
                    currentParamValueEqualNullNum += 1;
                }
            }
            //全都没值的项，如果在限制的参数项范围内不外显、解决前台大片空白参数项问题
            if (configId > 0) {
                if (Spec.DYNAMIC_DISPLAY_PARAM_ITEMS.contains(configId) && currentParamValueEqualNullNum == specIdList.size()) {
                    continue;
                }
            }
            String name = (String) map.get("name");
            if (StringUtils.equals(currentItemType, "基本参数")) {
                //全部是非新能源车型，隐藏“基本参数”的部分新能源参数
                if (tempInfo.getNewEnergyNum() == 0 && Spec.listNewEnergyParam.contains(name)) {
                    continue;
                } else if (tempInfo.isAllSpecIsPEV() && Spec.listNotDisPlayOfPEVCarParam.contains(name)) {//全是纯电动车型要隐藏“基本参数”中的部分参数。
                    continue;
                }
                //燃料形式是全是油的不显电动机(Ps)这项基本参数
                else if (tempInfo.getOilEnergyNum() == specIdList.size() && StringUtils.equals(name, "电动机(Ps)")) {
                    continue;
                }

                //最大功率的特殊处理
                if (reWriteMaxKw && StringUtils.equals(name, "最大功率(kW)") && !CollectionUtils.isEmpty(arrMaxKw)) {
                    paramItems.addAll(arrMaxKw);
                    reWriteMaxKw = false;
                    continue;
                }
                //最大扭矩(N·m)的特殊处理
                if (reWriteMaxTorque && StringUtils.equals(name, "最大扭矩(N·m)") && !CollectionUtils.isEmpty(arrMaxTorque)) {
                    paramItems.addAll(arrMaxTorque);
                    reWriteMaxTorque = false;
                    continue;
                }
            }
            if (StringUtils.equals(currentItemType, "车身")) {
                //全是纯电动车型要隐藏“基本参数”中的部分参数。
                if (tempInfo.isAllSpecIsPEV() && Spec.listNotDisPlayOfPEVCarParam.contains(name)) {
                    continue;
                }
            }
            paramItems.add(ParamTypeItems.ParamItems.builder().name(name).id(configId).valueitems(specValueItems).build());
            //汽油、柴油、48v轻混的都不显示电动机大类。
            //判断末尾有电动机的分类，如果没有电的参数，跳出大类。乘用车和商用车的大类排序不一致。此处理是判断商用车
            if (tempInfo.getOilEnergyNum() == specIdList.size() && StringUtils.equals(currentItemType, "电动机")) {
                continue;
            }

            if (i == len - 1) {
                paramTypeItems.add(ParamTypeItems.builder().name(lastItemType).paramitems(paramItems).build());
            }
        }
        return paramTypeItems;
    }

    private List<ParamTypeItems.ParamItems> getList(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps, boolean reWriteMaxTorque, String str1, String str2, String str3) {
        List<ParamTypeItems.ParamItems> list = Collections.emptyList();
        if (reWriteMaxTorque) {
            List<Map<String, Object>> drMaxTorqueMap = specConfigMaps.stream().
                    filter(spec -> StringUtils.equals((String) spec.get("name"), str1) && StringUtils.equals((String) spec.get("item"), "发动机") ||
                            StringUtils.equals((String) spec.get("name"), str2) && StringUtils.equals((String) spec.get("item"), "电动机")).
                    collect(Collectors.toList());
            list = drMaxTorqueMap.parallelStream().map(map -> {
                List<ParamTypeItems.ValueItems> specValueItems = specIdList.parallelStream().map(specId -> {
                    int fuelTypeDetail = this.getFuelTypeDetail(specId);
                    String value;
                    if (StringUtils.equals((String) map.get("item"), "发动机")) {
                        value = fuelTypeDetail == 4 ? "-" : CommonFunction.getDefaultParamSign((String) map.get(String.valueOf(specId)));
                    } else if (StringUtils.equals((String) map.get("item"), "电动机")) {
                        value = Spec.OIL_FUEL_TYPE_LIST.contains(fuelTypeDetail) ? "-" : CommonFunction.getDefaultParamSign((String) map.get(String.valueOf(specId)));
                    } else {
                        value = "-";
                    }
                    return ParamTypeItems.ValueItems.builder().specid(specId).value(value).build();
                }).collect(Collectors.toList());
                String name = StringUtils.equals((String) map.get("name"), str1) ? str3 : (String) map.get("name");
                return ParamTypeItems.ParamItems.builder().id((int) map.get("configId")).name(name)
                        .valueitems(specValueItems).build();
            }).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 系统综合扭矩(N·m)
     */
    private boolean isReWriteMaxTorque(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps) {
        return specConfigMaps.stream()
                .filter(spec -> StringUtils.equals((String) spec.get("name"), "系统综合扭矩(N·m)"))
                .flatMap(spec -> specIdList.stream().map(specId -> {
                    int fuelTypeDetail = this.getFuelTypeDetail(specId);
                    String itemValue = spec.getOrDefault(String.valueOf(specId), "").toString();
                    return Spec.FUEL_TYPE_FILTER_IDS.contains(fuelTypeDetail) && StringUtils.equalsAny(itemValue, "", "0");
                }))
                .reduce(false, Boolean::logicalOr);
    }

    private boolean isReWriteMaxKw(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps) {
        //是否有插电或油电混合的车型
        boolean fuelType35Exist = specIdList.stream().anyMatch(specId -> {
            int fuelTypeDetail = this.getFuelTypeDetail(specId);
            return fuelTypeDetail == 3 || fuelTypeDetail == 5;
        });
        if (!fuelType35Exist) {
            return false;
        }
        return specConfigMaps.stream()
                .filter(spec -> StringUtils.equals((String) spec.get("name"), "系统综合功率(kW)"))
                .flatMap(spec -> specIdList.stream().map(specId -> {
                    int fuelTypeDetail = this.getFuelTypeDetail(specId);
                    String itemValue = spec.getOrDefault(String.valueOf(specId), "").toString();
                    return Spec.FUEL_TYPE_FILTER_IDS.contains(fuelTypeDetail) && StringUtils.equalsAny(itemValue, "", "0");
                }))
                .reduce(false, Boolean::logicalOr);
    }

    private int getFuelTypeDetail(int specId) {
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        return specBaseInfo == null ? -1 : specBaseInfo.getFuelTypeDetail();
    }

    private List<Map<String, Object>> getSpecConfigMaps(boolean isCvSpec, boolean unCvSpec, List<Integer> specIdList, boolean filterOpt) {
        List<Map<String, Object>> resultMapList = Collections.emptyList();
        if (!isCvSpec && !unCvSpec) {
            List<OptParItemInfoEntity> itemInfoEntities = optParItemInfoService.get().join();
            resultMapList = CommonFunction.listToMap(itemInfoEntities);

        }
        Map<Integer, List<SpecConfigEntity>> serviceMap = specConfigService.getMap(specIdList);
        resultMapList = !CollectionUtils.isEmpty(resultMapList) && filterOpt ? getMapsFromOptParItem(specIdList, resultMapList, serviceMap) : getMaps(specIdList, resultMapList, serviceMap);
        resultMapList.sort((o1, o2) -> {
            Integer pordercls1 = Integer.parseInt(String.valueOf(o1.get("pordercls")));
            Integer pordercls2 = Integer.parseInt(String.valueOf(o2.get("pordercls")));
            int result = pordercls1.compareTo(pordercls2);
            if (result != 0) {
                return result;
            }
            Integer ordercls1 = Integer.parseInt(String.valueOf(o1.get("ordercls")));
            Integer ordercls2 = Integer.parseInt(String.valueOf(o2.get("ordercls")));
            return ordercls1.compareTo(ordercls2);
        });

        return resultMapList;
    }

    private List<Map<String, Object>> getMapsFromOptParItem(List<Integer> specIdList, List<Map<String, Object>> optParItemMapList, Map<Integer, List<SpecConfigEntity>> serviceMap) {
        for (Integer specId : specIdList) {
            List<SpecConfigEntity> specConfigEntities = serviceMap.get(specId);
            if (CollectionUtils.isEmpty(specConfigEntities)) {
                continue;
            }
            Map<String, Map<String, Object>> entityMap =
                    optParItemMapList.stream().collect(Collectors.toMap(map -> String.format("%s%s", map.get("item"), map.get("name")),
                            Function.identity()));
            for (SpecConfigEntity value : specConfigEntities) {
                if (Objects.isNull(value)) {
                    continue;
                }
                String key = String.format("%s%s", value.getItem(), value.getName());
                Map<String, Object> maps = entityMap.get(key);
                if (maps != null) {
                    maps.put(String.valueOf(specId), value.getItemValue());
                }
            }
        }
        return optParItemMapList;
    }

    private List<Map<String, Object>> getMaps(List<Integer> specIdList, List<Map<String, Object>> resultMapList, Map<Integer, List<SpecConfigEntity>> serviceMap) {
        boolean flag = false;
        for (Integer specId : specIdList) {
            List<SpecConfigEntity> specConfigEntities = serviceMap.get(specId);
            if (CollectionUtils.isEmpty(specConfigEntities)) {
                continue;
            }
            if (CollectionUtils.isEmpty(resultMapList)) {
                resultMapList = SpecConfigEntity.listToMap(specConfigEntities, specId);
                continue;
            }
            Map<String, Map<String, Object>> entityMap =
                    resultMapList.stream().collect(Collectors.toMap(map -> String.format("%s%s%s%s", map.get("item"), map.get("name"), map.get("pordercls"), map.get("ordercls")),
                            Function.identity()));
            for (SpecConfigEntity value : specConfigEntities) {
                if (Objects.isNull(value)) {
                    continue;
                }
                String key = String.format("%s%s%s%s", value.getItem(), value.getName(), value.getPordercls(), value.getOrdercls());
                Map<String, Object> maps = entityMap.get(key);
                if (maps != null) {
                    maps.put(String.valueOf(specId), value.getItemValue());
                } else {
                    flag = true;
                    resultMapList.add(SpecConfigEntity.getObjToMap(value, specId));
                }
            }
        }
        if (flag) {
            List<String> collect = resultMapList.stream().map(map -> String.valueOf(map.get("item"))).distinct().collect(Collectors.toList());
            Map<String, List<Map<String, Object>>> item = resultMapList.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("item"))));
            resultMapList = collect.stream().map(item::get).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return resultMapList;
    }

    @Override
    public Spec25PictureBySpecIdResponse getSpec25PictureBySpecId(Spec25PictureBySpecIdRequest request) {
        Spec25PictureBySpecIdResponse.Builder builder = Spec25PictureBySpecIdResponse.newBuilder();
        Spec25PictureBySpecIdResponse.Result.Builder result = Spec25PictureBySpecIdResponse.Result.newBuilder();
        int specid = request.getSpecid();
        if (specid == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        if(specid < 1000000){
//            List<Car25PictureViewEntity> list = specPictureStatisticsMapper.GetDicSpec25Pic(specid);
            List<Car25PictureViewEntity> list = spec25PicBaseService.get(specid).join();
            if(!CollectionUtils.isEmpty(list)){
//                List<KeyValueDto<Integer,String>> map = picClassMapper.getDicCar25PictureType();
                List<KeyValueDto<Integer,String>> map = getCar25PictureType();
                for (Car25PictureViewEntity item:list) {
                    Optional<KeyValueDto<Integer,String>> iname = map.stream().filter(v -> v.getKey() == item.getId()).findFirst();
                    Spec25PictureBySpecIdResponse.Result.Picitems.Builder pic = Spec25PictureBySpecIdResponse.Result.Picitems.newBuilder();
                    pic.setItemid(item.getOrdercls());
                    pic.setTypeid(item.getTopId());
                    pic.setItemname(iname.isPresent()==true?iname.get().getValue():"");
                    pic.setPicid(item.getPicId());
                    pic.setPicpath(ImageUtil.getFullImagePath(item.getPicPath()));
                    pic.setRemark(item.getRemark().replace("<br>"," "));
                    result.addPicitems(pic);
                }
            }
        }
        SpecBaseInfo specBase = specBaseService.get(specid).join();
        if(specBase != null){
            result.setSpecname(specBase.getSpecName());
            result.setSeriesid(specBase.getSeriesId());
            SeriesBaseInfo seriesBase = seriesBaseService.get(specBase.getSeriesId()).join();
            if(seriesBase != null){
                result.setSeriesname(seriesBase.getName());
                result.setBrandid(seriesBase.getBrandId());
                BrandBaseInfo brandBase = brandBaseService.get(seriesBase.getBrandId()).join();
                if(brandBase != null){
                    result.setBrandname(brandBase.getName());
                }
                else {
                    result.setBrandname("");
                }
            }
            else {
                result.setSeriesname("");
            }
        }
        else {
            result.setSpecname("");
        }
        result.setSpecid(specid);
        result.setTotal(result.getPicitemsCount());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public SpecInnerColorBySpecIdResponse getSpecInnerColorBySpecId(SpecInnerColorBySpecIdRequest request) {
        SpecInnerColorBySpecIdResponse.Builder builder = SpecInnerColorBySpecIdResponse.newBuilder();
        SpecInnerColorBySpecIdResponse.Result.Builder result = SpecInnerColorBySpecIdResponse.Result.newBuilder();
        int specid = request.getSpecid();
        if (specid == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SpecBaseInfo specBase = specBaseService.get(specid).join();
        if(specBase != null){
            List<SpecPicColorStatisticsEntity> list = seriesSpecPicInnerColorStatistics.get(specBase.getSeriesId());
            if(list == null){
                list = new ArrayList<>();
            }
            list = list.stream().filter(item -> item.getSpecId() == specid).collect(Collectors.toList());
            String colorString = specBase.getInnerColorIds();
            String priceString = specBase.getInnerColorPrices();
            String remarkString = specBase.getInnerColorRemarks();
            if(!StringUtils.isBlank(colorString)){
                int[] colors = StringIntegerUtils.convertToInt32(colorString,",",0);
                int[] prices = new int[colors.length];
                String[] remarks = new String[colors.length];
                if(!StringUtils.isBlank(priceString)){
                    prices = StringIntegerUtils.convertToInt32(priceString,",",0);
                }
                if(!StringUtils.isBlank(remarkString)){
                    remarks = remarkString.split(",",-1);
                }
                List<SpecPicColorStatisticsEntity> colorList = new ArrayList<>();
                for (int i = 0; i < colors.length;i++) {
                    int idx = i;
                    List<SpecPicColorStatisticsEntity> finalList = list.stream().filter(v -> v.getColorId() == colors[idx]).collect(Collectors.toList());
                    int[] finalPrices = prices;
                    String[] finalRemarks = remarks;
                    colorList.add(new SpecPicColorStatisticsEntity(){
                        {
                            setColorId(colors[idx]);
                            setPrice(finalPrices[idx]);
                            setRemarks(finalRemarks[idx] == null?"":finalRemarks[idx]);
                            setPicNumber(finalList.stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber)));
                            setClubPicNumber(finalList.stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getClubPicNumber)));
                        }
                    });
                }
//                for (int color:colors) {
//                    List<SpecPicColorStatisticsEntity> finalList = list.stream().filter(v -> v.getColorId() == color).collect(Collectors.toList());
//                    colorList.add(new SpecPicColorStatisticsEntity(){
//                        {
//                            setColorId(color);
//                            setPicNumber(finalList.stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber)));
//                            setClubPicNumber(finalList.stream().collect(Collectors.summingInt(SpecPicColorStatisticsEntity::getClubPicNumber)));
//                        }
//                    });
//                }
                colorList = colorList.stream().sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getPicNumber,Comparator.reverseOrder())).collect(Collectors.toList());
                for (SpecPicColorStatisticsEntity color:colorList) {
                    SpecInnerColorBySpecIdResponse.Result.Coloritems.Builder pic = SpecInnerColorBySpecIdResponse.Result.Coloritems.newBuilder();
                    ColorBaseInfo colorBase = innerColorBaseService.getColor(color.getColorId());
                    pic.setId(color.getColorId());
                    pic.setName(colorBase == null?"": colorBase.getName());
                    pic.setValue(colorBase == null?"": colorBase.getValue());
                    pic.setPicnum(color.getPicNumber());
                    pic.setClubpicnum(color.getClubPicNumber());
                    pic.setPrice(color.getPrice());
                    pic.setRemark(color.getRemarks());
                    result.addColoritems(pic);
                }
            }
        }
        result.setSpecid(specid);
        result.setTotal(result.getColoritemsCount());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public SpecInfoBySeriesIdResponse getSpecInfoBySeriesId (SpecInfoBySeriesIdRequest request) {
        SpecInfoBySeriesIdResponse.Builder builder = SpecInfoBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
        if(baseInfo != null){
            boolean iscv = Level.isCVLevel(baseInfo.getLevelId());
            List<SpecViewEntity> list = seriesSpecInfoBaseService.get(seriesId,iscv).join();
            if(!CollectionUtils.isEmpty(list)){
                //            List<SpecViewEntity> list = specViewMapper.getSpecInfoBySeriesId(seriesId,iscv);
                List<Integer> specIds = new ArrayList<>();
                list.forEach(x ->{
                    specIds.add(x.getSpecId());
                });
                List<SpecBaseInfo> specBaseList = specBaseService.getList(specIds.stream().distinct().collect(Collectors.toList()));
                Map<Integer,SpecBaseInfo> map = specBaseList.stream().collect(Collectors.toMap(SpecBaseInfo::getId,a -> a,(x,y)->x));
                for (SpecViewEntity item:list) {
//                    SpecBaseInfo specBase = specBaseService.get(item.getSpecId()).join();
                    SpecBaseInfo specBase = map.get(item.getSpecId());
                    SpecInfoBySeriesIdResponse.Result.Builder result = SpecInfoBySeriesIdResponse.Result.newBuilder();
                    result.setId(item.getSpecId());
                    result.setSpecparamisshow(specBase == null?0:specBase.getIsSpecParamIsShow());
                    result.setSpecstate(item.getSpecState());
                    result.setSpecisimage(item.getSpecIsImage());
                    result.setYear(item.getSyear());
                    builder.addResult(result);
                }
            }
        }
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public SpecPictureCountByConditionResponse getSpecPictureCountByCondition(SpecPictureCountByConditionRequest request) {
        SpecPictureCountByConditionResponse.Builder builder = SpecPictureCountByConditionResponse.newBuilder();
        SpecPictureCountByConditionResponse.Result.Builder result = SpecPictureCountByConditionResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int colorId = request.getColorid();
        int classId = request.getClassid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        if (colorId > 0 || classId > 0) {
            if(colorId > 0){
                List<SpecPicColorStatisticsEntity> list = specColorPicNumBaseService.get(seriesId).join();
                if(!CollectionUtils.isEmpty(list)){
                    //                List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecColorPicNumBySeries(seriesId);
                    list = list.stream().filter(item -> item.getColorId() == colorId && (classId == 0 || item.getPicClass() == classId)).collect(Collectors.toList());
                    Function<SpecPicColorStatisticsEntity, List<Object>> sel = v -> Arrays.<Object>asList(v.getSpecId(), v.getSyear(), v.getSpecState());
                    Map<List<Object>, Integer> agg = list.stream().collect(Collectors.groupingBy(sel, Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber)));
                    for (Map.Entry<List<Object>, Integer> item:agg.entrySet()) {
                        int specId = (int)item.getKey().get(0);
                        SpecBaseInfo specBase = specBaseService.get(specId).join();
                        SpecPictureCountByConditionResponse.Specitems.Builder pic = SpecPictureCountByConditionResponse.Specitems.newBuilder();
                        pic.setId(specId);
                        pic.setName(specBase.getSpecName());
                        pic.setSyear((int)item.getKey().get(1));
                        pic.setIspublic((int)item.getKey().get(2));
                        pic.setPiccount(item.getValue());
                        pic.setDisplacement(specBase.getDisplacement());
                        pic.setFlowmodename(CommonFunction.admissionMehtod(specBase.getFlowMode()));
                        pic.setHorsepower(specBase.getHorsepower());
                        result.addSpecitems(pic);
                    }
                }
            }
            else {
                SeriesBaseInfo seriesBase = seriesBaseService.get(seriesId).join();
                if(seriesBase != null){
                    List<SpecPicColorStatisticsEntity> list = specColorPicNumCVBaseService.get(seriesId).join();
                    if(!CollectionUtils.isEmpty(list)){
                        //                    List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecColorPicNumBySeriesCV(seriesId,Level.isCVLevel(seriesBase.getLevelId()));
                        list = list.stream().filter(item -> item.getPicClass() == classId).collect(Collectors.toList());
                        list = list.stream().sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getSpecId,Comparator.reverseOrder())).collect(Collectors.toList());
                        for (SpecPicColorStatisticsEntity item:list) {
                            int specId = item.getSpecId();
                            SpecBaseInfo specBase = specBaseService.get(specId).join();
                            SpecPictureCountByConditionResponse.Specitems.Builder pic = SpecPictureCountByConditionResponse.Specitems.newBuilder();
                            pic.setId(specId);
                            pic.setName(specBase.getSpecName());
                            pic.setSyear(item.getSpecYear());
                            pic.setIspublic(item.getCarState());
                            pic.setPiccount(item.getPicNumber());
                            pic.setDisplacement(specBase.getDisplacement());
                            pic.setFlowmodename(CommonFunction.admissionMehtod(specBase.getFlowMode()));
                            pic.setHorsepower(specBase.getHorsepower());
                            result.addSpecitems(pic);
                        }
                    }
                }
            }
        }
        else {
            List<CarSpecPictureStatisticsEntity> list = specPictureStatisticsBaseService.get(seriesId).join();
            if(!CollectionUtils.isEmpty(list)){
//                List<CarSpecPictureStatisticsEntity> list = specPictureStatisticsMapper.getSpecPictureStatisticsBySeriesId(seriesId);
                for (CarSpecPictureStatisticsEntity item:list) {
                    int specId = item.getSpecId();
                    SpecBaseInfo specBase = specBaseService.get(specId).join();
                    if(specBase != null){
                        SpecPictureCountByConditionResponse.Specitems.Builder pic = SpecPictureCountByConditionResponse.Specitems.newBuilder();
                        pic.setId(specId);
                        pic.setName(specBase.getSpecName());
                        pic.setSyear(item.getSpecYear());
                        pic.setIspublic(item.getCarState());
                        pic.setPiccount(item.getPicNumber());
                        pic.setDisplacement(specBase.getDisplacement());
                        pic.setFlowmodename(CommonFunction.admissionMehtod(specBase.getFlowMode()));
                        pic.setHorsepower(specBase.getHorsepower());
                        result.addSpecitems(pic);
                    }
                }
            }
        }
        result.setClassid(classId);
        result.setColorid(colorId);
        result.setSeriesid(seriesId);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public SpecPictureCountByConditionResponseV2 getSpecPictureCountByConditionV2(SpecPictureCountByConditionRequestV2 request){
        SpecPictureCountByConditionResponseV2.Builder builder = SpecPictureCountByConditionResponseV2.newBuilder();
        SpecPictureCountByConditionResponseV2.Result.Builder result = SpecPictureCountByConditionResponseV2.Result.newBuilder();
        int seriesId = request.getSeriesid();
        int colorId = request.getInnercolorid();
        int classId = request.getClassid();
        if (seriesId == 0) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        if (colorId > 0 || classId > 0){
            result.setInnerColorId(colorId);
            if(colorId > 0){
                List<SpecPicColorStatisticsEntity> list = specInnerColorPicNumBaseService.get(seriesId).join();
                if(!CollectionUtils.isEmpty(list)) {
//                List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecColorPicNumBySeries(seriesId);
                    list = list.stream().filter(item -> item.getColorId() == colorId && (classId == 0 || item.getPicClass() == classId)).collect(Collectors.toList());
                    Function<SpecPicColorStatisticsEntity, List<Object>> sel = v -> Arrays.<Object>asList(v.getSpecId(), v.getSyear(), v.getSpecState());
                    Map<List<Object>, Integer> agg = list.stream().collect(Collectors.groupingBy(sel, Collectors.summingInt(SpecPicColorStatisticsEntity::getPicNumber)));
                    List<Integer> specIds = new ArrayList<>();
                    list.forEach(x -> {
                        specIds.add(x.getSpecId());
                    });
                    List<SpecBaseInfo> specBaseList = specBaseService.getList(specIds.stream().distinct().collect(Collectors.toList()));
                    Map<Integer,SpecBaseInfo> map = specBaseList.stream().collect(Collectors.toMap(SpecBaseInfo::getId,a -> a,(x,y)->x));
                    for (Map.Entry<List<Object>, Integer> item : agg.entrySet()) {
                        int specId = (int) item.getKey().get(0);
//                        SpecBaseInfo specBase = specBaseService.get(specId).join();
                        SpecBaseInfo specBase = map.get(specId);
                        SpecPictureCountByConditionResponseV2.Specitems.Builder pic = SpecPictureCountByConditionResponseV2.Specitems.newBuilder();
                        pic.setId(specId);
                        pic.setName(specBase.getSpecName());
                        pic.setSyear((int) item.getKey().get(1));
                        pic.setIspublic((int) item.getKey().get(2));
                        pic.setPiccount(item.getValue());
                        pic.setDisplacement(specBase.getDisplacement());
                        pic.setFlowmodename(CommonFunction.admissionMehtod(specBase.getFlowMode()));
                        pic.setHorsepower(specBase.getHorsepower());
                        result.addSpecitems(pic);
                    }
                }
            }
            else {
                SeriesBaseInfo seriesBase = seriesBaseService.get(seriesId).join();
                if(seriesBase != null){
                    List<SpecPicColorStatisticsEntity> list = specColorPicNumCVBaseService.get(seriesId).join();
                    if(!CollectionUtils.isEmpty(list)) {
//                    List<SpecPicColorStatisticsEntity> list = specColorMapper.getSpecColorPicNumBySeriesCV(seriesId,Level.isCVLevel(seriesBase.getLevelId()));
                        list = list.stream().filter(item -> item.getPicClass() == classId).collect(Collectors.toList());
                        list = list.stream().sorted(Comparator.comparing(SpecPicColorStatisticsEntity::getSpecId, Comparator.reverseOrder())).collect(Collectors.toList());
                        List<Integer> specIds = new ArrayList<>();
                        list.forEach(x -> {
                            specIds.add(x.getSpecId());
                        });
                        List<SpecBaseInfo> specBaseList = specBaseService.getList(specIds.stream().distinct().collect(Collectors.toList()));
                        Map<Integer,SpecBaseInfo> map = specBaseList.stream().collect(Collectors.toMap(SpecBaseInfo::getId,a -> a,(x,y)->x));
                        for (SpecPicColorStatisticsEntity item : list) {
                            int specId = item.getSpecId();
//                            SpecBaseInfo specBase = specBaseService.get(specId).join();
                            SpecBaseInfo specBase = map.get(specId);
                            if (specBase != null) {
                                SpecPictureCountByConditionResponseV2.Specitems.Builder pic = SpecPictureCountByConditionResponseV2.Specitems.newBuilder();
                                pic.setId(specId);
                                pic.setName(specBase.getSpecName());
                                pic.setSyear(item.getSpecYear());
                                pic.setIspublic(item.getCarState());
                                pic.setPiccount(item.getPicNumber());
                                pic.setDisplacement(specBase.getDisplacement());
                                pic.setFlowmodename(CommonFunction.admissionMehtod(specBase.getFlowMode()));
                                pic.setHorsepower(specBase.getHorsepower());
                                result.addSpecitems(pic);
                            }
                        }
                    }
                }
            }
        }
        else {
            result.setColorid(colorId);
            List<CarSpecPictureStatisticsEntity> list = specPictureStatisticsBaseService.get(seriesId).join();
//            List<CarSpecPictureStatisticsEntity> list = specPictureStatisticsMapper.getSpecPictureStatisticsBySeriesId(seriesId);
            if(!CollectionUtils.isEmpty(list)){
                List<Integer> specIds = new ArrayList<>();
                list.forEach(x -> {
                    specIds.add(x.getSpecId());
                });
                List<SpecBaseInfo> specBaseList = specBaseService.getList(specIds.stream().distinct().collect(Collectors.toList()));
                Map<Integer,SpecBaseInfo> map = specBaseList.stream().collect(Collectors.toMap(SpecBaseInfo::getId,a -> a,(x,y)->x));
                for (CarSpecPictureStatisticsEntity item:list) {
                    int specId = item.getSpecId();
//                    SpecBaseInfo specBase = specBaseService.get(specId).join();
                    SpecBaseInfo specBase = map.get(specId);
                    if(specBase != null){
                        SpecPictureCountByConditionResponseV2.Specitems.Builder pic = SpecPictureCountByConditionResponseV2.Specitems.newBuilder();
                        pic.setId(specId);
                        pic.setName(specBase.getSpecName());
                        pic.setSyear(item.getSpecYear());
                        pic.setIspublic(item.getCarState());
                        pic.setPiccount(item.getPicNumber());
                        pic.setDisplacement(specBase.getDisplacement());
                        pic.setFlowmodename(CommonFunction.admissionMehtod(specBase.getFlowMode()));
                        pic.setHorsepower(specBase.getHorsepower());
                        result.addSpecitems(pic);
                    }
                }
            }
        }
        result.setClassid(classId);
        result.setSeriesid(seriesId);
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public ApiResult<SpecBaseInfoItems> getSpecBaseInfoBySpecIds(GetSpecBaseInfoBySpecIdsRequest request) {
        List<Integer> specIds = CommonFunction.getListFromStr(request.getSpecids());
        if ( CollectionUtils.isEmpty(specIds) || specIds.size() > 100) {
            return new ApiResult<>(new SpecBaseInfoItems() {{ setSpecitems(Collections.emptyList()); }}, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }
        List<SpecBaseInfoItem> itemList = new ArrayList<>();

        int id = 1;
        //车型基础信息
        Map<Integer, SpecBaseInfo> specBaseInfoMap = specBaseService.getMap(specIds);
        if(CollectionUtils.isEmpty(specBaseInfoMap)){
            return new ApiResult<>(new SpecBaseInfoItems() {{ setSpecitems(Collections.emptyList()); }}, ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
        }

        List<Integer> seriesIds = specBaseInfoMap.values()
                .stream()
                .mapToInt(SpecBaseInfo::getSeriesId)
                .boxed()
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, SeriesConfig> seriesConfigsMap = seriesConfigService.getMap(seriesIds);
        Map<Integer, List<CarPhotoViewItemMessage>> carPhotoViewEntitiesMap = new HashMap<>();
        if(request.getClassid() > 0){
            carPhotoViewEntitiesMap =  photosService.getMap(seriesIds);
        }

        for (Integer specId: specIds) {
            // 组装数据
            SpecBaseInfoItem item = new SpecBaseInfoItem();
            item.setId(id);
            item.setSpecid(specId);
            item.setMaxprice("0");
            item.setMinprice("0");
            item.setUrl(String.format("http://www.autohome.com.cn/spec/%d/", specId));
            if (specBaseInfoMap.containsKey(specId)) {
                SpecBaseInfo specInfo = specBaseInfoMap.get(specId);
                // 查车系信息
                if(seriesConfigsMap != null){
                    SeriesConfig seriesConfig = seriesConfigsMap.get(specInfo.getSeriesId());
                    if(seriesConfig != null){
                        item.setBrandid(seriesConfig.getBrandid());
                        item.setBrandname(seriesConfig.getBrandname());
                        item.setFctid(seriesConfig.getFctid());
                        item.setFctname(seriesConfig.getFctname());
                        item.setSeriesname(seriesConfig.getName());
                        item.setLevelid(seriesConfig.getLevelid());
                        item.setLevelname(seriesConfig.getLevelname());
                    }
                }
                item.setSeriesid(specInfo.getSeriesId());
                item.setSpecname(specInfo.getSpecName());
                item.setSpecisstop(specInfo.getSpecState() == 40 ? 1 : 0);
                item.setSpecimg(ImageUtil.getFullImagePath(specInfo.getLogo()));
                item.setMinprice(String.valueOf(specInfo.getSpecMinPrice()));
                item.setMaxprice(String.valueOf(specInfo.getSpecMaxPrice()));
                item.setIspevcar(specInfo.getFuelTypeDetail() == 4 ? 1 : 0);
                // 取图
                List<SpecBaseInfoImgItem> imgList = new ArrayList<>();
                List<CarPhotoViewItemMessage> carPhotoViewEntities = carPhotoViewEntitiesMap.get(specInfo.getSeriesId());
                if(!CollectionUtils.isEmpty(carPhotoViewEntities)){
                    carPhotoViewEntities = carPhotoService.carPhotoBySpecAndClass(carPhotoViewEntities, specId, request.getClassid(), false);
                    imgList = carPhotoViewEntities.stream()
                            .limit(5)
                            .map(carPhotoViewEntity -> {
                                SpecBaseInfoImgItem img = new SpecBaseInfoImgItem();
                                img.setPicid(carPhotoViewEntity.getPicId());
                                img.setPicurl(ImageUtil.getFullImagePathWithoutReplace(carPhotoViewEntity.getPicFilePath()));
                                return img;
                            })
                            .collect(Collectors.toList());
                }
                item.setImglist(imgList);
                item.setClasspicnum(CollectionUtils.isEmpty(carPhotoViewEntities) ? 0 :carPhotoViewEntities.size());
            }
            itemList.add(item);
            id++;
        }

        SpecBaseInfoItems result = new SpecBaseInfoItems();
        result.setSpecitems(itemList);
        return new ApiResult<>(result, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ApiResult<SpecColorListItems> getSpecInnerColorBySeriesId(GetSpecInnerColorBySeriesIdRequest request) {
        return getSpecColorListBySeriesId(request.getSeriesid(), true);
    }

    @Override
    public ApiResult<SpecColorListItems> getSpecSpecColorListBySeriesId(GetSpecSpecColorBySeriesIdRequest request) {
        return getSpecColorListBySeriesId(request.getSeriesid(), false);
    }
    /**
     * 根据车系id获取各状态下车型数量
     * @param request
     * @return
     */
    @Override
    public ApiResult<SpecCountItem> getSpecCountBySeriesId(GetSpecCountBySeriesIdRequest request) {
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        List<SpecViewEntity> specViewEntities = null;
        if(null != seriesBaseInfo){
            boolean isCv = Level.isCVLevel(seriesBaseInfo.getLevelId());
            specViewEntities = seriesSpecBaseService.get(seriesId,isCv).join();
        }
        List<SpecCountItem.CountItem> countItems = new ArrayList<>();
        //不为空
        if(!CollectionUtils.isEmpty(specViewEntities)){
            //排序分组
            LinkedHashMap<Integer, ArrayList<SpecViewEntity>> specViewEntityMap = specViewEntities.stream().sorted(Comparator.comparing(SpecViewEntity::getSpecState)).
                    collect(Collectors.groupingBy(SpecViewEntity::getSpecState, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            //遍历
            for (Map.Entry<Integer, ArrayList<SpecViewEntity>> specViewMap:specViewEntityMap.entrySet()){
                SpecCountItem.CountItem countItem = new SpecCountItem.CountItem();
                countItem.setState(specViewMap.getKey());
                countItem.setCount(specViewMap.getValue().size());
                boolean isCv = Level.isCVLevel(seriesBaseInfo.getLevelId());
                AtomicInteger imageSpecCount = new AtomicInteger(0);
                if(!isCv){
                    specViewMap.getValue().forEach(specViewEntity -> {
                        if(specViewEntity.getSpecIsImage() == 1){
                            imageSpecCount.addAndGet(1);
                        }
                    });
                }
                countItem.setNoimgcount(specViewMap.getValue().size() - imageSpecCount.get());
                countItems.add(countItem);
            }
        }
        //组装信息
        SpecCountItem specCountItem = new SpecCountItem();
        specCountItem.setSeriesid(seriesId);
        specCountItem.setTotal(countItems.size());
        specCountItem.setItems(countItems);
        return new ApiResult<>(specCountItem,RETURN_MESSAGE_ENUM0);
    }

    @Override
    public GetSpecNameResponse getSpecNameBySpecId(GetSpecInfoBySpecIdRequest request) {
        GetSpecNameResponse.Builder builder = GetSpecNameResponse.newBuilder();
        int specId = request.getSpecid();
        if (specId <= 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetSpecNameResponse.Result.Builder result = GetSpecNameResponse.Result.newBuilder();
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        result.setSpecid(specId);
        if (Objects.isNull(specBaseInfo)) {
            log.info("车型Id: {}, 不存在", specId);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        int seriesId = specBaseInfo.getSeriesId();
        SeriesInfo seriesInfo = seriesInfoService.get(seriesId, false, false);
        if (Objects.isNull(seriesInfo)) {
            log.warn("车型Id: {}, 对应的车系：{}, 不存在", specId, seriesId);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        result.setSpecname(StringUtils.defaultString(specBaseInfo.getSpecName()));
        result.setSeriesid(seriesId);
        result.setSeriesname(StringUtils.defaultString(seriesInfo.getSeriesname()));
        result.setBrandid(seriesInfo.getBrandid());
        result.setBrandname(StringUtils.defaultString(seriesInfo.getBrandname()));
        result.setFctid(seriesInfo.getFctid());
        result.setFctname(StringUtils.defaultString(seriesInfo.getFctname()));
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetSpecLogoResponse getSpecLogoBySpecId(GetSpecInfoBySpecIdRequest request) {
        GetSpecLogoResponse.Builder builder = GetSpecLogoResponse.newBuilder();
        int specId = request.getSpecid();
        if (specId <= 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetSpecLogoResponse.Result.Builder result = GetSpecLogoResponse.Result.newBuilder();
        result.setSpecid(specId);
        SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
        if (Objects.isNull(specBaseInfo)) {
            log.warn("车型：{}, 不存在", specId);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        result.setSpeclogo(ImageUtil.getFullImagePathNew(specBaseInfo.getSpecLogoImg(), true));
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetConfigListResponse getConfigListBySpecId(GetConfigListRequest request) {
        GetConfigListResponse.Result.Builder result = GetConfigListResponse.Result.newBuilder();
        GetConfigListResponse.Builder builder = GetConfigListResponse.newBuilder();
        int specId = request.getSpecid();
        if (specId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        result.setSpecid(specId);
        if (CommonFunction.checkSpecParamIsShow(Collections.singletonList(specId), specBaseService)) {
            log.info("车型：{}, 不展示", specId);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        CompletableFuture<SpecBaseInfo> specBaseInfoFuture = CompletableFuture.supplyAsync(() -> specBaseService.get(specId).join());
        CompletableFuture<List<ConfigTypeBaseInfo>> listFuture = CompletableFuture.supplyAsync(() -> configListService.get());
        CompletableFuture<List<SpecConfigRelationEntity>> valueFuture = CompletableFuture.supplyAsync(() -> specConfigRelationService.get(specId));
        CompletableFuture<Map<Integer, String>> itemValuesFuture = CompletableFuture.supplyAsync(() -> configItemValueService.get());
        CompletableFuture<List<SpecConfigPriceEntity>> priceItemsFuture = CompletableFuture.supplyAsync(() -> specConfigPriceService.getBySpecId(specId));
        CompletableFuture<Map<Integer, String>> subItemsFuture = CompletableFuture.supplyAsync(() -> configSubItemService.get());
        CompletableFuture<List<SpecConfigSubItemEntity>> specSubItemsFuture = CompletableFuture.supplyAsync(() -> specConfigSubItemService.get(specId));

        CompletableFuture.allOf(specBaseInfoFuture, listFuture, valueFuture, itemValuesFuture, priceItemsFuture, subItemsFuture, specSubItemsFuture).join();

        SpecBaseInfo specBaseInfo = specBaseInfoFuture.join();
        if (specBaseInfo == null) {
            log.info("车型：{}, 不存在", specId);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        int fuelTypeDetail = specBaseInfo.getFuelTypeDetail();
        List<ConfigTypeBaseInfo> list = listFuture.join().stream().collect(Collectors.toList());
        List<SpecConfigRelationEntity> values = valueFuture.join();
        Map<Integer, String> itemValues = itemValuesFuture.join(); //有2757行数据
        List<SpecConfigPriceEntity> priceItems = priceItemsFuture.join();
        Map<Integer, String> subItems = subItemsFuture.join();
        List<SpecConfigSubItemEntity> specSubItems = specSubItemsFuture.join();

        String defaultValue = itemValues.get(0);
        //纯电动车型相关判断，如果是新源车型隐藏相关配置项目
        boolean IsPEVCar = fuelTypeDetail == 4;
        list = JsonUtils.toObjectList(JsonUtils.toString(list),ConfigTypeBaseInfo.class);
        if (request.getDisptype() == 0) {
            return getConfigListBySpecIdV2(specId, list, priceItems, values, itemValues, subItems, defaultValue, IsPEVCar, result, builder);
        }
        return getGetConfigListResponse(result, builder, specId, list, values, itemValues, priceItems, subItems, specSubItems, defaultValue, IsPEVCar, request.getType());
    }

    public GetConfigListResponse getConfigListBySpecIdV2(int specId, List<ConfigTypeBaseInfo> list, List<SpecConfigPriceEntity> priceItems,
                                                         List<SpecConfigRelationEntity> values, Map<Integer, String> itemValues,
                                                         Map<Integer, String> subItems, String defaultValue, boolean IsPEVCar,
                                                         GetConfigListResponse.Result.Builder result, GetConfigListResponse.Builder builder) {
        List<ConfigTypeBaseInfo> tempList = ToolUtils.deepCopyList(list);

        Iterator<ConfigTypeBaseInfo> iterator = tempList.iterator();
        while (iterator.hasNext()) {
            ConfigTypeBaseInfo next = iterator.next();
            List<ConfigItemBaseInfo> items = next.getItems();
            items = items.stream().filter(e -> (Spec.isCvSpec(specId) && e.getCVIsShow() == 1) || (!Spec.isCvSpec(specId) && e.getIsShow() == 1)).
                    filter(e -> e.getDisplayType() == 0).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(items)) {
                iterator.remove();
            } else {
                next.setItems(items);
            }

        }
        int count = 0;
        for (ConfigTypeBaseInfo baseInfo : tempList) {
            count++;

            List<GetConfigListResponse.ConfTypeItems> items = new ArrayList<>();
            List<GetConfigListResponse.ConfTypeItems.ConfigItems> configItems = new ArrayList<>();
            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                List<GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem> valueItems = new ArrayList<>();
                List<SpecConfigPriceEntity> prices = priceItems.stream().filter(x -> x.getItemId() == item.getItemId()).collect(Collectors.toList());
                GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.Builder valueBuilder = GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.newBuilder();
                valueBuilder.setSpecid(specId);
                SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);
                String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                if (prices.size() > 0) {
                    for (SpecConfigPriceEntity price : prices) {
                        GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.SubPrice.Builder priceBuilder = GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.SubPrice.newBuilder();
                        priceBuilder.setSubname(subItems.getOrDefault(price.getSubItemId(), ""));
                        priceBuilder.setPrice(price.getPrice());
                        valueBuilder.addPrice(priceBuilder);
                    }
                }
                valueBuilder.setValue(relation == null ? defaultValue : StringUtils.defaultString(strValue));
                valueBuilder.addAllSublist(Collections.emptyList());

                if (IsPEVCar && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId())) {
                    continue;
                }
                valueItems.add(valueBuilder.build());
                configItems.add(GetConfigListResponse.ConfTypeItems.ConfigItems.newBuilder().
                        setName(item.getItemName()).
                        setId(0).
                        addAllValueitems(valueItems).build());
            }
            if (!CollectionUtils.isEmpty(configItems)) {
                items.add(GetConfigListResponse.ConfTypeItems.newBuilder().setName(baseInfo.getTypeName()).addAllConfigitems(configItems).build());
            } else {
                if (count != list.size()) {
                    items.add(GetConfigListResponse.ConfTypeItems.newBuilder().setName(baseInfo.getTypeName()).addAllConfigitems(configItems).build());
                }
            }
            result.setSpecid(specId).addAllConfigtypeitems(items);
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    private GetConfigListResponse getGetConfigListResponse(GetConfigListResponse.Result.Builder result, GetConfigListResponse.Builder builder, int specId, List<ConfigTypeBaseInfo> list, List<SpecConfigRelationEntity> values, Map<Integer, String> itemValues, List<SpecConfigPriceEntity> priceItems, Map<Integer, String> subItems, List<SpecConfigSubItemEntity> specSubItems, String defaultValue, boolean isPEVCar, int type) {

        List<ConfigTypeBaseInfo> tempList = ToolUtils.deepCopyList(list);
        if (Spec.isCvSpec(specId)) {
            Iterator<ConfigTypeBaseInfo> iterator = tempList.iterator();
            while (iterator.hasNext()) {
                ConfigTypeBaseInfo next = iterator.next();
                List<ConfigItemBaseInfo> items = next.getItems();
                items = items.stream().filter(e -> Spec.isCvSpec(specId) && e.getCVIsShow() == 1).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(items)) {
                    iterator.remove();
                } else {
                    next.setItems(items);
                }
            }
        }
        int count = 0;
        for (ConfigTypeBaseInfo baseInfo : tempList) {
            count++;
            List<GetConfigListResponse.ConfTypeItems> items = new ArrayList<>();
            List<GetConfigListResponse.ConfTypeItems.ConfigItems> configItems = new ArrayList<>();
            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if (!Spec.isCvSpec(specId) && item.getIsShow() != 1) {
                    continue;
                }
                int configId = item.getItemId();
                int displayType = item.getDisplayType();
                int dynamicShow = item.getDynamicShow();
                boolean currentConfigValueEqualNull = false; //配置项相关值是否等于空
                List<GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem> valueItems = new ArrayList<>();
                List<SpecConfigPriceEntity> prices = priceItems.stream().filter(x -> x.getItemId() == item.getItemId()).collect(Collectors.toList());
                GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.Builder valueBuilder = GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.newBuilder();
                valueBuilder.setSpecid(specId);
                if (displayType == 0) {  //横排
                    SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);
//                    if (Objects.isNull(relation)) {
//                        continue;
//                    }
                    String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                    if (prices.size() > 0) {
                        for (SpecConfigPriceEntity price : prices) {
                            GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.SubPrice.Builder priceBuilder = GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.SubPrice.newBuilder();
                            priceBuilder.setSubname(subItems.getOrDefault(price.getSubItemId(), ""));
                            priceBuilder.setPrice(price.getPrice());
                            valueBuilder.addPrice(priceBuilder);
                        }
                    }
                    valueBuilder.setValue(relation == null ? defaultValue : strValue);
                    valueBuilder.addAllSublist(Collections.emptyList());
                    //判断当前配置是否有值 一行展示的配置项只判断value相关业务
                    if (StringUtils.isBlank(strValue) || strValue.equals("-") || strValue.equals("0")) {
                        currentConfigValueEqualNull = true;
                    }
                    if (dynamicShow == 1 && currentConfigValueEqualNull) {
                        continue;
                    }
                } else if (displayType == 1) { //竖排
                    if (!CollectionUtils.isEmpty(specSubItems)) {
                        List<SpecConfigSubItemEntity> tempSubItems = specSubItems.stream().filter(e -> e.getItemId() == configId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(tempSubItems)) {
                            for (SpecConfigSubItemEntity specConfigSubItem : tempSubItems) {
                                SpecConfigPriceEntity price = prices.stream().filter(x -> x.getSubItemId() == specConfigSubItem.getSubItemId()).findFirst().orElse(null);
                                GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.SubItem.Builder subItem = GetConfigListResponse.ConfTypeItems.ConfigItems.Valueitem.SubItem.newBuilder();
                                subItem.setSubname(subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", ""));
                                subItem.setSubvalue(specConfigSubItem.getSubValue());
                                subItem.setPrice(price == null ? 0 : price.getPrice());
                                valueBuilder.addSublist(subItem);
                            }
                            valueBuilder.addAllPrice(new ArrayList<>());
                            valueBuilder.setValue(valueBuilder.getSublistCount() > 0 ? "" : "-");
                        } else {
                            currentConfigValueEqualNull = true;
                        }
                    } else {
                        currentConfigValueEqualNull = true;
                    }

                }
                if (isPEVCar && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId())) {
                   continue;
                }
                if(dynamicShow == 1 && currentConfigValueEqualNull){
                    continue;
                }
                valueItems.add(valueBuilder.build());
                configItems.add(GetConfigListResponse.ConfTypeItems.ConfigItems.newBuilder().
                        setConfigid(configId).
                        setName(item.getItemName()).
                        setId(0).
                        setDisptype(displayType).
                        addAllValueitems(valueItems).build());

            }
            if (!CollectionUtils.isEmpty(configItems)) {
                items.add(GetConfigListResponse.ConfTypeItems.newBuilder().setName(baseInfo.getTypeName()).addAllConfigitems(configItems).build());
            } else {
                if (count != list.size()) {
                    items.add(GetConfigListResponse.ConfTypeItems.newBuilder().setName(baseInfo.getTypeName()).addAllConfigitems(configItems).build());
                }
            }

            result.setSpecid(specId).addAllConfigtypeitems(items);
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public ApiResult<ParamTypeItemPage> getSpecParamsBySeriesId(GetSpecInfoBySeriesIdRequest request) {
        int seriesId = request.getSeriesid();
        if (seriesId == 0) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<SpecViewEntity> specViewEntities = commService.getSpecViewEntities(seriesId, 1);
        ParamTypeItemPage paramTypeItemPage = new ParamTypeItemPage();
        paramTypeItemPage.setSeriesid(seriesId);
        if (CollectionUtils.isEmpty(specViewEntities)) {
            paramTypeItemPage.setParamtypeitems(Collections.emptyList());
            return new ApiResult<>(paramTypeItemPage, RETURN_MESSAGE_ENUM0);
        }
        List<Integer> specIds = specViewEntities.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
        Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(specIds, false);
        paramTypeItemPage.setParamtypeitems(paramTypeItems.orElse(Collections.emptyList()));
        return new ApiResult<>(paramTypeItemPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public GetAppSpecParamBySpecListResponse getAppSpecParamBySpecList(GetSpecLogoBySpecListRequest request) {
        GetAppSpecParamBySpecListResponse.Builder builder = GetAppSpecParamBySpecListResponse.newBuilder();
        String specList = request.getSpeclist();
        List<Integer> specIdList;
        if (!CommonFunction.check(specIdList = CommonFunction.getListFromStr(specList))) {
            return builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        Map<Integer, SpecBaseInfo> map = specBaseService.getMap(specIdList);
        List<Integer> seriesIds = map.values().stream().filter(Objects::nonNull).map(SpecBaseInfo::getSeriesId).distinct().collect(Collectors.toList());
        Map<Integer, SeriesBaseInfo> seriesMap = seriesBaseService.getMap(seriesIds);
        List<String> stringList = Spec.PARAM_TYPE;
        List<GetAppSpecParamBySpecListResponse.Result.Specinfoitem> specInfoItems = new ArrayList<>();
        for (String type : stringList) {
            List<GetAppSpecParamBySpecListResponse.Result.Specinfoitem.Valueitem> valueItems = new ArrayList<>();
            for (Integer specId : specIdList) {
                SpecBaseInfo specBaseInfo = map.get(specId);
                if (specBaseInfo == null) {
                    specBaseInfo = new SpecBaseInfo();
                    specBaseInfo.setId(specId);
                }
                String value = "";
                if (StringUtils.equals("车系名称", type)) {
                    SeriesBaseInfo seriesBaseInfo = seriesMap.get(specBaseInfo.getSeriesId());
                    value = seriesBaseInfo == null ? "" : StringUtils.defaultString(seriesBaseInfo.getName());
                } else if (StringUtils.equals("车型名称", type)) {
                    value = StringUtils.defaultString(specBaseInfo.getSpecName());
                } else if (StringUtils.equals("车型图片", type)) {
                    value = StringUtils.defaultString(ImageUtil.getFullImagePath(specBaseInfo.getLogo()));
                }
                valueItems.add(GetAppSpecParamBySpecListResponse.Result.Specinfoitem.Valueitem.newBuilder()
                        .setSpecid(specBaseInfo.getId())
                        .setValue(value).build());
            }
            specInfoItems.add(GetAppSpecParamBySpecListResponse.Result.Specinfoitem.newBuilder().setName(type).addAllValueitems(valueItems).build());
        }
        GetAppSpecParamBySpecListResponse.Result.Builder result = GetAppSpecParamBySpecListResponse.Result.newBuilder();
        result.addAllSpecinfoitem(specInfoItems);
        return builder.setResult(result).build();
    }

    @Override
    public List<ParamTypeItems> getParamTypeItems(String specList, boolean filterOpt) {
        List<Integer> specIds =  CommonFunction.getListFromStr(specList);
        Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(specIds, filterOpt);
        return paramTypeItems.orElse(Collections.emptyList());
    }

    private ApiResult<SpecColorListItems> getSpecColorListBySeriesId(int seriesId, boolean inner) {
        if(seriesId == 0){
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        CompletableFuture<SeriesBaseInfo> seriesBaseInfoFuture = seriesBaseService.get(seriesId);
        CompletableFuture<List<SpecColorListBaseInfo>> specColorsFuture = inner ? specInnerColorBaseService.get(seriesId) : specSpecColorBaseService.get(seriesId);
        CompletableFuture<List<SpecPicColorStatisticsEntity>> specColorEntitiesFuture = inner ? CompletableFuture.completedFuture(seriesSpecPicInnerColorStatistics.get(seriesId)) : specPicColorStatisticsBaseService.get(seriesId);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(seriesBaseInfoFuture, specColorsFuture, specColorEntitiesFuture);
        allFutures.join();

        SeriesBaseInfo seriesBaseInfo = seriesBaseInfoFuture.join();
        List<SpecColorListBaseInfo> specColors = specColorsFuture.join();
        List<SpecPicColorStatisticsEntity> specColorEntities = specColorEntitiesFuture.join();
        if (Objects.isNull(seriesBaseInfo)) {
            return new ApiResult<>(SpecColorListItems.builder().total(0).specitems(Collections.emptyList()).build(), RETURN_MESSAGE_ENUM0);
        }
        //商用车无颜色
        if (Level.isCVLevel(seriesBaseInfo.getLevelId())) {
            return new ApiResult<>(SpecColorListItems.builder().total(0).specitems(Collections.emptyList()).build(), RETURN_MESSAGE_ENUM0);
        }
        if (CollectionUtils.isEmpty(specColors)) {
            return new ApiResult<>(SpecColorListItems.builder().total(0).specitems(Collections.emptyList()).build(), RETURN_MESSAGE_ENUM0);
        }

        List<SpecColorListBaseInfo> specColorsCopy = ToolUtils.deepCopyList(specColors);
        for (SpecColorListBaseInfo specColor : specColorsCopy) {
            List<SpecPicColorStatisticsEntity> specPicColorStatisticsItem;
            if (!CollectionUtils.isEmpty(specColorEntities)) {
                specPicColorStatisticsItem = specColorEntities.stream()
                        .filter(s -> specColor.getSpecId() == s.getSpecId())
                        .filter(s -> specColor.getColorId() == s.getColorId())
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(specPicColorStatisticsItem)) {
                    specColor.setPicNumber(specPicColorStatisticsItem.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum());
                    specColor.setClubPicNumber(specPicColorStatisticsItem.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum());
                }
            }
        }

        specColorsCopy.sort(Comparator.comparing(SpecColorListBaseInfo::getSpecId)
                .thenComparing(Comparator.comparing(SpecColorListBaseInfo::getPicNumber).reversed()));

        List<Integer> colorIds = specColorsCopy.stream().map(SpecColorListBaseInfo::getColorId).collect(Collectors.toList());
        Map<Integer,ColorBaseInfo>  colorBaseInfoMap = inner ? innerColorBaseService.getColorMap(colorIds) : colorBaseService.getColorMap(colorIds);

        Map<Integer, List<SpecColorListItems.ColorItem>> colorItemMap = specColorsCopy.stream()
                .collect(Collectors.groupingBy(SpecColorListBaseInfo::getSpecId, Collectors.mapping(specInnerColor -> {
                    ColorBaseInfo colorBaseInfo = CollectionUtils.isEmpty(colorBaseInfoMap) ? null : colorBaseInfoMap.get(specInnerColor.getColorId());
                    return SpecColorListItems.ColorItem.builder()
                            .id(specInnerColor.getColorId())
                            .name(colorBaseInfo != null ? colorBaseInfo.getName() : "")
                            .value(colorBaseInfo != null ? colorBaseInfo.getValue() : "")
                            .picnum(specInnerColor.getPicNumber())
                            .clubpicnum(specInnerColor.getClubPicNumber())
                            .price(specInnerColor.getPrice())
                            .remark(specInnerColor.getRemarks())
                            .build();
                }, Collectors.toList())));

        List<SpecColorListItems.SpecItem> specItems = specColorsCopy.stream().map(specInnerColor -> {
                    List<SpecColorListItems.ColorItem> colorItems = colorItemMap.get(specInnerColor.getSpecId());
                    if (!CollectionUtils.isEmpty(colorItems)) {
                        return SpecColorListItems.SpecItem.builder()
                                .specid(specInnerColor.getSpecId())
                                .coloritems(colorItems)
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        SpecColorListItems result = SpecColorListItems.builder()
                .total(specItems.size())
                .specitems(specItems)
                .build();
        return new ApiResult<>(result, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public GetSpecStateCountBySeriesIdResponse getSpecStateCountBySeriesId(GetSpecStateCountBySeriesIdRequest request){
        GetSpecStateCountBySeriesIdResponse.Builder builder = GetSpecStateCountBySeriesIdResponse.newBuilder();
        GetSpecStateCountBySeriesIdResponse.Result.Builder result = GetSpecStateCountBySeriesIdResponse.Result.newBuilder();
        int seriesId = request.getSeriesid();
        SeriesBaseInfo seriesInfo;
        result.setSeriesid(seriesId);
        if (seriesId == 0 || Objects.isNull(seriesInfo = seriesBaseService.get(seriesId).join())) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }

        boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
        List<SpecViewEntity> list = seriesSpecBaseService.get(seriesId, isCV).join();
        if(CollectionUtils.isEmpty(list)){
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        List<GetSpecStateCountBySeriesIdResponse.StateItem> stateList = list.stream()
                .collect(Collectors.groupingBy(SpecViewEntity::getSpecState))
                .entrySet().stream()
                .map(entry -> {
                    return GetSpecStateCountBySeriesIdResponse.StateItem.newBuilder()
                            .setState(entry.getKey())
                            .setCount(entry.getValue().size())
                            .setNoimgcount(entry.getValue().size() - entry.getValue().stream().mapToInt(SpecViewEntity::getSpecIsImage).sum())
                    .build();
                })
                .sorted(Comparator.comparingInt(GetSpecStateCountBySeriesIdResponse.StateItem::getState))
                .collect(Collectors.toList());
        result.addAllItems(stateList);
        result.setTotal(stateList.size());
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetSpecDetailBySpecListResponse getSpecDetailBySpecList(GetSpecDetailBySpecListRequest request) {
        GetSpecDetailBySpecListResponse.Builder builder = GetSpecDetailBySpecListResponse.newBuilder();
        GetSpecDetailBySpecListResponse.Result.Builder result = GetSpecDetailBySpecListResponse.Result.newBuilder();

        List<Integer> specIds = CommonFunction.getListFromStr(request.getSpeclist());
        if(CollectionUtils.isEmpty(specIds)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        SpecBaseInfo specFirst = specBaseService.get(specIds.get(0)).join();
        if(specFirst == null){
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        //找同车系
        SeriesBaseInfo seriesInfo = seriesBaseService.get(specFirst.getSeriesId()).join();
        if (seriesInfo == null) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }

        boolean isCV = Level.isCVLevel(seriesInfo.getLevelId());
        List<SpecViewEntity> list = seriesSpecBaseService.get(seriesInfo.getId(), isCV).join();
        if (CollectionUtils.isEmpty(list)) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        list = list.stream().filter(x -> x.getSpecState() >= 10).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        Map<Integer, SpecViewEntity> specViewMap = list.stream().collect(Collectors.toMap(SpecViewEntity::getSpecId, x -> x,(x,y)->x));

        Map<Integer, SpecBaseInfo> serviceMap = specBaseService.getMap(specIds);
        for (Integer specId : specIds) {
            SpecViewEntity item = specViewMap.get(specId);
            if(item == null){
                continue;
            }
            SpecBaseInfo specInfo = serviceMap.get(item.getSpecId());
            if (Objects.isNull(specInfo)) {
                continue;
            }
            String drivingmoden = isCV ? Spec.DriveMode(item.getDriveForm()) : item.getSpecDrivingMode() == null ? "" : item.getSpecDrivingMode();
            GetSpecDetailBySpecListResponse.SpecDetail.Builder spec = GetSpecDetailBySpecListResponse.SpecDetail.newBuilder()
                    .setId(item.getSpecId())//车型id
                    .setName(StringUtils.defaultString(specInfo.getSpecName(), ""))//车型名称
                    .setLogo(ImageUtil.getFullImagePath(specInfo.getLogo())) //车型代表图
                    .setYear(item.getSyear())//年代款
                    .setMinprice(item.getMinPrice())//车型指导价(低价)
                    .setMaxprice(item.getMaxPrice())//车型指导价(高价)
                    .setTransmission(StringUtils.defaultString(specInfo.getGearBox(), ""))//变速箱
                    .setState(item.getSpecState())//state:车型状态
                    .setDrivingmodename(drivingmoden)
                    .setFlowmodeid(item.getFlowMode())//进气形式id
                    .setFlowmodename(Spec.AdmissionMethod(item.getFlowMode()))//进气形式名称
                    .setDisplacement(item.getSpecDisplacement())//排气量
                    .setEnginepower(item.getSpecEnginePower())//马力
                    .setIspreferential(specInfo.getIsPreferential())//是否惠民
                    .setIstaxrelief(specInfo.getSpecTaxType() == 1 ? 1 : 0)//是否减税
                    .setIstaxexemption(specInfo.getSpecTaxType() == 2 ? 1 : 0)//是否免税
                    .setOrder(item.getSpecOrdercls())//车型排序
                    .setSpecorder(item.getSpecOrdercls())//车型排序
                    .setParamisshow((item.getSpecState() == 40 || item.getSpecState() >= 10 && specInfo.getIsSpecParamIsShow() == 1) ? 1 : 0);//是否参数外显
            result.addSpecitems(spec);
        }
        return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }


    @Override
    public SpecBaseInfbySpecListResponse specBaseInfbySpecList(SpecBaseInfbySpecListRequest request){
        SpecBaseInfbySpecListResponse.Builder builder = SpecBaseInfbySpecListResponse.newBuilder();
        SpecBaseInfbySpecListResponse.Result.Builder result = SpecBaseInfbySpecListResponse.Result.newBuilder();
        String specString = request.getSpeclist();
        int[] specIds = StringIntegerUtils.convertToInt32(specString,",",0);
        if(specIds.length == 0 || Arrays.stream(specIds).anyMatch(item -> item == 0)){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        Arrays.stream(specIds).forEach(id -> {
            SpecBaseInfo specBase = specBaseService.get(id).join();
            if(specBase != null){
                SeriesBaseInfo seriesBase = seriesBaseService.get(specBase.getSeriesId()).join();
                SpecBaseInfbySpecListResponse.SpecItem.Builder item = SpecBaseInfbySpecListResponse.SpecItem.newBuilder();
                item.setSpecid(id);
                item.setSpecname(specBase.getSpecName());
                item.setSeriesid(specBase.getSeriesId());
                item.setSeriesname(seriesBase == null?"":seriesBase.getName());
                item.setPic(ImageUtil.getFullImagePath(specBase.getSpecLogoImg()));
                item.setMinprice(specBase.getSpecMinPrice());
                item.setMaxprice(specBase.getSpecMaxPrice());
                result.addList(item);
            }
        });
        result.setRowcount(result.getListCount());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public SpecAllSpecInfoResponse specAllSpecInfo(SpecAllSpecInfoRequest request){
        SpecAllSpecInfoResponse.Builder builder = SpecAllSpecInfoResponse.newBuilder();
        SpecAllSpecInfoResponse.Result.Builder result = SpecAllSpecInfoResponse.Result.newBuilder();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        List<SpecSearchEntity> list = specSearchService.get();
        if (CollectionUtils.isEmpty(list)) {
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
//            //未售(0X0003)
//            case SELL_3:
//                flag = true;
//                list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
//                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
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
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(!flag){
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
        //分组后保持原来的顺序
        Map<String, List<SpecSearchEntity>> seriesGroup = list.stream().collect(Collectors.groupingBy(row -> row.getSeriesId() + "_" + row.getSeriesPlace(),
                LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
        for (Map.Entry<String, List<SpecSearchEntity>> item : seriesGroup.entrySet()) {
            SpecAllSpecInfoResponse.SeriesItem.Builder seriesItem = SpecAllSpecInfoResponse.SeriesItem.newBuilder();
            for(SpecSearchEntity spec : item.getValue()){
                SpecAllSpecInfoResponse.SpecItem.Builder specItem = SpecAllSpecInfoResponse.SpecItem.newBuilder();
                specItem.setSpecid(spec.getSpecId());
                specItem.setSpecname(spec.getSpecName());
                specItem.setSpecstat(spec.getSpecState());
                String specUrl = "";
                if(Strings.isNotBlank(spec.getSpecimg())){
                    if (spec.getSpecimg().contains("cardfs")) {
                        specUrl ="https://car2.autoimg.cn" + spec.getSpecimg().replace("~/","/").replace("/l_","/");
                    } else {
                        specUrl = "https://car0.autoimg.cn" + spec.getSpecimg().replace("~/", "/").replace("/l_","/");
                    }
                }
                specItem.setSpeclogo(specUrl);
                specItem.setMinprice(spec.getMinPrice());
                specItem.setMaxprice(spec.getMaxPrice());
                specItem.setFueltype(spec.getFuelType());
                seriesItem.addSpeclist(specItem);
            }

            SpecSearchEntity first = item.getValue().get(0);
            seriesItem.setSeriesname(first.getSeriesName() != null ? HtmlUtils.decode(first.getSeriesName()) : "");
            seriesItem.setLevelid(first.getLevelId());
            seriesItem.setLevelname(first.getLevelName() != null ? first.getLevelName() : "");
            seriesItem.setSeriesid(first.getSeriesId());
            seriesItem.setSeriesplace(first.getSeriesPlace());
            result.addList(seriesItem);
        }
        result.setNum(seriesGroup.size());
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }

    @Override
    public GetCarSpecParamListByYearIdResponse getCarSpecParamListByYearId(GetCarSpecParamListByYearIdRequest request) {
        GetCarSpecParamListByYearIdResponse.Builder builder = GetCarSpecParamListByYearIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if (seriesId == 0 || yearId == 0) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        builder.setReturnCode(0).setReturnMsg("成功");
        GetCarSpecParamListByYearIdResponse.Result.Builder result = GetCarSpecParamListByYearIdResponse.Result.newBuilder()
                .setSeriesid(seriesId)
                .setYearid(yearId)
                .addAllSpeclist(Collections.emptyList())
                .addAllParamtypeitems(Collections.emptyList());

        List<GetCarSpecParamListByYearIdResponse.SpecList> specList = new ArrayList<>();
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(seriesBaseInfo != null){
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            //查年代ids
            List<SpecStateEntity> specStates = specListSameYearByYearService.get(seriesId, yearId, isCV).join();
            if(!CollectionUtils.isEmpty(specStates)){
                specStates.sort(Comparator.comparingInt(SpecStateEntity::getSpecOrder)
                        .thenComparing(SpecStateEntity::getOrderBy)
                        .thenComparingInt(SpecStateEntity::getOrders)
                        .thenComparing(SpecStateEntity::getSpecId, Comparator.reverseOrder()));
                List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
                Map<Integer,SpecBaseInfo> specInfos = specBaseService.getMap(specIds);
                for(Integer specId :specIds){
                    SpecBaseInfo spec = specInfos.get(specId);
                    if(CollectionUtils.isEmpty(specInfos)){
                        break;
                    }
                    if(spec == null){
                        continue;
                    }
                    //车型列表
                    GetCarSpecParamListByYearIdResponse.SpecList.Builder specShow = GetCarSpecParamListByYearIdResponse.SpecList.newBuilder();
                    int showState = (spec.getSpecState() == 10) ? spec.getIsBooked() : -1;
                    specList.add(specShow.setSpecid(spec.getId()).setSpecstate(spec.getSpecState()).setShowstate(showState).build());
                }
                result.addAllSpeclist(specList);

                List<GetCarSpecParamListByYearIdResponse.Paramtypeitems> paramsList = new ArrayList<>();
                Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(specIds, false);
                if(paramTypeItems.isPresent()){
                    int modelId = 1;
                    Map<String, Integer> configModelMap = paramConfigModelService.getConfMap(modelId);
                    for(ParamTypeItems item : paramTypeItems.get()){
                        GetCarSpecParamListByYearIdResponse.Paramtypeitems.Builder paramType = GetCarSpecParamListByYearIdResponse.Paramtypeitems.newBuilder();
                       List<GetCarSpecParamListByYearIdResponse.Paramtypeitems.Paramitems> paramItems = new ArrayList<>();
                        paramType.setName(item.getName());
                        List<ParamTypeItems.ParamItems> params = item.getParamitems();
                        if(!CollectionUtils.isEmpty(params)){
                            for(ParamTypeItems.ParamItems param : params){
                                GetCarSpecParamListByYearIdResponse.Paramtypeitems.Paramitems.Builder paramBuild = GetCarSpecParamListByYearIdResponse.Paramtypeitems.Paramitems.newBuilder();
                                paramBuild.setId(param.getId());
                                paramBuild.setName(param.getName());
                                int pnidStr = configModelMap != null && configModelMap.containsKey(param.getName()) && configModelMap.get(param.getName()) > 0 ? configModelMap.get(param.getName()) : -1 ;
                                paramBuild.setPnid(modelId + "_" + pnidStr);
                                List<GetCarSpecParamListByYearIdResponse.Paramtypeitems.Valueitems> valueList = new ArrayList<>();
                                if(!CollectionUtils.isEmpty(param.getValueitems())){
                                    for(ParamTypeItems.ValueItems valueItem : param.getValueitems()){
                                        valueList.add(GetCarSpecParamListByYearIdResponse.Paramtypeitems.Valueitems.newBuilder()
                                                .setSpecid(valueItem.getSpecid())
                                                .setValue(valueItem.getValue())
                                                .build()
                                        );
                                    }
                                }
                                paramBuild.addAllValueitems(valueList);
                                paramItems.add(paramBuild.build());
                            }
                        }
                        paramType.addAllParamitems(paramItems);
                        paramsList.add(paramType.build());
                    }
                }
                result.addAllParamtypeitems(paramsList);
            }
        }
        return builder.setResult(result).build();
    }

    @Override
    public GetCarPriceSpecParamListByYearIdResponse getCarPriceSpecParamListByYearId(GetCarPriceSpecParamListByYearIdRequest request) {
        GetCarPriceSpecParamListByYearIdResponse.Builder builder = GetCarPriceSpecParamListByYearIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if (seriesId == 0 || yearId == 0) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        builder.setReturnCode(0).setReturnMsg("成功");
        GetCarPriceSpecParamListByYearIdResponse.Result.Builder result = GetCarPriceSpecParamListByYearIdResponse.Result.newBuilder()
                .setSeriesid(seriesId)
                .setYearid(yearId)
                .addAllSpeclist(Collections.emptyList())
                .addAllParamtypeitems(Collections.emptyList());

        List<GetCarPriceSpecParamListByYearIdResponse.SpecList> specList = new ArrayList<>();
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(seriesBaseInfo != null){
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            //查年代ids
            List<SpecStateEntity> specStates = specListSameYearByYearService.get(seriesId, yearId, isCV).join();
            if(!CollectionUtils.isEmpty(specStates)){
                specStates.sort(Comparator.comparingInt(SpecStateEntity::getSpecOrder)
                        .thenComparing(SpecStateEntity::getOrderBy)
                        .thenComparingInt(SpecStateEntity::getOrders)
                        .thenComparing(SpecStateEntity::getSpecId, Comparator.reverseOrder()));
                List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
                Map<Integer,SpecBaseInfo> specInfos = specBaseService.getMap(specIds);
                if(!CollectionUtils.isEmpty(specIds)){
                    for(Integer specId :specIds){
                        if(CollectionUtils.isEmpty(specInfos)){
                            break;
                        }
                        SpecBaseInfo spec = specInfos.get(specId);
                        if(spec == null){
                            continue;
                        }
                        //车型列表
                        GetCarPriceSpecParamListByYearIdResponse.SpecList.Builder specShow = GetCarPriceSpecParamListByYearIdResponse.SpecList.newBuilder();
                        int showState = (spec.getSpecState() == 10) ? spec.getIsBooked() : -1;
                        specList.add(specShow.setSpecid(spec.getId()).setSpecstate(spec.getSpecState()).setShowstate(showState).build());
                    }
                }
                result.addAllSpeclist(specList);

                List<GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems> paramsList = new ArrayList<>();
                Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(specIds, false);
                if(paramTypeItems.isPresent()){
                    for(ParamTypeItems item : paramTypeItems.get()){
                        GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.Builder paramType = GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.newBuilder();
                        List<GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.Paramitems> paramItems = new ArrayList<>();
                        paramType.setName(item.getName());
                        List<ParamTypeItems.ParamItems> params = item.getParamitems();
                        if(!CollectionUtils.isEmpty(params)){
                            for(ParamTypeItems.ParamItems param : params){
                                GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.Paramitems.Builder paramBuild = GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.Paramitems.newBuilder();
                                paramBuild.setId(param.getId());
                                paramBuild.setName(param.getName());
                                List<GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.Valueitems> valueList = new ArrayList<>();
                                if(!CollectionUtils.isEmpty(param.getValueitems())){
                                    for(ParamTypeItems.ValueItems valueItem : param.getValueitems()){
                                        valueList.add(GetCarPriceSpecParamListByYearIdResponse.Paramtypeitems.Valueitems.newBuilder()
                                                .setSpecid(valueItem.getSpecid())
                                                .setValue(valueItem.getValue())
                                                .setUrl(valueItem.getUrl() != null ? valueItem.getUrl() : "")
                                                .build()
                                        );
                                    }
                                }
                                paramBuild.addAllValueitems(valueList);
                                paramItems.add(paramBuild.build());
                            }
                        }
                        paramType.addAllParamitems(paramItems);
                        paramsList.add(paramType.build());
                    }
                }
                result.addAllParamtypeitems(paramsList);
            }
        }
        return builder.setResult(result).build();
    }

    @Override
    public GetCarSpecPriceByYearIdResponse getCarSpecPriceByYearId(GetCarSpecPriceByYearIdRequest request){
        GetCarSpecPriceByYearIdResponse.Builder builder = GetCarSpecPriceByYearIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getSyearid();
        if (seriesId == 0 || yearId == 0) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        builder.setReturnCode(0).setReturnMsg("成功");
        List<SpecPriceViewEntity> carPriceList = autoCacheService.carSpecPriceBySeriesId(seriesId);
        if(CollectionUtils.isEmpty(carPriceList)){
            return builder.build();
        }

        List<SpecPriceViewEntity> matchYearList = carPriceList.stream()
                .filter(entity -> entity.getSyearid() == yearId)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(matchYearList)){
            return builder.build();
        }
        List<GetCarSpecPriceByYearIdResponse.SpecItems> result = new ArrayList<>();

        List<Integer> specIds = matchYearList.stream().map(SpecPriceViewEntity::getSpecId).collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specBaseInfoMap = specBaseService.getMap(specIds);

        // 第一层分组和排序（按照 horsepower 进行分组，并在每层内按照 horsepower 进行排序）
        TreeMap<Integer, List<SpecPriceViewEntity>> sortedHorseMap = matchYearList.stream()
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
                    for(Map.Entry<Double, List<SpecPriceViewEntity>> electricEntry : sortedElectricMap.entrySet()){
                        Double electricKW = electricEntry.getKey();
                        List<SpecPriceViewEntity> specs = electricEntry.getValue();

                        StringBuilder groupTitle = new StringBuilder();
                        groupTitle.append(deliveryKey);
                        String flowKey = flow == 1 ? "" : electricKW > 0 ? "" : " " + flowMode;//进气形式
                        groupTitle.append(flowKey);

                        String enginePower = "";
                        if (horse > 0 && electricKW <= 0 || delivery.equals("0")) {
                            enginePower = " " + horse + "马力";
                        } else if (electricKW > 0 && horse > 0) {
                            enginePower = " " + horse + "马力";
                        }
                        groupTitle.append(enginePower);

                        GetCarSpecPriceByYearIdResponse.SpecItems.Builder specItem = GetCarSpecPriceByYearIdResponse.SpecItems.newBuilder();
                        List<GetCarSpecPriceByYearIdResponse.SpecList> specList = new ArrayList<>();
                        for(SpecPriceViewEntity spec : specs){
                            SpecBaseInfo specBase = specBaseInfoMap.get(spec.getSpecId());
                            specList.add(GetCarSpecPriceByYearIdResponse.SpecList.newBuilder()
                                    .setSpecid(spec.getSpecId())
                                    .setSpecname(specBase != null ? specBase.getSpecName() :"")
                                    .setSpecstate(spec.getSpecState())
                                    .setMinprice(spec.getFctMinPrice())
                                    .setMaxprice(spec.getFctMaxPrice())
                                    .setFueltype(spec.getFuelType())
                                    .setFueltypedetail(spec.getFueltypedetail())
                                    .setDriveform(CommonFunction.driveMode(spec.getDriveForm()))
                                    .setDrivetype(CommonFunction.DriveType(spec.getDriveType()))
                                    .setGearbox(specBase != null && specBase.getGearBox() != null ? specBase.getGearBox(): "")
                                    .setEvflag(spec.getFuelType() == 4 ? "电动" : spec.getFuelType() == 3 ? "混动" : "")
                                    .setNewcarflag("")
                                    .setSubsidy("")
                                    .setSyear(spec.getSyear())
                                    .setParamisshow(((specBase != null && spec.getSpecState() == 40) || (specBase != null && specBase.getIsSpecParamIsShow() == 1)) ? 1 : 0)
                                    .build()
                            );
                        }
                        specItem.setName(groupTitle.toString());
                        specItem.addAllSpeclist(specList);
                        result.add(specItem.build());
                    }
                }
            }
        }
        return builder.addAllResult(result).build();
    }

//    @Override
//    public GetCarSpecParamListBySeriesIdResponse getCarSpecParamListBySeriesId(GetCarSpecParamListBySeriesIdRequest request) {
//        GetCarSpecParamListBySeriesIdResponse.Builder builder = GetCarSpecParamListBySeriesIdResponse.newBuilder();
//        int seriesId = request.getSeriesid();
//        if (seriesId == 0) {
//            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
//        }
//
//        builder.setReturnCode(0).setReturnMsg("成功");
//        GetCarSpecParamListBySeriesIdResponse.Result.Builder result = GetCarSpecParamListBySeriesIdResponse.Result.newBuilder()
//                .setSeriesid(seriesId)
//                .addAllSpeclist(Collections.emptyList())
//                .addAllParamtypeitems(Collections.emptyList());
//
//        List<GetCarSpecParamListBySeriesIdResponse.SpecList> specList = new ArrayList<>();
//        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
//        if(seriesBaseInfo != null){
//            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
//            List<SpecStateEntity> specStates = autoCacheService.getSpecListBySeriesId(seriesId, isCV);
//            if(!CollectionUtils.isEmpty(specStates)){
//                List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
//                Map<Integer,SpecBaseInfo> specInfos = specBaseService.getMap(specIds);
//                for(Integer specId :specIds){
//                    SpecBaseInfo spec = specInfos.get(specId);
//                    if(CollectionUtils.isEmpty(specInfos)){
//                        break;
//                    }
//                    if(spec == null){
//                        continue;
//                    }
//                    //车型列表
//                    GetCarSpecParamListBySeriesIdResponse.SpecList.Builder specShow = GetCarSpecParamListBySeriesIdResponse.SpecList.newBuilder();
//                    int showState = (spec.getSpecState() == 10) ? spec.getIsBooked() : -1;
//                    specList.add(specShow.setSpecid(spec.getId()).setSpecstate(spec.getSpecState()).setShowstate(showState).build());
//                }
//                result.addAllSpeclist(specList);
//
//                List<GetCarSpecParamListBySeriesIdResponse.Paramtypeitems> paramsList = new ArrayList<>();
//                Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(specIds, false);
//                if(paramTypeItems.isPresent()){
//                    int modelId = 1;
//                    Map<String, Integer> configModelMap = paramConfigModelService.getConfMap(modelId);
//                    for(ParamTypeItems item : paramTypeItems.get()){
//                        GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.Builder paramType = GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.newBuilder();
//                        List<GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.Paramitems> paramItems = new ArrayList<>();
//                        paramType.setName(item.getName());
//                        List<ParamTypeItems.ParamItems> params = item.getParamitems();
//                        if(!CollectionUtils.isEmpty(params)){
//                            for(ParamTypeItems.ParamItems param : params){
//                                GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.Paramitems.Builder paramBuild = GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.Paramitems.newBuilder();
//                                paramBuild.setId(param.getId());
//                                paramBuild.setName(param.getName());
//                                int pnidStr = configModelMap != null && configModelMap.containsKey(param.getName()) && configModelMap.get(param.getName()) > 0 ? configModelMap.get(param.getName()) : -1 ;
//                                paramBuild.setPnid(modelId + "_" + pnidStr);
//                                List<GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.Valueitems> valueList = new ArrayList<>();
//                                if(!CollectionUtils.isEmpty(param.getValueitems())){
//                                    for(ParamTypeItems.ValueItems valueItem : param.getValueitems()){
//                                        valueList.add(GetCarSpecParamListBySeriesIdResponse.Paramtypeitems.Valueitems.newBuilder()
//                                                .setSpecid(valueItem.getSpecid())
//                                                .setValue(valueItem.getValue())
//                                                .build()
//                                        );
//                                    }
//                                }
//                                paramBuild.addAllValueitems(valueList);
//                                paramItems.add(paramBuild.build());
//                            }
//                        }
//                        paramType.addAllParamitems(paramItems);
//                        paramsList.add(paramType.build());
//                    }
//                }
//                result.addAllParamtypeitems(paramsList);
//            }
//        }
//        return builder.setResult(result).build();
//    }

    @Override
    public GetCarSeriesNameByFctIdResponse getCarSeriesNameByFctId(GetCarSeriesNameByFctIdRequest request) {
        GetCarSeriesNameByFctIdResponse.Builder resp = GetCarSeriesNameByFctIdResponse.newBuilder();
        if(request.getFctid() == 0){
            return GetCarSeriesNameByFctIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        GetCarSeriesNameByFctIdResponse.Result.Builder result = GetCarSeriesNameByFctIdResponse.Result.newBuilder();
        result.setId(request.getFctid());

        FactoryBaseInfo fctInfo = factoryInfoService.getFactory(request.getFctid());
        if(fctInfo == null){
            return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }

        result.setName(fctInfo.getName());
        result.setLogo(ImageUtil.getFullImagePath(fctInfo.getLogo()));
        result.setOfficialurl(fctInfo.getUrl());

        List<SeriesFctEntity> seriesList = autoCacheService.getSeriesByFctId(request.getFctid());
        if(CollectionUtils.isEmpty(seriesList)){
            return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }
        List<Integer> seriesIds = seriesList.stream().map(SeriesFctEntity::getSeriesId).collect(Collectors.toList());

        Map<Integer,SeriesConfig> seriesMap = seriesConfigService.getMap(seriesIds);
        if(CollectionUtils.isEmpty(seriesMap)){
            return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }

        for(SeriesFctEntity seriesFct : seriesList){
            SeriesConfig seriesItem = seriesMap.get(seriesFct.getSeriesId());
            if(seriesItem == null){
                continue;
            }
            result.addSeriesitems(GetCarSeriesNameByFctIdResponse.Result.Seriesitems.newBuilder()
                    .setId(seriesItem.getId())
                    .setName(seriesItem.getName())
                    .setLevelid(seriesItem.getLevelid())
                    .setLevelname(seriesItem.getLevelname())
                    .setIspublic(seriesFct.getSeriesIspublic())
                    .setSeriesstate(seriesItem.getState())
                    .setPnglogo(seriesItem.getPnglogo())
                    .setMinprice(seriesItem.getMinprice())
                    .setMaxprice(seriesItem.getMaxprice())
            );
        }
        return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }

    @Override
    public GetLabelPicConfigListResponse getLabelPicConfigListBySpecId(GetSpecInfoBySpecIdRequest request) {
        GetLabelPicConfigListResponse.Builder builder = GetLabelPicConfigListResponse.newBuilder();
        GetLabelPicConfigListResponse.Result.Builder result = GetLabelPicConfigListResponse.Result.newBuilder();
        int specId = request.getSpecid();
        result.setSpecid(specId);
        try {
            if (specId == 0) {
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
            }
            if (CommonFunction.checkSpecParamIsShow(Collections.singletonList(specId), specBaseService)) {
                return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
            }

            SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
            if (Objects.isNull(specBaseInfo)) {
                return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
            }
            CompletableFuture<Map<Integer, String>> itemValuesFuture = CompletableFuture.supplyAsync(() -> configItemValueService.get());
            CompletableFuture<List<KeyValueDto<Integer, String>>> configItemFuture = CompletableFuture.supplyAsync(() -> autoCacheService.getAllConfigItem());
            CompletableFuture<List<KeyValueDto<Integer, String>>> configTypeFuture = CompletableFuture.supplyAsync(() -> autoCacheService.getAllConfigType());
            CompletableFuture<Map<Integer, String>> subItemsFuture = CompletableFuture.supplyAsync(() -> configSubItemService.get());
            CompletableFuture<List<ConfItemEntity>> confItemEntitiesFuture = CompletableFuture.supplyAsync(() -> Spec.isCvSpec(specId) ? autoCacheService.getCvSpecConfItem(specId) : autoCacheService.getSpecConfItem(specId));
            CompletableFuture.allOf(itemValuesFuture, configItemFuture, configTypeFuture, subItemsFuture, confItemEntitiesFuture).join();

            Map<Integer, String> itemValuesMap = itemValuesFuture.join();
            List<KeyValueDto<Integer, String>> configItemList = configItemFuture.join();
            Map<Integer, String> configItemMap = configItemList.stream().collect(Collectors.toMap(KeyValueDto::getKey, KeyValueDto::getValue));
            List<KeyValueDto<Integer, String>> configTypeList = configTypeFuture.join();
            Map<Integer, String> configTypeMap = configTypeList.stream().collect(Collectors.toMap(KeyValueDto::getKey, KeyValueDto::getValue));
            Map<Integer, String> subItemsMap = subItemsFuture.join();


            boolean IsPEVCar = specBaseInfo.getFuelTypeDetail() == 4;
            List<ConfItemEntity> confItemEntities = confItemEntitiesFuture.join();
            Map<Integer, List<ConfItemEntity>> typeIdMap = confItemEntities.stream().collect(Collectors.groupingBy(ConfItemEntity::getTypeId));


            List<GetLabelPicConfigListResponse.Result.Configtypeitem> configTypeItems = new ArrayList<>();
            for (Map.Entry<Integer, List<ConfItemEntity>> entry : typeIdMap.entrySet()) {
                int typeId = entry.getKey();
                List<ConfItemEntity> itemEntityList = entry.getValue();
                List<String> gpTypeItemKeys = itemEntityList.stream().map(e -> e.getItemId() + "-" + e.getDisplayType() + "-" + e.getItemValueId()).distinct().collect(Collectors.toList());
                Map<String, List<ConfItemEntity>> gpTypeItemMap = itemEntityList.stream().collect(Collectors.groupingBy(e -> e.getItemId() + "-" + e.getDisplayType() + "-" + e.getItemValueId()));

                List<GetLabelPicConfigListResponse.Result.Configtypeitem.Configitem> configItems = new ArrayList<>();
                for (String gpTypeItemKey : gpTypeItemKeys) {
                    String[] spilt = StringUtils.split(gpTypeItemKey, "-");
                    int configId = Integer.parseInt(spilt[0]);
                    if (IsPEVCar && SpecElectric.dicExcludePEVCarConfig.containsKey(configId)) {
                        continue;
                    }
                    int dispType = Integer.parseInt(spilt[1]);
                    int itemValueId = Integer.parseInt(spilt[2]);
                    List<GetLabelPicConfigListResponse.Result.Configtypeitem.SubList> subList = new ArrayList<>();
                    for (ConfItemEntity subItem : gpTypeItemMap.get(gpTypeItemKey)) {
                        if (NumberUtils.isDigits(subItem.getSubItemId()) && NumberUtils.isDigits(subItem.getSubValue())) {
                            int subItemId = Integer.parseInt(subItem.getSubItemId());
                            int subValue = Integer.parseInt(subItem.getSubValue());
                            subList.add(GetLabelPicConfigListResponse.Result.Configtypeitem.SubList.newBuilder().
                                    setSubitemid(subItemId).setSubname(StringUtils.defaultString(subItemsMap.get(subItemId))).setSubvalue(subValue).build());
                        }
                    }
                    configItems.add(GetLabelPicConfigListResponse.Result.Configtypeitem.Configitem.newBuilder().
                            setConfigid(configId).
                            setName(StringUtils.defaultString(configItemMap.get(configId))).
                            setDisptype(dispType).
                            setValue(dispType == 1 ? "" : itemValuesMap.get(itemValueId)).
                            addAllSublist(subList).build());
                }
                configTypeItems.add(GetLabelPicConfigListResponse.Result.Configtypeitem.newBuilder().setName(StringUtils.defaultString(configTypeMap.get(typeId))).addAllConfigitems(configItems).build());
            }
            result.setSpecid(specId);
            result.addAllConfigtypeitems(configTypeItems);
            return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        } catch (Exception e) {
            log.error("根据车型id获取多个配置信息异常：", e);
            return builder.setResult(result).setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
    }

    /**
     * 根据日期获取当天上传图片相关的车型列表
     * @param request
     * @return
     */
    @Override
    public GetPicSpecListByDateResponse getPicSpecListByDate(GetPicSpecListByDateRequest request) {
        GetPicSpecListByDateResponse.Builder builder = GetPicSpecListByDateResponse.newBuilder();
        String strDate = request.getDate();
        boolean matches = CommonFunction.DATE_PATTERN.matcher(strDate).matches();
        if(!matches){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        Date startDate = LocalDateUtils.parseDate(strDate);
        if(null == startDate){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        Date endDate = LocalDateUtils.addDateDays(startDate, 1);
        List<Integer> specList = autoCacheService.getSpecListByDate(startDate, endDate);
        GetPicSpecListByDateResponse.Result.Builder resultBuilder = GetPicSpecListByDateResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(specList)){
            for(Integer specId : specList){
                resultBuilder.addSpeclist(specId);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    @Override
    public ApiResult<ParamTypeItemPage> getSpecParamListBySpecId(GetSpecParamListBySpecIdRequest request) {
        int specId = request.getSpecid();
        if (specId == 0) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        ParamTypeItemPage paramTypeItemPage = new ParamTypeItemPage();
        paramTypeItemPage.setSpecid(specId);
        List<SpecStateEntity> specStateEntities = specListSameYearBaseService.get(specId).join();
        if (CollectionUtils.isEmpty(specStateEntities)) {
            return new ApiResult<>(paramTypeItemPage, RETURN_MESSAGE_ENUM0);
        }
        List<Integer> allSpecList = specStateEntities.stream().map(SpecStateEntity::getSpecId).collect(Collectors.toList());
        List<Integer> unShowSpecList = specStateEntities.stream().filter(e -> e.getSpecState() != 40).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
        List<Integer> showSpecList = specStateEntities.stream().filter(e -> e.getSpecState() == 40).map(SpecStateEntity::getSpecId).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(unShowSpecList)) {
            Map<Integer, SpecBaseInfo> map = specBaseService.getMap(unShowSpecList);
            Iterator<Integer> iterator = allSpecList.iterator();
            while (iterator.hasNext()) {
                Integer id = iterator.next();
                if (showSpecList.contains(id)) {
                    continue;
                }
                SpecBaseInfo specBaseInfo = !CollectionUtils.isEmpty(map) ? map.get(id) : null;
                if(specBaseInfo != null && specBaseInfo.getIsSpecParamIsShow() == 1) {
                    continue;
                }
                iterator.remove();
            }
        }
        if (CollectionUtils.isEmpty(allSpecList)) {
            return new ApiResult<>(paramTypeItemPage, RETURN_MESSAGE_ENUM0);
        }
        Optional<List<ParamTypeItems>> paramTypeItems = getParamTypeItems(allSpecList, false);

        paramTypeItemPage.setParamtypeitems(paramTypeItems.orElse(Collections.emptyList()));
        return new ApiResult<>(paramTypeItemPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ConfigListByYearIdResponse getConfigListByYearId(ConfigListByYearIdRequest request){
        ConfigListByYearIdResponse.Builder builder = ConfigListByYearIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if (yearId == 0) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        builder.setReturnCode(0).setReturnMsg("成功");
        ConfigListByYearIdResponse.Result.Builder result = ConfigListByYearIdResponse.Result.newBuilder()
                .setSeriesid(seriesId)
                .setYearid(yearId)
                .addAllSpeclist(Collections.emptyList())
                .addAllConfigtypeitems(Collections.emptyList());

        List<ConfigListByYearIdResponse.ConfigSpecItem> specList = new ArrayList<>();
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(seriesBaseInfo != null){
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            //查年代ids
            List<SpecStateEntity> specStates = specListSameYearByYearService.get(seriesId, yearId, isCV).join();
            if(!CollectionUtils.isEmpty(specStates)){
                specStates.sort(Comparator.comparingInt(SpecStateEntity::getSpecOrder)
                        .thenComparing(SpecStateEntity::getOrderBy)
                        .thenComparingInt(SpecStateEntity::getOrders)
                        .thenComparing(SpecStateEntity::getSpecId, Comparator.reverseOrder()));
                List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
                Map<Integer,SpecBaseInfo> specInfos = specBaseService.getMap(specIds);
                for(Integer specId :specIds){
                    SpecBaseInfo spec = specInfos.get(specId);
                    if(CollectionUtils.isEmpty(specInfos)){
                        break;
                    }
                    if(spec == null){
                        continue;
                    }
                    //车型列表
                    ConfigListByYearIdResponse.ConfigSpecItem.Builder specShow = ConfigListByYearIdResponse.ConfigSpecItem.newBuilder();
                    int showState = (spec.getSpecState() == 10) ? spec.getIsBooked() : -1;
                    specList.add(specShow.setSpecid(spec.getId()).setSpecstate(spec.getSpecState()).setShowstate(showState).build());
                }
                result.addAllSpeclist(specList);

                Pair<ReturnMessageEnum, List<ConfigTypeItem>> configRes = commService.getConfigListBySpecList(specIds, request.getDisptype());
                if(configRes.getKey() != RETURN_MESSAGE_ENUM0){
                    return builder.setReturnCode(configRes.getKey().getReturnCode())
                            .setReturnMsg(configRes.getKey().getReturnMsg())
                            .build();
                }
                if(!CollectionUtils.isEmpty(configRes.getValue())){
                    result.addAllConfigtypeitems(configRes.getValue());
                }
            }
        }
        return builder.setResult(result).build();
    }

}