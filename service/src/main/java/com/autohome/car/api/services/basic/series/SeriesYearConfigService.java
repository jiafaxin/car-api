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
import com.autohome.car.api.services.models.SeriesYearConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SeriesYearConfigService extends BaseService<SeriesYearConfig> {

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
    protected SeriesYearConfig getData(Map<String, Object> params) {
        int seriesId = (int)params.get("seriesId");
        int yearId = (int)params.get("yearId");
        int state = (int)params.get("state");
        return getSeriesConfig(seriesId,yearId,state);
    }

    public SeriesYearConfig get(int seriesId,int yearId,int state) {
        return get(makeParam(seriesId,yearId,state));
    }

    private SeriesYearConfig getSeriesConfig(int seriesId,int yearId,Integer state) {
        AtomicReference<SeriesBaseInfo> baseInfoAR = new AtomicReference<>();
        AtomicReference<SeriesViewEntity> seriesViewAR = new AtomicReference<>();
        AtomicReference<List<SeriesPictureEntity>> seriesPicturesAR = new AtomicReference<>();
        AtomicReference<List<SpecViewAndPicEntity>> specsAR = new AtomicReference<>();
        AtomicReference<List<SeriesElectricEntity>> puerElectricAR = new AtomicReference<>();
        AtomicReference<List<ElectricParamEntity>> electricParamAR = new AtomicReference<>();
        AtomicBoolean isCV = new AtomicBoolean(false);
        AtomicInteger seriesState = new AtomicInteger(-1);

        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(seriesBaseService.get(seriesId).thenComposeAsync(baseInfo -> {
            if (baseInfo == null)
                return CompletableFuture.completedFuture(null);
            baseInfoAR.set(baseInfo);
            List<CompletableFuture> cTasks = new ArrayList<>();
            isCV.set(Level.isCVLevel(baseInfo.getLevelId()));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCvSpecs(seriesId) : specViewMapper.getSpecs(seriesId)).thenAccept(x -> specsAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCVSeriesElectricBase(seriesId) : specViewMapper.getSeriesElectricBase(seriesId)).thenAccept(x -> puerElectricAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> isCV.get() ? specViewMapper.getCVElectricParam(seriesId) : specViewMapper.getElectricParam(seriesId)).thenAccept(x -> electricParamAR.set(x)));
            cTasks.add(CompletableFuture.supplyAsync(() -> {
                if (baseInfo.getSeriesState() == 40) {
                    return specViewMapper.isSeriesIsImgSpec(seriesId) <= 0;
                } else {
                    return (isCV.get() ? specViewMapper.getCVSeriesParamIsShow(seriesId) : specViewMapper.getSeriesParamIsShow(seriesId)) > 0;
                }
            }));
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
            return CompletableFuture.allOf(cTasks.toArray(new CompletableFuture[cTasks.size()]));
        }));

        tasks.add(CompletableFuture.supplyAsync(() -> seriesMapper.getSeriesView(seriesId)).thenAccept(x -> seriesViewAR.set(x)));
        tasks.add(CompletableFuture.supplyAsync(() -> carPhotoViewMapper.getSeriesPicture(seriesId)).thenAccept(x -> seriesPicturesAR.set(x)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        List<SpecViewAndPicEntity> specs = specsAR.get();
        List<SeriesPictureEntity> sps = seriesPicturesAR.get();
        List<SeriesElectricEntity> pes = puerElectricAR.get();
        List<ElectricParamEntity> eps = electricParamAR.get();

        specs = specs.stream().filter(x->x.getSyearId()==yearId && x.getSpecState() == state).collect(Collectors.toList());
        sps = sps.stream().filter(x->x.getSyearId()==yearId && x.getState()== state).collect(Collectors.toList());
        pes = pes.stream().filter(x->x.getSyearId() == yearId && x.getSpecState() == state).collect(Collectors.toList());
        eps = eps.stream().filter(x->x.getSyearId() == yearId && x.getSpecState() == state).collect(Collectors.toList());

        return getConfig(
                baseInfoAR.get(),
                specs,
                seriesViewAR.get(),
                sps,
                pes,
                eps,
                isCV.get(),
                seriesState.get()
        );
    }

    public void refreshAll(Consumer<String> log) {
        List<SeriesViewEntity> seriesViews = seriesMapper.getAllSeriesView();
        List<SeriesPictureEntity> allSeriesPictures = carPhotoViewMapper.getAllSeriesPicture();
        List<SeriesPictureEntity> allSeriesPictures2 = carPhotoViewMapper.getAllSeriesPicture2();
        List<SeriesPictureEntity> allSeriesPictures3 = carPhotoViewMapper.getAllSeriesPicture3();
        List<SeriesElectricEntity> allPuerElectrics = specViewMapper.getAllSeriesElectricBase();
        List<ElectricParamEntity> allElectricParam = specViewMapper.getAllElectricParam();
        List<KeyValueDto<Integer, Integer>> allSeriesState = specViewMapper.getAllSeriesState();
        List<SpecViewAndPicEntity> allSpecs = specViewMapper.getAllSpecs();
        allSpecs.addAll(specViewMapper.getAllCvSpecs());

        seriesViews.forEach(seriesView -> {
            try {
                int seriesId = seriesView.getSeriesid();

                SeriesBaseInfo baseInfo = seriesBaseService.get(seriesId).join();
                if(baseInfo==null)
                    return;

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
                List<SpecViewAndPicEntity> specs = allSpecs.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList());

                List<Integer> yearIds = specs.stream().map(x -> x.getSyearId()).distinct().collect(Collectors.toList());
                List<Integer> states = Arrays.asList(0, 10, 20, 40);

                List<SeriesPictureEntity> asp = allSeriesPictures.stream().filter(x -> x.getSeriesId() == seriesId ).collect(Collectors.toList());
                List<SeriesPictureEntity> asp2 = allSeriesPictures2.stream().filter(x -> x.getSeriesId() == seriesId ).collect(Collectors.toList());
                List<SeriesPictureEntity> asp3 = allSeriesPictures3.stream().filter(x -> x.getSeriesId() == seriesId ).collect(Collectors.toList());

                List<SeriesElectricEntity> ape = allPuerElectrics.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList());
                List<ElectricParamEntity> aep = allElectricParam.stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList());

                for (int yearId : yearIds) {
                    for (int state : states) {
                        List<SpecViewAndPicEntity> currentSpecs = specs.stream().filter(x ->  {
                            if(x.getSyearId() != yearId)
                                return false;
                            if(state != 20 && state == x.getSpecState())
                                return true;
                            if(state == 20 && x.getSpecState() >=20 && x.getSpecState() <=30)
                                return true;
                            return false;
                        }).collect(Collectors.toList());
                        if(currentSpecs==null||currentSpecs.size()==0)
                            continue;

                        List<SeriesPictureEntity> pics = asp.stream().filter(x -> x.getSyearId() == yearId).sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex)).collect(Collectors.toList());
                        if(pics==null||pics.size()!=3){
                            pics = asp2.stream().filter(x -> x.getSyearId() == yearId).sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex)).collect(Collectors.toList());
                        }
                        if(pics==null||pics.size()<3){
                            for (SeriesPictureEntity seriesPictureEntity : asp3.stream().filter(x -> x.getSyearId() == yearId).collect(Collectors.toList())) {
                                if(pics.stream().anyMatch(x->x.getPhotoFilepath().replace("~","").equals(seriesPictureEntity.getPhotoFilepath().replace("~",""))))
                                    continue;
                                pics.add(seriesPictureEntity);
                                if(pics.size()==3)
                                    break;
                            }
                        }


                        SeriesYearConfig config = getConfig(
                                seriesBaseService.get(seriesId).join(),
                                currentSpecs,
                                seriesView,
                                pics,
                                ape.stream().filter(x -> {
                                    if(x.getSyearId() != yearId)
                                        return false;
                                    if(state != 20 && state == x.getSpecState())
                                        return true;
                                    if(state == 20 && x.getSpecState() >=20 && x.getSpecState() <=30)
                                        return true;
                                    return false;
                                }).collect(Collectors.toList()),
                                aep.stream().filter(x -> {
                                    if(x.getSyearId() != yearId)
                                        return false;
                                    if(state != 20 && state == x.getSpecState())
                                        return true;
                                    if(state == 20 && x.getSpecState() >=20 && x.getSpecState() <=30)
                                        return true;
                                    return false;
                                }).collect(Collectors.toList()),
                                isCV,
                                seriesState
                        );
                        refresh(makeParam(seriesId,yearId,state), config);
                    }
                }
            } catch (Exception e) {
                log.accept(ExceptionUtil.getStackTrace(e));
            }
        });
    }

    Map<String, Object> makeParam(int seriesId,int yesrId,int state){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("seriesId", seriesId);
        params.put("yesrId", yesrId);
        params.put("state", state);
        return params;
    }

    public static SeriesYearConfig getConfig(
            SeriesBaseInfo baseInfo,
            List<SpecViewAndPicEntity> specs,
            SeriesViewEntity seriesView,
            List<SeriesPictureEntity> seriesPictures,
            List<SeriesElectricEntity> puerElectric,
            List<ElectricParamEntity> electricParam,
            boolean isCV,
            int state
    ){
        if (baseInfo == null)
            return null;
        if (specs == null || specs.size() == 0){
            return null;
        }

//        Map<Integer,Integer> ss = new LinkedHashMap<>();
//        specs.forEach(x->{
//            ss.put(x.getSpecId(),x.getSpecState());
//        });

        int maxPrice = specs.stream().map(x->x.getMinPrice()).filter(x->x>0).mapToInt(x->x).max().orElse(0);
        int minPrice = maxPrice == 0 ? 0 : specs.stream().map(x->x.getMinPrice()).filter(x->x>0).mapToInt(x->x).min().orElse(0);

        if(maxPrice >0 && minPrice == 0){
            System.out.println("error");
        }

        SeriesYearConfig config = new SeriesYearConfig();
        config.setId(baseInfo.getId());
        config.setMaxprice(maxPrice);
        config.setMinprice(minPrice);

        List<String> structList = specs.stream().filter(x -> x.getSpecStructureType() != null)
                .sorted(Comparator.comparing(x -> Stuct.StructOrderBy(x.getSpecStructureType(),isCV)))
                .map(x -> isCV ? Stuct.getCVName(Integer.parseInt(x.getSpecStructureType())) : Stuct.getName(x.getSpecStructureType()))
                .distinct().filter(x-> StringUtils.isNotBlank(x)).collect(Collectors.toList());
        config.setStructitems(structList);

        List<String> transmissionList = specs.stream().filter(x -> x.getSpecTransmissionType() != null)
                .sorted(Comparator.comparing( x-> CommonFunction.transmissionOrderBy(x.getSpecTransmissionType(),isCV)))
                .map(x -> isCV ? Transmission.getCVTransmission(Integer.parseInt(x.getSpecTransmissionType())) : Transmission.shortName(x.getSpecTransmissionType()))
                .distinct().filter(x->StringUtils.isNotBlank(x)).collect(Collectors.toList());
        config.setTransmissionitems(transmissionList);

        config.setDisplacementitems(getDisplacement(specs));

        List<String> imgList = seriesPictures.stream().sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex)).map(x -> ImageUtil.getFullImagePath(x.getPhotoFilepath().replace("~", ""))).collect(Collectors.toList());
        config.setPicitems(imgList);
        List<SeriesYearConfig.SeriesLogo> logos = seriesPictures.stream().sorted(Comparator.comparing(SeriesPictureEntity::getOrderIndex))
                .map(x -> {return new SeriesYearConfig.SeriesLogo(x, x.getState());})
                .collect(Collectors.toList());
        config.setPicinfoitems(logos);

        config.setSpecnum(specs.size());
        config.setPicnum(specs.stream().mapToInt(SpecViewAndPicEntity::getPicNumber).sum());

        config.setIsshow(specs.stream().findFirst().get().getSeriesIsShow());

        config.setShowelectricparam(puerElectric.size() <= 0 || puerElectric.stream().anyMatch(x -> x.getPureelectric() != 1) ? 0 : 1);

        List<Integer> electricmotormileage = electricParam.stream().sorted(Comparator.comparing(ElectricParamEntity::getElectricMotorMileage).reversed()).map(x -> x.getElectricMotorMileage()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).distinct().map(x->Integer.parseInt(x)).collect(Collectors.toList());
        config.setElectricmotormileage(electricmotormileage);

        List<Double> lectricmotorkw = electricParam.stream().sorted(Comparator.comparing(ElectricParamEntity::getElectricKW).reversed()).map(x -> x.getElectricKW()).filter(x -> StringUtils.isNotBlank(x) && !x.equals("-")).distinct().map(x->Double.parseDouble(x)).collect(Collectors.toList());
        config.setElectricmotorkw(lectricmotorkw);
        config.setElectricchargetime(electricParam.stream().filter(x -> StringUtils.isNotBlank(x.getElectricKW()) && !x.getElectricKW().equals("-")).sorted(Comparator.comparing(ElectricParamEntity::getElectricKW).reversed()).map(x -> x.getChargeTime()).findFirst().orElse(""));
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
