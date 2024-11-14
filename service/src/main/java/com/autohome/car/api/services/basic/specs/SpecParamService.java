package com.autohome.car.api.services.basic.specs;

import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.BrandBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.common.RedisUtil;
import com.autohome.car.api.services.models.SeriesConfig;
import com.autohome.car.api.services.models.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SpecParamService  extends BaseService<SpecParam> {

    @Autowired
    SpecViewMapper specViewMapper;

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
    CarSpecPicColorStatisticsMapper carSpecPicColorStatisticsMapper;

    @Autowired
    SpecPicClassStatisticsMapper specPicClassStatisticsMapper;

    @Autowired
    SpecParamViewMapper specParamViewMapper;

    @Autowired
    SpecPicClassStatisticsBaseService specPicClassStatisticsBaseService;

    @Autowired
    SeriesSpecBaseService seriesSpecBaseService;


    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected SpecParam getData(Map<String, Object> params) {
        return GetSpecParam((int)params.get("specId"));
    }

    public SpecParam get(int specId){
        return get(makeParam(specId));
    }

    Map<String,Object> makeParam(int specId){
        Map<String,Object> param = new LinkedHashMap<>();
        param.put("specId",specId);
        return param;
    }

    public Map<Integer,SpecParam> getMap(List<Integer> specIds) {
        List<SpecParam> list = getList(specIds);
        return list.stream().collect(Collectors.toMap(SpecParam::getSpecid, x -> x,(x, y)->x));
    }

    public List<SpecParam> getList(List<Integer> specIds){
        if(CollectionUtils.isEmpty(specIds))
            return new ArrayList<>();

        List<Map<String,Object>> params = new ArrayList<>();
        for (Integer id : specIds) {
            if(id==null) {
                continue;
            }
            params.add(makeParam(id));
        }
        return mGet(params);
    }

    SpecParam GetSpecParam(int specId) {
        SpecParam specParam = new SpecParam();
        //以下为设置默认值
        specParam.setSpecid(specId);
        specParam.setSpecparamisshow(1);
        if (specId == 0)
            return specParam;

        AtomicReference<SpecBaseInfo> baseInfoAR = new AtomicReference<>();
        AtomicReference<SeriesBaseInfo> seriesBaseInfoAR = new AtomicReference<>();
        AtomicReference<String> qrCodeAR = new AtomicReference<>();
        AtomicReference<SpecViewEntity> specViewAR = new AtomicReference<>();
        AtomicReference<BrandBaseInfo> brandBaseInfoAR = new AtomicReference<>();
        AtomicReference<String> fctNameAR = new AtomicReference<>();
        AtomicReference<String> levelNameAR = new AtomicReference<>();

        List<CompletableFuture> tasks = new ArrayList<>();

        tasks.add(specBaseService.get(specId).thenCompose(bi -> {
            if (bi == null)
                return CompletableFuture.completedFuture(null);
            baseInfoAR.set(bi);

            List<CompletableFuture> cts = new ArrayList<>();
            cts.add(seriesBaseService.get(bi.getSeriesId()).thenAccept(x -> {
                seriesBaseInfoAR.set(x);
                brandBaseInfoAR.set(brandBaseService.get(x.getBrandId()).join());
                fctNameAR.set(factoryInfoService.getName(x.getFactId()));
                levelNameAR.set(levelBaseInfoService.getName(x.getLevelId()));
            }));

            cts.add(qrCodeService.spec(bi.getSeriesId(), specId).thenAccept(x -> {
                qrCodeAR.set(x);
            }));
            return CompletableFuture.allOf(cts.toArray(new CompletableFuture[cts.size()]));
        }));

        tasks.add(CompletableFuture.supplyAsync(() -> Spec.isCvSpec(specId)
                ? specViewMapper.getCv(specId)
                : specViewMapper.get(specId)).thenAccept(x -> specViewAR.set(x)));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        return getBase(
                baseInfoAR.get(),
                seriesBaseInfoAR.get(),
                brandBaseInfoAR.get(),
                specViewAR.get(),
                fctNameAR.get(),
                levelNameAR.get(),
                qrCodeAR.get()
        );
    }


    public void refreshAll(Consumer<String> log){
        List<SpecViewEntity> allSpecs = specViewMapper.getAll();
        AtomicInteger count = new AtomicInteger(0);
        for (SpecViewEntity item : allSpecs) {
            try {
                int specId = item.getSpecId();
                SpecBaseInfo bi = specBaseService.get(specId).join();
                if(bi == null){
                    continue;
                }
                SeriesBaseInfo x = seriesBaseService.get(bi.getSeriesId()).join();
                SpecParam param = getBase(
                        bi,
                        x,
                        brandBaseService.get(x.getBrandId()).join(),
                        item,
                        factoryInfoService.getName(x.getFactId()),
                        levelBaseInfoService.getName(x.getLevelId()),
                        qrCodeService.spec(bi.getSeriesId(), specId).join()
                );
                refresh(makeParam(specId), param);
            } catch (Exception e) {
                log.accept("error：" + ExceptionUtil.getStackTrace(e));
            }
        }
    }

    SpecParam getBase(
            SpecBaseInfo baseInfo,
            SeriesBaseInfo seriesBaseInfo,
            BrandBaseInfo brandBaseInfo,
            SpecViewEntity specView,
            String fctName,
            String levelName,
            String qrCode
    ) {
        if (baseInfo == null) {
            return null;
        }

        int specId = baseInfo.getId();
        SpecParam specParam = new SpecParam();
        specParam.setSpecid(specId);
        specParam.setSpecparamisshow(1);
        specParam.setSpecid(specId);
        specParam.setSpecname(baseInfo.getSpecName());
        specParam.setSeriesid(baseInfo.getSeriesId());
        specParam.setSeriesname(seriesBaseInfo.getName());
        specParam.setBrandid(seriesBaseInfo.getBrandId());
        specParam.setBrandname(brandBaseInfo.getName());
        specParam.setFctid(seriesBaseInfo.getFactId());
        specParam.setFctname(fctName);
        specParam.setLevelid(seriesBaseInfo.getLevelId());
        specParam.setLevelname(levelName);
        specParam.setSpecpicount(specView.getSpecPicNum());
        specParam.setSpeclogo(ImageUtil.getFullImagePath(baseInfo.getLogo()));
        specParam.setSpecminprice(specView.getMinPrice());
        specParam.setSpecmaxprice(specView.getMaxPrice());
        specParam.setSpecengineid(specView.getEngineId());
        specParam.setSpecenginename(specView.getEngineName());
        specParam.setSpecstructuredoor(specView.getDoors());
        specParam.setSpecstructureseat(specView.getSeats());
        specParam.setSpecstructuretypename(Spec.isCvSpec(specId) ? CommonFunction.carBodyStruct(specView.getStructType()) : specView.getSpecStructureType());
        specParam.setSpectransmission(baseInfo.getGearBox());
        specParam.setSpecstate(specView.getSpecState());
        specParam.setSpecoiloffical(specView.getOfficalOil());
        specParam.setSpeclength(specView.getLength());
        specParam.setSpecwidth(specView.getWidth());
        specParam.setSpecheight(specView.getHeight());
        specParam.setSpecweight(specView.getWeightkg());
        specParam.setSpecdrivingmodename(Spec.isCvSpec(specId) ? CommonFunction.driveMode(specView.getDriveForm()) : specView.getSpecDrivingMode());
        specParam.setSpecflowmodeid(specView.getFlowMode());
        specParam.setSpecflowmodename(CommonFunction.admissionMehtod(specView.getFlowMode()));
        specParam.setSpecdisplacement(specView.getSpecDisplacement());
        specParam.setSpecenginepower(specView.getSpecEnginePower());
        specParam.setSpecparamisshow(baseInfo.getIsSpecParamIsShow());
        specParam.setSpecispreferential(baseInfo.getIsPreferential());
        specParam.setSpecistaxrelief(baseInfo.getSpecTaxType() == 1 ? 1 : 0);
        specParam.setSpecistaxexemption(baseInfo.getSpecTaxType() == 2 ? 1 : 0);
        specParam.setSpecquality(specView.getQuality());
        specParam.setSpecisimport(specView.getSeriesIsImport());
        specParam.setSpecisbooked(baseInfo.getIsBooked() == 1);
        String dp = specView.getSpecState() == 10
                ? (baseInfo.getIsBooked() == 1 ? "订金:" + PriceUtils.getStrPrice(specView.getMinPrice(), specView.getMaxPrice()) : "预售价:" + PriceUtils.getStrPrice(specView.getMinPrice(), specView.getMaxPrice()))
                : "指导价:" + PriceUtils.getStrPrice(specView.getMinPrice(), specView.getMaxPrice());
        specParam.setDynamicprice(dp);
        specParam.setOilboxvolume(baseInfo.getOilBoxVolume());
        specParam.setFueltype(specView.getFuelType());
        specParam.setFastchargetime(new DecimalFormat("#########.#########").format(specView.getOfficialFastChargetime()));
        specParam.setSlowchargetime(new DecimalFormat("#########.#########").format(specView.getOfficialSlowChargetime()));
        specParam.setFastchargePercent(specView.getFastChargeBatteryPercentage());
        specParam.setBatterycapacity(new DecimalFormat("#########.#########").format(specView.getBatteryCapacity()));
        specParam.setMile(specView.getEndurancemileage());
        specParam.setFueltypedetail(specView.getFuelTypeDetail());
        specParam.setFueltypename(CommonFunction.carFuel(specView.getFuelTypeDetail()));
        specParam.setGreenstandards(baseInfo.getDicEmissionStandards());
        specParam.setEnginetorque(specView.getTorque());
        specParam.setEngingkw(Spec.isCvSpec(specId) ? baseInfo.getEngingKW() : specView.getEngingKW());
        specParam.setPricedescription(specView.getPricedescription());
        specParam.setElectricmotorgrosspower(specView.getElectricMotorGrossPower());
        specParam.setElectricmotorgrosstorque(specView.getElectricMotorGrossTorque());
        specParam.setOillabel(baseInfo.getOilLabe());
        specParam.setWheelbase(baseInfo.getWheelBase());
        specParam.setQrcode(qrCode);
        specParam.setSsuo(specView.getSsuo());
        specParam.setSpecMaxspeed(specView.getSpecMaxspeed());
        return specParam;
    }
}
