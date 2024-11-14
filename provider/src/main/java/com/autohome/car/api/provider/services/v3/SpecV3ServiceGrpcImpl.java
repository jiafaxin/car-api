package com.autohome.car.api.provider.services.v3;

import autohome.rpc.car.car_api.v3.spec.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.JsonUtils;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.SpecConfigPriceEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigRelationEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigSubItemEntity;
import com.autohome.car.api.data.popauto.entities.SpecStateEntity;
import com.autohome.car.api.services.SpecListConfigService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.ConfigItemBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.specs.SpecConfigPriceService;
import com.autohome.car.api.services.basic.specs.SpecConfigRelationService;
import com.autohome.car.api.services.basic.specs.SpecConfigSubItemService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.common.SpecElectric;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@DubboService
@RestController
public class SpecV3ServiceGrpcImpl extends DubboSpecV3ServiceTriple.SpecV3ServiceImplBase {

    @Autowired
    ConfigListService configListService;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    SpecConfigRelationService specConfigRelationService;

    @Autowired
    ConfigItemValueService configItemValueService;

    @Autowired
    SpecConfigPriceService specConfigPriceService;

    @Autowired
    ConfigSubItemService configSubItemService;

    @Autowired
    SpecConfigSubItemService specConfigSubItemService;

    @Autowired
    SpecListConfigService specListConfigService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    SpecListSameYearByYearService specListSameYearByYearService;

    @Override
    @GetMapping("/v3/CarPrice/Config_GetListBySpecList.ashx")
    public ConfigGetListBySpecListResponse configGetListBySpecList(ConfigGetListBySpecListRequest request) {
        return specListConfigService.configGetListBySpecList(request.getSpeclistList());
    }

    @Override
    @GetMapping({"/v3/CarPrice/Config_GetListBySpecId.ashx", "/v3/carprice/Config_GetListBySpecId.ashx"})
    public ConfigGetListBySpecIdResponse configGetListBySpecId(ConfigGetListBySpecIdRequest request) {
        if (request.getSpecid() == 0) {
            return ConfigGetListBySpecIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        ConfigGetListBySpecIdResponse.Builder resp = ConfigGetListBySpecIdResponse.newBuilder().setReturnCode(0).setReturnMsg("成功");
        ConfigGetListBySpecIdResponse.Result.Builder resultBuilder = ConfigGetListBySpecIdResponse.Result.newBuilder();
        resultBuilder.setSpecid(request.getSpecid());

        SpecBaseInfo specInfo = specBaseService.get(request.getSpecid()).join();
        if (specInfo != null && specInfo.getIsSpecParamIsShow() != 1) {
            return resp.setResult(resultBuilder).build();
        }

        //电动车车型
        boolean isPEVCar = specInfo != null && specInfo.getFuelTypeDetail() == 4;

        //基本配置
        List<ConfigTypeBaseInfo> list = configListService.get().stream().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(list)){
            return resp.setResult(resultBuilder).build();
        }

        list = JsonUtils.toObjectList(JsonUtils.toString(list),ConfigTypeBaseInfo.class);

        list.forEach(baseInfo -> {
            List<ConfigItemBaseInfo> items = baseInfo.getItems();
            if (items != null && !items.isEmpty()) {
                items.removeIf(x -> Spec.isCvSpec(request.getSpecid()) ? x.getCVIsShow() != 1 : x.getIsShow() != 1);
            }
        });
        list.removeIf(x -> x.getItems() == null || x.getItems().isEmpty());

        //获取配置id值全集
        Map<Integer, String> itemValues = configItemValueService.get();
        if(CollectionUtils.isEmpty(itemValues)){
            return resp.setResult(resultBuilder).build();
        }
        String defaultValue = itemValues.get(0);

        //关联valueId
        CompletableFuture<List<SpecConfigRelationEntity>> valuesFuture =  CompletableFuture.supplyAsync(() -> specConfigRelationService.get(request.getSpecid()));
        //item对应price
        CompletableFuture<List<SpecConfigPriceEntity>> priceItemsFuture = CompletableFuture.supplyAsync(() -> specConfigPriceService.getBySpecId(request.getSpecid()));
        //subItem值全集
        CompletableFuture<Map<Integer, String>> subItemsFuture = CompletableFuture.supplyAsync(() -> configSubItemService.get());
        //获取subItem和subValue
        CompletableFuture<List<SpecConfigSubItemEntity>> specSubItemsFuture = CompletableFuture.supplyAsync(() -> specConfigSubItemService.get(request.getSpecid()));
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(valuesFuture, priceItemsFuture, subItemsFuture, specSubItemsFuture);
        allFutures.join();

        List<SpecConfigRelationEntity> values = valuesFuture.join();
        List<SpecConfigPriceEntity> priceItems =priceItemsFuture.join();
        Map<Integer, String> subItems = subItemsFuture.join();
        List<SpecConfigSubItemEntity> specSubItems = specSubItemsFuture.join();

        for (ConfigTypeBaseInfo baseInfo : list) {
            List<ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();
            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                //如果是纯电动车型不加载汽油车的相关配置项
                if (isPEVCar && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId())) {
                    continue;
                }
                ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.Builder configItem = ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.newBuilder();
                boolean currentConfigValueEqualNull = false; //配置项相关值是否等于空

                List<SpecConfigPriceEntity> prices = new ArrayList<>();
                if(!CollectionUtils.isEmpty(priceItems)){
                    prices = priceItems.stream().filter(x -> x.getSpecId() == request.getSpecid() && x.getItemId() == item.getItemId()).collect(Collectors.toList());
                }
                if (item.getDisplayType() == 0) {  //横排
                    SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);
                    String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                    if (prices.size() > 0) {
                        for (SpecConfigPriceEntity price : prices) {
                            ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.SubPrice.Builder priceBuilder = ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.SubPrice.newBuilder();
                            priceBuilder.setSubname(subItems != null ? subItems.getOrDefault(price.getSubItemId(), "") : "");
                            priceBuilder.setPrice(price.getPrice());
                            configItem.addPrice(priceBuilder);
                        }
                    }
                    configItem.setValue(relation == null ? defaultValue : strValue);
                    configItem.addAllSublist(new ArrayList<>());
                    //判断当前配置是否有值 一行展示的配置项只判断value相关业务
                    if (StringUtils.isBlank(strValue) || strValue.equals("-") || strValue.equals("0")) {
                        currentConfigValueEqualNull = true;
                    }
                } else if (item.getDisplayType() == 1) { //竖排
                    List<SpecConfigSubItemEntity> specSubItemList = specSubItems == null ? null : specSubItems.stream().filter(x -> x.getItemId() == item.getItemId()).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(specSubItemList)) {
                        currentConfigValueEqualNull = true;
                    } else {
                        for (SpecConfigSubItemEntity specConfigSubItem : specSubItemList) {
                            SpecConfigPriceEntity price = prices.stream().filter(x -> x.getSubItemId() == specConfigSubItem.getSubItemId()).findFirst().orElse(null);
                            ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.SubItem.Builder subItem = ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.SubItem.newBuilder();
                            subItem.setSubitemid(specConfigSubItem.getSubItemId());
                            subItem.setSubname(subItems != null ? subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", ""): "");
                            subItem.setSubvalue(specConfigSubItem.getSubValue());
                            subItem.setPrice(price == null ? 0 : price.getPrice());
                            configItem.addSublist(subItem);
                        }
                    }
                    configItem.addAllPrice(new ArrayList<>());
                    configItem.setValue(configItem.getSublistCount() > 0 ? "" : "-");
                }

                //判断是否动态隐藏指定范围内的配置项，如果是且该配置项下没有值，不显示此配置项
                if (item.getDynamicShow() == 1 && currentConfigValueEqualNull) {
                    continue;
                }

                configItem.setConfigid(item.getItemId());
                configItem.setName(item.getItemName());
                configItem.setDisptype(item.getDisplayType());
                items.add(configItem.build());
            }
            if (items.size() > 0) {
                ConfigGetListBySpecIdResponse.Result.Configtypeitem.Builder typeItem = ConfigGetListBySpecIdResponse.Result.Configtypeitem.newBuilder();
                typeItem.setName(baseInfo.getTypeName());
                typeItem.setGroupname(Spec.DicConfig_Group.getOrDefault(baseInfo.getTypeName(), ""));
                typeItem.addAllConfigitems(items);
                resultBuilder.addConfigtypeitems(typeItem);
            }
        }

        return resp.setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }

    @Override
    @GetMapping("/v3/carprice/Config_GetListByYearId.ashx")
    public ConfigGetListByYearIdResponse getConfigListByYearId(ConfigGetListByYearIdRequest request) {
        if(request.getSeriesid() == 0||request.getYearid()==0)
            return ConfigGetListByYearIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();

        AtomicReference<List<ConfigTypeBaseInfo>> listTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> itemValuesTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigRelationEntity>>> datasTask = new AtomicReference<>();
        AtomicReference<Map<Integer,List<SpecConfigPriceEntity>>> priceItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> subItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> specSubItemsTask = new AtomicReference<>();
        AtomicReference<List<SpecBaseInfo>> specsTask = new AtomicReference<>();
        List<CompletableFuture> tasks = new ArrayList<>();

        ConfigGetListByYearIdResponse.Result.Builder resultBuilder = ConfigGetListByYearIdResponse.Result.newBuilder();
        resultBuilder.setSeriesid(request.getSeriesid()).setYearid(request.getYearid());
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(request.getSeriesid()).join();
        if(seriesBaseInfo != null){
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            //查年代ids
            List<SpecStateEntity> specStates = specListSameYearByYearService.get(request.getSeriesid(), request.getYearid(), isCV).join();
            if(!CollectionUtils.isEmpty(specStates)) {
                specStates.sort(Comparator.comparingInt(SpecStateEntity::getSpecOrder)
                        .thenComparing(SpecStateEntity::getOrderBy)
                        .thenComparingInt(SpecStateEntity::getOrders)
                        .thenComparing(SpecStateEntity::getSpecId, Comparator.reverseOrder()));
                List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());

                tasks.add(CompletableFuture.supplyAsync(() -> specBaseService.getList(specIds)).thenAccept(x -> {
                    specsTask.set(x);
                }));

                tasks.add(configListService.getAsync().thenAccept(x -> {
                    listTask.set(x);
                }));
                tasks.add(configItemValueService.getAsync().thenAccept(x -> {
                    itemValuesTask.set(x);
                }));
                tasks.add(CompletableFuture.supplyAsync(() -> specConfigRelationService.getList(specIds)).thenAccept(x -> {
                    datasTask.set(x);
                }));
                tasks.add(specConfigPriceService.getList(specIds).thenAccept(x -> {
                    priceItemsTask.set(x);
                }));
                tasks.add(configSubItemService.getAsync().thenAccept(x -> {
                    subItemsTask.set(x);
                }));
                tasks.add(CompletableFuture.supplyAsync(() -> specConfigSubItemService.getList(specIds)).thenAccept(x -> {
                    specSubItemsTask.set(x);
                }));

                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

                List<SpecBaseInfo> specs = specsTask.get();
                if (specs.stream().anyMatch(x -> x != null && x.getIsSpecParamIsShow() != 1)) {
                    return ConfigGetListByYearIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
                }

                //电动车车型的数量
                long pevSpecNum = specs.stream().filter(x -> x.getFuelTypeDetail() == 4).count();

                List<ConfigTypeBaseInfo> list = listTask.get();
                Map<Integer, String> itemValues = itemValuesTask.get();
                Map<Integer, List<SpecConfigRelationEntity>> datas = datasTask.get();
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

                list = JsonUtils.toObjectList(JsonUtils.toString(list),ConfigTypeBaseInfo.class);

//                list = list.stream().collect(Collectors.toList());
                if (specIds.stream().allMatch(x -> Spec.isCvSpec(x))) {
                    for (ConfigTypeBaseInfo baseInfo : list) {
                        baseInfo.getItems().removeIf(x -> x.getCVIsShow() != 1);
                    }
                } else if (specIds.stream().allMatch(x -> !Spec.isCvSpec(x))) {
                    for (ConfigTypeBaseInfo baseInfo : list) {
                        baseInfo.getItems().removeIf(x -> x.getIsShow() != 1);
                    }
                }

                list.removeIf(x -> x.getItems() == null || x.getItems().size() == 0);

                String defaultValue = itemValues.get(0);


                for (ConfigTypeBaseInfo baseInfo : list) {

                    List<ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();

                    if (baseInfo == null || baseInfo.getItems() == null || baseInfo.getItems().size() == 0) continue;

                    for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                        if (pevSpecNum == specIds.size() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                            continue;
                        AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                        List<ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem> valueitems = new ArrayList<>();
                        specIds.forEach(specId -> {
                            List<SpecConfigRelationEntity> values = datas.get(specId);
                            String key = specId + "-" + item.getItemId();
                            List<SpecConfigPriceEntity> prices = priceItemsMap.containsKey(key) ? priceItemsMap.get(key) : new ArrayList<>();//.stream().filter(x -> x.getSpecId() == specId && x.getItemId() == item.getItemId()).collect(Collectors.toList());
                            ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem.Builder valueBuilder = ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem.newBuilder();
                            valueBuilder.setSpecid(specId);
                            if (item.getDisplayType() == 0) {  //横排
                                SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                                String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                                if (prices != null && prices.size() > 0) {
                                    for (SpecConfigPriceEntity price : prices) {
                                        ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubPrice.Builder priceBuilder = ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubPrice.newBuilder();
                                        priceBuilder.setSubname(subItems.containsKey(price.getSubItemId()) ? subItems.get(price.getSubItemId()) : "");
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
                                String skey = specId + "-" + item.getItemId();
                                List<SpecConfigSubItemEntity> specSubItemList = specSubItemsMap.containsKey(skey) ? specSubItemsMap.get(skey) : new ArrayList<>();// !specSubItems.containsKey(specId) ? null : specSubItems.get(specId).stream().filter(x -> x.getItemId() == item.getItemId()).collect(Collectors.toList());
                                if (specSubItemList == null || specSubItemList.size() == 0) {
                                    currentConfigValueEqualNullNum.addAndGet(1);
                                } else {
                                    Map<Integer, SpecConfigPriceEntity> priceMap = new LinkedHashMap<>();
                                    for (SpecConfigPriceEntity price : prices) {
                                        priceMap.put(price.getSubItemId(), price);
                                    }
                                    for (SpecConfigSubItemEntity specConfigSubItem : specSubItemList) {
                                        SpecConfigPriceEntity price = priceMap.containsKey(specConfigSubItem.getSubItemId()) ? priceMap.get(specConfigSubItem.getSubItemId()) : null;//  prices.stream().filter(x -> x.getSubItemId() == specConfigSubItem.getSubItemId()).findFirst().orElse(null);
                                        ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.Builder subItem = ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.newBuilder();
                                        subItem.setSubname(subItems.containsKey(specConfigSubItem.getSubItemId()) ? subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", "") : "");
                                        subItem.setSubvalue(specConfigSubItem.getSubValue());
                                        subItem.setPrice(price == null ? 0 : price.getPrice());
                                        valueBuilder.addSublist(subItem);
                                    }
                                }
                                valueBuilder.addAllPrice(new ArrayList<>());
                                valueBuilder.setValue(valueBuilder.getSublistCount() > 0 ? "" : "-");
                            }
                            valueitems.add(valueBuilder.build());
                        });

                        if (item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() == specIds.size())
                            continue;

                        ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.Builder itemBuilder = ConfigGetListByYearIdResponse.Result.Configtypeitem.Configitem.newBuilder();
                        itemBuilder.setConfigid(item.getItemId());
                        itemBuilder.setName(item.getItemName());
                        itemBuilder.setDisptype(item.getDisplayType());
                        itemBuilder.addAllValueitems(valueitems);
                        items.add(itemBuilder.build());
                    }
                    if (items.size() > 0) {
                        ConfigGetListByYearIdResponse.Result.Configtypeitem.Builder typeItem = ConfigGetListByYearIdResponse.Result.Configtypeitem.newBuilder();
                        typeItem.setName(baseInfo.getTypeName());
                        typeItem.setGroupname(Spec.DicConfig_Group.containsKey(baseInfo.getTypeName()) ? Spec.DicConfig_Group.get(baseInfo.getTypeName()) : "");
                        typeItem.addAllConfigitems(items);
                        resultBuilder.addConfigtypeitems(typeItem);
                    }
                }
            }
        }

        return ConfigGetListByYearIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }

    /**
     * 多个车型获取配置信息（新接口; .net没有）
     * @param request
     * @return
     */
    @GetMapping("/v3/CarPrice/getConfigInfoBySpecIdsAndTypeIds")
    @Override
    public ConfigInfoBySpecIdsAndTypeIdsResponse getConfigInfoBySpecIdsAndTypeIds(ConfigInfoBySpecIdsAndTypeIdsRequest request) {
        List<Integer> specIds = CommonFunction.getListFromStr(request.getSpecids());
        //大分类id集合
        List<Integer> typeIds = CommonFunction.getListFromStr(request.getTypeids());
        if(CollectionUtils.isEmpty(specIds) || specIds.size() > 2 || CollectionUtils.isEmpty(typeIds)) {
            return ConfigInfoBySpecIdsAndTypeIdsResponse.newBuilder().
                    setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    build();
        }
        return specListConfigService.getConfigInfoBySpecIdsAndTypeIds(specIds,typeIds);
    }
}