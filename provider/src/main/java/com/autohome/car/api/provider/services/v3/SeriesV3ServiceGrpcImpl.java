package com.autohome.car.api.provider.services.v3;

import autohome.rpc.car.car_api.v3.series.*;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.entities.SpecConfigPriceEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigRelationEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigSubItemEntity;
import com.autohome.car.api.services.SeriesService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.ConfigItemBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesSpecService;
import com.autohome.car.api.services.basic.series.SeriesSpecWithStateService;
import com.autohome.car.api.services.basic.specs.SpecConfigPriceService;
import com.autohome.car.api.services.basic.specs.SpecConfigRelationService;
import com.autohome.car.api.services.basic.specs.SpecConfigSubItemService;
import com.autohome.car.api.services.common.SpecElectric;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@DubboService
@RestController
public class SeriesV3ServiceGrpcImpl extends DubboSeriesV3ServiceTriple.SeriesV3ServiceImplBase {

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
    SeriesSpecService seriesSpecService;

    @Autowired
    SeriesSpecWithStateService seriesSpecWithStateService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Resource
    private SeriesService seriesService;


    //spec base service
    //seriesSpecWithStateService
    @Override
    @GetMapping("/v3/CarPrice/Config_GetListBySeriesId.ashx")
    public ConfigGetListBySeriesIdResponse configGetListBySeriesId(ConfigGetListBySeriesIdRequest request) {

        if (request.getSeriesid() <= 0)
            return ConfigGetListBySeriesIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();

        AtomicReference<List<ConfigTypeBaseInfo>> listTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> itemValuesTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigRelationEntity>>> datasTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigPriceEntity>>> priceItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> subItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> specSubItemsTask = new AtomicReference<>();
        AtomicReference<List<SpecBaseInfo>> specsTask = new AtomicReference<>();

        ConfigGetListBySeriesIdResponse.Builder builder = ConfigGetListBySeriesIdResponse.newBuilder();
        ConfigGetListBySeriesIdResponse.Result.Builder resultBuilder = ConfigGetListBySeriesIdResponse.Result.newBuilder();
        resultBuilder.setSeriesid(request.getSeriesid());
        builder.setReturnCode(0).setReturnMsg("成功");

        List<Integer> specIds = seriesSpecWithStateService.getSpecIds(request.getSeriesid(), Arrays.asList(0,10,20,30));
        if (specIds == null || specIds.size() == 0) {
            return builder.setResult(resultBuilder).build();
        }

        List<CompletableFuture> tasks = new ArrayList<>();

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

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(request.getSeriesid()).join();


        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        List<SpecBaseInfo> specs = specsTask.get();

        specs = specs.stream().filter(x->x.getIsSpecParamIsShow() == 1 && x.getSpecIsImage() == 0).collect(Collectors.toList());

//        if (specs.stream().anyMatch(x -> x != null && x.getIsSpecParamIsShow() != 1)) {
//            return builder.setResult(resultBuilder).build();
//        }

        if(Level.isCVLevel(seriesBaseInfo.getLevelId())) {
            specs = specs.stream().sorted(Comparator.comparing(SpecBaseInfo::getSpecOrderForConfig).thenComparing(SpecBaseInfo::getSpecOrdercls).thenComparing(SpecBaseInfo::getId,Comparator.reverseOrder())).collect(Collectors.toList());
        }else{
            specs = specs.stream().sorted(Comparator.comparing(SpecBaseInfo::getSpecOrderForConfig).thenComparing(SpecBaseInfo::getIsclassic).thenComparing(SpecBaseInfo::getSpecOrdercls).thenComparing(SpecBaseInfo::getId,Comparator.reverseOrder())).collect(Collectors.toList());
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

        boolean allIsCVSpec = specIds.stream().allMatch(x -> Spec.isCvSpec(x));
        boolean allIsNotCVSpec = specIds.stream().allMatch(x -> !Spec.isCvSpec(x));

        //防止对原对象进行操作，不在此处处理list
//        if (specIds.stream().allMatch(x -> Spec.isCvSpec(x))) {
//            for (ConfigTypeBaseInfo baseInfo : list) {
//                baseInfo.getItems().removeIf(x -> x.getCVIsShow() != 1);
//            }
//        } else if (specIds.stream().allMatch(x -> !Spec.isCvSpec(x))) {
//            for (ConfigTypeBaseInfo baseInfo : list) {
//                baseInfo.getItems().removeIf(x -> x.getIsShow() != 1);
//            }
//        }
//        list.removeIf(x -> x.getItems() == null || x.getItems().size() == 0);

//
//        System.out.println("seriesId:" + request.getSeriesid() + " count:" + list.get(0).getItems().size());
//        if(list.get(0).getItems().size()<30) {
//            System.out.println("seriesId:" + request.getSeriesid() + " count:" + list.get(0).getItems().size());
//        }
        String defaultValue = itemValues.get(0);

        for (ConfigTypeBaseInfo baseInfo : list) {

            List<ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();

            if (baseInfo.getItems() == null) continue;

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if(allIsCVSpec && item.getCVIsShow() !=1 )continue;
                if(allIsNotCVSpec && item.getIsShow() !=1 )continue;

                if (pevSpecNum == specs.size() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                    continue;
                AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                List<ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem> valueitems = new ArrayList<>();
                specs.forEach(spec -> {
                    int specId = spec.getId();
                    List<SpecConfigRelationEntity> values = datas.get(specId);
                    String key = specId + "-" + item.getItemId();
                    List<SpecConfigPriceEntity> prices = priceItemsMap.containsKey(key) ? priceItemsMap.get(key) : new ArrayList<>();//.stream().filter(x -> x.getSpecId() == specId && x.getItemId() == item.getItemId()).collect(Collectors.toList());
                    ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.Builder valueBuilder = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.newBuilder();
                    valueBuilder.setSpecid(specId);
                    if (item.getDisplayType() == 0) {  //横排
                        SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                        String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                        if (prices != null && prices.size() > 0) {
                            for (SpecConfigPriceEntity price : prices) {
                                ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubPrice.Builder priceBuilder = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubPrice.newBuilder();
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
                                ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.Builder subItem = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.newBuilder();
                                subItem.setSubname(subItems.containsKey(specConfigSubItem.getSubItemId()) ? subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", "") : "");
                                subItem.setSubvalue(specConfigSubItem.getSubValue());
                                subItem.setPrice(price == null ? 0 : price.getPrice());
                                subItem.setSubid(specConfigSubItem.getSubItemId());
                                valueBuilder.addSublist(subItem);
                            }
                        }
                        valueBuilder.addAllPrice(new ArrayList<>());
                        valueBuilder.setValue(valueBuilder.getSublistCount() > 0 ? "" : "-");
                    }
                    valueitems.add(valueBuilder.build());
                });

                if (item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() == specs.size())
                    continue;

                ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Builder itemBuilder = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.newBuilder();
                itemBuilder.setConfigid(item.getItemId());
                itemBuilder.setName(item.getItemName());
                itemBuilder.setDisptype(item.getDisplayType());
                itemBuilder.addAllValueitems(valueitems);
                items.add(itemBuilder.build());
            }
            if (items.size() > 0) {
                ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Builder typeItem = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.newBuilder();
                typeItem.setName(baseInfo.getTypeName());
                typeItem.setGroupname(Spec.DicConfig_Group.containsKey(baseInfo.getTypeName()) ? Spec.DicConfig_Group.get(baseInfo.getTypeName()) : "");
                typeItem.addAllConfigitems(items);
                resultBuilder.addConfigtypeitems(typeItem);
            }
        }

        return builder.setResult(resultBuilder.build()).build();
    }
    /**
     * APP参数配置页展示智能类视频
     * @param request
     * @return
     */
    @GetMapping("/v3/config/configWithAiVideoForApp")
    @Override
    public ConfigWithAiVideoResponse configWithAiVideoForApp(ConfigWithAiVideoRequest request) {
        return seriesService.getConfigWithAiVideoForApp(request);
    }
}