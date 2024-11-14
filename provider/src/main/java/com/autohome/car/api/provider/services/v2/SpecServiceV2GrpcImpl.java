package com.autohome.car.api.provider.services.v2;

import autohome.rpc.car.car_api.v2.spec.*;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.provider.common.MessageUtil;
import com.autohome.car.api.services.SpecService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.ConfigItemBaseInfo;
import com.autohome.car.api.services.basic.models.ConfigTypeBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.specs.*;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.common.SpecElectric;
import com.autohome.car.api.services.impls.AutoCacheServiceImpl;
import com.autohome.car.api.services.models.SpecBaseInfoItems;
import com.autohome.car.api.services.models.SpecParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@DubboService
@RestController
public class SpecServiceV2GrpcImpl extends DubboSpecServiceTriple.SpecServiceImplBase {

    @Autowired
    SpecService specService;

    @Autowired
    SpecParamService specParamService;

    @Autowired
    SpecBaseService specBaseService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    ConfigListService configListService;

    @Autowired
    SpecConfigRelationService specConfigRelationService;


    @Autowired
    ConfigItemValueService configItemValueService;

    @Resource
    SpecListSameYearBaseService specListSameYearBaseService;

    @Resource
    SpecListSameYearByYearService specListSameYearByYearService;

    @Autowired
    SpecConfigBagService specConfigBagService;

    @Autowired
    SpecConfigPriceService specConfigPriceService;

    @Autowired
    SpecConfigBagNewService specConfigBagNewService;

    @Autowired
    AutoCacheServiceImpl autoCacheServiceImpl;

    @Autowired
    ConfigSubItemService configSubItemService;

    @Autowired
    SpecConfigSubItemService specConfigSubItemService;

    @Override
    @GetMapping("/v2/carprice/spec_detailbyseriesId.ashx")
    public GetSpecDetailBySeriesIdResponse getSpecDetailBySeriesId(GetSpecDetailBySeriesIdRequest request) {
        return specService.getSpecDetailBySeriesId(request);
    }

    @Override
    @GetMapping("/v2/CarPic/Spec_PhotoBySpecId.ashx")
    public SpecPhotoBySpecIdResponse specPhotoBySpecId(SpecPhotoBySpecIdRequest request) {
        SpecPhotoBySpecIdResponse.Result.Builder resultBuilder = SpecPhotoBySpecIdResponse.Result.newBuilder();
        for (Integer specId : request.getSpeclistList()) {
            SpecBaseInfo specBaseInfo = specBaseService.get(specId).join();
            if (specBaseInfo == null)
                continue;
            SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(specBaseInfo.getSeriesId()).join();
            if (seriesBaseInfo == null)
                continue;

            SpecPhotoBySpecIdResponse.Result.SpecItem specItem = SpecPhotoBySpecIdResponse.Result.SpecItem.newBuilder()
                    .setId(specId)
                    .setPicpath(ImageUtil.getFullImagePathNew(specBaseInfo.getPngLogo(),false))
                    .setSeriespicpath(StringUtils.isBlank(seriesBaseInfo.getPp()) ? "" : ImageUtil.getFullImagePathNew(seriesBaseInfo.getPp(),false))
                    .build();

            resultBuilder.addSpecitems(specItem);
        }

        return SpecPhotoBySpecIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }


    @Override
    @GetMapping("/v2/carprice/Config_GetListBySpecId.ashx")
    public ConfigGetListBySpecIdResponse configGetListBySpecId(ConfigGetListBySpecIdRequest request) {
        ConfigGetListBySpecIdResponse.Builder builder = ConfigGetListBySpecIdResponse.newBuilder();
        if(request.getSpecid() == 0){
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        builder.setReturnCode(0).setReturnMsg("成功");
        ConfigGetListBySpecIdResponse.Result.Builder resultBuilder = ConfigGetListBySpecIdResponse.Result.newBuilder();
        resultBuilder.setSpecid(request.getSpecid());
        SpecBaseInfo specBaseInfo = specBaseService.get(request.getSpecid()).join();
        Map<Integer, String> itemValues = configItemValueService.get();
        if (null == specBaseInfo || (specBaseInfo.getSpecState() != 40 && specBaseInfo.getIsSpecParamIsShow() != 1)) {
            return builder.setResult(resultBuilder).build();
        }

        List<ConfigTypeBaseInfo> list = configListService.get();
        List<SpecConfigRelationEntity> datas = specConfigRelationService.get(request.getSpecid());
        int defalutValue = Spec.isCvSpec(request.getSpecid()) ? 2 : 1;
        for (ConfigTypeBaseInfo baseInfo : list) {
            ConfigGetListBySpecIdResponse.Result.Configtypeitem.Builder typeBuilder = ConfigGetListBySpecIdResponse.Result.Configtypeitem.newBuilder();
            boolean hasChild = false;
            for (ConfigItemBaseInfo item : baseInfo.getItems().stream().filter(x->x.getDisplayType()==0 && x.getIsShow() == 1).collect(Collectors.toList())) {

                SpecConfigRelationEntity itemValue = datas.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);
                if (itemValue == null) {
                    continue;
                }

                //纯电动车型相关判断，如果是纯电动车型隐藏相关配置项目，但是会显示typename
                hasChild = true;
                if (specBaseInfo.getFuelTypeDetail() == 4 && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                    continue;


                ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.Builder itemBuilder = ConfigGetListBySpecIdResponse.Result.Configtypeitem.Configitem.newBuilder();
                itemBuilder.setName(item.getItemName());
                itemBuilder.setValue(
                        itemValues.containsKey(itemValue.getValueId())
                                ? itemValues.get(itemValue.getValueId())
                                : itemValues.get(defalutValue)
                );
                typeBuilder.addConfigitems(itemBuilder);
            }
            if(!hasChild)
                continue;
            typeBuilder.setName(baseInfo.getTypeName());
            resultBuilder.addConfigtypeitems(typeBuilder);
        }
        return builder.setResult(resultBuilder).build();
    }


    @Override
    @GetMapping("/v2/car/Config_BagBySpecIdList.ashx")
    public ConfigBagBySpecIdListResponse configBagBySpecIdList(ConfigBagBySpecIdListRequest request) {
        ConfigBagBySpecIdListResponse.Builder builder = ConfigBagBySpecIdListResponse.newBuilder();
        if (request.getSpeclistList().size() == 0
                || request.getSpeclistList().stream().distinct().count() < request.getSpeclistCount()
                || request.getSpeclistList().stream().anyMatch(x -> x == 0)
        ) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        Map<Integer, List<SpecConfigBagEntity>> map = specConfigBagNewService.getList(request.getSpeclistList());

        List<ConfigBagBySpecIdListResponse.Result.Bagtypeitem.Bagitem> bagitems = new ArrayList<>();
        for (Integer specId : request.getSpeclistList()) {
            ConfigBagBySpecIdListResponse.Result.Bagtypeitem.Bagitem.Builder bagItemBuilder = ConfigBagBySpecIdListResponse.Result.Bagtypeitem.Bagitem.newBuilder();
            bagItemBuilder.setSpecid(specId);
            List<SpecConfigBagEntity> list = map.get(specId);
            if(list!=null) {
                for (SpecConfigBagEntity valeItem : list) {
                    bagItemBuilder.addValueitems(
                            ConfigBagBySpecIdListResponse.Result.Bagtypeitem.Bagitem.ValueItem.newBuilder()
                                    .setBagid(valeItem.getBagId())
                                    .setName(valeItem.getBagName())
                                    .setDescription(valeItem.getDescrip())
                                    .setPrice(valeItem.getPrice())
                                    .setPricedesc(CommonFunction.getPriceDesc(valeItem.getPrice()))
                    );
                }
            }
            bagitems.add(bagItemBuilder.build());
        }

        return ConfigBagBySpecIdListResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(
                        ConfigBagBySpecIdListResponse.Result.newBuilder().addBagtypeitems(
                                ConfigBagBySpecIdListResponse.Result.Bagtypeitem.newBuilder()
                                        .setName("选装包")
                                        .addAllBagitems(bagitems)
                        )
                )
                .build();
    }

    @Override
    @GetMapping("/v2/CarPic/Spec_PictureCountByCondition.ashx")
    public SpecPictureCountByConditionResponseV2 getSpecPictureCountByCondition(SpecPictureCountByConditionRequestV2 request) {
        return specService.getSpecPictureCountByConditionV2(request);
    }



    @Override
    @GetMapping("/v2/CarPrice/Spec_GetSpecDetailBySeriesId.ashx")
    public SpecGetSpecDetailBySeriesIdResponse specGetSpecDetailBySeriesId(SpecGetSpecDetailBySeriesIdRequest request) {
        return specService.getSpecDetailBySeriesIdV2(request);
    }

    @Override
    @GetMapping("/v2/CarPrice/Spec_BaseInfoBySpecIds.ashx")
    public GetSpecBaseInfoBySpecIdsResponse getSpecBaseInfoBySpecIds(GetSpecBaseInfoBySpecIdsRequest request) {
        ApiResult<SpecBaseInfoItems> apiResult = specService.getSpecBaseInfoBySpecIds(request);
        GetSpecBaseInfoBySpecIdsResponse.Builder builder = GetSpecBaseInfoBySpecIdsResponse.newBuilder()
                .setReturnCode(apiResult.getReturncode())
                .setReturnMsg(apiResult.getMessage());
        if (ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode() != apiResult.getReturncode() || apiResult.getResult() == null) {
            return builder.build();
        }
        GetSpecBaseInfoBySpecIdsResponse.Result result = MessageUtil.toMessage(apiResult.getResult(), GetSpecBaseInfoBySpecIdsResponse.Result.class);
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v2/car/Config_BagOfYearBySpecId.ashx")
    public ConfigBagOfYearBySpecIdResponse configBagOfYearBySpecIdList(ConfigBagOfYearBySpecIdRequest request) {
        ConfigBagOfYearBySpecIdResponse.Builder builder = ConfigBagOfYearBySpecIdResponse.newBuilder();
        if (request.getSpecid() == 0) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        List<ConfigBagOfYearBySpecIdResponse.Result.SpecList> specList = new ArrayList<>();
        List<ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem> bagitems = new ArrayList<>();

        //查年代specids
        List<SpecStateEntity> specStates = specListSameYearBaseService.get(request.getSpecid()).join();
        if(!CollectionUtils.isEmpty(specStates)){
            List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
            Map<Integer,SpecBaseInfo> specInfos = specBaseService.getMap(specIds);
            //查配置全集
            Map<Integer, List<SpecConfigBagEntity>> bagMap = specConfigBagNewService.getList(specIds);

            for(Integer specId :specIds){
                SpecBaseInfo spec = specInfos.get(specId);
                if(CollectionUtils.isEmpty(specInfos)){
                    break;
                }
                if(spec == null){
                    continue;
                }
                ConfigBagOfYearBySpecIdResponse.Result.SpecList.Builder specShow = ConfigBagOfYearBySpecIdResponse.Result.SpecList.newBuilder();
                int showState = (spec.getSpecState() == 10) ? spec.getIsBooked() : -1;
                specList.add(specShow.setSpecid(spec.getId()).setSpecstate(spec.getSpecState()).setShowstate(showState).build());

                ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.Builder bagItemBuilder = ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.newBuilder();
                bagItemBuilder.setSpecid(spec.getId());
                List<SpecConfigBagEntity> list = bagMap == null ? null : bagMap.get(spec.getId());
                if(list!=null) {
                    List<ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.ValueItem> itemList = new ArrayList<>();
                    for (SpecConfigBagEntity valeItem : list) {
                        itemList.add(ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.ValueItem.newBuilder()
                                .setBagid(valeItem.getBagId())
                                .setName(valeItem.getBagName())
                                .setDescription(valeItem.getDescrip())
                                .setPrice(valeItem.getPrice())
                                .setPricedesc(CommonFunction.getPriceDesc(valeItem.getPrice())).build());
                    }
                    List<ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.ValueItem> sortedItemList = itemList.stream()
                            .sorted(Comparator
                                    .comparingInt(ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.ValueItem::getPrice)
                                    .thenComparingInt(ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.Bagitem.ValueItem::getBagid))
                            .collect(Collectors.toList());
                    bagItemBuilder.addAllValueitems(sortedItemList);
                }
                bagitems.add(bagItemBuilder.build());
            }
        }

        return ConfigBagOfYearBySpecIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(
                        ConfigBagOfYearBySpecIdResponse.Result.newBuilder()
                                .addBagtypeitems(
                                        ConfigBagOfYearBySpecIdResponse.Result.Bagtypeitem.newBuilder()
                                                .setName("选装包")
                                                .addAllBagitems(bagitems)
                                )
                                .addAllSpeclist(specList)
                                .setSpecid(request.getSpecid())
                )
                .build();

    }

    @Override
    @GetMapping("/v2/car/Config_BagBySeriesIdYearId.ashx")
    public ConfigBagOfYearByYearIdResponse configBagOfYearByYearIdList(ConfigBagOfYearByYearIdRequest request) {
        ConfigBagOfYearByYearIdResponse.Builder builder = ConfigBagOfYearByYearIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int yearId = request.getYearid();
        if (seriesId == 0 || yearId == 0) {
            return builder.setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        List<ConfigBagOfYearByYearIdResponse.Result.SpecList> specList = new ArrayList<>();
        List<ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem> bagitems = new ArrayList<>();

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(seriesBaseInfo != null){
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            //查年代ids
            List<SpecStateEntity> specStates = specListSameYearByYearService.get(seriesId, yearId, isCV).join();
            if(!CollectionUtils.isEmpty(specStates)){
                specStates.sort(Comparator.comparingInt(SpecStateEntity::getSpecOrder)
                        .thenComparing(SpecStateEntity::getOrderBy)
                        .thenComparingInt(SpecStateEntity::getOrders)
                        .thenComparing(SpecStateEntity::getSpecId, Comparator.reverseOrder()));
                List<Integer> specIds = specStates.stream().filter(x -> x.getSpecIsImage() == 0).map(SpecStateEntity::getSpecId).collect(Collectors.toList());
                Map<Integer,SpecBaseInfo> specInfos = specBaseService.getMap(specIds);
                //查配置全集
                Map<Integer, List<SpecConfigBagEntity>> bagMap = specConfigBagNewService.getList(specIds);

                for(Integer specId :specIds){
                    SpecBaseInfo spec = specInfos.get(specId);
                    if(CollectionUtils.isEmpty(specInfos)){
                        break;
                    }
                    if(spec == null){
                        continue;
                    }
                    ConfigBagOfYearByYearIdResponse.Result.SpecList.Builder specShow = ConfigBagOfYearByYearIdResponse.Result.SpecList.newBuilder();
                    int showState = (spec.getSpecState() == 10) ? spec.getIsBooked() : -1;
                    specList.add(specShow.setSpecid(spec.getId()).setSpecstate(spec.getSpecState()).setShowstate(showState).build());

                    ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.Builder bagItemBuilder = ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.newBuilder();
                    bagItemBuilder.setSpecid(spec.getId());
                    List<SpecConfigBagEntity> list = bagMap == null ? null : bagMap.get(spec.getId());
                    if(list!=null) {
                        List<ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.ValueItem> itemList = new ArrayList<>();
                        for (SpecConfigBagEntity valeItem : list) {
                            itemList.add(ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.ValueItem.newBuilder()
                                    .setBagid(valeItem.getBagId())
                                    .setName(valeItem.getBagName())
                                    .setDescription(valeItem.getDescrip())
                                    .setPrice(valeItem.getPrice())
                                    .setPricedesc(CommonFunction.getPriceDesc(valeItem.getPrice())).build());
                        }
                        List<ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.ValueItem> sortedItemList = itemList.stream()
                                .sorted(Comparator
                                        .comparingInt(ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.ValueItem::getPrice)
                                        .thenComparingInt(ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.Bagitem.ValueItem::getBagid))
                                .collect(Collectors.toList());
                        bagItemBuilder.addAllValueitems(sortedItemList);
                    }
                    bagitems.add(bagItemBuilder.build());
                }
            }
        }


        return ConfigBagOfYearByYearIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(
                        ConfigBagOfYearByYearIdResponse.Result.newBuilder()
                                .addBagtypeitems(
                                        ConfigBagOfYearByYearIdResponse.Result.Bagtypeitem.newBuilder()
                                                .setName("选装包")
                                                .addAllBagitems(bagitems)
                                )
                                .addAllSpeclist(specList)
                                .setSeriesid(seriesId)
                                .setYearid(yearId)
                )
                .build();

    }

    /**
     * 根据多个车型id及城市id获取补贴金额
     * @param request
     * @return
     */
    @GetMapping("/v2/CarPrice/Spec_ElectricSubsidyBySpecList.ashx")
    @Override
    public GetSpecElectricSubsidyBySpecListResponse getSpecElectricSubsidyBySpecList(GetSpecElectricSubsidyBySpecListRequest request) {
        return GetSpecElectricSubsidyBySpecListResponse.newBuilder()
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }
    @Override
    @GetMapping(value = {"/v2/carprice/Config_GetListBySpeclist.ashx", "/v2/carprice/Config_GetListBySpecList.ashx"})
    public ConfigGetListBySpecListResponse configGetListBySpecList(ConfigGetListBySpecListRequest request) {

        if(request.getSpeclistList()==null||request.getSpeclistList().size()==0)
            return ConfigGetListBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();

        List<SpecBaseInfo> specs = specBaseService.getList(request.getSpeclistList());

        if(specs.stream().anyMatch(x-> x!=null && x.getIsSpecParamIsShow()!=1)){
            return ConfigGetListBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        List<Integer> specIds = request.getSpeclistList().stream().distinct().collect(Collectors.toList());

        //电动车车型的数量
        long pevSpecNum = specs.stream().filter(x -> x.getFuelTypeDetail() == 4).count();

        List<ConfigTypeBaseInfo> list = configListService.get().stream().collect(Collectors.toList());
//        if(request.getSpeclistList().stream().allMatch(x->Spec.isCvSpec(x))){
//            for (ConfigTypeBaseInfo baseInfo : list) {
//                baseInfo.getItems().removeIf(x->x.getCVIsShow()!=1);
//            }
//        }else if(request.getSpeclistList().stream().allMatch(x-> !Spec.isCvSpec(x))){
//            for (ConfigTypeBaseInfo baseInfo : list) {
//                baseInfo.getItems().removeIf(x->x.getIsShow()!=1);
//            }
//        }

        boolean allCV = specIds.stream().allMatch(x->Spec.isCvSpec(x));
        boolean allNotCV = specIds.stream().allMatch(x-> !Spec.isCvSpec(x));

//        list.removeIf(x->x.getItems()==null||x.getItems().size()==0);
        Map<Integer, String> itemValues = configItemValueService.get();
        Map<Integer, List<SpecConfigRelationEntity>> datas = specConfigRelationService.getList(specIds);
        String defaultValue = itemValues.get(0);

        ConfigGetListBySpecListResponse.Result.Builder resultBuilder = ConfigGetListBySpecListResponse.Result.newBuilder();

        for (ConfigTypeBaseInfo baseInfo : list) {
            List<ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();

            if (baseInfo.getItems() == null) continue;

            AtomicBoolean hasRelation = new AtomicBoolean(false);

            List<ConfigItemBaseInfo> bis = baseInfo.getItems();
            if(bis == null)
                continue;
            if(allCV){
                bis = bis.stream().filter(x->x.getCVIsShow() == 1).collect(Collectors.toList());
            }else if(allNotCV){
                bis = bis.stream().filter(x->x.getIsShow() == 1).collect(Collectors.toList());
            }
            if(bis.size()==0)
                continue;

            for (ConfigItemBaseInfo item : bis) {
                if (item.getDisplayType() != 0) continue;
                AtomicInteger currentConfigValueEqualNullNum = new AtomicInteger();
                List<ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem> valueitems = new ArrayList<>();

                AtomicInteger rc = new AtomicInteger(0);

                specIds.forEach(specId -> {
                    List<SpecConfigRelationEntity> values = datas.get(specId);
                    ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.Builder valueBuilder = ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Valueitem.newBuilder();
                    valueBuilder.setSpecid(specId);
                    SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                    if (relation != null) {
                        rc.incrementAndGet();
                        hasRelation.set(true);
                    }

                    String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                    valueBuilder.setValue(relation == null || strValue == null ? defaultValue : strValue);
                    if (StringUtils.isBlank(strValue) || strValue.equals("-") || strValue.equals("0")) {
                        currentConfigValueEqualNullNum.addAndGet(1);
                    }
                    valueitems.add(valueBuilder.build());
                });

//                if(item.getDynamicShow() == 1 && currentConfigValueEqualNullNum.get() == request.getSpeclistCount() )
//                    continue;

                if (rc.get() == 0) {
                    continue;
                }


                if (pevSpecNum == request.getSpeclistCount() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                    continue;

                ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.Builder itemBuilder = ConfigGetListBySpecListResponse.Result.Configtypeitem.Configitem.newBuilder();
                itemBuilder.setName(item.getItemName());
                itemBuilder.addAllValueitems(valueitems);
                items.add(itemBuilder.build());
            }
            if(!hasRelation.get())
                continue;
            ConfigGetListBySpecListResponse.Result.Configtypeitem.Builder typeItem = ConfigGetListBySpecListResponse.Result.Configtypeitem.newBuilder();
            typeItem.setName(baseInfo.getTypeName());
            typeItem.addAllConfigitems(items);
            resultBuilder.addConfigtypeitems(typeItem);
        }

        return ConfigGetListBySpecListResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }


    @Override
    @GetMapping("/v2/carprice/Config_GetListBySeriesId.ashx")
    public ConfigGetListBySeriesIdResponse configGetListBySeriesId(ConfigGetListBySeriesIdRequest request) {
        if(request.getSeriesid() == 0) {
            return ConfigGetListBySeriesIdResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        ConfigGetListBySeriesIdResponse.Result.Builder resultBuilder = ConfigGetListBySeriesIdResponse.Result.newBuilder();
        resultBuilder.setSeriesid(request.getSeriesid());

        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(request.getSeriesid()).join();
        if(seriesBaseInfo == null){
            resultBuilder.addAllConfigtypeitems(Collections.emptyList());
            return ConfigGetListBySeriesIdResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("成功")
                    .setResult(resultBuilder.build())
                    .build();
        }

        List<SpecStateEntity> specStates = autoCacheServiceImpl.getSpecListBySeriesId(request.getSeriesid(), Level.isCVLevel(seriesBaseInfo.getLevelId()));
        if(CollectionUtils.isEmpty(specStates)){
            resultBuilder.addAllConfigtypeitems(Collections.emptyList());
            return ConfigGetListBySeriesIdResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("成功")
                    .setResult(resultBuilder.build())
                    .build();
        }

        List<Integer> specIds = specStates.stream().map(SpecStateEntity::getSpecId).collect(Collectors.toList());
        List<SpecBaseInfo> specs = specBaseService.getList(specIds);

        //电动车车型的数量
        long pevSpecNum = specs.stream().filter(x -> x.getFuelTypeDetail() == 4).count();

        List<ConfigTypeBaseInfo> list = configListService.get();
        list = ToolUtils.deepCopyList(list);
        list.removeIf(x->x.getItems()==null||x.getItems().size()==0);

        Map<Integer, String> itemValues = configItemValueService.get();
        Map<Integer, List<SpecConfigRelationEntity>> datas = specConfigRelationService.getList(specIds);
        String defaultValue = itemValues.get(0);

        for (ConfigTypeBaseInfo baseInfo : list) {
            List<ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();

            if (baseInfo.getItems() == null) continue;

            AtomicBoolean hasRelation = new AtomicBoolean(false);

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if (item.getDisplayType() != 0) continue;
                List<ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem> valueitems = new ArrayList<>();

                AtomicInteger rc = new AtomicInteger(0);

                specIds.forEach(specId -> {
                    List<SpecConfigRelationEntity> values = datas.get(specId);
                    ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.Builder valueBuilder = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Valueitem.newBuilder();
                    valueBuilder.setSpecid(specId);
                    SpecConfigRelationEntity relation = values == null ? null : values.stream().filter(x -> x.getItemId() == item.getItemId()).findFirst().orElse(null);

                    if (relation != null) {
                        rc.incrementAndGet();
                        hasRelation.set(true);
                    }

                    String strValue = relation == null ? "" : itemValues.get(relation.getValueId());
                    valueBuilder.setValue(relation == null || strValue == null ? defaultValue : strValue);
                    valueitems.add(valueBuilder.build());
                });

                if (rc.get() == 0) {
                    continue;
                }

                if (pevSpecNum == specIds.size() && SpecElectric.dicExcludePEVCarConfig.containsKey(item.getItemId()))
                    continue;

                ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.Builder itemBuilder = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Configitem.newBuilder();
                itemBuilder.setName(item.getItemName());
                itemBuilder.addAllValueitems(valueitems);
                items.add(itemBuilder.build());
            }
            if(!hasRelation.get())
                continue;
            ConfigGetListBySeriesIdResponse.Result.Configtypeitem.Builder typeItem = ConfigGetListBySeriesIdResponse.Result.Configtypeitem.newBuilder();
            typeItem.setName(baseInfo.getTypeName());
            typeItem.addAllConfigitems(items);
            resultBuilder.addConfigtypeitems(typeItem);
        }

        return ConfigGetListBySeriesIdResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }

    @Override
    @GetMapping("/v2/CarPrice/Config_GetPriceListBySpecList.ashx")
    public ConfigGetPriceListBySpecListResponse configGetPriceListBySpecList(ConfigGetPriceListBySpecListRequest request) {
        List<Integer> specIds = request.getSpeclistList();
        if (specIds.size() == 0 || specIds.size() > 4) {
            return ConfigGetPriceListBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        AtomicReference<List<ConfigTypeBaseInfo>> listTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigPriceEntity>>> priceItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, String>> subItemsTask = new AtomicReference<>();
        AtomicReference<Map<Integer, List<SpecConfigSubItemEntity>>> specSubItemsTask = new AtomicReference<>();
        AtomicReference<List<SpecBaseInfo>> specsTask = new AtomicReference<>();

        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(CompletableFuture.supplyAsync(() -> specBaseService.getList(request.getSpeclistList())).thenAccept(x -> {
            specsTask.set(x);
        }));

        tasks.add(configListService.getAsync().thenAccept(x -> {
            listTask.set(x);
        }));
        tasks.add(specConfigPriceService.getList(request.getSpeclistList()).thenAccept(x -> {
            priceItemsTask.set(x);
        }));
        tasks.add(configSubItemService.getAsync().thenAccept(x -> {
            subItemsTask.set(x);
        }));
        tasks.add(CompletableFuture.supplyAsync(() -> specConfigSubItemService.getList(request.getSpeclistList())).thenAccept(x -> {
            specSubItemsTask.set(x);
        }));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        List<SpecBaseInfo> specs = specsTask.get();

        if (specs.stream().anyMatch(x -> x != null && x.getIsSpecParamIsShow() != 1 && x.getSpecState() != 40)) {
            return ConfigGetPriceListBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }

        List<ConfigTypeBaseInfo> list = listTask.get();
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

        ConfigGetPriceListBySpecListResponse.Result.Builder resultBuilder = ConfigGetPriceListBySpecListResponse.Result.newBuilder();

        for (ConfigTypeBaseInfo baseInfo : list) {
            List<ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem> items = new ArrayList<>();
            if (baseInfo == null || baseInfo.getItems() == null || baseInfo.getItems().size() == 0) continue;

            for (ConfigItemBaseInfo item : baseInfo.getItems()) {
                if(item.getIsShow() != 1) continue;
                List<ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.Priceitem> priceItemList = new ArrayList<>();
                specIds.forEach(specId -> {
                    String key = specId + "-" + item.getItemId();
                    List<SpecConfigPriceEntity> prices = priceItemsMap.getOrDefault(key, null);
                    ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.Priceitem.Builder priceItemBuilder = ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.Priceitem.newBuilder();
                    priceItemBuilder.setSpecid(specId);
                    if (prices != null && prices.size() > 0) {
                        for (SpecConfigPriceEntity price : prices) {
                            ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.Priceitem.Price.Builder priceBuilder = ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.Priceitem.Price.newBuilder();
                            priceBuilder.setName(subItems.containsKey(price.getSubItemId()) ? subItems.get(price.getSubItemId()) : item.getItemName() != null ? item.getItemName() : "");
                            priceBuilder.setPrice(String.format("%d.00", price.getPrice()));
                            priceItemBuilder.addPrice(priceBuilder);
                        }
                        priceItemList.add(priceItemBuilder.build());
                    }
                });
                if (priceItemList.size() > 0) {
                    ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.Builder configItem = ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Configitem.newBuilder();
                    configItem.setName(item.getItemName());
                    configItem.addAllPriceitems(priceItemList);
                    items.add(configItem.build());
                }

            }
            if (items.size() > 0) {
                ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.Builder typeItem = ConfigGetPriceListBySpecListResponse.Result.Configtypeitem.newBuilder();
                typeItem.setName(baseInfo.getTypeName());
                typeItem.addAllConfigitems(items);
                resultBuilder.addConfigtypeitems(typeItem);
            }
        }

        return ConfigGetPriceListBySpecListResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .setResult(resultBuilder.build())
                .build();
    }

    @Override
    @GetMapping("/v2/CarPrice/Spec_BaseInfoBySpecList.ashx")
    public GetSpecBaseInfoBySpecListResponse getSpecBaseInfoBySpecList(GetSpecBaseInfoBySpecListRequest request) {
        List<Integer> specIds = request.getSpeclistList();
        if(specIds.size() == 0 || specIds.stream().anyMatch(val -> val == 0)){
            return GetSpecBaseInfoBySpecListResponse.newBuilder().setReturnCode(102).setReturnMsg("请求参数格式错误").build();
        }
        GetSpecBaseInfoBySpecListResponse.Builder resp = GetSpecBaseInfoBySpecListResponse.newBuilder();
        GetSpecBaseInfoBySpecListResponse.Result.Builder result = GetSpecBaseInfoBySpecListResponse.Result.newBuilder();

        boolean hasAnyNoCV = false;
        List<SpecParam> specParamList = specParamService.getList(specIds);
        if(CollectionUtils.isEmpty(specParamList)){
            result.setRowcount(0);
            result.addAllList(Collections.emptyList());
            return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
        }
        List<SpecParam> sortedList = specParamList.stream().sorted(Comparator.comparing(SpecParam::getSpecid)).collect(Collectors.toList());
        List< GetSpecBaseInfoBySpecListResponse.Result.SpecInfo> specList = new ArrayList<>();
        for(SpecParam specParam :  sortedList){
            if(!Spec.isCvSpec(specParam.getSpecid())){
                hasAnyNoCV = true;
            }
            specList.add(GetSpecBaseInfoBySpecListResponse.Result.SpecInfo.newBuilder()
                        .setSpecid(specParam.getSpecid())
                        .setSpecname(specParam.getSpecname())
                        .setPic(specParam.getSpeclogo())
                        .setSeriesname(specParam.getSeriesname())
                        .setBrandname(specParam.getBrandname())
                        .setMinprice(specParam.getSpecminprice())
                        .setMaxprice(specParam.getSpecmaxprice())
                        .setPowertrain(specParam.getSpecdrivingmodename() != null ? specParam.getSpecdrivingmodename() : "")
                        .setTransmissionitems(specParam.getSpectransmission() != null ? specParam.getSpectransmission() : "")
                        .build()
            );
        }
        //只要存在不是cv的车型，就只取非cv车型
        if(hasAnyNoCV){
            specList = specList.stream().filter(x -> !Spec.isCvSpec(x.getSpecid())).collect(Collectors.toList());
        }

        result.setRowcount(specIds.size());
        result.addAllList(specList);
        return resp.setResult(result).setReturnCode(0).setReturnMsg("成功").build();
    }
}
