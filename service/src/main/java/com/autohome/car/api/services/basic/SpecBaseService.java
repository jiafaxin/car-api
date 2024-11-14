package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.EhCacheName;
import com.autohome.car.api.common.ExceptionUtil;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.InnerSpecColorPriceRemarkEntity;
import com.autohome.car.api.data.popauto.entities.SpecBaseEntity;
import com.autohome.car.api.data.popauto.entities.SpecCVViewEntity;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class SpecBaseService extends BaseService<SpecBaseInfo> {

    @Autowired
    SpecViewMapper specViewMapper;

    @Autowired
    SpecParamViewMapper specParamViewMapper;

    @Autowired
    ParamSubItemMapper paramSubItemMapper;

    @Autowired
    ParamSpecRelationMapper paramSpecRelationMapper;

    @Autowired
    SpecPhotoMapper specPhotoMapper;

    @Autowired
    private SpecMapper specMapper;

    @Autowired
    SpecColorMapper specColorMapper;

    @Autowired
    InnerSpecColorMapper innerSpecColorMapper;

    @Autowired
    ElectricSpecViewMapper electricSpecViewMapper;

    @Resource
    private CarPhotoViewMapper carPhotoViewMapper;

    public CompletableFuture<SpecBaseInfo> get(int specId) {
        return getAsync(makeParams(specId));
    }

    public Map<Integer,SpecBaseInfo> getMap(List<Integer> specIds) {
        List<SpecBaseInfo> list = getList(specIds);
        return list.stream().filter(x->x!=null).collect(Collectors.toMap(SpecBaseInfo::getId, x -> x,(x,y)->x));
    }

    public List<SpecBaseInfo> getList(List<Integer> specIds){
        if(specIds==null || specIds.size()==0)
            return new ArrayList<>();

        List<Map<String,Object>> params = new ArrayList<>();
        for (Integer id : specIds) {
            if(id==null)
                continue;
            params.add(makeParams(id));
        }
        return mGet(params);
    }

    public Map<Integer,SpecBaseInfo> getMapBigData(List<Integer> specIds) {
        List<SpecBaseInfo> list = getListBigData(specIds);
        return list.stream().collect(Collectors.toMap(SpecBaseInfo::getId, x -> x,(x,y)->x));
    }

    public List<SpecBaseInfo> getListBigData(List<Integer> specIds){
        if(specIds==null || specIds.size()==0)
            return new ArrayList<>();

        List<CompletableFuture<List<SpecBaseInfo>>> futures = new ArrayList<>();
        List<List<Integer>> splitList = ToolUtils.splitList(specIds, 200);

        for (List<Integer> group : splitList) {
            List<Map<String, Object>> params = group.stream()
                    .map(this::makeParams)
                    .collect(Collectors.toList());

            CompletableFuture<List<SpecBaseInfo>> future = CompletableFuture.supplyAsync(() -> mGet(params));
            futures.add(future);
            if (futures.size() >= 20) {
                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                allOf.join();
                futures.clear();
            }
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    Map<String,Object> makeParams(int specId){
        Map<String,Object> params = new HashMap<>();
        params.put("specId",specId);
        return params;
    }

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected SpecBaseInfo getData(Map<String, Object> params) {
        int specId = (int) params.get("specId");
        return getData(specId);
    }

    SpecBaseInfo getData(int specId) {
        SpecBaseEntity baseEntity = specViewMapper.getBase(specId);
        if (baseEntity == null)
            return null;

        SpecCVViewEntity specCVViewEntity = specViewMapper.getSpecViewBySpecId(specId);
        KeyValueDto<Integer, String> gearBox = specParamViewMapper.getGearBox(specId);
        KeyValueDto<Integer,String> dicEmissionStandards = specParamViewMapper.getDicEmissionStandards(specId);
        KeyValueDto<Integer,String> oilLabe = paramSubItemMapper.getSpecOilLabe(specId);
        KeyValueDto<Integer,String> wheelBase = paramSpecRelationMapper.getSpecWheelBase(specId);
        KeyValueDto<Integer,String> oilBoxVolume = specParamViewMapper.getOilBoxVolume(specId);
        KeyValueDto<Integer,String> engingKW = specParamViewMapper.getEngineKW(specId);
        SpecViewEntity fuelBase = specViewMapper.getSpecInfoBySpecId(specId);
        KeyValueDto<Integer, String> ekw = specViewMapper.getElectroTotalKW(specId);
        KeyValueDto<Integer, String> specLogo = specViewMapper.getSpecLogoBySpecId(specId);
        KeyValueDto<Integer, String> pngLogo = specPhotoMapper.getPngPhoto(specId);
        KeyValueDto<Integer, Integer> isHaveMaintains = specViewMapper.isHaveMaintains(specId);
        KeyValueDto<Integer, String> specRealTestUrl = specMapper.getSpecRealTestUrl(specId);
        List<KeyValueDto<Integer, Integer>> specInnerColorList = specColorMapper.getSpecInnerColorList(specId);
        List<KeyValueDto<Integer, Integer>> specColorList = specColorMapper.getColorBySpecId(specId);
        KeyValueDto<Integer, Integer> horsePower = specMapper.getSpecHorsePower(specId);
        KeyValueDto<Integer, Integer> flowMode = specMapper.getSpecFlowMode(specId);
//        KeyValueDto<Integer, String> innerColors = innerSpecColorMapper.getInnerSpecColor(specId);
        InnerSpecColorPriceRemarkEntity innerColors = innerSpecColorMapper.getInnerSpecColorPriceRemark(specId);
        List<Integer> specOfficialPicIsNew = carPhotoViewMapper.getAllSpecOfficialPicIsNew();

        return convert(baseEntity,specCVViewEntity,gearBox,dicEmissionStandards,oilLabe,wheelBase,oilBoxVolume,engingKW,fuelBase, ekw, specLogo,pngLogo,isHaveMaintains, specRealTestUrl, specInnerColorList, specColorList,horsePower,flowMode,innerColors, specOfficialPicIsNew);
    }

    public int refreshAll(Consumer<String> log) {
        List<SpecBaseEntity> baseEntity = specViewMapper.getAllBase();
        List<SpecCVViewEntity> specCVViewEntities = specViewMapper.getSpecViewAll();
        List<KeyValueDto<Integer, String>> gearBox = specParamViewMapper.getAllGearBox();
        List<KeyValueDto<Integer, String>> dicEmissionStandards = specParamViewMapper.getAllDicEmissionStandards();
        List<KeyValueDto<Integer, String>> oilLabe = paramSubItemMapper.getAllSpecOilLabe();
        List<KeyValueDto<Integer, String>> wheelBase = paramSpecRelationMapper.getAllSpecWheelBase();
        List<KeyValueDto<Integer, String>> oilBoxVolume = specParamViewMapper.getAllOilBoxVolume();
        List<KeyValueDto<Integer, String>> engingKW = specParamViewMapper.getAllEngineKW();
        List<SpecViewEntity> fuelBase = specViewMapper.getAllSpecInfoBySpecId();
        List<KeyValueDto<Integer, String>> electrickw = specViewMapper.getAllElectroTotalKW();
        List<KeyValueDto<Integer, String>> allSpecLogo = specViewMapper.getAllSpecLogo();
        List<KeyValueDto<Integer, String>> allPngLogo = specPhotoMapper.getAllPngLogo();
        List<KeyValueDto<Integer, Integer>> isAllHaveMaintains = specViewMapper.isAllHaveMaintains();
        List<KeyValueDto<Integer, String>> allSpecRealTestUrl = specMapper.getAllSpecRealTestUrl();
        List<KeyValueDto<Integer, Integer>> specAllInnerColorList = specColorMapper.getAllSpecInnerColorList();
        List<KeyValueDto<Integer, Integer>> specAllColorList = specColorMapper.getAllColor();
        List<KeyValueDto<Integer, Integer>> specAllHorsePowerList = specMapper.getAllSpecHorsePower();
        List<KeyValueDto<Integer, Integer>> specAllFlowModeList = specMapper.getAllSpecFlowMode();
//        List<KeyValueDto<Integer, String>> innerColorList = innerSpecColorMapper.getAllInnerSpecColor();
        List<InnerSpecColorPriceRemarkEntity> innerColorList = innerSpecColorMapper.getAllInnerSpecColorPriceRemark();

        Map<Integer,SpecCVViewEntity> specCVViewEntitiesMap = specCVViewEntities.stream().collect(Collectors.toMap(SpecCVViewEntity::getSpecId,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> gearBoxMap = gearBox.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> dicEmissionStandardsMap = dicEmissionStandards.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> oilLabeMap = oilLabe.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> wheelBaseMap = wheelBase.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> oilBoxVolumeMap = oilBoxVolume.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> engingKWMap = engingKW.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,SpecViewEntity> fuelBaseMap = fuelBase.stream().collect(Collectors.toMap(SpecViewEntity::getSpecId,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> electrickwMap = electrickw.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> allSpecLogoMap = allSpecLogo.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> allPngLogoMap = allPngLogo.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, Integer>> isHaveMaintainsMap = isAllHaveMaintains.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer,KeyValueDto<Integer, String>> allSpecRealTestUrlMap = allSpecRealTestUrl.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer, List<KeyValueDto<Integer, Integer>>> allSpecInnerColorMap = specAllInnerColorList.stream().collect(Collectors.groupingBy(KeyValueDto::getKey));
        Map<Integer, List<KeyValueDto<Integer, Integer>>> specAllColorMap = specAllColorList.stream().collect(Collectors.groupingBy(KeyValueDto::getKey));
        Map<Integer, KeyValueDto<Integer, Integer>> specAllHorsePowerMap = specAllHorsePowerList.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer, KeyValueDto<Integer, Integer>> specAllFlowModeMap = specAllFlowModeList.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
//        Map<Integer, KeyValueDto<Integer, String>> innerColorMap = innerColorList.stream().collect(Collectors.toMap(KeyValueDto::getKey,a -> a,(x,y)->x));
        Map<Integer, InnerSpecColorPriceRemarkEntity> innerColorMap = innerColorList.stream().collect(Collectors.toMap(InnerSpecColorPriceRemarkEntity::getSpecid,a -> a,(x,y)->x));
        List<Integer> specOfficialPicIsNew = carPhotoViewMapper.getAllSpecOfficialPicIsNew();

        AtomicInteger count = new AtomicInteger(0);
        baseEntity.forEach(item -> {
            try {
                int specId = item.getId();
                Map<String, Object> params = new HashMap<>();
                params.put("specId", specId);
                refresh(params, convert(
                        item,
                        specCVViewEntitiesMap.get(specId),
                        gearBoxMap.get(specId),
                        dicEmissionStandardsMap.get(specId),
                        oilLabeMap.get(specId),
                        wheelBaseMap.get(specId),
                        oilBoxVolumeMap.get(specId),
                        engingKWMap.get(specId),
                        fuelBaseMap.get(specId),
                        electrickwMap.get(specId),
                        allSpecLogoMap.get(specId),
                        allPngLogoMap.get(specId),
                        isHaveMaintainsMap.get(specId),
                        allSpecRealTestUrlMap.get(specId),
                        allSpecInnerColorMap.get(specId),
                        specAllColorMap.get(specId),
                        specAllHorsePowerMap.get(specId),
                        specAllFlowModeMap.get(specId),
                        innerColorMap.get(specId),
                        specOfficialPicIsNew
                ));

            } catch (Exception e) {
                log.accept("error >> " + item.getId() + " >> " + ExceptionUtil.getStackTrace(e));
            }
        });
        return baseEntity.size();
    }

    SpecBaseInfo convert(
            SpecBaseEntity baseEntity,
            SpecCVViewEntity specCVViewEntity,
            KeyValueDto<Integer, String> gearBox,
            KeyValueDto<Integer,String> dicEmissionStandards,
            KeyValueDto<Integer,String> oilLabe,
            KeyValueDto<Integer,String> wheelBase,
            KeyValueDto<Integer,String> oilBoxVolume,
            KeyValueDto<Integer,String> engingKW,
            SpecViewEntity fuelBase,
            KeyValueDto<Integer, String> ekw,
            KeyValueDto<Integer,String> specLogo,
            KeyValueDto<Integer, String> pngLogo,
            KeyValueDto<Integer,Integer> isHaveMaintains,
            KeyValueDto<Integer, String> specRealTestUrl,
            List<KeyValueDto<Integer, Integer>> specInnerColorList,
            List<KeyValueDto<Integer, Integer>> specColorList,
            KeyValueDto<Integer, Integer> horsePower,
            KeyValueDto<Integer, Integer> flowMode,
            InnerSpecColorPriceRemarkEntity innerColor,
            List<Integer> specOfficialPicIsNew
    ){
        SpecBaseInfo baseInfo = new SpecBaseInfo();

        baseInfo.setId(baseEntity.getId());
        baseInfo.setIsclassic(baseInfo.getIsclassic());
        baseInfo.setSpecName(baseEntity.getSpecName());
        baseInfo.setSeriesId(baseEntity.getSeriesId());
        baseInfo.setIsBooked(baseEntity.getIsBooked());
        baseInfo.setIsSpecParamIsShow(baseEntity.getIsSpecParamIsShow());
        baseInfo.setLogo(baseEntity.getLogo());
        baseInfo.setSpecTaxType(baseEntity.getSpecTaxType());
        baseInfo.setIsPreferential(baseEntity.getIsPreferential());
        baseInfo.setTimeMarket(baseEntity.getTimeMarket());

        baseInfo.setSpecMinPrice(baseEntity.getSpecMinPrice());
        baseInfo.setSpecMaxPrice(baseEntity.getSpecMaxPrice());
        baseInfo.setIsNew(baseEntity.getIsNew());
        baseInfo.setStopTime(baseEntity.getStopTime());
        baseInfo.setEditTime(baseEntity.getEditTime());
        //spec_view赋值
        if(null != specCVViewEntity){
            baseInfo.setSYear(specCVViewEntity.getSYear());
            baseInfo.setSYearId(specCVViewEntity.getSYearId());
            baseInfo.setBrandFirstLetter(specCVViewEntity.getBrandFirstLetter());
            baseInfo.setFctFirstLetter(specCVViewEntity.getFctFirstLetter());
            baseInfo.setSeriesFirstLetter(specCVViewEntity.getSeriesFirstLetter());
            baseInfo.setSpecQuality(specCVViewEntity.getSpecQuality());
            baseInfo.setSpecState(specCVViewEntity.getSpecState());
            baseInfo.setSpecIsImage(specCVViewEntity.getSpecIsImage());
            baseInfo.setSpecOrdercls(specCVViewEntity.getSpecOrdercls());
            baseInfo.setBrandId(specCVViewEntity.getBrandId());
            baseInfo.setDriveForm(specCVViewEntity.getDriveForm());
            baseInfo.setSpecDrivingMode(specCVViewEntity.getSpecDrivingMode());
            baseInfo.setSeats(specCVViewEntity.getSeats());
        }
        if (gearBox != null) {
            baseInfo.setGearBox(gearBox.getValue());
        }

        if(dicEmissionStandards != null){
            baseInfo.setDicEmissionStandards(dicEmissionStandards.getValue());
        }

        if(oilLabe != null){
            baseInfo.setOilLabe(oilLabe.getValue());
        }

        if(wheelBase !=null){
            try {
                baseInfo.setWheelBase((int)Float.parseFloat(wheelBase.getValue()));
            }
            catch (Exception e){

            }
        }

        if(oilBoxVolume !=null){
            baseInfo.setOilBoxVolume(StringUtils.isNotBlank(oilBoxVolume.getValue())
                    ?(int)Float.parseFloat(oilBoxVolume.getValue()):0);
        }

        if(pngLogo != null){
            baseInfo.setPngLogo(pngLogo.getValue());
        }

        if(Spec.isCvSpec(baseEntity.getId())){
            if(engingKW !=null){
                baseInfo.setEngingKW(engingKW.getValue());
            }
        }
        if(fuelBase != null){
            baseInfo.setFuelType(fuelBase.getFuelType());
            baseInfo.setFuelTypeDetail(fuelBase.getFuelTypeDetail());
            baseInfo.setDisplacement(fuelBase.getDisplacement());
            baseInfo.setPricedescription(fuelBase.getPricedescription());
        }
        if (ekw != null) {
            baseInfo.setElectroTotalKW(Double.parseDouble(ekw.getValue()));
        }

        if (Objects.nonNull(specLogo)) {
            baseInfo.setSpecLogoImg(specLogo.getValue());
        }
        //车型是否有保养
        if(null != isHaveMaintains){
           baseInfo.setIsHaveMaintains(isHaveMaintains.getValue());
        }
        if (Objects.nonNull(specRealTestUrl)) {
            baseInfo.setSpeedUrl(specRealTestUrl.getValue());
        }
        if (!CollectionUtils.isEmpty(specInnerColorList)) {
            List<Integer> collect = specInnerColorList.stream().map(KeyValueDto::getValue).collect(Collectors.toList());
            baseInfo.setSic(Joiner.on(",").join(collect));
        }
        if (!CollectionUtils.isEmpty(specColorList)) {
            List<Integer> collect = specColorList.stream().map(KeyValueDto::getValue).collect(Collectors.toList());
            baseInfo.setSc(Joiner.on(",").join(collect));
        }
        if(horsePower != null){
            baseInfo.setHorsepower(horsePower.getValue());
        }
        if(flowMode != null){
            baseInfo.setFlowMode(flowMode.getValue());
        }
        if(innerColor != null){
            baseInfo.setInnerColorIds(innerColor.getInnerColorIds());
            baseInfo.setInnerColorPrices(innerColor.getInnerColorPrices());
            baseInfo.setInnerColorRemarks(innerColor.getInnerColorRemarks());
        }

        baseInfo.setOpn(specOfficialPicIsNew != null && specOfficialPicIsNew.contains(baseEntity.getId()) ? 1 : 0);
        return baseInfo;
    }


}
