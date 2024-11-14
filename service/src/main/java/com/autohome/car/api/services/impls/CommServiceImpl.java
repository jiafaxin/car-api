package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.car.ConfigTypeItem;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.*;
import com.autohome.car.api.services.basic.specs.SpecConfigPriceService;
import com.autohome.car.api.services.basic.specs.SpecConfigRelationService;
import com.autohome.car.api.services.basic.specs.SpecConfigSubItemService;
import com.autohome.car.api.services.common.SpecElectric;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
public class CommServiceImpl implements CommService {
    @Resource
    private FactoryBaseService factoryBaseService;

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private SpecBaseService specBaseService;

    @Resource
    private BrandBaseService brandBaseService;

    @Resource
    private LevelBaseService levelBaseService;

    @Resource
    private SeriesSpecBaseService seriesSpecBaseService;

    @Resource
    private BrandSeriesRelationBaseService brandSeriesRelationBaseService;

    @Resource
    private ElectricSpecViewBaseService electricSpecViewBaseService;

    @Resource
    private ConfigListService configListService;

    @Resource
    private ConfigItemValueService configItemValueService;

    @Resource
    private SpecConfigRelationService specConfigRelationService;

    @Resource
    private SpecConfigPriceService specConfigPriceService;

    @Resource
    private ConfigSubItemService configSubItemService;

    @Resource
    private SpecConfigSubItemService specConfigSubItemService;

    @Resource
    private ParamConfigModelService paramConfigModelService;

    @Override
    public List<SeriesBaseInfo> getSeriesBaseInfoNoMap(List<Integer> seriesIds) {
        if(CollectionUtils.isEmpty(seriesIds)) {
            return Collections.emptyList();
        }
        List<CompletableFuture> tasks = new ArrayList<>();
        Vector<SeriesBaseInfo> seriesBaseInfos = new Vector<>();
        for (int seriesId : seriesIds) {
            tasks.add(seriesBaseService.get(seriesId).thenAccept(seriesBaseInfo->{
                if (null != seriesBaseInfo){
                    seriesBaseInfos.add(seriesBaseInfo);
                }
            }));
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        return seriesBaseInfos;
    }

    /**
     * 获取车系信息
     * @param seriesIds
     * @return
     */
    @Override
    public Map<Integer, SeriesBaseInfo> getSeriesBaseInfo(List<Integer> seriesIds) {
        Map<Integer,SeriesBaseInfo> seriesBaseInfoMap = new HashMap<>();
        if(CollectionUtils.isEmpty(seriesIds)) {
            return seriesBaseInfoMap;
        }
        List<CompletableFuture> tasks = new ArrayList<>();
        Vector<SeriesBaseInfo> seriesBaseInfos = new Vector<>();
        for (int seriesId : seriesIds) {
            tasks.add(seriesBaseService.get(seriesId).thenAccept(seriesBaseInfo->{
                if (null != seriesBaseInfo){
                    seriesBaseInfos.add(seriesBaseInfo);
                }
            }));
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        seriesBaseInfoMap = seriesBaseInfos.stream().collect(Collectors.toMap(SeriesBaseInfo::getId,
                seriesBaseInfo -> seriesBaseInfo,(key1, key2)->key1));
        return seriesBaseInfoMap;
    }
    /**
     * 获取厂商信息
     * @param fctIds
     * @return
     */
    @Override
    public Map<Integer, FactoryBaseInfo> getFactoryBaseInfo(List<Integer> fctIds) {
        Map<Integer,FactoryBaseInfo> fctMap = new HashMap<>();
        if(CollectionUtils.isEmpty(fctIds)) {
            return fctMap;
        }
        List<FactoryBaseInfo> factoryBaseInfos = factoryBaseService.getFactoryByIds(fctIds);
        if(CollectionUtils.isEmpty(factoryBaseInfos)){
            return fctMap;
        }
        fctMap = factoryBaseInfos.stream().collect(Collectors.toMap(FactoryBaseInfo::getId, factoryBaseInfo -> factoryBaseInfo,(key1, key2)->key1));
        return fctMap;
    }
    /**
     * 获取车型信息
     * @param specIds
     * @return
     */
    @Override
    public Map<Integer, SpecBaseInfo> getSpecBaseInfo(List<Integer> specIds) {
        return specBaseService.getMap(specIds);
    }
    /**
     * 获取品牌信息
     * @param brandIds
     * @return
     */
    @Override
    public Map<Integer, BrandBaseInfo> getBrandBaseInfo(List<Integer> brandIds) {
        Map<Integer,BrandBaseInfo> brandBaseInfoMap = new HashMap<>();
        if(CollectionUtils.isEmpty(brandIds)) {
            return brandBaseInfoMap;
        }
        List<CompletableFuture> tasks = new ArrayList<>();
        Vector<BrandBaseInfo> brandBaseInfos = new Vector<>();
        for (int brandId : brandIds) {
            tasks.add(brandBaseService.get(brandId).thenAccept(brandBaseInfo->{
                if (null != brandBaseInfo){
                    brandBaseInfos.add(brandBaseInfo);
                }
            }));
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        brandBaseInfoMap = brandBaseInfos.stream().collect(Collectors.toMap(BrandBaseInfo::getId,
                brandBaseInfo -> brandBaseInfo,(key1, key2)->key1));
        return brandBaseInfoMap;
    }

    @Override
    public List<BrandBaseInfo> getBrandBaseInfoList(List<Integer> brandIds) {
        Vector<BrandBaseInfo> brandBaseInfos = new Vector<>();
        if(CollectionUtils.isEmpty(brandIds)) {
            return brandBaseInfos;
        }
        List<CompletableFuture> tasks = new ArrayList<>();

        for (Integer brandId : brandIds) {
            tasks.add(brandBaseService.get(brandId).thenAccept(brandBaseInfo->{
                if (null != brandBaseInfo){
                    brandBaseInfos.add(brandBaseInfo);
                }
            }));
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        return brandBaseInfos;
    }

    /**
     * 获取级别信息
     * @param levelIds
     * @return
     */
    @Override
    public Map<Integer, LevelBaseInfo> getLevelBaseInfo(List<Integer> levelIds) {
        Map<Integer,LevelBaseInfo> levelBaseInfoMap = new HashMap<>();
        if(CollectionUtils.isEmpty(levelIds)) {
            return levelBaseInfoMap;
        }
        List<LevelBaseInfo> levelBaseInfos = levelBaseService.getLevelList(levelIds);
        if(CollectionUtils.isEmpty(levelBaseInfos)){
            return levelBaseInfoMap;
        }
        levelBaseInfoMap = levelBaseInfos.stream().collect(Collectors.toMap(LevelBaseInfo::getId,
                levelBaseInfo -> levelBaseInfo ,(key1, key2)->key1));
        return levelBaseInfoMap;
    }



    /**
     * 根据品牌获取车系id
     * @param brandIds
     * @return
     */
    @Override
    public Map<Integer, List<Integer>> getSeriesIdListByBrands(List<Integer> brandIds) {
        Map<Integer, List<Integer>> seriesIdListMap = new HashMap<>();
        if(CollectionUtils.isEmpty(brandIds)) {
            return seriesIdListMap;
        }

        List<CompletableFuture> tasks = new ArrayList<>();
        for (Integer brandId : brandIds) {
            tasks.add(CompletableFuture.supplyAsync(() -> brandSeriesRelationBaseService.getSeriesIds(brandId))
                    .thenAccept(list -> {
                        if (null != list){
                            seriesIdListMap.put(brandId, list);
                        }
                    })
            );
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        return seriesIdListMap;
    }

    /**
     *
     * @param seriesIdList
     * @return
     */
    @Override
    public List<EleSpecViewBaseEntity> getEleSpecViewBaseEntities(List<Integer> seriesIdList) {
        Vector<EleSpecViewBaseEntity> eleSpecViewBaseEntities = new Vector<>();
        if(CollectionUtils.isEmpty(seriesIdList)) {
            return eleSpecViewBaseEntities;
        }
        List<CompletableFuture> tasks = new ArrayList<>();

        for (int seriesId : seriesIdList) {
            tasks.add(electricSpecViewBaseService.get(seriesId).thenAccept(entityList->{
                if (!CollectionUtils.isEmpty(entityList)){
                    eleSpecViewBaseEntities.addAll(entityList);
                }
            }));
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        return eleSpecViewBaseEntities;
    }

    @Override
    public List<SpecViewEntity> getSpecViewEntities(int seriesId, int type) {
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (Objects.isNull(seriesBaseInfo)) {
            return Collections.emptyList();
        }
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        List<SpecViewEntity> specViewEntities = seriesSpecBaseService.get(seriesId, isCV).join();
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(specViewEntities)) {
            return Collections.emptyList();
        }
        List<SpecViewEntity> tempSpecViewEntities;
        if (isCV) {
            tempSpecViewEntities = specViewEntities.stream().filter(specViewEntity -> specViewEntity.getSpecState() >=10 && specViewEntity.getSpecState() <= 30)
                    .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).thenComparing(SpecViewEntity::getSpecOrdercls).thenComparing(Comparator.comparing(SpecViewEntity::getSpecId).reversed()))
                    .collect(Collectors.toList());
        } else {
            tempSpecViewEntities = specViewEntities.stream().filter(specViewEntity ->  specViewEntity.getSpecState() <= 30 &&specViewEntity.getSpecIsImage() == 0)
                    .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).thenComparing(SpecViewEntity::getIsclassic).thenComparing(SpecViewEntity::getSpecOrdercls).thenComparing(Comparator.comparing(SpecViewEntity::getSpecId).reversed()))
                    .collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(tempSpecViewEntities)) {
            List<Integer> specIds = tempSpecViewEntities.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
            Map<Integer, SpecBaseInfo> baseInfoMap = specBaseService.getMap(specIds);
            tempSpecViewEntities = tempSpecViewEntities.stream().filter(Objects::nonNull).filter(specViewEntity -> {
                SpecBaseInfo specBaseInfo = baseInfoMap.get(specViewEntity.getSpecId());
                return specBaseInfo != null && (specBaseInfo.getIsSpecParamIsShow() == 1 || specBaseInfo.getSpecState() == 40);
            }).collect(Collectors.toList());
        }
        return tempSpecViewEntities;
    }

    @Override
    public Pair<ReturnMessageEnum, List<ConfigTypeItem>> getConfigListBySpecList(List<Integer> specIds, int dispType) {
        List<ConfigTypeItem> result = new ArrayList<>();

        //------------------------------------------
        AtomicReference<List<ConfigTypeBaseInfo>> listTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> itemValuesTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigRelationEntity>>> datasTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigPriceEntity>>> priceItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> subItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> specSubItemsTask = new AtomicReference<>();
        AtomicReference<List<SpecBaseInfo>> specsTask = new AtomicReference<>();
        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(CompletableFuture.supplyAsync(() -> specBaseService.getList(specIds)).thenAccept(specsTask::set));
        tasks.add(configListService.getAsync().thenAccept(listTask::set));
        tasks.add(configItemValueService.getAsync().thenAccept(itemValuesTask::set));
        tasks.add(CompletableFuture.supplyAsync(() -> specConfigRelationService.getList(specIds)).thenAccept(datasTask::set));
        tasks.add(specConfigPriceService.getList(specIds).thenAccept(priceItemsTask::set));
        tasks.add(configSubItemService.getAsync().thenAccept(subItemsTask::set));
        tasks.add(CompletableFuture.supplyAsync(() -> specConfigSubItemService.getList(specIds)).thenAccept(specSubItemsTask::set));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        List<SpecBaseInfo> specs = specsTask.get();
        if (specs.stream().anyMatch(x -> x != null && x.getIsSpecParamIsShow() != 1)) {
            return Pair.of(RETURN_MESSAGE_ENUM102, new ArrayList<>());
        }

        List<ConfigTypeBaseInfo> list = listTask.get();
        Map<Integer, String> itemValues = itemValuesTask.get();
        Map<Integer, List<SpecConfigRelationEntity>> relationItems = datasTask.get();
        Map<Integer, List<SpecConfigPriceEntity>> priceItems = priceItemsTask.get();
        Map<Integer, String> subItems = subItemsTask.get();
        Map<Integer, List<SpecConfigSubItemEntity>> specSubItems = specSubItemsTask.get();

        Map<String, List<SpecConfigPriceEntity>> priceItemsMap = new LinkedHashMap<>();
        priceItems.forEach((k, v) -> {
            for (SpecConfigPriceEntity item : v) {
                String key = k + "-" + item.getItemId();
                if (!priceItemsMap.containsKey(key)) {
                    priceItemsMap.put(key, new ArrayList<>());
                }
                priceItemsMap.get(key).add(item);
            }
        });

        Map<String, List<SpecConfigSubItemEntity>> specSubItemsMap = new LinkedHashMap<>();
        for (Integer specId : specSubItems.keySet()) {
            for (SpecConfigSubItemEntity item : specSubItems.get(specId)) {
                String key = specId + "-" + item.getItemId();
                if (!specSubItemsMap.containsKey(key)) {
                    specSubItemsMap.put(key, new ArrayList<>());
                }
                specSubItemsMap.get(key).add(item);
            }
        }

        list = ToolUtils.deepCopyList(list);
        if (specIds.stream().allMatch(Spec::isCvSpec)) {
            for (ConfigTypeBaseInfo baseInfo : list) {
                baseInfo.getItems().removeIf(x -> x.getCVIsShow() != 1);
                if (dispType != 1) {
                    baseInfo.getItems().removeIf(x -> x.getDisplayType() != 0);
                }
            }
        } else if (specIds.stream().noneMatch(Spec::isCvSpec)) {
            for (ConfigTypeBaseInfo baseInfo : list) {
                baseInfo.getItems().removeIf(x -> x.getIsShow() != 1);
                if (dispType != 1) {
                    baseInfo.getItems().removeIf(x -> x.getDisplayType() != 0);
                }
            }
        }
        list.removeIf(x -> x.getItems() == null || x.getItems().size() == 0);

        //随机模板
        int modelId = new Random().nextInt(2) + 1;
        Map<String, Integer> configModelMap = paramConfigModelService.getConfMap(modelId);
        String defaultValue = itemValues.get(0);
        //电动车车型的数量
        long pevSpecNum = specs.stream().filter(x -> x.getFuelTypeDetail() == 4).count();

        for (ConfigTypeBaseInfo baseInfo : list) {
            List<ConfigTypeItem.ConfigItem> items = new ArrayList<>();

            if (baseInfo == null || baseInfo.getItems() == null || baseInfo.getItems().size() == 0) continue;

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if (pevSpecNum == specIds.size() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId())) {
                    continue;
                }
                AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                List<ConfigTypeItem.ValueItem> valueItems = new ArrayList<>();
                specIds.forEach(specId -> {
                    List<SpecConfigRelationEntity> values = relationItems.get(specId);
                    String key = specId + "-" + item.getItemId();
                    List<SpecConfigPriceEntity> prices = priceItemsMap.containsKey(key) ? priceItemsMap.get(key) : new ArrayList<>();
                    ConfigTypeItem.ValueItem.Builder valueBuilder = ConfigTypeItem.ValueItem.newBuilder();
                    valueBuilder.setSpecid(specId);
                    if (item.getDisplayType() == 0) {  //横排
                        SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);
                        String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                        if (prices != null && prices.size() > 0) {
                            for (SpecConfigPriceEntity price : prices) {
                                ConfigTypeItem.SubPrice.Builder priceBuilder = ConfigTypeItem.SubPrice.newBuilder();
                                priceBuilder.setSubname(subItems.getOrDefault(price.getSubItemId(), ""));
                                priceBuilder.setPrice(price.getPrice());
                                valueBuilder.addPrice(priceBuilder);
                            }
                        }
                        valueBuilder.setValue(relation == null ? defaultValue : strValue);
                        valueBuilder.addAllSublist(new ArrayList<>());
                        if (StringUtils.isBlank(strValue) || strValue.equals("-") || strValue.equals("0")) {
                            currentConfigValueEqualNullNum.addAndGet(1);
                        }
                    } else if (item.getDisplayType() == 1) { //竖排
                        String sKey = specId + "-" + item.getItemId();
                        List<SpecConfigSubItemEntity> specSubItemList = specSubItemsMap.containsKey(sKey) ? specSubItemsMap.get(sKey) : new ArrayList<>();
                        if (specSubItemList == null || specSubItemList.size() == 0) {
                            currentConfigValueEqualNullNum.addAndGet(1);
                        } else {
                            Map<Integer, SpecConfigPriceEntity> priceMap = new LinkedHashMap<>();
                            for (SpecConfigPriceEntity price : prices) {
                                priceMap.put(price.getSubItemId(), price);
                            }
                            for (SpecConfigSubItemEntity specConfigSubItem : specSubItemList) {
                                SpecConfigPriceEntity price = priceMap.getOrDefault(specConfigSubItem.getSubItemId(), null);
                                ConfigTypeItem.SubItem.Builder subItem = ConfigTypeItem.SubItem.newBuilder();
                                subItem.setSubname(subItems.containsKey(specConfigSubItem.getSubItemId()) ? subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", "") : "");
                                subItem.setSubvalue(specConfigSubItem.getSubValue());
                                subItem.setPrice(price == null ? 0 : price.getPrice());
                                valueBuilder.addSublist(subItem);
                            }
                        }
                        valueBuilder.addAllPrice(new ArrayList<>());
                        valueBuilder.setValue(valueBuilder.getSublistCount() > 0 ? "" : "-");
                    }
                    valueItems.add(valueBuilder.build());
                });

                //判断是否动态隐藏指定范围内的配置项
                if (dispType == 1 && item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() == specIds.size()) {
                    continue;
                }

                ConfigTypeItem.ConfigItem.Builder itemBuilder = ConfigTypeItem.ConfigItem.newBuilder();
                itemBuilder.setConfigid(item.getItemId());
                itemBuilder.setName(item.getItemName());
                int pnidStr = configModelMap != null && configModelMap.containsKey(item.getItemName()) && configModelMap.get(item.getItemName()) > 0 ? configModelMap.get(item.getItemName()) : -1;
                itemBuilder.setPnid(modelId + "_" + pnidStr);
                itemBuilder.setDisptype(item.getDisplayType());
                itemBuilder.addAllValueitems(valueItems);
                items.add(itemBuilder.build());
            }
            if (items.size() > 0) {
                ConfigTypeItem.Builder typeItem = ConfigTypeItem.newBuilder();
                typeItem.setName(baseInfo.getTypeName());
                typeItem.setGroupname(Spec.DicConfig_Group.getOrDefault(baseInfo.getTypeName(), ""));
                typeItem.addAllConfigitems(items);
                result.add(typeItem.build());
            }
        }
        return Pair.of(ReturnMessageEnum.RETURN_MESSAGE_ENUM0, result);
    }

}