package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Stuct;
import com.autohome.car.api.common.BaseConfig.Transmission;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.*;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.SeriesConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SeriesConfigService extends BaseService<SeriesConfig> {


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

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected boolean getFromDB(){
        return false;
    }

    @Override
    protected SeriesConfig getData(Map<String, Object> params) {
        int seriesId = (int)params.get("seriesId");
        return getSeriesConfig(seriesId);
    }

    public SeriesConfig get(int seriesId) {
        return get(makeParam(seriesId));
    }

    public Map<Integer,SeriesConfig> getMap(List<Integer> seriesIds) {
        List<SeriesConfig> list = getList(seriesIds);
        return list.stream().collect(Collectors.toMap(SeriesConfig::getId, x -> x,(x,y)->x));
    }

    public List<SeriesConfig> getList(List<Integer> seriesIds){
        if(seriesIds==null || seriesIds.size()==0)
            return new ArrayList<>();

        List<Map<String,Object>> params = new ArrayList<>();
        for (Integer id : seriesIds) {
            if(id==null)
                continue;
            params.add(makeParam(id));
        }
        return mGet(params);
    }

    private SeriesConfig getSeriesConfig(int seriesId) {
        AtomicReference<SeriesBaseInfo> baseInfoAR = new AtomicReference<>();
        AtomicReference<SeriesViewEntity> seriesViewAR = new AtomicReference<>();
        AtomicReference<FactoryBaseInfo> factoryBaseInfoAR = new AtomicReference<>();
        AtomicReference<BrandBaseInfo> brandBaseInfoAR = new AtomicReference<>();
        AtomicReference<LevelBaseInfo> levelBaseInfoAR = new AtomicReference<>();
        AtomicReference<List<SeriesPictureEntity>> seriesPicturesAR = new AtomicReference<>();
        AtomicReference<List<SpecViewAndPicEntity>> specsAR = new AtomicReference<>();
        AtomicReference<List<SeriesElectricEntity>> puerElectricAR = new AtomicReference<>();
        AtomicReference<List<ElectricParamEntity>> electricParamAR = new AtomicReference<>();
        AtomicReference<SeriesFuelConsumptionEntity> fuelConsumptionAR = new AtomicReference<>();
        AtomicBoolean paramIsShowAR = new AtomicBoolean(false);
        AtomicBoolean isCV = new AtomicBoolean(false);
        AtomicInteger seriesState = new AtomicInteger(-1);
        AtomicReference<List<SpecColorEntity>> seriesColorAR = new AtomicReference<>();
        AtomicInteger paramNewIsShowAR = new AtomicInteger(0);


        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(seriesBaseService.get(seriesId).thenComposeAsync(baseInfo -> {
            if (baseInfo == null)
                return CompletableFuture.completedFuture(null);
            baseInfoAR.set(baseInfo);
            List<CompletableFuture> cTasks = new ArrayList<>();
            cTasks.add(brandBaseService.get(baseInfo.getBrandId()).thenAccept(x -> {
                brandBaseInfoAR.set(x);
            }));
            isCV.set(Level.isCVLevel(baseInfo.getLevelId()));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCvSpecs(seriesId) : specViewMapper.getSpecs(seriesId)).thenAccept(x -> specsAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCVSeriesElectricBase(seriesId) : specViewMapper.getSeriesElectricBase(seriesId)).thenAccept(x -> puerElectricAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCVElectricParam(seriesId) : specViewMapper.getElectricParam(seriesId)).thenAccept(x -> electricParamAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCVSeriesFuelConsumption(seriesId) : specViewMapper.getSeriesFuelConsumption(seriesId)).thenAccept(x -> fuelConsumptionAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> {
                if (baseInfo.getSeriesState() == 40) {
                    return specViewMapper.isSeriesIsImgSpec(seriesId) <= 0;
                } else {
                    return (isCV.get() ? specViewMapper.getCVSeriesParamIsShow(seriesId) : specViewMapper.getSeriesParamIsShow(seriesId)) > 0;
                }
            }).thenAccept(x -> paramIsShowAR.set(x)));
            //参数外显新逻辑
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCvSeriesParamNewIsShowBySeriesId(seriesId) :
                    specViewMapper.getSeriesParamNewIsShowBySeriesId(seriesId)).thenAccept(x -> paramNewIsShowAR.set(x)));

            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCVSeriesState(seriesId) : specViewMapper.getSeriesState(seriesId)).thenAccept(x -> {
                if (x == null || x.size() == 0) {
                    seriesState.set(-1);
                } else {
                    for (KeyValueDto<Integer, Integer> v : x) {
                        if (seriesState.get() == -1) {
                            seriesState.set(v.getValue());
                        } else if (v.getValue() != 40) {
                            seriesState.set(v.getValue());
                        }
                    }
                }
            }));

            factoryBaseInfoAR.set(factoryBaseService.getFactory(baseInfo.getFactId()));
            levelBaseInfoAR.set(levelBaseService.getLevel(baseInfo.getLevelId()));

            return CompletableFuture.allOf(cTasks.toArray(new CompletableFuture[cTasks.size()]));
        }));

        tasks.add(CompletableFuture.supplyAsync(() -> seriesMapper.getSeriesView(seriesId)).thenAccept(x -> seriesViewAR.set(x)));
        tasks.add(CompletableFuture.supplyAsync(() -> carPhotoViewMapper.getSeriesPicture(seriesId)).thenAccept(x -> seriesPicturesAR.set(x)));
        tasks.add(CompletableFuture.supplyAsync(() -> specColorMapper.getSeriesColor(seriesId)).thenAccept(x -> seriesColorAR.set(x)));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        return getConfig(
                baseInfoAR.get(),
                specsAR.get(),
                factoryBaseInfoAR.get(),
                brandBaseInfoAR.get(),
                seriesViewAR.get(),
                seriesPicturesAR.get(),
                seriesColorAR.get(),
                levelBaseInfoAR.get(),
                puerElectricAR.get(),
                electricParamAR.get(),
                fuelConsumptionAR.get(),
                paramIsShowAR.get(),
                isCV.get(),
                seriesState.get(),
                paramNewIsShowAR.get()
        );
    }

    public void refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        List<SeriesPictureEntity> allSeriesPictures = carPhotoViewMapper.getAllSeriesPicture();
        List<SeriesElectricEntity> allPuerElectrics = specViewMapper.getAllSeriesElectricBase();
        List<ElectricParamEntity> allElectricParam = specViewMapper.getAllElectricParam();
        List<SeriesFuelConsumptionEntity> allFuelConsumption = specViewMapper.getAllSeriesFuelConsumption();
        List<Integer> allParamIsShow = specViewMapper.getAllSeriesParamIsShow();
        List<Integer> allSeriesIsImgSpec = specViewMapper.getALLSeriesIsImgSpec();
        List<KeyValueDto<Integer, Integer>> allSeriesState = specViewMapper.getAllSeriesState();
        List<SpecViewAndPicEntity> allSpecs = specViewMapper.getAllSpecs();
        allSpecs.addAll(specViewMapper.getAllCvSpecs());
        List<SpecColorEntity> allSeriesColor = specColorMapper.getAllSeriesColor(0);
        List<Integer> allParamNewIsShow = specViewMapper.getAllSeriesParamNewIsShow();
        seriesViews.forEach(seriesView -> {
            try {
                int seriesId = seriesView.getSeriesid();

                SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
                boolean paramIsShow = baseInfo.getSeriesState() == 40
                        ? !allSeriesIsImgSpec.contains(seriesId)
                        : allParamIsShow.contains(seriesId);

                boolean isCV = Level.isCVLevel(baseInfo.getLevelId());
                //参数外显新逻辑
                int paramNewIsShow = allParamNewIsShow.stream().filter(x -> x == seriesId).findFirst().orElse(0);

                int seriesState = -1;
                List<KeyValueDto<Integer, Integer>> sl = allSeriesState.stream().filter(x -> x.getKey().equals(seriesId)).collect(Collectors.toList());
                if (sl == null || sl.size() == 0) {
                    seriesState = -1;
                } else {
                    for (KeyValueDto<Integer, Integer> v : sl) {
                        if (seriesState == -1) {
                            seriesState = (v.getValue());
                        } else if (v.getValue() != 40) {
                            seriesState = (v.getValue());
                        }
                    }
                }

                SeriesConfig config = getConfig(
                        seriesBaseService.get(seriesId).join(),
                        allSpecs.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        factoryBaseService.getFactory(baseInfo.getFactId()),
                        brandBaseService.get(baseInfo.getBrandId()).join(),
                        seriesView,
                        allSeriesPictures.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        allSeriesColor.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        levelBaseService.getLevel(baseInfo.getLevelId()),
                        allPuerElectrics.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        allElectricParam.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        allFuelConsumption.stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null),
                        paramIsShow,
                        isCV,
                        seriesState,
                        paramNewIsShow
                );

                refresh(makeParam(seriesId),config);
            }catch (Exception e){
                log.accept(ExceptionUtil.getStackTrace(e));
            }
        });
    }

    Map<String, Object> makeParam(int seriesId){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("seriesId", seriesId);
        return params;
    }

    public static SeriesConfig getConfig(
            SeriesBaseInfo baseInfo,
            List<SpecViewAndPicEntity> specs,
            FactoryBaseInfo factoryBaseInfo,
            BrandBaseInfo brandBaseInfo,
            SeriesViewEntity seriesView,
            List<SeriesPictureEntity> seriesPictures,
            List<SpecColorEntity> specColors,
            LevelBaseInfo levelBaseInfo,
            List<SeriesElectricEntity> puerElectric,
            List<ElectricParamEntity> electricParam,
            SeriesFuelConsumptionEntity fuelConsumption,
            boolean paramIsShow,
            boolean isCV,
            int state,
            int paramNewIsShow
    ){
        if (baseInfo == null)
            return null;
        if (specs == null || specs.size() == 0){
            return null;
        }
        List<SpecViewAndPicEntity> onSellSpecs = specs.stream().filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).collect(Collectors.toList());

        Map<Integer,Integer> ss = new LinkedHashMap<>();
        specs.forEach(x->{
            ss.put(x.getSpecId(),x.getSpecState());
        });

        SeriesConfig config = new SeriesConfig();
        config.setId(baseInfo.getId());
        config.setSeriesRelationSeriesId(baseInfo.getRId());
        config.setName(baseInfo.getName());
        config.setMaxprice(seriesView.getSeriesPriceMax());
        config.setMinprice(seriesView.getSeriesPriceMin());
        config.setState(seriesView.getSeriesState());
        config.setSeriesConfigFilePath(seriesView.getSeriesConfigFilePath());
        config.setSeriesOfficialUrl(baseInfo.getUrl());
        config.setFctid(baseInfo.getFactId());
        config.setFctname(factoryBaseInfo ==null ? "" : factoryBaseInfo.getName());
        config.setBrandid(baseInfo.getBrandId());
        config.setBrandname(brandBaseInfo.getName());
        config.setLevelid(baseInfo.getLevelId());
        config.setLevelname(levelBaseInfo.getName());
        //参数配置外显新逻辑
        config.setParamnewisshow(paramNewIsShow > 0 ? 1 : 0);

        List<String> structList = onSellSpecs.stream().filter(x -> x.getSpecStructureType() != null)
                .sorted(Comparator.comparing(x -> Stuct.StructOrderBy(x.getSpecStructureType(),isCV)))
                .map(x -> isCV ? Stuct.getCVName(Integer.parseInt(x.getSpecStructureType())) : Stuct.getName(x.getSpecStructureType()))
                .distinct().filter(x-> StringUtils.isNotBlank(x)).collect(Collectors.toList());
        config.setStructitems(structList);

        List<String> transmissionList = onSellSpecs.stream().filter(x -> x.getSpecTransmissionType() != null)
                .sorted(Comparator.comparing( x-> CommonFunction.transmissionOrderBy(x.getSpecTransmissionType(),isCV)))
                .map(x -> isCV ? Transmission.getCVTransmission(Integer.parseInt(x.getSpecTransmissionType())) : Transmission.shortName(x.getSpecTransmissionType()))
                .distinct().filter(x->StringUtils.isNotBlank(x)).collect(Collectors.toList());
        config.setTransmissionitems(transmissionList);
        int tempMaxPrice, tempMinPrice;
        if (!isCV) {
            tempMaxPrice = onSellSpecs.stream().map(SpecViewAndPicEntity::getMinPrice).max(Integer::compareTo).orElse(0);
        } else {
            tempMaxPrice = onSellSpecs.stream().map(SpecViewAndPicEntity::getMaxPrice).max(Integer::compareTo).orElse(0);
        }
        tempMinPrice = tempMaxPrice == 0 ? 0 : onSellSpecs.stream().map(SpecViewAndPicEntity::getMinPrice).filter(minPrice -> minPrice !=0).min(Integer::compareTo).orElse(0);
        config.setTempMaxPrice(tempMaxPrice);
        config.setTempMinPrice(tempMinPrice);
        config.setTempState(state);

        config.setDisplacementitems(getDisplacement(onSellSpecs));

        List<String> imgList = seriesPictures.stream().sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex)).map(x -> ImageUtil.getFullImagePath(x.getPhotoFilepath().replace("~", ""))).collect(Collectors.toList());
        config.setPicitems(imgList);
        List<SeriesConfig.SeriesLogo> logos = seriesPictures.stream().sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                .map(x -> {return new SeriesConfig.SeriesLogo(x, ss.containsKey(x.getSpecid())?ss.get(x.getSpecid()):0);})
                .collect(Collectors.toList());
        config.setPicinfoitems(logos);
        List<SpecColorEntity> colorList = isCV ? Collections.emptyList() :
                specColors.stream().
                        filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).
                        sorted(Comparator.comparing(SpecColorEntity::getPicNum).reversed()).
                        collect(Collectors.toList());
        config.setColorList(SpecColorInfo.getSpecColorInfoList(colorList));

        config.setSpecnum(onSellSpecs.size());
        config.setSellspecnum((int) specs.stream().filter(x -> x.getSpecState() >= 20 && x.getSpecState() <= 30).count());
        config.setStopspecnum((int) specs.stream().filter(x -> x.getSpecState() == 40).count());
        config.setWaitspecnum((int) specs.stream().filter(x -> x.getSpecState() == 10).count());
        config.setPicnum(onSellSpecs.stream().mapToInt(SpecViewAndPicEntity::getPicNumber).sum());
        config.setPicallnum(specs.stream().mapToInt(SpecViewAndPicEntity::getPicNumber).sum());

        if (fuelConsumption != null) {
            config.setMinfuelconsumption(fuelConsumption.getMinFuelConsumption().setScale(0, BigDecimal.ROUND_HALF_EVEN).intValue());
            config.setMaxfuelconsumption(fuelConsumption.getMaxFuelConsumption().setScale(0,BigDecimal.ROUND_HALF_EVEN).intValue());
            config.setMinoilwear(fuelConsumption.getMinFuelConsumption());
            config.setMaxoilwear(fuelConsumption.getMaxFuelConsumption());
        }else{
            config.setMinoilwear(new BigDecimal(0));
            config.setMaxoilwear(new BigDecimal(0));
        }

        config.setIsshow(specs.stream().findFirst().get().getSeriesIsShow());

        config.setParamisshow(paramIsShow ? 1 : 0);

        config.setExistmaintain(baseInfo.getEm());

        puerElectric = puerElectric.stream().filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).collect(Collectors.toList());
        config.setShowelectricparam(puerElectric.size() <= 0 || puerElectric.stream().anyMatch(x -> x.getPureelectric() != 1) ? 0 : 1);

        electricParam = electricParam.stream().filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).collect(Collectors.toList());
        List<Integer> electricmotormileage = electricParam.stream().sorted(Comparator.comparing(ElectricParamEntity::getElectricMotorMileage).reversed()).map(x -> x.getElectricMotorMileage()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).distinct().map(x->Integer.parseInt(x)).collect(Collectors.toList());
        config.setElectricmotormileage(electricmotormileage);

        List<Double> lectricmotorkw = electricParam.stream().sorted(Comparator.comparing(ElectricParamEntity::getElectricKW).reversed()).map(x -> x.getElectricKW()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).distinct().map(x->Double.parseDouble(x)).collect(Collectors.toList());
        config.setElectricmotorkw(lectricmotorkw);

        config.setElectricchargetime(electricParam.stream().filter(x -> StringUtils.isNotBlank(x.getElectricKW()) && !x.getElectricKW().equals("-")).sorted(Comparator.comparing(ElectricParamEntity::getElectricKW).reversed()).map(x -> x.getChargeTime()).findFirst().orElse(""));

        List<Double> electricRONGLIANG = electricParam.stream().map(x -> x.getElectricRONGLIANG()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).map(x->Double.parseDouble(x)).distinct().collect(Collectors.toList());
        config.setElectricrongliang(electricRONGLIANG);

        config.setCreatetime(seriesView.getSeriesCreateTime() == null ? "" : new SimpleDateFormat("yyyy/M/d H:mm:ss").format(seriesView.getSeriesCreateTime()));

        config.setContainbookedspec(baseInfo.getCb());
        config.setContainstopspec(baseInfo.getCs());
        config.setNewenergy(seriesView.getSeriesisnewenergy());
        config.setLogo(ImageUtil.getFullImagePath(baseInfo.getLogo()));
        config.setPnglogo(ImageUtil.getFullImagePath(baseInfo.getNoBgLogo()));
        config.setPricedescription(seriesView.getPricedescription());
        config.setSeriesplace(seriesView.getSeriesplace());
        config.setBbsShow(baseInfo.getBbsShow());
        config.setLevelRank(baseInfo.getLevelRank());
        config.setSeriesConfigFilePath(seriesView.getSeriesConfigFilePath());
        return config;
    }


    public static List<String> getDisplacement(List<SpecViewAndPicEntity> onSellSpecs) {
        return onSellSpecs.stream()
                .filter(x -> x != null && x.getSpecDisplacement() != null && x.getFlowMode() > 0 && x.getSpecDisplacement().compareTo(new BigDecimal(0)) > 0)
                .sorted(Comparator.comparing(SpecViewAndPicEntity::getSpecDisplacement).thenComparing(SpecViewAndPicEntity::getFlowMode))
                .map(x -> {
                    String v = new DecimalFormat("#0.0").format(x.getSpecDisplacement().setScale(1,BigDecimal.ROUND_HALF_UP));
                    if (x.getFlowMode() <= 1) {
                        return v.concat("L");
                    } else {
                        return v.concat("T");
                    }
                }).distinct().collect(Collectors.toList());
    }
}
