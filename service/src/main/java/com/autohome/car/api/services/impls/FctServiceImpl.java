package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.fct.*;
import autohome.rpc.car.car_api.v2.fct.GetAllFactoryRequest;
import autohome.rpc.car.car_api.v2.fct.GetFctByBrandIdAndStateRequest;
import autohome.rpc.car.car_api.v2.fct.GetFctByBrandIdAndStateResponse;
import autohome.rpc.car.car_api.v2.fct.GetFctNameByIdRequest;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.BaseConfig.Spec;
import com.autohome.car.api.data.popauto.FactoryMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.FctService;
import com.autohome.car.api.services.basic.BrandFactorysBaseService;
import com.autohome.car.api.services.basic.BrandSeriesRelationBaseService;
import com.autohome.car.api.services.basic.FactoryBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import com.autohome.car.api.services.models.fct.FctCorrelateInfo;
import com.autohome.car.api.services.models.fct.FctItem;
import com.autohome.car.api.services.models.fct.FctNameItem;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.LocalDateUtils.DATE_TIME_PATTERN_TWO;

@Service
public class FctServiceImpl implements FctService {

    @Resource
    private FactoryBaseService factoryBaseService;

    @Autowired
    BrandFactorysBaseService brandFactorysBaseService;

    @Autowired
    FactoryMapper factoryMapper;

    @Autowired
    AutoCacheService autoCacheService;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Resource
    private CommService commService;

    @Resource
    private BrandSeriesRelationBaseService brandSeriesRelationBaseService;



    @Override
    public GetFctLogoByFctIdResponse getFctLogoByFctId(GetFctLogoByFctIdRequest request) {
        GetFctLogoByFctIdResponse.Builder builder = GetFctLogoByFctIdResponse.newBuilder();
        GetFctLogoByFctIdResponse.Result.Builder result = GetFctLogoByFctIdResponse.Result.newBuilder();
        int fctId = request.getFctid();
        if (fctId <= 0) {
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        FactoryBaseInfo factory = factoryBaseService.getFactory(fctId);
        result.setFctid(fctId);
        result.setFctlogo(factory != null ? ImageUtil.getFullImagePathNew(factory.getLogo(), true) : "");
        return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).setResult(result).build();
    }

    @Override
    public GetGetFctNameByFctIdResponse getFctNameByFctId(GetFctNameByFctIdRequest request) {
        GetGetFctNameByFctIdResponse.Builder builder = GetGetFctNameByFctIdResponse.newBuilder();
        GetGetFctNameByFctIdResponse.Result.Builder result = GetGetFctNameByFctIdResponse.Result.newBuilder();
        int fctId = request.getFctid();
        if (fctId <= 0) {
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).build();
        }
        result.setFctid(fctId);
        result.setFctname(factoryBaseService.getName(fctId));
        return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).setResult(result).build();
    }

    @Override
    public GetGetFctNameResponse getFactoryNames(GetFctNameRequest request) {
        GetGetFctNameResponse.Builder builder = GetGetFctNameResponse.newBuilder();
        GetGetFctNameResponse.Result.Builder result = GetGetFctNameResponse.Result.newBuilder();
        List<FactoryBaseInfo> allFactory = factoryBaseService.getAllFactory();
        if (CollectionUtils.isEmpty(allFactory)) {
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).setResult(result).build();
        }
        List<FactoryBaseInfo> tempFactory = allFactory.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(FactoryBaseEntity::getId)).collect(Collectors.toList());
        for (FactoryBaseInfo factoryBaseInfo : tempFactory) {
            result.addFactoryitems(GetGetFctNameResponse.Result.Factoryitems.newBuilder().setId(factoryBaseInfo.getId()).setName(CommonFunction.replaceFactName(factoryBaseInfo.getName())).build());
        }
        result.setTotal(result.getFactoryitemsCount());
        return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).setResult(result).build();
    }

    @Override
    public FactoryAndSeriesByBrandResponse factoryAndSeriesByBrand(FactoryAndSeriesByBrandRequest request){
        FactoryAndSeriesByBrandResponse.Builder builder = FactoryAndSeriesByBrandResponse.newBuilder();
        FactoryAndSeriesByBrandResponse.Result.Builder result = FactoryAndSeriesByBrandResponse.Result.newBuilder();
        int brandId = request.getBrandid();
        SpecStateEnum state = Spec.getSpecState(request.getState());
        int typeId = request.getTypeid();
        int isFilterSpecImage = StringIntegerUtils.getIntOrDefault( request.getIsFilterSpecImage(),0);
        typeId = typeId > 2 ? 0 : typeId;
        if (brandId == 0 || state == SpecStateEnum.NONE)
        {
            builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg());
            return builder.build();
        }
//        List<FactoryInfoEntity> list = brandFactorysBaseService.get(brandId).join();
//        List<FactoryInfoEntity> list = factoryMapper.getAllFactoryInfos();
        List<FactoryInfoEntity> list = autoCacheService.getAllFactoryInfos();
        List<SeriesInfoEntity> seriesList = autoCacheService.getAllSeriesItems();
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        boolean flag = false;
        switch (state) {
            //未上市(0X0001)
            case NO_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() == 0).collect(Collectors.toList());
                break;
            //即将上市(0X0002)
            case WAIT_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() == 10).collect(Collectors.toList());
                break;
            //在产在售(0X0004)
            case SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() == 20).collect(Collectors.toList());
                break;
            //停产在售(0X0008)
            case SELL_IN_STOP:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() == 30).collect(Collectors.toList());
                break;
            //停售(0X0010)
            case STOP_SELL:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() == 40).collect(Collectors.toList());
                break;
            //未售(0X0003)
            case SELL_3:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() <= 10).collect(Collectors.toList());
                break;
            //在售(0X000C)
            case SELL_12:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() >= 20 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_14:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() >= 10 && s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            //在售+停售(0X001C)
            case SELL_28:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() >= 20).collect(Collectors.toList());
                break;
            //未售+在售(0X000F)
            case SELL_15:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() <= 30).collect(Collectors.toList());
                break;
            case SELL_30:
                flag = true;
                list = list.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                seriesList = seriesList.stream().filter(s -> s.getSpecState() >= 10).collect(Collectors.toList());
                break;
            //全部(0X001F)
            case SELL_31:
                flag = true;
                break;
        }
        if(!flag){
            builder.setResult(result);
            builder.setReturnMsg("成功");
            return builder.build();
        }
        list = list.stream().filter(s -> s.getBrandId() == brandId).collect(Collectors.toList());
        seriesList = seriesList.stream().filter(s -> s.getBrandId() == brandId).collect(Collectors.toList());
        if(typeId > 0){
            int finalTypeId = typeId;
            list = list.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
            seriesList = seriesList.stream().filter(s -> s.getIsCV() == finalTypeId).collect(Collectors.toList());
        }
        if(isFilterSpecImage == 1){
            list = list.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
            seriesList = seriesList.stream().filter(s -> s.getSpecIsImage() == 0).collect(Collectors.toList());
        }
        list = list.stream().sorted(Comparator.comparing(FactoryInfoEntity::getRankIndex)).collect(Collectors.toList());
        seriesList = seriesList.stream().sorted(Comparator.comparing(SeriesInfoEntity::getRankIndex)).collect(Collectors.toList());
        for (FactoryInfoEntity item:list) {
            FactoryBaseInfo fctBase = factoryBaseService.getFactory(item.getFactoryId());
            FactoryAndSeriesByBrandResponse.Factoryitems.Builder fct = FactoryAndSeriesByBrandResponse.Factoryitems.newBuilder();
            fct.setId(item.getFactoryId());
            fct.setName(fctBase == null?"":fctBase.getName());
            fct.setFirstletter(item.getFFirstLetter());
            List<SeriesInfoEntity> mseriesList = seriesList.stream().filter(s -> s.getFactoryId() == item.getFactoryId()).collect(Collectors.toList());
//            List<Integer> seriesIds = new ArrayList<>();
//            mseriesList.forEach(x -> {
//                seriesIds.add(x.getSeriesId());
//            });
//            List<SeriesBaseInfo> specBaseList = seriesBaseService.get(seriesIds);
//            Map<Integer,SeriesBaseInfo> map = specBaseList.stream().collect(Collectors.toMap(SeriesBaseInfo::getId,a -> a,(x,y)->x));
            mseriesList.forEach(x -> {
                SeriesBaseInfo seriesBase = seriesBaseService.get(x.getSeriesId()).join();
                FactoryAndSeriesByBrandResponse.SeriesItem.Builder seriesItem = FactoryAndSeriesByBrandResponse.SeriesItem.newBuilder();
                seriesItem.setId(x.getSeriesId());
                seriesItem.setName(seriesBase.getName());
                seriesItem.setFirstletter(x.getSFirstLetter());
                seriesItem.setSeriesstate(x.getSeriesstate());
                seriesItem.setSeriesorder(x.getSeriesOrder());
                fct.addSeriesitems(seriesItem);
            });
            List<FactoryAndSeriesByBrandResponse.SeriesItem> series = fct.getSeriesitemsList().stream().distinct().collect(Collectors.toList());
            fct.clearSeriesitems();
            series.forEach(x->{
                fct.addSeriesitems(x);
            });
            result.addFactoryitems(fct);
        }
        List<FactoryAndSeriesByBrandResponse.Factoryitems> fcts = result.getFactoryitemsList().stream().distinct().collect(Collectors.toList());
        result.clearFactoryitems();
        fcts.forEach(x->{
            result.addFactoryitems(x);
        });
        builder.setResult(result);
        builder.setReturnMsg("成功");
        return builder.build();
    }


    @Override
    public ApiResult<FctItem> getAllFactory(GetAllFactoryRequest request) {
        List<FactoryBaseInfo> factoryBaseInfoList = factoryBaseService.getFactoryAll();
        List<FctItem.FctDetailItem> fctDetailItems = new ArrayList<>();
        if(!CollectionUtils.isEmpty(factoryBaseInfoList)){
            for (FactoryBaseInfo factoryBaseInfo:factoryBaseInfoList){
                FctItem.FctDetailItem fctDetailItem = new FctItem.FctDetailItem();
                fctDetailItem.setId(factoryBaseInfo.getId());
                fctDetailItem.setName(null != factoryBaseInfo.getName() ? HtmlUtils.decode(factoryBaseInfo.getName()) : "");
                fctDetailItem.setUrl(factoryBaseInfo.getUrl());
                fctDetailItem.setIsimport(factoryBaseInfo.getIsimport());
                fctDetailItem.setCreatetime(null != factoryBaseInfo.getCreateTime() ?
                        LocalDateUtils.format(factoryBaseInfo.getCreateTime(), DATE_TIME_PATTERN_TWO) : "");
                fctDetailItem.setEdittime(null != factoryBaseInfo.getEditTime() ?
                        LocalDateUtils.format(factoryBaseInfo.getEditTime(), DATE_TIME_PATTERN_TWO) : "");
                fctDetailItem.setFirstletter(factoryBaseInfo.getFirstletter());
                fctDetailItem.setLogo(null != factoryBaseInfo.getLogo() ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "");
                fctDetailItems.add(fctDetailItem);
            }
        }
        FctItem fctItem = new FctItem();
        fctItem.setTotal(fctDetailItems.size());
        fctItem.setFctitems(fctDetailItems);
        return new ApiResult<>(fctItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 据厂商ID获取厂商名称
     * @param request
     * @return
     */
    @Override
    public ApiResult<FctNameItem> getFctNameByIdV2(GetFctNameByIdRequest request) {
        int fctId = request.getFctid();
        if(fctId == 0){
            return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        String fctName = factoryBaseService.getName(fctId);
        FctNameItem fctNameItem = new FctNameItem();
        fctNameItem.setFctid(fctId);
        fctNameItem.setFctname(fctName);
        return new ApiResult<>(fctNameItem,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据厂商id获取厂商及厂商下车系信息
     * @param request
     * @return
     */
    @Override
    public ApiResult<FctCorrelateInfo> getFctCorrelateInfoByFctId(GetFctCorrelateInfoByFctIdRequest request) {
        int fctId = request.getFctid();
        if(fctId == 0){
            return new ApiResult<>(null,ReturnMessageEnum.RETURN_MESSAGE_ENUM102);
        }
        List<BFSInfoEntity> bfsInfoEntities = autoCacheService.getBFSInfoByFctId(fctId);
        List<FctCorrelateInfo.SeriesItem> seriesItems = new ArrayList<>();
        AtomicInteger sellSpecCount = new AtomicInteger(0);
        if(CollectionUtils.isNotEmpty(bfsInfoEntities)){
            List<Integer> seriesIds = bfsInfoEntities.stream().map(BFSInfoEntity::getSeriesId).distinct().collect(Collectors.toList());
            Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);
            for (BFSInfoEntity bfsInfoEntity : bfsInfoEntities){
                FctCorrelateInfo.SeriesItem seriesItem = new FctCorrelateInfo.SeriesItem();
                seriesItem.setSeriesid(bfsInfoEntity.getSeriesId());
                SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(bfsInfoEntity.getSeriesId());
                if(null != seriesBaseInfo){
                    seriesItem.setSeriesname(seriesBaseInfo.getName());
                    seriesItem.setSeriesLogo(ImageUtil.getFullImagePath(seriesBaseInfo.getLogo()));
                    seriesItem.setMinprice(String.valueOf(seriesBaseInfo.getSeriesPriceMin()));
                    seriesItem.setMaxprice(String.valueOf(seriesBaseInfo.getSeriesPriceMax()));
                }
                seriesItems.add(seriesItem);
                sellSpecCount.addAndGet(bfsInfoEntity.getSsns());
            }
        }
        FctCorrelateInfo fctCorrelateInfo = new FctCorrelateInfo();
        fctCorrelateInfo.setFctid(fctId);
        FactoryBaseInfo factoryBaseInfo = factoryBaseService.getFactory(fctId);
        fctCorrelateInfo.setFctname(null != factoryBaseInfo ? factoryBaseInfo.getName() : "");
        fctCorrelateInfo.setFctlogo(null != factoryBaseInfo ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "" );
        fctCorrelateInfo.setOfficialurl(null != factoryBaseInfo ? factoryBaseInfo.getUrl() : "");
        fctCorrelateInfo.setSellseriescount(seriesItems.size());
        fctCorrelateInfo.setSerieslist(seriesItems);
        fctCorrelateInfo.setSellspeccount(sellSpecCount.get());
        return new ApiResult<>(fctCorrelateInfo,ReturnMessageEnum.RETURN_MESSAGE_ENUM0);
    }

    /**
     * 根据品牌ID获取品牌下厂商列表
     * @param request
     * @return
     */
    @Override
    public GetFctByBrandIdAndStateResponse getFctByBrandIdAndState(GetFctByBrandIdAndStateRequest request) {
        GetFctByBrandIdAndStateResponse.Builder builder = GetFctByBrandIdAndStateResponse.newBuilder();
        int brandId = request.getBrandid();
        if (brandId == 0){
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .build();
        }
        SpecStateEnum stateEnum = Spec.getSpecState(request.getState());
        if(!(stateEnum == SpecStateEnum.SELL_12 || stateEnum == SpecStateEnum.STOP_SELL || stateEnum == SpecStateEnum.SELL_15 ||
                stateEnum == SpecStateEnum.SELL_28 || stateEnum == SpecStateEnum.SELL_31)){
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode())
                    .build();
        }
        List<Integer> seriesIds = brandSeriesRelationBaseService.getSeriesIds(brandId);
        List<SeriesBaseInfo> seriesBaseInfos = commService.getSeriesBaseInfoNoMap(seriesIds);
        switch (stateEnum){
            case SELL_12 :
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo -> seriesBaseInfo.getSeriesIsPublic() == 1).collect(Collectors.toList());
                break;
            case STOP_SELL :
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo -> seriesBaseInfo.getSeriesIsPublic() == 2).collect(Collectors.toList());
                break;
            case SELL_15 :
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo -> seriesBaseInfo.getSeriesIsPublic() <= 1).collect(Collectors.toList());
                break;
            case SELL_28 :
                seriesBaseInfos = seriesBaseInfos.stream().filter(seriesBaseInfo -> seriesBaseInfo.getSeriesIsPublic() >= 1).collect(Collectors.toList());
                break;
        }
        List<Integer> fctIds = seriesBaseInfos.stream().map(SeriesBaseInfo::getFactId).distinct().collect(Collectors.toList());
        List<FactoryBaseInfo> factoryBaseInfos = factoryBaseService.getFactoryByIds(fctIds);
        if(CollectionUtils.isNotEmpty(factoryBaseInfos)){
            factoryBaseInfos.stream().sorted(Comparator.comparing(FactoryBaseInfo::getId)).forEach(factoryBaseInfo ->{
                GetFctByBrandIdAndStateResponse.Result.Builder resultBuilder = GetFctByBrandIdAndStateResponse.Result.newBuilder();
                resultBuilder.setFctid(factoryBaseInfo.getId());
                resultBuilder.setFctname(null != factoryBaseInfo.getName() ? factoryBaseInfo.getName() : "");
                resultBuilder.setFctofficialurl(null != factoryBaseInfo.getUrl() ? factoryBaseInfo.getUrl() : "");
                resultBuilder.setFctisimport(null != factoryBaseInfo.getIsimport() ? factoryBaseInfo.getIsimport() : "");
                resultBuilder.setFctlogo(null != factoryBaseInfo.getLogo() ? factoryBaseInfo.getLogo().replace("~","") : "");
                resultBuilder.setFctfirstletter(null != factoryBaseInfo.getFirstletter() ? factoryBaseInfo.getFirstletter() : "");
                builder.addResult(resultBuilder);
            });
        }
        return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();

    }

    /**
     * 根据厂商id获取厂商model
     * @param request
     * @return
     */
    @Override
    public GetFctInfoByFctIdResponse getFctInfoByFctId(GetFctInfoByFctIdRequest request) {
        GetFctInfoByFctIdResponse.Builder builder = GetFctInfoByFctIdResponse.newBuilder();
        int fctId = request.getFctid();
        if (fctId == 0) {
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnMsg()).
                    setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM102.getReturnCode()).
                    build();
        }
        FactoryBaseInfo factoryBaseInfo = factoryBaseService.getFactory(fctId);
        if(null != factoryBaseInfo){
            GetFctInfoByFctIdResponse.Result.Builder resultBuilder = GetFctInfoByFctIdResponse.Result.newBuilder();
            resultBuilder.setFctid(fctId);
            resultBuilder.setFctname(null != factoryBaseInfo.getName() ? factoryBaseInfo.getName() : "");
            resultBuilder.setFctlogo(null != factoryBaseInfo.getLogo() ? ImageUtil.getFullImagePath(factoryBaseInfo.getLogo()) : "");
            resultBuilder.setFctofficialurl(null != factoryBaseInfo.getUrl() ? factoryBaseInfo.getUrl() : "");
            resultBuilder.setFctfirstletter(null != factoryBaseInfo.getFirstletter() ? factoryBaseInfo.getFirstletter() : "");
            resultBuilder.setFctisimport(null != factoryBaseInfo.getIsimport() ? factoryBaseInfo.getIsimport() : "");
            builder.setResult(resultBuilder);
        }
        return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    @Override
    public ShowFctResponse showFct(ShowFctRequest request){
        List<FctEntity> list = factoryMapper.getShowFcts();
        ShowFctResponse.Builder builder = ShowFctResponse.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");
        ShowFctResponse.Result.Builder result = ShowFctResponse.Result.newBuilder();
        result.setTotal(list.size());
        for (FctEntity item : list) {
            result.addFctitems(ShowFctResponse.Result.Fctitem.newBuilder()
                    .setId(item.getFctId())
                    .setName(item.getFctName())
                    .setFirstletter(item.getFirstLetter())
            );
        }
        builder.setResult(result);
        return builder.build();
    }
}
