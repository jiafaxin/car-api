package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.electric.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.SpecStateEnum;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.ElectricSpecViewMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.ElectricService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.basic.models.BrandDicInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.series.SeriesInfoService;
import com.autohome.car.api.services.basic.series.SeriesViewElectricService;
import com.autohome.car.api.services.basic.specs.SpecChargeService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;


@Service
public class ElectricServiceImpl implements ElectricService {

    @Resource
    private SpecChargeService specChargeService;

    @Resource
    private ElectricSpecViewBaseService electricSpecViewBaseService;

    @Resource
    private SeriesViewElectricService seriesViewElectricService;

    @Resource
    private SeriesInfoService seriesInfoService;

    @Resource
    private SeriesConfigService seriesConfigService;

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private SpecViewBrandService specViewBrandService;

    @Resource
    private ElectricBrandService electricBrandService;

    @Resource
    private CommService commService;

    @Resource
    private AutoCacheService autoCacheService;

    @Resource
    private FactoryBaseService factoryBaseService;

    @Resource
    private ElectricSpecViewMapper electricSpecViewMapper;

    @Resource
    private BrandBaseService brandBaseService;


    @Override
    public ApiResult<List<ElectricParam>> getElectricParamBySeriesId(GetElectricParamBySeriesIdRequest request) {
        if (!CommonFunction.check(Collections.singletonList(request.getSeriesid()))) {
            return new ApiResult<>(null, RETURN_MESSAGE_ENUM102);
        }
        List<EleSpecViewBaseEntity> eleSpecViewSimpList = electricSpecViewBaseService.get(request.getSeriesid()).join();

        List<ElectricParam> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(eleSpecViewSimpList)) {
            return new ApiResult<>(result, RETURN_MESSAGE_ENUM0);
        }
        List<SpecConfigChargeEntity> allChargeTime = specChargeService.get(request.getSeriesid()).join().stream().distinct().collect(Collectors.toList());
        List<SpecConfigChargeEntity> fastChargeTime = Collections.emptyList();
        List<SpecConfigChargeEntity> slowChargeTime = Collections.emptyList();
        if (!CollectionUtils.isEmpty(allChargeTime)) {
            fastChargeTime = allChargeTime.stream().filter(entity -> StringUtils.equals(entity.getNm(), "快充时间(小时)")).collect(Collectors.toList());
            slowChargeTime = allChargeTime.stream().filter(entity -> StringUtils.equals(entity.getNm(), "慢充时间(小时)")).collect(Collectors.toList());
        }

        List<String> horsepowerList = eleSpecViewSimpList.stream().map(EleSpecViewBaseEntity::getHorsepower).distinct().collect(Collectors.toList());
        result.add(ElectricParam.builder().key("马力").type(1).value(horsepowerList).build());

        List<String> mileageList = eleSpecViewSimpList.stream().map(EleSpecViewBaseEntity::getMileage).distinct().collect(Collectors.toList());
        result.add(ElectricParam.builder().key("续航").type(2).value(mileageList).build());

        List<String> fastCharge = fastChargeTime.stream().map(SpecConfigChargeEntity::getCt).filter(ct -> !StringUtils.equals(ct, "0")).distinct().collect(Collectors.toList());
        result.add(ElectricParam.builder().key("快充电时间").type(3).value(fastCharge).build());

        List<String> slowCharge = slowChargeTime.stream().map(SpecConfigChargeEntity::getCt).filter(ct -> !StringUtils.equals(ct, "0")).distinct().collect(Collectors.toList());
        result.add(ElectricParam.builder().key("慢充电时间").type(4).value(slowCharge).build());

        return new ApiResult<>(result, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public ApiResult<ElectricSeriesItemPage> getElectricSeriesListByBrandId(GetElectricSeriesListByBrandIdRequest request) {
        List<SeriesViewElectricEntity> list = seriesViewElectricService.get(request.getBrandId());
        List<ElectricSeriesItem> seriesList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            boolean isCopy = list.stream().anyMatch(e -> {
                String scoresRanks = e.getScoresRanks();
                return StringUtils.isBlank(scoresRanks) || !NumberUtils.isDigits(scoresRanks);
            });
            if (isCopy) {
                list = ToolUtils.deepCopyList(list);
                int num = -10000;
                for (SeriesViewElectricEntity e : list) {
                    String scoresRanks = e.getScoresRanks();
                    if (StringUtils.isBlank(scoresRanks) || !NumberUtils.isDigits(scoresRanks)) {
                        e.setScoresRanks(String.valueOf(num++));
                    }
                }
            }
            Map<Integer, List<SeriesViewElectricEntity>> result = list.stream().collect(Collectors.groupingBy(SeriesViewElectricEntity::getSeriesId));
            Set<Map.Entry<Integer, List<SeriesViewElectricEntity>>> entries = result.entrySet();
            for (Map.Entry<Integer, List<SeriesViewElectricEntity>> entry : entries) {
                int seriesId = entry.getKey();
                SeriesInfo seriesInfo = seriesInfoService.get(seriesId, false, false);
                SeriesConfig seriesConfig = seriesConfigService.get(seriesId);
                SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
                seriesList.add(ElectricSeriesItem.buildElectricSeriesItem(seriesInfo, seriesConfig, seriesBaseInfo, entry.getValue()));
            }
            if (!CollectionUtils.isEmpty(seriesList)) {
                seriesList = seriesList.stream().sorted(Comparator.comparingInt(ElectricSeriesItem::getOrderSeriesState).thenComparing(ElectricSeriesItem::getScoresRanks)).
                        collect(Collectors.toList());
            }
        }
        ElectricSeriesItemPage electricSeriesItemPage = new ElectricSeriesItemPage();
        electricSeriesItemPage.setList(seriesList);
        return new ApiResult<>(electricSeriesItemPage, RETURN_MESSAGE_ENUM0);
    }

    @Override
    public GetElectricBrandListResponse getElectricBrandList(GetElectricBrandListRequest request) {
        GetElectricBrandListResponse.Builder builder = GetElectricBrandListResponse.newBuilder();

        List<NewEnergyBrandEntity> brandEntities = electricBrandService.getAll();
        Map<String, List<BrandDicInfo>> map = specViewBrandService.getMap();

        List<String> cacheKey = specViewBrandService.getCacheKey();
        Map<Integer, String> dicBrandFuelType = map.get(cacheKey.get(0)).stream().collect(Collectors.toMap(BrandDicInfo::getBId, BrandDicInfo::getValue));
        Map<Integer, String> dicEnduranceMileage = map.get(cacheKey.get(1)).stream().collect(Collectors.toMap(BrandDicInfo::getBId, BrandDicInfo::getValue));
        Map<Integer, String> dicFastChargeTime = map.get(cacheKey.get(2)).stream().collect(Collectors.toMap(BrandDicInfo::getBId, BrandDicInfo::getValue));
        List<GetElectricBrandListResponse.Result.Branditem> list = new ArrayList<>();
        for (NewEnergyBrandEntity brandEntity : brandEntities) {
            if (Objects.isNull(brandEntity)) {
                continue;
            }
            String[] split = StringUtils.split(dicBrandFuelType.get(brandEntity.getBId()), ",");
            List<Integer> flueType = Collections.emptyList();
            if (split != null && split.length > 0) {
                List<String> stringList = Arrays.asList(split);
                flueType = stringList.stream().map(Integer::parseInt).collect(Collectors.toList());
            }
            String fastChargeTimeStr = dicFastChargeTime.get(brandEntity.getBId());
            double fastChargeTime = StringUtils.isBlank(fastChargeTimeStr) ? 0.0 : Double.parseDouble(fastChargeTimeStr);
            String maxMileageStr = dicEnduranceMileage.get(brandEntity.getBId());
            int maxMileage = StringUtils.isBlank(maxMileageStr) && !NumberUtils.isDigits(maxMileageStr) ? 0 : Integer.parseInt(maxMileageStr);
            GetElectricBrandListResponse.Result.Branditem build = GetElectricBrandListResponse.Result.Branditem.newBuilder().setId(brandEntity.getBId()).
                    setName(brandEntity.getBn()).
                    setLogo(ImageUtil.getFullImagePathNew(brandEntity.getBi(), false)).
                    setFirstletter(StringUtils.defaultString(brandEntity.getBfl())).
                    setState(brandEntity.getHs()).
                    setCountry(StringUtils.defaultString(brandEntity.getCountry())).
                    addAllFueltype(flueType).
                    setMaxmileage(maxMileage).
                    setFastestchargetime(fastChargeTime).build();

            list.add(build);
        }
        Map<String, List<GetElectricBrandListResponse.Result.Branditem>> collect = list.stream().collect(Collectors.groupingBy(GetElectricBrandListResponse.Result.Branditem::getFirstletter));
        for (Map.Entry<String, List<GetElectricBrandListResponse.Result.Branditem>> e : collect.entrySet()) {
            GetElectricBrandListResponse.Result.Builder result = GetElectricBrandListResponse.Result.newBuilder();
            result.setFirstletter(e.getKey()).addAllBranditems(e.getValue());
            builder.addResult(result);
        }
        return builder.setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    /**
     * 根据车系id列表获取车系下所有燃料类型
     * @param request
     * @return
     */
    @Override
    public ApiResult<List<FuelTypeItem>> getFuelTypeBySeriesList(GetFuelTypeBySeriesListRequest request) {
        List<Integer> seriesIds = CommonFunction.getListFromStr(request.getSeriesids());
        if(CollectionUtils.isEmpty(seriesIds)){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        SpecStateEnum specState = Spec.getSpecState(request.getState());
        if(!(specState == SpecStateEnum.NO_SELL || specState == SpecStateEnum.WAIT_SELL ||specState == SpecStateEnum.SELL ||
                specState == SpecStateEnum.SELL_IN_STOP ||specState == SpecStateEnum.SELL_12 ||specState == SpecStateEnum.STOP_SELL ||
                specState == SpecStateEnum.SELL_15 ||specState == SpecStateEnum.SELL_28 ||specState == SpecStateEnum.SELL_31)){
            return new ApiResult<>(null,RETURN_MESSAGE_ENUM102);
        }
        List<EleSpecViewBaseEntity> eleSpecViewBaseEntities = commService.getEleSpecViewBaseEntities(seriesIds);
        if(!CollectionUtils.isEmpty(eleSpecViewBaseEntities)){
            switch (specState){
                case NO_SELL:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() == 0).collect(Collectors.toList());
                    break;
                case WAIT_SELL:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() == 10).collect(Collectors.toList());
                    break;
                case SELL:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() == 20).collect(Collectors.toList());
                    break;
                case SELL_IN_STOP:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() == 30).collect(Collectors.toList());
                    break;
                case SELL_12:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() >= 20 && eleSpecViewBaseEntity.getSpecState() <= 30).collect(Collectors.toList());
                    break;
                case STOP_SELL:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() == 40).collect(Collectors.toList());
                    break;
                case SELL_15:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() <= 30).collect(Collectors.toList());
                    break;
                case SELL_28:
                    eleSpecViewBaseEntities = eleSpecViewBaseEntities.stream().filter(eleSpecViewBaseEntity ->
                            eleSpecViewBaseEntity.getSpecState() >= 20).collect(Collectors.toList());
                    break;
            }
        }
        Map<Integer,List<EleSpecViewBaseEntity>> eleSpecViewMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(eleSpecViewBaseEntities)){
            eleSpecViewMap = eleSpecViewBaseEntities.stream().collect(Collectors.groupingBy(EleSpecViewBaseEntity::getSeriesId));
        }
        List<FuelTypeItem> fuelTypeItems = new ArrayList<>();
        for(int seriesId : seriesIds){
            FuelTypeItem fuelTypeItem = new FuelTypeItem();
            List<String> fuelTypeNames = new ArrayList<>();
            List<EleSpecViewBaseEntity> specViewBaseEntities = eleSpecViewMap.get(seriesId);
            if(!CollectionUtils.isEmpty(specViewBaseEntities)){
                List<Integer> fuelTypes = specViewBaseEntities.stream().map(EleSpecViewBaseEntity::getFuelType).distinct().collect(Collectors.toList());
                fuelTypes.forEach(fuelType ->{
                    String fuelTypeName = CommonFunction.electricFuelTypeName(fuelType);
                    if(StringUtils.isNotBlank(fuelTypeName)){
                        fuelTypeNames.add(fuelTypeName);
                    }
                });
            }
            fuelTypeItem.setSeriesid(seriesId);
            fuelTypeItem.setFueltyplist(fuelTypeNames);
            fuelTypeItems.add(fuelTypeItem);
        }
        return new ApiResult<>(fuelTypeItems,RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据品牌id和其他条件获取车系信息
     * @param request
     * @return
     */
    @Override
    public GetElectricSeriesListByBrandIdAndOtherResponse getElectricSeriesListByBrandIdAndOther(GetElectricSeriesListByBrandIdAndOtherRequest request) {
        GetElectricSeriesListByBrandIdAndOtherResponse.Builder builder = GetElectricSeriesListByBrandIdAndOtherResponse.newBuilder();
        int brandId = request.getBrandid();
        int isFilterSpecImage = request.getIsFilterSpecImage();
        int typeId = request.getTypeid();
        SpecStateEnum stateEnum = Spec.getSpecState(request.getState());
        typeId = typeId > 2 ? 0 : typeId;
        if (brandId == 0 || stateEnum == SpecStateEnum.NONE) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<ElectricSpecViewEntity> electricSpecViewEntities = autoCacheService.getElectricInfoByBrandId(brandId);
        GetElectricSeriesListByBrandIdAndOtherResponse.Result.Builder resultBuilder = GetElectricSeriesListByBrandIdAndOtherResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(electricSpecViewEntities)){
            //过滤
            electricSpecViewEntities = this.electricSpecViewEntityFilter(electricSpecViewEntities,stateEnum,isFilterSpecImage,typeId);
            List<Integer> seriesIds = electricSpecViewEntities.stream().map(ElectricSpecViewEntity::getSeriesId).distinct().collect(Collectors.toList());
            //车系信息
            Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);

            List<SeriesConfig> seriesConfigs = seriesConfigService.getList(seriesIds);
            //分组后还是原来的顺序
            Map<Integer, List<ElectricSpecViewEntity>> electricSpecMap = electricSpecViewEntities.stream().collect(Collectors.groupingBy(electricSpecViewEntity ->
                    electricSpecViewEntity.getFctId(),LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
            for(Map.Entry<Integer, List<ElectricSpecViewEntity>> electricSpecViewEntityMap : electricSpecMap.entrySet()){
                Integer fctId = electricSpecViewEntityMap.getKey();
                GetElectricSeriesListByBrandIdAndOtherResponse.FactoryItem.Builder factoryItem = GetElectricSeriesListByBrandIdAndOtherResponse.FactoryItem.newBuilder();
                factoryItem.setFctid(fctId);
                factoryItem.setFctname(factoryBaseService.getName(fctId));
                Map<Integer, List<ElectricSpecViewEntity>> seriesMap = electricSpecViewEntityMap.getValue().stream().collect(Collectors.groupingBy(electricSpecViewEntity ->
                        electricSpecViewEntity.getSeriesId(),LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                for(Map.Entry<Integer, List<ElectricSpecViewEntity>> eleSeriesMap : seriesMap.entrySet()){
                    GetElectricSeriesListByBrandIdAndOtherResponse.SeriesItem.Builder seriesItem = GetElectricSeriesListByBrandIdAndOtherResponse.SeriesItem.newBuilder();
                    Integer seriesId = eleSeriesMap.getKey();
                    seriesItem.setSeriesid(seriesId);
                    SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(seriesId);
                    if(null != seriesBaseInfo){
                        seriesItem.setSeriesname(StringUtils.defaultString(seriesBaseInfo.getName()));
                        seriesItem.setSerieslogo(ImageUtil.getFullImagePath(StringUtils.defaultString(seriesBaseInfo.getLogo())));
                        seriesItem.setSeriespnglogo(ImageUtil.getFullImagePath(seriesBaseInfo.getNoBgLogo()));
                        seriesItem.setState(seriesBaseInfo.getSeriesState());
                    }
                    if(!CollectionUtils.isEmpty(seriesConfigs)){
                        SeriesConfig seriesConfig = seriesConfigs.stream().filter(x -> x.getId() == seriesId).findFirst().orElse(null);
                        seriesItem.setMinprice(null != seriesConfig ? seriesConfig.getMinprice() : 0);
                        seriesItem.setMaxprice(null != seriesConfig ? seriesConfig.getMaxprice() : 0);
                        seriesItem.addAllEndurancemileage(null != seriesConfig ? seriesConfig.getElectricmotormileage() : new ArrayList<>());
                    }
                    seriesItem.setSeriesorder(eleSeriesMap.getValue().get(0).getSeriesOrderCls());
                    List<Integer> fuelTypes = eleSeriesMap.getValue().stream().map(ElectricSpecViewEntity::getFuelType).distinct().collect(Collectors.toList());
                    fuelTypes.forEach(fuelType ->{
                        GetElectricSeriesListByBrandIdAndOtherResponse.FuelItem.Builder fuelItem = GetElectricSeriesListByBrandIdAndOtherResponse.FuelItem.newBuilder();
                        fuelItem.setId(fuelType == 4 ? 3 : fuelType);
                        fuelItem.setName(CommonFunction.electricFuelTypeName(fuelType));
                        seriesItem.addFueltype(fuelItem);
                    });
                    factoryItem.addSeriesitems(seriesItem);
                }
                resultBuilder.addFactoryitems(factoryItem);
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    private List<ElectricSpecViewEntity> electricSpecViewEntityFilter(List<ElectricSpecViewEntity> electricSpecViewEntities,SpecStateEnum stateEnum,int isFilterSpecImage,int typeId){
        switch (stateEnum){
            case NO_SELL:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() == 0).collect(Collectors.toList());
                break;
            case WAIT_SELL:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() == 10).collect(Collectors.toList());
                break;
            case SELL:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() == 20).collect(Collectors.toList());
                break;
            case SELL_IN_STOP:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() == 30).collect(Collectors.toList());
                break;
            case STOP_SELL:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() == 40).collect(Collectors.toList());
                break;
            case SELL_3:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() <= 10).collect(Collectors.toList());
                break;
            case SELL_12:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() >= 20 && electricSpecViewEntity.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() >= 10 && electricSpecViewEntity.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_28:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() >= 20).collect(Collectors.toList());
                break;
            case SELL_15:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_30:
                electricSpecViewEntities = electricSpecViewEntities.stream().
                        filter(electricSpecViewEntity -> electricSpecViewEntity.getSpecState() >= 10).collect(Collectors.toList());
                break;
        }
        if(isFilterSpecImage == 1){
            electricSpecViewEntities = electricSpecViewEntities.stream().filter(electricSpecViewEntity ->
                    electricSpecViewEntity.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        if(typeId > 0){
            if(typeId == 1){
                electricSpecViewEntities = electricSpecViewEntities.stream().filter(electricSpecViewEntity ->
                        electricSpecViewEntity.getSpecState() <= 9 || electricSpecViewEntity.getSpecState() >= 15).collect(Collectors.toList());
            }else{
                electricSpecViewEntities = electricSpecViewEntities.stream().filter(electricSpecViewEntity ->
                        electricSpecViewEntity.getSpecState() >= 11 && electricSpecViewEntity.getSpecState() <= 14).collect(Collectors.toList());
            }
        }
        return electricSpecViewEntities;
    }

    @Override
    public GetElectricSeriesListResponse getElectricSeriesList(GetElectricSeriesListRequest request) {
        GetElectricSeriesListResponse.Builder builder = GetElectricSeriesListResponse.newBuilder();
        List<EleSeriesViewEntity> list = electricSpecViewMapper.getAllElectricSeries();
        if(CollectionUtils.isEmpty(list)){
            return builder.setReturnCode(0).setReturnMsg("成功").build();
        }
        List<Integer> seriesIds = list.stream().map(EleSeriesViewEntity::getSeriesId).collect(Collectors.toList());
        List<Integer> brandIds = list.stream().map(EleSeriesViewEntity::getBrandId).collect(Collectors.toList());
        CompletableFuture<Map<Integer, SeriesConfig>> seriesFuture = CompletableFuture.supplyAsync(() -> seriesConfigService.getMap(seriesIds));
        CompletableFuture<Map<Integer, BrandBaseInfo>> brandFuture = CompletableFuture.supplyAsync(() -> brandBaseService.getMap(brandIds));
        CompletableFuture.allOf(seriesFuture, brandFuture);
        Map<Integer,SeriesConfig> seriesMap = seriesFuture.join();
        Map<Integer, BrandBaseInfo> brandMap = brandFuture.join();

        Map<Integer, List<EleSeriesViewEntity>> groupedAndSortedMap = list.stream()
//                .sorted(Comparator.comparing(EleSeriesViewEntity::getSeriesId))
                .collect(Collectors.groupingBy(EleSeriesViewEntity::getSeriesId, LinkedHashMap::new, Collectors.toList()));
        Map<Integer, List<EleSeriesViewEntity>> sortedSeriesMap = new LinkedHashMap<>(groupedAndSortedMap);
        List<GetElectricSeriesListResponse.SeriesList> electricSeriesList = new ArrayList<>();
        for (Map.Entry<Integer, List<EleSeriesViewEntity>> entry : sortedSeriesMap.entrySet()) {
            Integer seriesId = entry.getKey();
            List<EleSeriesViewEntity> entities = entry.getValue();
            SeriesConfig series = CollectionUtils.isEmpty(seriesMap) ? null : seriesMap.getOrDefault(seriesId, null);
            BrandBaseInfo brand = series == null || CollectionUtils.isEmpty(brandMap) ? null : brandMap.getOrDefault(series.getBrandid(), null);
            int seriesstate =  entities.size() > 0 ? entities.get(0).getSeriesState() : 0;
            String brandfirstletter = brand != null && brand.getFirstLetter() != null ? brand.getFirstLetter() : "";

            GetElectricSeriesListResponse.SeriesList.Builder electricSeriesItem =  GetElectricSeriesListResponse.SeriesList.newBuilder();
            if (!CollectionUtils.isEmpty(entities)) {
                List<String> fuelTypes = entities.stream().map(EleSeriesViewEntity -> CommonFunction.electricFuelTypeName(EleSeriesViewEntity.getFuelType())).filter(StringUtils::isNoneBlank).collect(Collectors.toList());
                electricSeriesItem.addAllSeriesdescribe(fuelTypes);
            }
            electricSeriesItem.setSeriesstate(seriesstate);

            if (Objects.nonNull(series)) {
                electricSeriesItem.setBrandid(series.getBrandid());
                electricSeriesItem.setBrandname(series.getBrandname());
                electricSeriesItem.setSeriesid(seriesId);
                electricSeriesItem.setSeriesname(series.getName());
                electricSeriesItem.setMaxprice(series.getTempMaxPrice());
                electricSeriesItem.setMinprice(series.getTempMinPrice());
                electricSeriesItem.addAllEndurancemileage(series.getElectricmotormileage());
                electricSeriesItem.setSeriespnglogo(series.getPnglogo() != null ? series.getPnglogo() : "");
                electricSeriesItem.setSerieslogo(series.getLogo() != null ? series.getLogo() : "");
                electricSeriesItem.setSubsidymaxprice(0);
                electricSeriesItem.setSubsidyminprice(0);
            }
            electricSeriesItem.setBrandletter(brandfirstletter);

            electricSeriesList.add(electricSeriesItem.build());
        }
//        electricSeriesList = electricSeriesList.stream()
//                .sorted(Comparator.comparing(GetElectricSeriesListResponse.SeriesList::getBrandid)
//                        .thenComparing(GetElectricSeriesListResponse.SeriesList::getSeriesid))
//                .collect(Collectors.toList());
        GetElectricSeriesListResponse.Result.Builder result = GetElectricSeriesListResponse.Result.newBuilder();
        result.addAllList(electricSeriesList);
        return builder.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }
}
