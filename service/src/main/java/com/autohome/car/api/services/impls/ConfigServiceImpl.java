package com.autohome.car.api.services.impls;


import autohome.rpc.car.car_api.v1.car.*;
import autohome.rpc.car.car_api.v1.config.GetConfigItemBaseInfoRequest;
import autohome.rpc.car.car_api.v1.config.GetConfigItemBaseInfoResponse;
import autohome.rpc.car.car_api.v3.GetSpecificConfigBySeriesIdRequest;
import autohome.rpc.car.car_api.v3.GetSpecificConfigBySpecListRequest;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.ConfigService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.SpecBaseInfo;
import com.autohome.car.api.services.basic.models.SpecificConfigInfo;
import com.autohome.car.api.services.basic.specs.SpecConfigService;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.ParamTypeItems;
import com.autohome.car.api.services.models.config.SeriesSpecificConfig;
import com.autohome.car.api.services.models.config.SpecificConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private SpecBaseService specBaseService;

    @Resource
    private SpecificConfigBaseService specificConfigBaseService;

    @Resource
    private CommService commService;

    @Resource
    private SeriesSpecBaseService seriesSpecBaseService;

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private AutoCacheService autoCacheService;

    @Resource
    SpecListSameYearByYearService specListSameYearByYearService;


    @Resource
    private SpecListSameYearBaseService specListSameYearBaseService;

    @Autowired
    private OptParItemInfoService optParItemInfoService;

    @Autowired
    private SpecConfigService specConfigService;

    @Resource
    private ParamConfigModelService paramConfigModelService;
    /**
     * 根据多个车型id获取多个配置信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<SpecificConfig> getSpecificConfigBySpecList(GetSpecificConfigBySpecListRequest request) {
        String[] splitSpecIds = request.getSpeclist().split(",");

        if(splitSpecIds.length == 0 || splitSpecIds.length > 100){
            return new ApiResult<>(null, ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }

        List<Integer> specList = CommonFunction.getListFromStr(request.getSpeclist()).stream().distinct().collect(Collectors.toList());
        //判断重复及是否包含0
        if(specList.size() != splitSpecIds.length || specList.contains(0)){
            return new ApiResult<>(null, ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specList);
        boolean flag = false;
        for (int i = 0; i < specList.size(); i++) {
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specList.get(i));
            if (null != specBaseInfo && !SpecParamIsShow(specBaseInfo)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            return new ApiResult<>(null, ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        //返回信息查询组装
        SpecificConfig specificConfig = new SpecificConfig();
        specificConfig.setConfigitems(getConfigItems(specList));
        return new ApiResult<>(specificConfig,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     *根据车系id获取多个配置信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<SeriesSpecificConfig> getSpecificConfigBySeriesId(GetSpecificConfigBySeriesIdRequest request) {
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        List<SpecViewEntity> viewEntities = null;
        boolean isCV = false;
        if(null != seriesBaseInfo){
            isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            viewEntities = seriesSpecBaseService.get(seriesId, isCV).join();
        }
        List<Integer> specIds = null;
        if(!CollectionUtils.isEmpty(viewEntities)){
            if(isCV){
                viewEntities = viewEntities.stream().filter(specViewEntity ->
                                specViewEntity.getSpecState() >= 10 && specViewEntity.getSpecState() <= 30 && specViewEntity.getSpecIsshow() == 1).
                        sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).
                        thenComparing(SpecViewEntity::getSpecOrdercls).
                        thenComparing(SpecViewEntity::getSpecId,Comparator.reverseOrder())).collect(Collectors.toList());
            }else{
                viewEntities = viewEntities.stream().filter(specViewEntity ->
                                specViewEntity.getSpecState() <= 30 && specViewEntity.getSpecIsImage() == 0 && specViewEntity.getSpecIsshow() == 1)
                        .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).
                                thenComparing(SpecViewEntity::getIsclassic).
                                thenComparing(SpecViewEntity::getSpecOrdercls).
                                thenComparing(SpecViewEntity::getSpecId,Comparator.reverseOrder())).collect(Collectors.toList());
            }
            specIds = viewEntities.stream().map(specViewEntity -> specViewEntity.getSpecId()).collect(Collectors.toList());
        }
        SeriesSpecificConfig seriesSpecificConfig = new SeriesSpecificConfig();
        seriesSpecificConfig.setSeriesid(seriesId);
        seriesSpecificConfig.setConfigitems(getConfigItems(specIds));
        return new ApiResult<>(seriesSpecificConfig,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }
    /**
     * 获取配置项基本信息
     * @param request
     * @return
     */
    @Override
    public GetConfigItemBaseInfoResponse getConfigItemBaseInfo(GetConfigItemBaseInfoRequest request) {
        GetConfigItemBaseInfoResponse.Builder builder = GetConfigItemBaseInfoResponse.newBuilder();
        List<ConfigBaseEntity> configBaseEntities = autoCacheService.getConfigItemAll();

        if(!CollectionUtils.isEmpty(configBaseEntities)){
            for(ConfigBaseEntity configBaseEntity : configBaseEntities){
                GetConfigItemBaseInfoResponse.Result.Builder resultBuilder = GetConfigItemBaseInfoResponse.Result.newBuilder();
                resultBuilder.setId(configBaseEntity.getId());
                resultBuilder.setName(null != configBaseEntity.getName() ? configBaseEntity.getName() : "");
                resultBuilder.setTypeid(configBaseEntity.getTypeId());
                resultBuilder.setDisptype(configBaseEntity.getDisplayType());
                resultBuilder.setIsshow(configBaseEntity.getIsShow());
                resultBuilder.setCvisshow(configBaseEntity.getCvIsShow());
                resultBuilder.setItemorder(configBaseEntity.getItemOrder());
                resultBuilder.setTypeorder(configBaseEntity.getTypeOrder());
                builder.addResult(resultBuilder);
            }
        }
        return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    /**
     * 根据车型id获取其同年代款下所有车型的配置（包含选装价格）
     * @param request
     * @return
     */
    @Override
    public GetConfigListOfYearBySpecIdResponse getConfigListOfYearBySpecId(GetConfigListOfYearBySpecIdRequest request) {
        GetConfigListOfYearBySpecIdResponse.Builder builder = GetConfigListOfYearBySpecIdResponse.newBuilder();
        int specId = request.getSpecid();
        if(specId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    build();
        }
        List<SpecStateEntity> specStateEntities = specListSameYearBaseService.get(specId).join();
        GetConfigListOfYearBySpecIdResponse.Result.Builder resultBuilder = GetConfigListOfYearBySpecIdResponse.Result.newBuilder();
        resultBuilder.setSpecid(specId);
        if(CollectionUtils.isEmpty(specStateEntities)){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                    .setResult(resultBuilder)
                    .build();
        }
        //车型信息,sql不对逻辑处理(现在底层sql没改，因为好多地方在用，如果sql改了，可以去掉过滤逻辑)
        List<Integer> specIds = specStateEntities.stream().filter(specStateEntity -> Spec.isCvSpec(specStateEntity.getSpecId()) ?
                specStateEntity.getSpecState() >= 10 : specStateEntity.getSpecIsImage() == 0)
                .map(SpecStateEntity::getSpecId).distinct().collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);
        //SpecItem赋值
        for(SpecStateEntity specStateEntity : specStateEntities){
            //商用车,sql不对逻辑处理(现在底层sql没改，因为好多地方在用，如果sql改了，可以去掉过滤逻辑)
            if(Spec.isCvSpec(specStateEntity.getSpecId())){
                if(specStateEntity.getSpecState() < 10){
                    continue;
                }
            }else{
                if(specStateEntity.getSpecIsImage() != 0){
                    continue;
                }
            }
            SpecListItem.Builder specItem = SpecListItem.newBuilder();
            specItem.setSpecid(specStateEntity.getSpecId());
            specItem.setSpecstate(specStateEntity.getSpecState());
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specStateEntity.getSpecId());
            int showstate = 0;
            //即将销售接受预定 obj.Key==1接受预订
            if (specStateEntity.getSpecState() == 10) {
                showstate = null != specBaseInfo && specBaseInfo.getIsBooked() == 1 ? 1 : 0;
            } else {
                showstate = -1;
            }
            specItem.setShowstate(showstate);
            resultBuilder.addSpeclist(specItem);
        }
        if(specIds.size() > 0){
            //显示类型：默认是横向，值为1垂直显示配置项
            Pair<ReturnMessageEnum, List<ConfigTypeItem>> configRes = commService.getConfigListBySpecList(specIds, request.getDisptype());
            if(configRes.getKey() != RETURN_MESSAGE_ENUM0){
                return builder.setReturnCode(configRes.getKey().getReturnCode())
                        .setReturnMsg(configRes.getKey().getReturnMsg())
                        .build();
            }
            if(!CollectionUtils.isEmpty(configRes.getValue())){
                resultBuilder.addAllConfigtypeitems(configRes.getValue());
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }
    /**
     * 根据车型id获取多个参数信息
     * @param request
     * @return
     */
    @Override
    public GetSpecParamListBySpecIdResponse getSpecParamListBySpecId(GetSpecParamListBySpecIdRequest request) {
        GetSpecParamListBySpecIdResponse.Builder builder = GetSpecParamListBySpecIdResponse.newBuilder();
        int specId = request.getSpecid();
        if(specId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    build();
        }
        GetSpecParamListBySpecIdResponse.Result.Builder resultBuilder = GetSpecParamListBySpecIdResponse.Result.newBuilder();
        resultBuilder.setSpecid(specId);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<SpecStateEntity> specStateEntities = specListSameYearBaseService.get(specId).join();
        //为空判断
        if(CollectionUtils.isEmpty(specStateEntities)){
            stopWatch.stop();
            resultBuilder.setT1((int) stopWatch.getTotalTimeMillis());
            return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                    .setResult(resultBuilder)
                    .build();
        }
        //车型信息,sql不对逻辑处理(现在底层sql没改，因为好多地方在用，如果sql改了，可以去掉过滤逻辑)
        List<Integer> specIds = specStateEntities.stream().filter(specStateEntity -> Spec.isCvSpec(specStateEntity.getSpecId()) ?
                        specStateEntity.getSpecState() >= 10 : specStateEntity.getSpecIsImage() == 0)
                .map(SpecStateEntity::getSpecId).distinct().collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);

        //SpecItem赋值
        for(SpecStateEntity specStateEntity : specStateEntities){
            //商用车,sql不对逻辑处理(现在底层sql没改，因为好多地方在用，如果sql改了，可以去掉过滤逻辑)
            if(Spec.isCvSpec(specStateEntity.getSpecId())){
                if(specStateEntity.getSpecState() < 10){
                    continue;
                }
            }else{
                if(specStateEntity.getSpecIsImage() != 0){
                    continue;
                }
            }
            SpecListItem.Builder specItem = SpecListItem.newBuilder();
            specItem.setSpecid(specStateEntity.getSpecId());
            specItem.setSpecstate(specStateEntity.getSpecState());
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specStateEntity.getSpecId());
            int showstate = 0;
            //即将销售接受预定 obj.Key==1接受预订
            if (specStateEntity.getSpecState() == 10) {
                showstate = null != specBaseInfo && specBaseInfo.getIsBooked() == 1 ? 1 : 0;
            } else {
                showstate = -1;
            }
            specItem.setShowstate(showstate);
            resultBuilder.addSpeclist(specItem);
        }
        stopWatch.stop();
        resultBuilder.setT1((int) stopWatch.getTotalTimeMillis());
        KeyValueDto<Boolean, Boolean> keyValueDto = CommonFunction.getCvType(specIds);
        //【获取数据】
        List<Map<String, Object>> specConfigMaps = this.getSpecConfigMaps(keyValueDto.getKey(), keyValueDto.getValue(),specIds);
        ////参数配置名维表信息，用来上报鼠标悬停统计
        int modelId = new Random().nextInt(2) + 1;
        Map<String, Integer> configModelMap = paramConfigModelService.getConfMap(modelId);
        int paramConfigModelId = modelId;
        //获取新能源 ，纯电，油车等的数量
        ParamTypeItems.TempInfo tempInfo = getTempInfo(specIds, specBaseInfoMap);
        //插电和油电没有“	系统综合扭矩(N·m)”时  要不要分开展示“发动机最大扭矩(N·m)”和“电动机总扭矩(N·m)”
        //true 时 基本参数大类下 最大功率不显示 分别显示
        boolean reWriteMaxKw = isReWriteMaxKw(specIds, specConfigMaps,specBaseInfoMap);
        List<ParaTypeItem.ParaItem> arrMaxKw = getList(specIds, specConfigMaps, reWriteMaxKw, "最大功率(kW)", "电动机总功率(kW)", "发动机最大功率(kW)",specBaseInfoMap);
        boolean reWriteMaxTorque = isReWriteMaxTorque(specIds, specConfigMaps,specBaseInfoMap);
        List<ParaTypeItem.ParaItem> arrMaxTorque = getList(specIds, specConfigMaps, reWriteMaxTorque, "最大扭矩(N·m)", "电动机总扭矩(N·m)", "发动机最大扭矩(N·m)",specBaseInfoMap);
        List<ParaTypeItem> paramTypeItems = getParamTypeItems(specIds, specConfigMaps, tempInfo, reWriteMaxKw, arrMaxKw, reWriteMaxTorque, arrMaxTorque,configModelMap,paramConfigModelId);
        //ParaTypeItem赋值
        resultBuilder.addAllParamtypeitems(paramTypeItems);

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 根据车系id获取多个参数信息
     * @param request
     * @return
     */
    @Override
    public GetSpecParamListBySeriesIdResponse getSpecParamListBySeriesId(GetSpecParamListBySeriesIdRequest request) {
        GetSpecParamListBySeriesIdResponse.Builder builder = GetSpecParamListBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    build();
        }
        GetSpecParamListBySeriesIdResponse.Result.Builder resultBuilder = GetSpecParamListBySeriesIdResponse.Result.newBuilder();
        //GetSpecListBySeriesId逻辑 specItem赋值
        List<SpecListItem> specItems = getSpecList(seriesId);
        resultBuilder.addAllSpeclist(specItems);
        //seriesId 赋值
        resultBuilder.setSeriesid(seriesId);
        List<Integer> specIdList = specItems.stream().map(SpecListItem::getSpecid).distinct().collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIdList);
        //GetParamsForJava逻辑
        KeyValueDto<Boolean, Boolean> keyValueDto = CommonFunction.getCvType(specIdList);
        //【获取数据】
        List<Map<String, Object>> specConfigMaps = this.getSpecConfigMaps(keyValueDto.getKey(), keyValueDto.getValue(),specIdList);
        ////参数配置名维表信息，用来上报鼠标悬停统计
        int modelId = new Random().nextInt(2) + 1;
        Map<String, Integer> configModelMap = paramConfigModelService.getConfMap(modelId);
        int paramConfigModelId = modelId;
        //获取新能源 ，纯电，油车等的数量
        ParamTypeItems.TempInfo tempInfo = getTempInfo(specIdList, specBaseInfoMap);
        //插电和油电没有“	系统综合扭矩(N·m)”时  要不要分开展示“发动机最大扭矩(N·m)”和“电动机总扭矩(N·m)”
        //true 时 基本参数大类下 最大功率不显示 分别显示
        boolean reWriteMaxKw = isReWriteMaxKw(specIdList, specConfigMaps,specBaseInfoMap);
        List<ParaTypeItem.ParaItem> arrMaxKw = getList(specIdList, specConfigMaps, reWriteMaxKw, "最大功率(kW)", "电动机总功率(kW)", "发动机最大功率(kW)",specBaseInfoMap);
        boolean reWriteMaxTorque = isReWriteMaxTorque(specIdList, specConfigMaps,specBaseInfoMap);
        List<ParaTypeItem.ParaItem> arrMaxTorque = getList(specIdList, specConfigMaps, reWriteMaxTorque, "最大扭矩(N·m)", "电动机总扭矩(N·m)", "发动机最大扭矩(N·m)",specBaseInfoMap);
        List<ParaTypeItem> paramTypeItems = getParamTypeItems(specIdList, specConfigMaps, tempInfo, reWriteMaxKw, arrMaxKw, reWriteMaxTorque, arrMaxTorque,configModelMap,paramConfigModelId);
        //ParaTypeItem赋值
        resultBuilder.addAllParamtypeitems(paramTypeItems);

        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    /**
     * 根据车系id获取多个配置信息
     * @param request
     * @return
     */
    @Override
    public GetConfigListBySeriesIdResponse getConfigListBySeriesId(GetConfigListBySeriesIdRequest request) {
        GetConfigListBySeriesIdResponse.Builder builder = GetConfigListBySeriesIdResponse.newBuilder();
        int seriesId = request.getSeriesid();
        if(seriesId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).
                    setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    build();
        }
        GetConfigListBySeriesIdResponse.Result.Builder resultBuilder = GetConfigListBySeriesIdResponse.Result.newBuilder();
        //GetSpecListBySeriesId逻辑 specItem赋值
        List<SpecListItem> specItems = getSpecList(seriesId);
        resultBuilder.addAllSpeclist(specItems);
        List<Integer> specIdList = specItems.stream().map(SpecListItem::getSpecid).distinct().collect(Collectors.toList());
        //seriesId 赋值
        resultBuilder.setSeriesid(seriesId);
        if(specIdList.size() > 0){
            //显示类型：默认是横向，值为1垂直显示配置项
            Pair<ReturnMessageEnum, List<ConfigTypeItem>> configRes = commService.getConfigListBySpecList(specIdList, request.getDisptype());
            if(configRes.getKey() != RETURN_MESSAGE_ENUM0){
                return builder.setReturnCode(configRes.getKey().getReturnCode())
                        .setReturnMsg(configRes.getKey().getReturnMsg())
                        .build();
            }
            if(!CollectionUtils.isEmpty(configRes.getValue())){
                resultBuilder.addAllConfigtypeitems(configRes.getValue());
            }
        }
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }

    private List<SpecListItem> getSpecList(int seriesId) {
        List<SpecListItem> specItems = new ArrayList<>();
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if (Objects.isNull(seriesBaseInfo)) {
            return Collections.emptyList();
        }
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        List<SpecViewEntity> specViewEntities = seriesSpecBaseService.get(seriesId, isCV).join();
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(specViewEntities)) {
            return Collections.emptyList();
        }
        if (isCV) {
            specViewEntities = specViewEntities.stream().filter(specViewEntity -> specViewEntity.getSpecState() >=10 && specViewEntity.getSpecState() <= 30)
                    .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).thenComparing(SpecViewEntity::getSpecOrdercls).thenComparing(Comparator.comparing(SpecViewEntity::getSpecId).reversed()))
                    .collect(Collectors.toList());
        } else {
            specViewEntities = specViewEntities.stream().filter(specViewEntity ->  specViewEntity.getSpecState() <= 30 &&specViewEntity.getSpecIsImage() == 0)
                    .sorted(Comparator.comparing(SpecViewEntity::getSpecOrder).thenComparing(SpecViewEntity::getIsclassic).thenComparing(SpecViewEntity::getSpecOrdercls).thenComparing(Comparator.comparing(SpecViewEntity::getSpecId).reversed()))
                    .collect(Collectors.toList());
        }
        List<Integer> specIds = specViewEntities.stream().map(SpecViewEntity::getSpecId).collect(Collectors.toList());
        Map<Integer, SpecBaseInfo> specBaseInfoMap = commService.getSpecBaseInfo(specIds);

        specViewEntities = specViewEntities.stream().filter(Objects::nonNull).filter(specViewEntity ->{
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specViewEntity.getSpecId());
            return (specBaseInfo != null && specBaseInfo.getIsSpecParamIsShow() == 1) || specViewEntity.getSpecState() == 40;
        }).collect(Collectors.toList());

        specViewEntities.forEach(specViewEntity -> {
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specViewEntity.getSpecId());
            int showstate = 0;
            //即将销售接受预定 obj.Key==1接受预订
            if (specViewEntity.getSpecState() == 10) {
                showstate = null != specBaseInfo && specBaseInfo.getIsBooked() == 1 ? 1 : 0;
            } else {
                showstate = -1;
            }
            specItems.add(SpecListItem.newBuilder().setSpecid(specViewEntity.getSpecId())
                    .setSpecstate(specViewEntity.getSpecState()).setShowstate(showstate).build());
        });
        return specItems;
    }

    private List<ParaTypeItem> getParamTypeItems(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps, ParamTypeItems.TempInfo tempInfo, boolean reWriteMaxKw, List<ParaTypeItem.ParaItem> arrMaxKw,
                                                 boolean reWriteMaxTorque, List<ParaTypeItem.ParaItem> arrMaxTorque,Map<String, Integer> configModelMap,int paramConfigModelId) {
        String lastItemType = null;//上一次参数类别名称
        String currentItemType = null;//当前参数类别名称
        List<ParaTypeItem> paramTypeItems = new ArrayList<>();
        List<ParaTypeItem.ParaItem> paramItems = new ArrayList<>();
        for (int i = 0, len = specConfigMaps.size(); i < len; i++) {
            Map<String, Object> map = specConfigMaps.get(i);
            if (map == null || map.size() == 0) {
                continue;
            }
            int currentParamValueEqualNullNum = 0;
            currentItemType = (String) map.get("item");
            //如果全是纯电动车型不显示发动机信息
            if (tempInfo.isAllSpecIsPEV() && StringUtils.equals(currentItemType, "发动机")) {
                continue;
            }
            if (!Objects.equals(currentItemType, lastItemType)) {
                if (i > 0) {
                    //汽油、柴油、48v轻混的都不显示电动机大类。
                    //判断中间有电动机的分类，如果没有电的参数，跳出大类。乘用车和商用车的大类排序不一致。此处理是判断乘用车
                    if (i < len - 1) {
                        if (tempInfo.getOilEnergyNum() == specIdList.size() && StringUtils.equals(currentItemType, "电动机")) {
                            continue;
                        }
                    }
                    paramTypeItems.add(ParaTypeItem.newBuilder().setName(lastItemType).addAllParamitems(paramItems).build());
                    paramItems = new ArrayList<>();
                }
                lastItemType = currentItemType;
            }
            List<ParaTypeItem.ValueItem> valueItems = new ArrayList<>();//车型参数值集合
            int configId = Integer.parseInt(map.get("configId").toString());//参数项id
            for (Integer specId : specIdList) {
                String strValue = CommonFunction.getDefaultParamSignNew(map.get(specId.toString()) + "");
                valueItems.add(ParaTypeItem.ValueItem.newBuilder().setSpecid(specId).setValue(strValue).build());
                if (StringUtils.equals(strValue, "-")) {
                    currentParamValueEqualNullNum += 1;
                }
            }
            //全都没值的项，如果在限制的参数项范围内不外显、解决前台大片空白参数项问题
            if (configId > 0) {
                if (Spec.DYNAMIC_DISPLAY_PARAM_ITEMS.contains(configId) && currentParamValueEqualNullNum == specIdList.size()) {
                    continue;
                }
            }
            String name = (String) map.get("name");
            int pnId = configModelMap != null && configModelMap.containsKey(name) && configModelMap.get(name)> 0 ? configModelMap.get(name) : -1;
            if (StringUtils.equals(currentItemType, "基本参数")) {
                //全部是非新能源车型，隐藏“基本参数”的部分新能源参数
                if (tempInfo.getNewEnergyNum() == 0 && Spec.listNewEnergyParam.contains(name)) {
                    continue;
                }
                if (tempInfo.isAllSpecIsPEV() && Spec.listNotDisPlayOfPEVCarParam.contains(name)) {//全是纯电动车型要隐藏“基本参数”中的部分参数。
                    continue;
                }
                //燃料形式是全是油的不显电动机(Ps)这项基本参数
                if (tempInfo.getOilEnergyNum() == specIdList.size() && StringUtils.equals(name, "电动机(Ps)")) {
                    continue;
                }
                //最大功率的特殊处理
                if (reWriteMaxKw && StringUtils.equals(name, "最大功率(kW)") && !CollectionUtils.isEmpty(arrMaxKw)) {
                    paramItems.addAll(arrMaxKw);
                    reWriteMaxKw = false;
                    continue;
                }
                //最大扭矩(N·m)的特殊处理
                if (reWriteMaxTorque && StringUtils.equals(name, "最大扭矩(N·m)") && !CollectionUtils.isEmpty(arrMaxTorque)) {
                    paramItems.addAll(arrMaxTorque);
                    reWriteMaxTorque = false;
                    continue;
                }
            }
            if (StringUtils.equals(currentItemType, "车身")) {
                //全是纯电动车型要隐藏“基本参数”中的部分参数。
                if (tempInfo.isAllSpecIsPEV() && Spec.listNotDisPlayOfPEVCarParam.contains(name)) {
                    continue;
                }
            }
            paramItems.add(ParaTypeItem.ParaItem.newBuilder().setName(name).setId(0).addAllValueitems(valueItems).
                    setPnid(paramConfigModelId + "_" + pnId).build());
            //汽油、柴油、48v轻混的都不显示电动机大类。
            //判断末尾有电动机的分类，如果没有电的参数，跳出大类。乘用车和商用车的大类排序不一致。此处理是判断商用车
            if (tempInfo.getOilEnergyNum() == specIdList.size() && StringUtils.equals(currentItemType, "电动机")) {
                continue;
            }
            if (i == len - 1) {
                paramTypeItems.add(ParaTypeItem.newBuilder().setName(lastItemType).addAllParamitems(paramItems).build());
            }
        }
        return paramTypeItems;
    }

    /**
     * 系统综合扭矩(N·m)
     */
    private boolean isReWriteMaxTorque(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps,Map<Integer, SpecBaseInfo> specBaseInfoMap) {
        return specConfigMaps.stream()
                .filter(spec -> StringUtils.equals((String) spec.get("name"), "系统综合扭矩(N·m)"))
                .flatMap(spec -> specIdList.stream().map(specId -> {
                    int fuelTypeDetail = null == specBaseInfoMap.get(specId) ? -1 : specBaseInfoMap.get(specId).getFuelTypeDetail();
                    String itemValue = spec.getOrDefault(String.valueOf(specId), "").toString();
                    return Spec.FUEL_TYPE_FILTER_IDS.contains(fuelTypeDetail) && StringUtils.equalsAny(itemValue, "", "0");
                }))
                .reduce(false, Boolean::logicalOr);
    }

    private List<ParaTypeItem.ParaItem> getList(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps, boolean reWriteMaxTorque, String str1, String str2, String str3,Map<Integer, SpecBaseInfo> specBaseInfoMap) {
        List<ParaTypeItem.ParaItem> list = new ArrayList<>();
        if (reWriteMaxTorque) {
            List<Map<String, Object>> drMaxTorqueMap = specConfigMaps.stream().
                    filter(spec -> StringUtils.equals((String) spec.get("name"), str1) && StringUtils.equals((String) spec.get("item"), "发动机") ||
                            StringUtils.equals((String) spec.get("name"), str2) && StringUtils.equals((String) spec.get("item"), "电动机")).
                    collect(Collectors.toList());
            list = drMaxTorqueMap.parallelStream().map(map -> {
                    List<ParaTypeItem.ValueItem> specValueItems = specIdList.parallelStream().map(specId -> {
                    int fuelTypeDetail = null == specBaseInfoMap.get(specId) ? -1 : specBaseInfoMap.get(specId).getFuelTypeDetail();
                    String value;
                    if (StringUtils.equals((String) map.get("item"), "发动机")) {
                        value = fuelTypeDetail == 4 ? "-" : CommonFunction.getDefaultParamSignNew((String) map.get(String.valueOf(specId)));
                    } else if (StringUtils.equals((String) map.get("item"), "电动机")) {
                        value = Spec.OIL_FUEL_TYPE_LIST.contains(fuelTypeDetail) ? "-" : CommonFunction.getDefaultParamSignNew((String) map.get(String.valueOf(specId)));
                    } else {
                        value = "-";
                    }
                    return ParaTypeItem.ValueItem.newBuilder().setSpecid(specId).setValue(value).build();
                }).collect(Collectors.toList());
                String name = StringUtils.equals((String) map.get("name"), str1) ? str3 : (String) map.get("name");
                return ParaTypeItem.ParaItem.newBuilder().setName(name)
                        .addAllValueitems(specValueItems).build();
            }).collect(Collectors.toList());
        }
        return list;
    }

    private boolean  isReWriteMaxKw(List<Integer> specIdList, List<Map<String, Object>> specConfigMaps,Map<Integer, SpecBaseInfo> specBaseInfoMap) {
        //是否有插电或油电混合的车型
        boolean fuelType35Exist = specIdList.stream().anyMatch(specId -> {
            int fuelTypeDetail = null == specBaseInfoMap.get(specId) ? -1 : specBaseInfoMap.get(specId).getFuelTypeDetail();
            return fuelTypeDetail == 3 || fuelTypeDetail == 5;
        });
        if (!fuelType35Exist) {
            return false;
        }
        return specConfigMaps.stream()
                .filter(spec -> StringUtils.equals((String) spec.get("name"), "系统综合功率(kW)"))
                .flatMap(spec -> specIdList.stream().map(specId -> {
                    int fuelTypeDetail = null == specBaseInfoMap.get(specId) ? -1 : specBaseInfoMap.get(specId).getFuelTypeDetail();
                    String itemValue = spec.getOrDefault(String.valueOf(specId), "").toString();
                    return Spec.FUEL_TYPE_FILTER_IDS.contains(fuelTypeDetail) && StringUtils.equalsAny(itemValue, "", "0");
                }))
                .reduce(false, Boolean::logicalOr);
    }
    private ParamTypeItems.TempInfo getTempInfo(List<Integer> specIds,Map<Integer, SpecBaseInfo> specBaseInfoMap){
        boolean allSpecIsPEV = false;
        int newEnergyNum = 0, oilEnergyNum = 0, pevSpecNum = 0;
        for (Integer specId : specIds) {
            SpecBaseInfo specBaseInfo = specBaseInfoMap.get(specId);
            int fuelTypeDetail = specBaseInfo == null ? -1 : specBaseInfo.getFuelTypeDetail();
            if (Spec.ARR_NEW_ENERGY_FUEL_TYPE.contains(fuelTypeDetail)) {
                newEnergyNum += 1;
            }
            if (fuelTypeDetail == 4 || fuelTypeDetail == 7) {
                pevSpecNum += 1;
            }
            if (CommonFunction.OILFUELTYPELIST.contains(fuelTypeDetail)) {
                oilEnergyNum += 1;
            }
        }
        if (pevSpecNum == specIds.size()) {
            allSpecIsPEV = true;
        }
        return ParamTypeItems.TempInfo.builder().newEnergyNum(newEnergyNum).oilEnergyNum(oilEnergyNum).pevSpecNum(pevSpecNum).allSpecIsPEV(allSpecIsPEV).build();
    }

    private List<Map<String, Object>> getSpecConfigMaps(boolean isCvSpec, boolean unCvSpec, List<Integer> specIdList) {
        List<Map<String, Object>> resultMapList = Collections.emptyList();
        if (!isCvSpec && !unCvSpec) {
            List<OptParItemInfoEntity> itemInfoEntities = optParItemInfoService.get().join();
            resultMapList = CommonFunction.listToMap(itemInfoEntities);

        }
        Map<Integer, List<SpecConfigEntity>> serviceMap = specConfigService.getMap(specIdList);
        resultMapList = !CollectionUtils.isEmpty(resultMapList) ? getMapsFromOptParItem(specIdList, resultMapList, serviceMap) : getMaps(specIdList, resultMapList, serviceMap);
        resultMapList.sort((o1, o2) -> {
            Integer pordercls1 = Integer.parseInt(String.valueOf(o1.get("pordercls")));
            Integer pordercls2 = Integer.parseInt(String.valueOf(o2.get("pordercls")));
            int result = pordercls1.compareTo(pordercls2);
            if (result != 0) {
                return result;
            }
            Integer ordercls1 = Integer.parseInt(String.valueOf(o1.get("ordercls")));
            Integer ordercls2 = Integer.parseInt(String.valueOf(o2.get("ordercls")));
            return ordercls1.compareTo(ordercls2);
        });

        return resultMapList;
    }

    private List<Map<String, Object>> getMapsFromOptParItem(List<Integer> specIdList, List<Map<String, Object>> optParItemMapList, Map<Integer, List<SpecConfigEntity>> serviceMap) {
        for (Integer specId : specIdList) {
            List<SpecConfigEntity> specConfigEntities = serviceMap.get(specId);
            if (CollectionUtils.isEmpty(specConfigEntities)) {
                continue;
            }
            Map<String, Map<String, Object>> entityMap =
                    optParItemMapList.stream().collect(Collectors.toMap(map -> String.format("%s%s", map.get("item"), map.get("name")),
                            Function.identity()));
            for (SpecConfigEntity value : specConfigEntities) {
                if (Objects.isNull(value)) {
                    continue;
                }
                String key = String.format("%s%s", value.getItem(), value.getName());
                Map<String, Object> maps = entityMap.get(key);
                if (maps != null) {
                    maps.put(String.valueOf(specId), value.getItemValue());
                }
            }
        }
        return optParItemMapList;
    }

    private List<Map<String, Object>> getMaps(List<Integer> specIdList, List<Map<String, Object>> resultMapList, Map<Integer, List<SpecConfigEntity>> serviceMap) {
        boolean flag = false;
        for (Integer specId : specIdList) {
            List<SpecConfigEntity> specConfigEntities = serviceMap.get(specId);
            if (CollectionUtils.isEmpty(specConfigEntities)) {
                continue;
            }
            if (CollectionUtils.isEmpty(resultMapList)) {
                resultMapList = SpecConfigEntity.listToMap(specConfigEntities, specId);
                continue;
            }
            Map<String, Map<String, Object>> entityMap =
                    resultMapList.stream().collect(Collectors.toMap(map -> String.format("%s%s%s%s", map.get("item"), map.get("name"), map.get("pordercls"), map.get("ordercls")),
                            Function.identity()));
            for (SpecConfigEntity value : specConfigEntities) {
                if (Objects.isNull(value)) {
                    continue;
                }
                String key = String.format("%s%s%s%s", value.getItem(), value.getName(), value.getPordercls(), value.getOrdercls());
                Map<String, Object> maps = entityMap.get(key);
                if (maps != null) {
                    maps.put(String.valueOf(specId), value.getItemValue());
                } else {
                    flag = true;
                    resultMapList.add(SpecConfigEntity.getObjToMap(value, specId));
                }
            }
        }
        if (flag) {
            List<String> collect = resultMapList.stream().map(map -> String.valueOf(map.get("item"))).distinct().collect(Collectors.toList());
            Map<String, List<Map<String, Object>>> item = resultMapList.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("item"))));
            resultMapList = collect.stream().map(item::get).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return resultMapList;
    }


    /**
     * 返回信息查询组装
     * @param specIdList
     * @return
     */
    public List<SpecificConfig.ConfigItem> getConfigItems(List<Integer> specIdList){
        List<SpecificConfig.ConfigItem> configItems = new ArrayList<>();
        if(CollectionUtils.isEmpty(specIdList)){
            return configItems;
        }
        Map<Integer, String> dictionary = new HashMap<>();
        dictionary.put(0, "-");
        dictionary.put(1, "●");
        dictionary.put(2, "○");
        if (specIdList.stream().allMatch(s -> s < 1000000)) {
            List<SpecificConfigInfo> specificBySpecIdList = specificConfigBaseService.getList(specIdList);
            //分组,分组后还是原来的顺序
            Map<String, List<SpecificConfigInfo>> configMap = specificBySpecIdList.stream().collect(Collectors.groupingBy(config ->
                    config.getItemId() + config.getItemName() + config.getBaiKeId() + config.getBaiKeUrl(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

            for(Map.Entry<String, List<SpecificConfigInfo>> configSpecificMap:configMap.entrySet()){
                //组装
                SpecificConfig.ConfigItem configItem = new SpecificConfig.ConfigItem();
                SpecificConfigInfo specificConfigInfo = configSpecificMap.getValue().get(0);
                configItem.setBaikeid(specificConfigInfo.getBaiKeId());
                configItem.setBaikeurl(specificConfigInfo.getBaiKeUrl());
                configItem.setConfigid(specificConfigInfo.getItemId());
                configItem.setName(specificConfigInfo.getItemName());
                List<SpecificConfig.ValueItem> valueItems = new ArrayList<>();
                for(int i = 0;i<specIdList.size();i++){
                    SpecificConfig.ValueItem valueItem = new SpecificConfig.ValueItem();
                    int key = 0;
                    String price = "";
                    int specId = specIdList.get(i);
                    SpecificConfigInfo configInfo = configSpecificMap.getValue().stream().filter(
                            configSpecific -> specId == configSpecific.getSpecId()).
                            findFirst().orElse(null);
                    if(null != configInfo){
                        key = configInfo.getItemValue();
                        price = configInfo.getPrice().compareTo(BigDecimal.ZERO)  == 0 ? "":configInfo.getPrice().toString();
                    }
                    valueItem.setSpecid(String.valueOf(specId));
                    valueItem.setValue(dictionary.get(key));
                    valueItem.setPrice(price);
                    valueItems.add(valueItem);
                }
                configItem.setValueitems(valueItems);
                //返回参数组装
                configItems.add(configItem);
            }
        }
        return configItems;
    }

    private boolean SpecParamIsShow(SpecBaseInfo specBaseInfo){
        return specBaseInfo.getSpecState() == 40 || specBaseInfo.getIsSpecParamIsShow() == 1;
    }

}
