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
//import com.autohome.car.api.services.models.SeriesYearStateConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeriesYearStateConfigService extends BaseService<SeriesConfig> {


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
        return true;
    }

    @Override
    protected SeriesConfig getData(Map<String, Object> params) {
        int seriesId = (int)params.get("seriesId");
        int yearId = (int)params.get("yearId");
        int state = (int)params.get("state");
//        return get(seriesId,yearId,state);
        return null;
    }

    public SeriesConfig get(int seriesId,int yearId,int state) {
        return get(makeParam(seriesId,yearId,state));
    }

    public SeriesConfig getSeriesConfig(int seriesid,int yearid,int stateid) {
        //SpecViewEntity
        Function<SpecViewAndPicEntity, List<Object>> sel = v -> Arrays.<Object>asList(v.getSeriesId(), v.getSyearId(), v.getState());

        List<SpecViewAndPicEntity> seriesYearState = specViewMapper.getAllSpecYearStateList();
        seriesYearState.addAll(specViewMapper.getAllCVSpecYearStateList());

        seriesYearState.forEach(v -> {
            if (v.getSpecState() >= 0 && v.getSpecState() <= 30) {
                v.setState(20);
            } else {
                v.setState(v.getSpecState());
            }
        });
//        List<SpecCVViewEntity> cvSpecYearState = specViewMapper.getAllCVSpecYearStateList();

        Map<List<Object>, List<SpecViewAndPicEntity>> seriesYearStateAgg = seriesYearState.stream().collect(Collectors.groupingBy(sel));

//        //电机相关
//        List<ElectricParamEntity> puerElectric = specViewMapper.getAllSeriesYearStateElectricBase();
//        puerElectric.forEach(v -> {
//            if(v.getSpecState() >= 0 && v.getSpecState() <= 30){v.setState(20);}
//            else {v.setState(v.getSpecState());}
//        });
//        Map<Integer, List<ElectricParamEntity>> puerElectricMap = puerElectric.stream().collect(Collectors.groupingBy(ElectricParamEntity::getSyearId));
//
//        List<ElectricParamEntity> electricParam = specViewMapper.getAllElectricParam();
//        electricParam.forEach(v -> {
//            if(v.getSpecState() >= 0 && v.getSpecState() <= 30){v.setState(20);}
//            else {v.setState(v.getSpecState());}
//        });
//        Map<Integer, List<ElectricParamEntity>> electricParamMap = electricParam.stream().collect(Collectors.groupingBy(ElectricParamEntity::getSyearId));

        //图片相关
        List<SeriesPictureEntity> pics1 = carPhotoViewMapper.getAllSyearPicture1();
        List<SeriesPictureEntity> pics2 = carPhotoViewMapper.getAllSyearPicture2();
        List<SeriesPictureEntity> pics3 = carPhotoViewMapper.getAllSyearPicture3();
//        Map<Integer,SeriesPictureEntity> pics1Map = pics1.stream().collect(Collectors.toMap(SeriesPictureEntity::getSyearId,a -> a,(x,y)->x));
//        Map<Integer,SeriesPictureEntity> pics2Map = pics2.stream().collect(Collectors.toMap(SeriesPictureEntity::getSyearId,a -> a,(x,y)->x));
//        Map<Integer,SeriesPictureEntity> pics3Map = pics3.stream().collect(Collectors.toMap(SeriesPictureEntity::getSyearId,a -> a,(x,y)->x));

        List<SpecBaseEntity> specs = specViewMapper.getAllBase();
        Map<Integer, SpecBaseEntity> specMap = specs.stream().collect(Collectors.toMap(SpecBaseEntity::getId, a -> a, (x, y) -> x));

        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
//        List<SeriesPictureEntity> allSeriesPictures = carPhotoViewMapper.getAllSeriesPicture();
        List<SeriesElectricEntity> allPuerElectrics = specViewMapper.getAllSeriesElectricBase();
        List<ElectricParamEntity> allElectricParam = specViewMapper.getAllElectricParam();
//        List<SeriesFuelConsumptionEntity> allFuelConsumption = specViewMapper.getAllSeriesFuelConsumption();
        List<Integer> allParamIsShow = specViewMapper.getAllSeriesParamIsShow();
        List<Integer> allSeriesIsImgSpec = specViewMapper.getALLSeriesIsImgSpec();
//        List<KeyValueDto<Integer, Integer>> allSeriesState = specViewMapper.getAllSeriesState();
//        List<SpecViewAndPicEntity> allSpecs = specViewMapper.getAllSpecs();
//        allSpecs.addAll(specViewMapper.getAllCvSpecs());
//        List<SpecColorEntity> allSeriesColor = specColorMapper.getAllSeriesColor(0);

        for (Map.Entry<List<Object>, List<SpecViewAndPicEntity>> item : seriesYearStateAgg.entrySet()) {
            try {
                int seriesId = (int) item.getKey().get(0);
                int syear = (int) item.getKey().get(1);
                int state = (int) item.getKey().get(2);
                if (seriesId != seriesid || syear != yearid || state != stateid) {
                    continue;
                }

//                log.accept("start seriesId:" + seriesId);
                SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
                boolean paramIsShow = baseInfo.getSeriesState() == 40
                        ? !allSeriesIsImgSpec.contains(seriesId)
                        : allParamIsShow.contains(seriesId);

                boolean isCV = Level.isCVLevel(baseInfo.getLevelId());

                SeriesViewEntity seriesView = seriesViews.stream().filter(v -> v.getSeriesid() == seriesId).findFirst().get();
                List<SpecViewAndPicEntity> specList = item.getValue();
                SeriesConfig config = getConfig(
                        seriesBaseService.get(seriesId).join(),
                        specList, //allSpecs.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        factoryBaseService.getFactory(baseInfo.getFactId()),
                        brandBaseService.get(baseInfo.getBrandId()).join(),
                        seriesView,
                        null,// allSeriesPictures.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        null,// allSeriesColor.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        levelBaseService.getLevel(baseInfo.getLevelId()),
                        allPuerElectrics.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        allElectricParam.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        null,// allFuelConsumption.stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null),
                        paramIsShow,
                        isCV,
                        state
                );
//                config.setYearid(syear);
//                config.setState(state);

//                SeriesYearStateConfig config = new SeriesYearStateConfig();
//                List<String> structs = item.getValue().stream().map(v -> v.getSpecStructureType()).distinct().collect(Collectors.toList());
//                config.setStructitems(structs);
//                List<String> displacements = item.getValue().stream().map(v -> String.valueOf(v.getDisplacement())).distinct().collect(Collectors.toList());
//                config.setDisplacementitems(displacements);
//                List<String> transmissions = item.getValue().stream().map(v -> v.getSpecTransmissionType()).distinct().collect(Collectors.toList());
//                config.setTransmissionitems(transmissions);
                config.setSpecnum(item.getValue().size());
                config.setPicnum(item.getValue().stream().collect(Collectors.summingInt(SpecViewAndPicEntity::getPicNumber)));

                if (isCV) {
//                    config.setMinprice(specs.stream().map(SpecViewAndPicEntity::getMinPrice).min(Integer::compareTo).orElse(0));
                    config.setMaxprice(specList.stream().map(SpecViewAndPicEntity::getMaxPrice).max(Integer::compareTo).orElse(0));
                    config.setMinprice(config.getMaxprice() == 0 ? 0 : specList.stream().map(SpecViewAndPicEntity::getMinPrice).filter(minPrice -> minPrice != 0).min(Integer::compareTo).orElse(0));

                } else {
                    config.setMinprice(specList.stream().map(SpecViewAndPicEntity::getSpecPrice).min(Integer::compareTo).orElse(0));
                    config.setMaxprice(specList.stream().map(SpecViewAndPicEntity::getSpecPrice).max(Integer::compareTo).orElse(0));
                }

                List<SeriesConfig.SeriesLogo> picinfoitems = new ArrayList<>();
                List<SeriesPictureEntity> fpics = pics1.stream().filter(s -> s.getSyearId() == syear)
                        .sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                        .collect(Collectors.toList());
                if (fpics.size() == 3) {
                    List<String> pics = fpics.stream().map(v -> ImageUtil.getFullImagePath(v.getPhotoFilepath())).distinct().collect(Collectors.toList());
                    config.setPicitems(pics);
                    fpics.forEach(v -> {
                        fillPicInfo(picinfoitems, v, specMap);
                    });
                } else {
                    fpics = pics2.stream().filter(s -> s.getSyearId() == syear)
                            .sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                            .collect(Collectors.toList());
                    List<String> pics = fpics.stream().map(v -> ImageUtil.getFullImagePath(v.getPhotoFilepath())).distinct().collect(Collectors.toList());
                    if (fpics.size() == 3) {
                        config.setPicitems(pics);
                        fpics.forEach(v -> {
                            fillPicInfo(picinfoitems, v, specMap);
                        });
                    } else {
                        fpics = pics3.stream().filter(s -> s.getSyearId() == syear)
                                .sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                                .collect(Collectors.toList());
                        for (SeriesPictureEntity p3 : fpics) {
                            pics.add(ImageUtil.getFullImagePath(p3.getPhotoFilepath()));
                            fillPicInfo(picinfoitems, p3, specMap);
                            if (pics.size() > 3) {
                                break;
                            }
                        }
                        config.setPicitems(pics);
                    }
                }
                config.setPicinfoitems(picinfoitems);

//                List<ElectricParamEntity> puerElectricList = puerElectricMap.get(syear);
////                if(!CollectionUtils.isEmpty(puerElectricList)){
////                    //纯电
////                    if(puerElectricList.stream().filter(v -> v.getPureelectric() == 1).collect(Collectors.toList()).size() == puerElectricList.size()){
////                        config.setShowelectricparam(1);
////                    }
////                }
//                List<ElectricParamEntity> electricParamList = electricParamMap.get(syear);
////                List<String> electricKWs = electricParamList.stream()
////                        .filter(v -> v.getElectricKW().equals("-")==false)
////                        .map(v -> v.getElectricKW()).distinct().collect(Collectors.toList());
////                config.setElectricmotorkw();
//
//                puerElectricList = puerElectricList.stream().filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).collect(Collectors.toList());
//                config.setShowelectricparam(puerElectricList.size() <= 0 || puerElectricList.stream().anyMatch(x -> x.getPureelectric() != 1) ? 0 : 1);
//
//                electricParamList = electricParamList.stream().filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).collect(Collectors.toList());
//                List<Integer> electricmotormileage = electricParamList.stream().sorted(Comparator.comparing(ElectricParamEntity::getElectricMotorMileage).reversed()).map(x -> x.getElectricMotorMileage()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).distinct().map(x->Integer.parseInt(x)).collect(Collectors.toList());
//                config.setElectricmotormileage(electricmotormileage);
//
//                List<Double> lectricmotorkw = electricParamList.stream().sorted(Comparator.comparing(ElectricParamEntity::getElectricKW).reversed()).map(x -> x.getElectricKW()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).distinct().map(x->Double.parseDouble(x)).collect(Collectors.toList());
//                config.setElectricmotorkw(lectricmotorkw);
//
//                config.setElectricchargetime(electricParamList.stream().filter(x -> StringUtils.isNotBlank(x.getElectricKW()) && !x.getElectricKW().equals("-")).sorted(Comparator.comparing(ElectricParamEntity::getElectricKW).reversed()).map(x -> x.getChargeTime()).findFirst().orElse(""));
//
//                List<Double> electricRONGLIANG = electricParamList.stream().map(x -> x.getElectricRONGLIANG()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).map(x->Double.parseDouble(x)).distinct().collect(Collectors.toList());
//                config.setElectricrongliang(electricRONGLIANG);
                return config;
//                refresh(makeParam(seriesId,syear,state),config);
//                log.accept("success seriesId:" + seriesId+(config == null? "  but content is null":""));
            } catch (Exception e) {
//                log.accept(ExceptionUtil.getStackTrace(e));
            }
        }
        return null;
    }

    public void refreshAll(Consumer<String> log) {
        //SpecViewEntity
        Function<SpecViewAndPicEntity, List<Object>> sel = v -> Arrays.<Object>asList(v.getSeriesId(), v.getSyearId(), v.getState());

        List<SpecViewAndPicEntity> seriesYearState = specViewMapper.getAllSpecYearStateList();
        seriesYearState.addAll(specViewMapper.getAllCVSpecYearStateList());

        seriesYearState.forEach(v -> {
            if(v.getSpecState() >= 0 && v.getSpecState() <= 30){v.setState(20);}
            else {v.setState(v.getSpecState());}
        });

        Map<List<Object>, List<SpecViewAndPicEntity>> seriesYearStateAgg = seriesYearState.stream().collect(Collectors.groupingBy(sel));

        //图片相关
        List<SeriesPictureEntity> pics1 = carPhotoViewMapper.getAllSyearPicture1();
        List<SeriesPictureEntity> pics2 = carPhotoViewMapper.getAllSyearPicture2();
        List<SeriesPictureEntity> pics3 = carPhotoViewMapper.getAllSyearPicture3();

        List<SpecBaseEntity> specs = specViewMapper.getAllBase();
        Map<Integer,SpecBaseEntity> specMap = specs.stream().collect(Collectors.toMap(SpecBaseEntity::getId,a -> a,(x,y)->x));

        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        List<SeriesElectricEntity> allPuerElectrics = specViewMapper.getAllSeriesElectricBase();
        List<ElectricParamEntity> allElectricParam = specViewMapper.getAllElectricParam();
        List<Integer> allParamIsShow = specViewMapper.getAllSeriesParamIsShow();
        List<Integer> allSeriesIsImgSpec = specViewMapper.getALLSeriesIsImgSpec();

        for (Map.Entry<List<Object>,List<SpecViewAndPicEntity>> item:seriesYearStateAgg.entrySet()) {
            try {
                int seriesId = (int)item.getKey().get(0);
                int syear = (int)item.getKey().get(1);
                int state = (int)item.getKey().get(2);

                SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
                boolean paramIsShow = baseInfo.getSeriesState() == 40
                        ? !allSeriesIsImgSpec.contains(seriesId)
                        : allParamIsShow.contains(seriesId);

                boolean isCV = Level.isCVLevel(baseInfo.getLevelId());

                SeriesViewEntity seriesView = seriesViews.stream().filter(v -> v.getSeriesid() == seriesId).findFirst().get();
                List<SpecViewAndPicEntity> specList = item.getValue();
                SeriesConfig config = getConfig(
                        seriesBaseService.get(seriesId).join(),
                        specList, //allSpecs.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        factoryBaseService.getFactory(baseInfo.getFactId()),
                        brandBaseService.get(baseInfo.getBrandId()).join(),
                        seriesView,
                        null,// allSeriesPictures.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        null,// allSeriesColor.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList()),
                        levelBaseService.getLevel(baseInfo.getLevelId()),
                        allPuerElectrics.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        allElectricParam.stream().filter(x -> x.getSeriesId() == seriesId && x.getSyearId() == syear).collect(Collectors.toList()),
                        null,// allFuelConsumption.stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null),
                        paramIsShow,
                        isCV,
                        state
                );
                config.setSpecnum(item.getValue().size());
                config.setPicnum(item.getValue().stream().collect(Collectors.summingInt(SpecViewAndPicEntity::getPicNumber)));

                if(isCV){
//                    config.setMinprice(specs.stream().map(SpecViewAndPicEntity::getMinPrice).min(Integer::compareTo).orElse(0));
                    config.setMaxprice(specList.stream().map(SpecViewAndPicEntity::getMaxPrice).max(Integer::compareTo).orElse(0));
                    config.setMinprice(config.getMaxprice() == 0 ? 0 : specList.stream().map(SpecViewAndPicEntity::getMinPrice).filter(minPrice -> minPrice !=0).min(Integer::compareTo).orElse(0));

                }
                else {
                    config.setMinprice(specList.stream().map(SpecViewAndPicEntity::getSpecPrice).min(Integer::compareTo).orElse(0));
                    config.setMaxprice(specList.stream().map(SpecViewAndPicEntity::getSpecPrice).max(Integer::compareTo).orElse(0));
                }

                List<SeriesConfig.SeriesLogo> picinfoitems = new ArrayList<>();
                List<SeriesPictureEntity> fpics = pics1.stream().filter(s -> s.getSyearId() == syear)
                        .sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                        .collect(Collectors.toList());
                if(fpics.size() == 3){
                    List<String> pics = fpics.stream().map(v -> ImageUtil.getFullImagePath(v.getPhotoFilepath())).distinct().collect(Collectors.toList());
                    config.setPicitems(pics);
                    fpics.forEach(v -> {
                        fillPicInfo(picinfoitems,v,specMap);
                    });
                }
                else {
                    fpics = pics2.stream().filter(s -> s.getSyearId() == syear)
                            .sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                            .collect(Collectors.toList());
                    List<String> pics= fpics.stream().map(v -> ImageUtil.getFullImagePath(v.getPhotoFilepath())).distinct().collect(Collectors.toList());
                    if(fpics.size() == 3){
                        config.setPicitems(pics);
                        fpics.forEach(v -> {
                            fillPicInfo(picinfoitems,v,specMap);
                        });
                    }
                    else {
                        fpics = pics3.stream().filter(s -> s.getSyearId() == syear)
                                .sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                                .collect(Collectors.toList());
                        for (SeriesPictureEntity p3:fpics) {
                            pics.add(ImageUtil.getFullImagePath(p3.getPhotoFilepath()));
                            fillPicInfo(picinfoitems,p3,specMap);
                            if(pics.size()>3){
                                break;
                            }
                        }
                        config.setPicitems(pics);
                    }
                }
                config.setPicinfoitems(picinfoitems);

                refresh(makeParam(seriesId,syear,state),config);
            }catch (Exception e){
                log.accept(ExceptionUtil.getStackTrace(e));
            }
        }
//        return null;
    }

    public void refreshAllOld(Consumer<String> log) {
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

        seriesViews.forEach(seriesView -> {
            try {
                int seriesId = seriesView.getSeriesid();

                log.accept("start seriesId:" + seriesId);

                SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
                boolean paramIsShow = baseInfo.getSeriesState() == 40
                        ? !allSeriesIsImgSpec.contains(seriesId)
                        : allParamIsShow.contains(seriesId);

                boolean isCV = Level.isCVLevel(baseInfo.getLevelId());

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
                        seriesState
                );

//                refresh(makeParam(seriesId),config);
                log.accept("success seriesId:" + seriesId+(config == null? "  but content is null":""));
            }catch (Exception e){
                log.accept(ExceptionUtil.getStackTrace(e));
            }
        });
    }

    Map<String, Object> makeParam(int seriesId,int yearId,int state){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("seriesId", seriesId);
        params.put("yearId", yearId);
        params.put("state", state);
        return params;
    }

    public void fillPicInfo(List<SeriesConfig.SeriesLogo> picinfoitems,SeriesPictureEntity v,Map<Integer,SpecBaseEntity> specMap){
        SeriesConfig.SeriesLogo logo = new SeriesConfig.SeriesLogo();
        logo.setSpecid(v.getSpecid());
        logo.setPicid(v.getPhotoId());
        logo.setPicpath(ImageUtil.getFullImagePath(v.getPhotoFilepath()));
        logo.setSpecstate(specMap.get(v.getSpecid()).getSpecState());
        picinfoitems.add(logo);
    }

    public SeriesConfig getConfig(
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
            int state
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
//        config.setMaxprice(seriesView.getSeriesPriceMax());
//        config.setMinprice(seriesView.getSeriesPriceMin());
        config.setState(seriesView.getSeriesState());
        config.setSeriesConfigFilePath(seriesView.getSeriesConfigFilePath());
        config.setSeriesOfficialUrl(baseInfo.getUrl());
        config.setFctid(baseInfo.getFactId());
        config.setFctname(factoryBaseInfo ==null ? "" : factoryBaseInfo.getName());
        config.setBrandid(baseInfo.getBrandId());
        config.setBrandname(brandBaseInfo.getName());
        config.setLevelid(baseInfo.getLevelId());
        config.setLevelname(levelBaseInfo.getName());

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

//        List<String> imgList = seriesPictures.stream().sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex)).map(x -> ImageUtil.getFullImagePath(x.getPhotoFilepath().replace("~", ""))).collect(Collectors.toList());
//        config.setPicitems(imgList);
//        List<SeriesConfig.SeriesLogo> logos = seriesPictures.stream().sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
//                .map(x -> {return new SeriesConfig.SeriesLogo(x, ss.containsKey(x.getSpecid())?ss.get(x.getSpecid()):0);})
//                .collect(Collectors.toList());
//        config.setPicinfoitems(logos);
//        List<SpecColorEntity> colorList = isCV ? Collections.emptyList() :
//                specColors.stream().
//                        filter(x -> state == 20 ? x.getSpecState() >= 20 && x.getSpecState() <= 30 : x.getSpecState() == state).
//                        sorted(Comparator.comparing(SpecColorEntity::getPicNum).reversed()).
//                        collect(Collectors.toList());
//        config.setColorList(SpecColorInfo.getSpecColorInfoList(colorList));

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
        config.setPnglogo(ImageUtil.getFullImagePath(baseInfo.getNoBgLogo()));
        config.setPricedescription(seriesView.getPricedescription());
        config.setSeriesplace(seriesView.getSeriesplace());
        config.setBbsShow(baseInfo.getBbsShow());
        config.setLevelRank(baseInfo.getLevelRank());
        config.setSeriesConfigFilePath(seriesView.getSeriesConfigFilePath());
        return config;
    }


    List<String> getDisplacement(List<SpecViewAndPicEntity> onSellSpecs) {
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
