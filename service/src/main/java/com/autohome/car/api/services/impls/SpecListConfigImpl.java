package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v3.spec.ConfigGetListBySpecListResponse;
import autohome.rpc.car.car_api.v3.spec.ConfigInfoBySpecIdsAndTypeIdsResponse;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.SpecConfigPriceEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigRelationEntity;
import com.autohome.car.api.data.popauto.entities.SpecConfigSubItemEntity;
import com.autohome.car.api.data.popauto.entities.VisualParamConfigViewEntity;
import com.autohome.car.api.services.SpecListConfigService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.ConfigItemBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.specs.SpecConfigPriceService;
import com.autohome.car.api.services.basic.specs.SpecConfigRelationService;
import com.autohome.car.api.services.basic.specs.SpecConfigSubItemService;
import com.autohome.car.api.services.basic.specs.VisualParamConfigViewBaseService;
import com.autohome.car.api.services.common.SpecElectric;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SpecListConfigImpl implements SpecListConfigService {


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

    @Resource
    private VisualParamConfigViewBaseService visualParamConfigViewBaseService;

    @Resource
    private ConfigSubItemValueRelationService configSubItemValueRelationService;

    @Override
    public ConfigGetListBySpecListResponse configGetListBySpecList(List<Integer> specIds) {

        if(specIds==null||specIds.size()==0)
            return ConfigGetListBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();

        AtomicReference<List<ConfigTypeBaseInfo>> listTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> itemValuesTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigRelationEntity>>> datasTask = new AtomicReference<>();
        AtomicReference<Map<Integer,List<SpecConfigPriceEntity>>> priceItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> subItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> specSubItemsTask = new AtomicReference<>();
        AtomicReference<List<SpecBaseInfo>> specsTask = new AtomicReference<>();

        List<CompletableFuture> tasks = new ArrayList<>();
        long ct = System.currentTimeMillis();

        tasks.add(CompletableFuture.supplyAsync(()->specBaseService.getList(specIds)).thenAccept(x->{
            specsTask.set(x);
        }));

        tasks.add(configListService.getAsync().thenAccept(x -> {
            listTask.set(x);
        }));
        tasks.add(configItemValueService.getAsync().thenAccept(x -> {
            itemValuesTask.set(x);
        }));
        tasks.add(CompletableFuture.supplyAsync(()->specConfigRelationService.getList(specIds)).thenAccept(x-> {
            datasTask.set(x);
        }));
        tasks.add(specConfigPriceService.getList(specIds).thenAccept(x -> {
            priceItemsTask.set(x);
        }));
        tasks.add(configSubItemService.getAsync().thenAccept(x -> {
            subItemsTask.set(x);
        }));
        tasks.add(CompletableFuture.supplyAsync(()->specConfigSubItemService.getList(specIds)).thenAccept(x->{
            specSubItemsTask.set(x);
        }));


        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        List<SpecBaseInfo> specs = specsTask.get();

        if(specs.stream().anyMatch(x-> x!=null && x.getIsSpecParamIsShow()!=1)){
            return ConfigGetListBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        //电动车车型的数量
        long pevSpecNum = specs.stream().filter(x -> x.getFuelTypeDetail() == 4).count();

        List<ConfigTypeBaseInfo> list = listTask.get().stream().collect(Collectors.toList());
        Map<Integer, String> itemValues = itemValuesTask.get();
        Map<Integer, List<SpecConfigRelationEntity>> datas = datasTask.get();
        Map<Integer,List<SpecConfigPriceEntity>> priceItems = priceItemsTask.get();
        Map<Integer, String> subItems = subItemsTask.get();
        Map<Integer, List<SpecConfigSubItemEntity>> specSubItems = specSubItemsTask.get();


        Map<String,List<SpecConfigPriceEntity>> priceItemsMap = new LinkedHashMap<>();
        priceItems.forEach((k,v)->{
            for (SpecConfigPriceEntity item : v) {
                String key = k+"-"+item.getItemId();
                if(!priceItemsMap.containsKey(key)){
                    priceItemsMap.put(key,new ArrayList<>());
                }
                priceItemsMap.get(key).add(item);
            }

        });

        Map<String,List<SpecConfigSubItemEntity>> specSubItemsMap = new LinkedHashMap<>();
        for (Integer specId : specSubItems.keySet()) {
            for (SpecConfigSubItemEntity item : specSubItems.get(specId)) {
                String key = specId+"-"+item.getItemId();
                if(!specSubItemsMap.containsKey(key)){
                    specSubItemsMap.put(key,new ArrayList<>());
                }
                specSubItemsMap.get(key).add(item);
            }
        }

        boolean allCV = specIds.stream().allMatch(x-> Spec.isCvSpec(x));
        boolean allNotCV = specIds.stream().allMatch(x-> !Spec.isCvSpec(x));

        String defaultValue = itemValues.get(0);

        ConfigGetListBySpecListResponse.Result.Builder resultBuilder = ConfigGetListBySpecListResponse.Result.newBuilder();

        for (ConfigTypeBaseInfo baseInfo : list) {

            List<ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();

            if(baseInfo.getItems()==null) continue;

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if(allCV && item.getCVIsShow()!=1) continue;
                if(allNotCV && item.getIsShow()!=1) continue;
                if (pevSpecNum == specIds.size() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                    continue;
                AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                List<ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem> valueitems = new ArrayList<>();
                specIds.forEach(specId -> {
                    List<SpecConfigRelationEntity> values = datas.get(specId);
                    String key = specId+"-"+item.getItemId();
                    List<SpecConfigPriceEntity> prices = priceItemsMap.containsKey(key)? priceItemsMap.get(key):new ArrayList<>();//.stream().filter(x -> x.getSpecId() == specId && x.getItemId() == item.getItemId()).collect(Collectors.toList());
                    ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.Builder valueBuilder = ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.newBuilder();
                    valueBuilder.setSpecid(specId);
                    if (item.getDisplayType() == 0) {  //横排
                        SpecConfigRelationEntity relation =values ==null?null: values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                        String strValue =relation ==null?"" : itemValues.get(relation.getValueId());
                        if (prices != null && prices.size() > 0) {
                            for (SpecConfigPriceEntity price : prices) {
                                ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubPrice.Builder priceBuilder = ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubPrice.newBuilder();
                                priceBuilder.setSubname(subItems.containsKey(price.getSubItemId())?subItems.get(price.getSubItemId()):"");
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
                        String skey = specId+"-"+item.getItemId();
                        List<SpecConfigSubItemEntity> specSubItemList = specSubItemsMap.containsKey(skey)?specSubItemsMap.get(skey) : new ArrayList<>() ;// !specSubItems.containsKey(specId) ? null : specSubItems.get(specId).stream().filter(x -> x.getItemId() == item.getItemId()).collect(Collectors.toList());
                        if (specSubItemList == null || specSubItemList.size() == 0) {
                            currentConfigValueEqualNullNum.addAndGet(1);
                        } else {
                            Map<Integer,SpecConfigPriceEntity> priceMap = new LinkedHashMap<>();
                            for (SpecConfigPriceEntity price : prices) {
                                priceMap.put(price.getSubItemId(),price);
                            }
                            for (SpecConfigSubItemEntity specConfigSubItem : specSubItemList) {
                                SpecConfigPriceEntity price = priceMap.containsKey(specConfigSubItem.getSubItemId())?priceMap.get(specConfigSubItem.getSubItemId()) : null;//  prices.stream().filter(x -> x.getSubItemId() == specConfigSubItem.getSubItemId()).findFirst().orElse(null);
                                ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.Builder subItem = ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.SubItem.newBuilder();
                                subItem.setSubname(subItems.containsKey(specConfigSubItem.getSubItemId())?subItems.get(specConfigSubItem.getSubItemId()).replace("标配/选配", ""):"");
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

                if(item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() == specIds.size() )
                    continue;

                ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Builder itemBuilder = ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.newBuilder();
                itemBuilder.setConfigid(item.getItemId());
                itemBuilder.setName(item.getItemName());
                itemBuilder.setDisptype(item.getDisplayType());
                itemBuilder.addAllValueitems(valueitems);
                items.add(itemBuilder.build());
            }
            if(items.size()>0) {
                ConfigGetListBySpecListResponse.Result.Configtypeitem.Builder typeItem = ConfigGetListBySpecListResponse.Result.Configtypeitem.newBuilder();
                typeItem.setName(baseInfo.getTypeName());
                typeItem.setGroupname(Spec.DicConfig_Group.containsKey(baseInfo.getTypeName()) ? Spec.DicConfig_Group.get(baseInfo.getTypeName()) : "");
                typeItem.addAllConfigitems(items);
                resultBuilder.addConfigtypeitems(typeItem);
            }
        }

        return ConfigGetListBySpecListResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }
    /**
     * 多个车型获取配置信息（新接口; .net没有）
     * @param specIds
     * @return
     */
    @Override
    public ConfigInfoBySpecIdsAndTypeIdsResponse getConfigInfoBySpecIdsAndTypeIds(List<Integer> specIds, List<Integer> typeIds) {

        AtomicReference<List<ConfigTypeBaseInfo>> listTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigRelationEntity>>> datasTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> subItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> specSubItemsTask = new AtomicReference<>();
        AtomicReference<List<SpecBaseInfo>> specsTask = new AtomicReference<>();
        AtomicReference<Map<Integer,List<VisualParamConfigViewEntity>>> visualParamConfigTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> configSubItemValueTask = new AtomicReference<>();

        List<CompletableFuture> tasks = new ArrayList<>();
        //车型信息
        tasks.add(CompletableFuture.supplyAsync(()->specBaseService.getList(specIds)).thenAccept(x->{
            specsTask.set(x);
        }));
        //配置大类
        tasks.add(configListService.getAsync().thenAccept(x -> {
            listTask.set(x);
        }));
        //主配置关联车型
        tasks.add(CompletableFuture.supplyAsync(()->specConfigRelationService.getList(specIds)).thenAccept(x-> {
            datasTask.set(x);
        }));
        //子配置id和name
        tasks.add(configSubItemService.getAsync().thenAccept(x -> {
            subItemsTask.set(x);
        }));
        //子配置关联车型
        tasks.add(CompletableFuture.supplyAsync(()->specConfigSubItemService.getList(specIds)).thenAccept(x->{
            specSubItemsTask.set(x);
        }));

        //配置项是否关联图片
        tasks.add(CompletableFuture.supplyAsync(()->visualParamConfigViewBaseService.getList(specIds)).thenAccept(x->{
            visualParamConfigTask.set(x);
        }));
        //ConfigSpecRelation(cv_ConfigSpecRelation)和ConfigSubItemValueRelation表inner
        tasks.add(CompletableFuture.supplyAsync(()->configSubItemValueRelationService.getList(specIds)).thenAccept(x->{
            configSubItemValueTask.set(x);
        }));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        List<SpecBaseInfo> specBaseInfos = specsTask.get();

        if(specBaseInfos.stream().anyMatch(specBaseInfo -> null != specBaseInfo && specBaseInfo.getIsSpecParamIsShow()!=1)){
            return ConfigInfoBySpecIdsAndTypeIdsResponse.newBuilder().
                    setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    build();
        }

        //电动车车型的数量
        long pevSpecNum = specBaseInfos.stream().filter(x -> x.getFuelTypeDetail() == 4).count();

        List<ConfigTypeBaseInfo> list = listTask.get().stream().collect(Collectors.toList());
        Map<Integer, List<SpecConfigRelationEntity>> datas = datasTask.get();
        Map<Integer, String> subItems = subItemsTask.get();
        Map<Integer, List<SpecConfigSubItemEntity>> specSubItems = specSubItemsTask.get();
        Map<Integer, List<VisualParamConfigViewEntity>> visualParamConfigs = visualParamConfigTask.get();
        Map<Integer, List<SpecConfigSubItemEntity>> configSubItemValueRelationsMap = configSubItemValueTask.get();

        Map<String,List<SpecConfigSubItemEntity>> specSubItemsMap = new LinkedHashMap<>();
        for (Integer specId : specSubItems.keySet()) {
            for (SpecConfigSubItemEntity item : specSubItems.get(specId)) {
                String key = specId+"-"+item.getItemId();
                if(!specSubItemsMap.containsKey(key)){
                    specSubItemsMap.put(key,new ArrayList<>());
                }
                specSubItemsMap.get(key).add(item);
            }
        }
        Map<String,List<SpecConfigSubItemEntity>> specSubItemValuesMap = new LinkedHashMap<>();
        for (Integer specId : configSubItemValueRelationsMap.keySet()) {
            for (SpecConfigSubItemEntity item : configSubItemValueRelationsMap.get(specId)) {
                String key = specId+"-"+item.getItemId();
                if(!specSubItemValuesMap.containsKey(key)){
                    specSubItemValuesMap.put(key,new ArrayList<>());
                }
                specSubItemValuesMap.get(key).add(item);
            }
        }

        boolean allCV = specIds.stream().allMatch(x-> Spec.isCvSpec(x));
        boolean allNotCV = specIds.stream().allMatch(x-> !Spec.isCvSpec(x));

        //result
        List<ConfigInfoBySpecIdsAndTypeIdsResponse.Result> resultList = new ArrayList<>();
        //车型
        for(Integer specId : specIds){
            ConfigInfoBySpecIdsAndTypeIdsResponse.Result.Builder result = ConfigInfoBySpecIdsAndTypeIdsResponse.Result.newBuilder();
            //大类项
            List<ConfigInfoBySpecIdsAndTypeIdsResponse.ConfigTypeItem> configTypeItems = new ArrayList<>();
            //子配置项是否关联图片
            List<VisualParamConfigViewEntity> paramConfigViewEntities = visualParamConfigs.get(specId);
            //ConfigSpecRelation表
            List<SpecConfigRelationEntity> values = datas.get(specId);

            for (ConfigTypeBaseInfo baseInfo : list) {
                //用户传大分类id，如果当前没有大分类，则跳过
                if(!typeIds.contains(baseInfo.getTypeId())){
                    continue;
                }
                ConfigInfoBySpecIdsAndTypeIdsResponse.ConfigTypeItem.Builder configTypeItem = ConfigInfoBySpecIdsAndTypeIdsResponse.ConfigTypeItem.newBuilder();

                List<ConfigInfoBySpecIdsAndTypeIdsResponse.ConfigItem> configItems = new ArrayList<>();
                if(null == baseInfo.getItems()){
                    continue;
                }
                //主配置项
                for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                    //主配置赋值
                    ConfigInfoBySpecIdsAndTypeIdsResponse.ConfigItem.Builder configItem = ConfigInfoBySpecIdsAndTypeIdsResponse.ConfigItem.newBuilder();
                    //都是商用车
                    if(allCV && item.getCVIsShow() != 1) {
                        continue;
                    }
                    //都不是商用车
                    if(allNotCV && item.getIsShow() != 1) {
                        continue;
                    }
                    //如果全是纯电动车型不加载汽油车的相关配置项
                    if (pevSpecNum == specIds.size() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId())) {
                        continue;
                    }

                    //车型子配置项
                    List<ConfigInfoBySpecIdsAndTypeIdsResponse.SubItem> subItemsBuilder = new ArrayList<>();

                    String key = specId + "-" + item.getItemId();
                    List<SpecConfigSubItemEntity> specSubItemValueList = specSubItemValuesMap.containsKey(key)?specSubItemValuesMap.get(key) : new ArrayList<>() ;
                    List<SpecConfigSubItemEntity> specSubItemList = specSubItemsMap.containsKey(key)?specSubItemsMap.get(key) : new ArrayList<>() ;
                    //ConfigSubItemValueRelation表中的数据和ConfigSubItemSpecRelation组成子项信息
                    if(!CollectionUtils.isEmpty(specSubItemValueList)){
                        specSubItemList.addAll(specSubItemValueList);
                    }
                    if(!CollectionUtils.isEmpty(specSubItemList)){
                        //具体子配置项
                        for (SpecConfigSubItemEntity specConfigSubItem : specSubItemList) {
                            ConfigInfoBySpecIdsAndTypeIdsResponse.SubItem.Builder subItemBuilder = ConfigInfoBySpecIdsAndTypeIdsResponse.SubItem.newBuilder();
                            VisualParamConfigViewEntity paramConfigView = null != paramConfigViewEntities ? paramConfigViewEntities.stream().
                                    filter(x -> x.getSubId() == specConfigSubItem.getSubItemId() && x.getDataType() == 2 && x.getSpecState() >= 10 && x.getSpecState()<=30).findFirst().orElse(null) : null;
                            subItemBuilder.setSubname(subItems.containsKey(specConfigSubItem.getSubItemId()) ? subItems.get(specConfigSubItem.getSubItemId()) : "");
                            subItemBuilder.setSubvalue(specConfigSubItem.getSubValue());
                            subItemBuilder.setSubid(specConfigSubItem.getSubItemId());
                            //子配置项是否关联图片
                            subItemBuilder.setRelationpic(null != paramConfigView ? 1 : 0);
                            //添加多个subItem
                            subItemsBuilder.add(subItemBuilder.build());
                        }
                    }
                    //动态外显是否需要判断，目前说的去掉判断
                    //判断是否动态隐藏指定范围内的配置项，如果是且该配置项下没有值，不显示此配置项
//                    if(item.getDynamicShow() == 1) {
//                        continue;
//                    }
                    SpecConfigRelationEntity relation = values == null ? null: values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                    //主配置项
                    configItem.setItemid(item.getItemId());
                    configItem.setItemname(item.getItemName());
                    configItem.setDisptype(item.getDisplayType());
                    configItem.setItemvalue(relation == null ? 0 : relation.getValueId());

                    //主项是否关联图片
                    VisualParamConfigViewEntity paramConfigView = null != paramConfigViewEntities ? paramConfigViewEntities.stream().
                            filter(x -> x.getItemId() == item.getItemId() && x.getDataType() == 2 && x.getSubId() == 0 && x.getSpecState() >= 10 && x.getSpecState()<=30).findFirst().orElse(null) : null;
                    configItem.setRelationpic(null != paramConfigView ? 1 : 0);
                    configItem.addAllSubitems(subItemsBuilder);
                    //添加多个configItems
                    configItems.add(configItem.build());
                }
                //大项
                configTypeItem.setTypeid(baseInfo.getTypeId());
                configTypeItem.setTypename(baseInfo.getTypeName());
                configTypeItem.addAllConfigitems(configItems);
                //添加多个configTypeItem
                configTypeItems.add(configTypeItem.build());
            }
            result.setSpecid(specId);
            result.addAllConfigtypeitems(configTypeItems);
            //添加多个result
            resultList.add(result.build());
        }
        //返回结果赋值
        return ConfigInfoBySpecIdsAndTypeIdsResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .addAllResult(resultList)
                .build();
    }

}
