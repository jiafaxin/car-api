package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.show.*;
import com.autohome.car.api.common.HtmlUtils;
import com.autohome.car.api.common.ImageUtil;
import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.ShowMapper;
import com.autohome.car.api.data.popauto.entities.*;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.CommService;
import com.autohome.car.api.services.ShowService;
import com.autohome.car.api.services.basic.FactoryBaseService;
import com.autohome.car.api.services.basic.GBrandBaseService;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.ShowBaseService;
import com.autohome.car.api.services.basic.models.FactoryBaseInfo;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.models.ShowBaseInfo;
import com.autohome.car.api.services.common.CommonFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM0;
import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@Service
@Slf4j
public class ShowServiceImpl implements ShowService {

    @Resource
    private AutoCacheService autoCacheService;

    @Resource
    private ShowBaseService showBaseService;

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private FactoryBaseService factoryBaseService;

    @Resource
    private CommService commService;

    @Resource
    private GBrandBaseService gBrandBaseService;

    @Resource
    private ShowMapper showMapper;

    @Override
    public GetShowInfoResponse getShowInfo(GetShowInfoRequest request) {
        GetShowInfoResponse.Builder builder = GetShowInfoResponse.newBuilder();

        GetShowInfoResponse.Result.Builder result = GetShowInfoResponse.Result.newBuilder();

        List<ShowBaseInfo> showBaseInfos = showBaseService.get(null);

        if (CollectionUtils.isEmpty(showBaseInfos)) {
            result.setTotal(0);
            return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        showBaseInfos = showBaseInfos.stream().sorted(Comparator.comparingInt(ShowBaseInfo::getId).reversed()).collect(Collectors.toList());
        for (ShowBaseInfo showBaseInfo : showBaseInfos) {
            if (Objects.isNull(showBaseInfo)) {
                continue;
            }
            result.addShowitems(GetShowInfoResponse.Result.Showitem.newBuilder().setId(showBaseInfo.getId()).setName(StringUtils.defaultString(showBaseInfo.getName())));
        }
        result.setTotal(result.getShowitemsCount());
        return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetShowPicInfoByBrandListResponse getShowPicInfoByBrandList(GetShowPicInfoByBrandListRequest request) {
        GetShowPicInfoByBrandListResponse.Builder builder = GetShowPicInfoByBrandListResponse.newBuilder();
        GetShowPicInfoByBrandListResponse.Result.Builder result = GetShowPicInfoByBrandListResponse.Result.newBuilder();

        int showId = request.getShowid();
        String brandList = request.getBrandlist();
        int evCar = request.getEvcar();
        if (showId == 0 || StringUtils.isBlank(brandList)) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        result.setShowid(showId);
        List<Integer> brands = CommonFunction.getListFromStr(brandList);
        List<ShowCarsViewEntity> list = evCar == 1 ? autoCacheService.getEvCarShowInfoByShowId(showId) : autoCacheService.getShowCarsInfoByShowId(showId);
        if (CollectionUtils.isEmpty(list)) {
            return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        list = list.stream().filter(e -> brands.contains(e.getBrandId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
        }
        List<Integer> collect = list.stream().map(ShowCarsViewEntity::getSeriesId).collect(Collectors.toList());
        Map<Integer, SeriesBaseInfo> map = seriesBaseService.getMap(collect);
        List<BrandBaseEntity> allBrandName = autoCacheService.getAllBrandName();
        Map<Integer, String> brandMaps = allBrandName.stream().collect(Collectors.toMap(BrandBaseEntity::getId, BrandBaseEntity::getName));
        List<FactoryBaseInfo> allFactory = factoryBaseService.getAllFactory();
        Map<Integer, String> factyMap = allFactory.stream().collect(Collectors.toMap(FactoryBaseEntity::getId, FactoryBaseEntity::getName));

        List<GBrandEntity> gBrandsAll = autoCacheService.getGBrandsAll();
        Map<Integer, GBrandEntity> gBrandMap = gBrandsAll.stream().collect(Collectors.toMap(GBrandEntity::getId, e -> e));
        for (ShowCarsViewEntity entity : list) {
            int seriesId = entity.getSeriesId();
            SeriesBaseInfo seriesBaseInfo = map.get(seriesId);
            GBrandEntity gBrandEntity = null;
            if (Objects.isNull(seriesBaseInfo)) {
                seriesBaseInfo = new SeriesBaseInfo();
            }
            boolean b = seriesId > 10000;
            if (b) {
                gBrandEntity = !CollectionUtils.isEmpty(gBrandMap) ? gBrandMap.get(seriesId) : null;
                if (gBrandEntity == null) {
                    gBrandEntity = new GBrandEntity();
                }
            }
            String name = b ? gBrandEntity.getName() : seriesBaseInfo.getName();
            int fctId = b ? seriesId / 10000 : seriesBaseInfo.getFactId();
            int brandId = b ? gBrandEntity.getNewFctId() : seriesBaseInfo.getBrandId();
            String brandName = CollectionUtils.isEmpty(brandMaps) ? "" : brandMaps.get(brandId);
            String fctName = CollectionUtils.isEmpty(factyMap) ? "" : factyMap.get(fctId);
            result.addSeriesitems(GetShowPicInfoByBrandListResponse.Result.Seriesitems.newBuilder().
                    setId(seriesId).
                    setName(StringUtils.defaultString(name)).
                    setFctid(fctId).
                    setBrandid(brandId).
                    setBrandname(HtmlUtils.decode(StringUtils.defaultString(brandName))).
                    setFctname(StringUtils.defaultString(fctName)).
                    setPicid(entity.getId()).
                    setFilepath(ImageUtil.getFullImagePathNew(entity.getSImg(), true)).
                    setPicnum(entity.getC()).
                    build());

        }
        result.setTotal(result.getSeriesitemsCount());
        return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }

    @Override
    public GetShowPicInfoByPavilionIdResponse getShowPicInfoByPavilionId(GetShowPicInfoByPavilionIdRequest request) {
        GetShowPicInfoByPavilionIdResponse.Builder builder = GetShowPicInfoByPavilionIdResponse.newBuilder();
        int pavilionId = request.getPavilionid();
        int showId = request.getShowid();
        if (showId == 0 || pavilionId == 0) {
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
        }
        GetShowPicInfoByPavilionIdResponse.Result.Builder result = GetShowPicInfoByPavilionIdResponse.Result.newBuilder();

        CompletableFuture<List<ShowCarsViewEntity>> carsViewEntitiesFuture = CompletableFuture.supplyAsync(() -> autoCacheService.getShowCarsInfoByPavilionId(showId, pavilionId));
        CompletableFuture<List<BrandBaseEntity>> allBrandNameFuture = CompletableFuture.supplyAsync(() -> autoCacheService.getAllBrandName());
        CompletableFuture<List<FactoryBaseInfo>> allFactoryFuture = CompletableFuture.supplyAsync(() -> factoryBaseService.getAllFactory());
        CompletableFuture<List<GBrandEntity>> gBrandsAllFuture = CompletableFuture.supplyAsync(() -> autoCacheService.getGBrandsAll());
        CompletableFuture.allOf(carsViewEntitiesFuture, allBrandNameFuture, allFactoryFuture, gBrandsAllFuture).join();

        List<FactoryBaseInfo> allFactory = allFactoryFuture.join();
        Map<Integer, String> factyMap = allFactory.stream().collect(Collectors.toMap(FactoryBaseEntity::getId, FactoryBaseEntity::getName));

        List<GBrandEntity> gBrandsAll = gBrandsAllFuture.join();
        Map<Integer, GBrandEntity> gBrandMap = gBrandsAll.stream().collect(Collectors.toMap(GBrandEntity::getId, e -> e));

        List<ShowCarsViewEntity> carsViewEntities = carsViewEntitiesFuture.join();
        List<Integer> seriesIds = carsViewEntities.stream().map(ShowCarsViewEntity::getSeriesId).collect(Collectors.toList());
        Map<Integer, SeriesBaseInfo> map = seriesBaseService.getMap(seriesIds);
        List<BrandBaseEntity> allBrandName = allBrandNameFuture.join();
        Map<Integer, String> brandMaps = allBrandName.stream().collect(Collectors.toMap(BrandBaseEntity::getId, BrandBaseEntity::getName));
        for (ShowCarsViewEntity carsViewEntity : carsViewEntities) {
            int seriesId = carsViewEntity.getSeriesId();
            SeriesBaseInfo seriesBaseInfo = map.get(seriesId);
            if (Objects.isNull(seriesBaseInfo)) {
                seriesBaseInfo = new SeriesBaseInfo();
            }
            boolean b = seriesId > 10000;
            GBrandEntity gBrandEntity = null;
            if (b) {
                gBrandEntity = !CollectionUtils.isEmpty(gBrandMap) ? gBrandMap.get(seriesId) : null;
                if (gBrandEntity == null) {
                    gBrandEntity = new GBrandEntity();
                }
            }
            String name = b ? gBrandEntity.getName() : seriesBaseInfo.getName();
            int fctId = b ? seriesId / 10000 : seriesBaseInfo.getFactId();
            int brandId = b ? gBrandEntity.getNewFctId() : seriesBaseInfo.getBrandId();
            String brandName = CollectionUtils.isEmpty(brandMaps) ? "" : brandMaps.get(brandId);
            String fctName = CollectionUtils.isEmpty(factyMap) ? "" : factyMap.get(fctId);
            result.addSeriesitems(GetShowPicInfoByPavilionIdResponse.Result.Seriesitem.newBuilder().
                    setId(seriesId).
                    setName(StringUtils.defaultString(name)).
                    setFctid(fctId).
                    setBrandid(brandId).
                    setBrandname(StringUtils.defaultString(brandName)).
                    setFctname(StringUtils.defaultString(fctName)).
                    setPicid(carsViewEntity.getId()).
                    setFilepath(ImageUtil.getFullImagePathNew(carsViewEntity.getSImg(), true)).
                    setPicnum(carsViewEntity.getC()).
                    build());
        }
        result.setTotal(result.getSeriesitemsCount());
        result.setPavilionid(pavilionId);
        result.setShowid(showId);
        return builder.setResult(result).setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg()).build();
    }
    /**
     * 根据车展ID,多个级别ID获取某车展前N条车系图片信息
     * @param request
     * @return
     */
    @Override
    public GetShowPicInfoByLevelListResponse getShowPicInfoByLevelList(GetShowPicInfoByLevelListRequest request) {
        GetShowPicInfoByLevelListResponse.Builder builder = GetShowPicInfoByLevelListResponse.newBuilder();
        int size = request.getSize();
        int isEvCar = request.getEvcar();
        int showId = request.getShowid();
        List<Integer> levelIds = new ArrayList<>();
        if(StringUtils.isNotBlank(request.getLevellist())){
            String[] split = request.getLevellist().split(",");
            for(String s : split){
                int levelId = 0;
                try {
                    levelId = Integer.parseInt(s);
                }catch (Exception e){
                    log.error("参数levelId转化异常 用户传参为：{}异常参数为:{}",request.getLevellist(),s);
                    levelId = 0;
                }
                levelIds.add(levelId);
            }
        }
        if (isEvCar == 0) {
            if (showId == 0 || levelIds.size() == 0) {
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                        .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                        .build();
            }
        } else if (isEvCar == 1) {
            if (showId == 0) {
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                        .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                        .build();
            }
        }
        GetShowPicInfoByLevelListResponse.Result.Builder resultBuilder = GetShowPicInfoByLevelListResponse.Result.newBuilder();
        List<ShowCarsViewEntity> showCarsViewEntities = isEvCar == 1 ? autoCacheService.getEvShowCarsNewEnergyByShowId(showId) : autoCacheService.getShowCarsInfoByShowId(showId);
        if(!CollectionUtils.isEmpty(showCarsViewEntities)){
            //
            if(levelIds.size() == 1){
                int levelId = levelIds.get(0);
                if(levelId == 9){
                    showCarsViewEntities = showCarsViewEntities.stream().filter(showCarsViewEntity ->
                            showCarsViewEntity.getLevelId() >= 16 && showCarsViewEntity.getLevelId() <= 20).collect(Collectors.toList());
                }else{
                    showCarsViewEntities = showCarsViewEntities.stream().filter(showCarsViewEntity ->
                            showCarsViewEntity.getLevelId() == levelId).collect(Collectors.toList());
                }
            }else if(levelIds.size() > 1){
                if(levelIds.contains(9)){
                    List<Integer> tempLevelList = levelIds.stream().filter(levelId -> levelId != 9).collect(Collectors.toList());
                    showCarsViewEntities = showCarsViewEntities.stream().filter(showCarsViewEntity ->
                            tempLevelList.contains(showCarsViewEntity.getLevelId()) ||
                                    (showCarsViewEntity.getLevelId() >= 16 && showCarsViewEntity.getLevelId() <= 20)).collect(Collectors.toList());
                }else{
                    showCarsViewEntities = showCarsViewEntities.stream().filter(showCarsViewEntity ->
                            levelIds.contains(showCarsViewEntity.getLevelId())).collect(Collectors.toList());
                }
            }
            //先排序再取top n
            showCarsViewEntities = showCarsViewEntities.stream().sorted(Comparator.comparing(ShowCarsViewEntity::getId,Comparator.reverseOrder())).collect(Collectors.toList());
            //获取top n
            if(size > 0){
                showCarsViewEntities = showCarsViewEntities.stream().limit(size).collect(Collectors.toList());
            }
            List<Integer> seriesIds = showCarsViewEntities.stream().map(ShowCarsViewEntity::getSeriesId).distinct().collect(Collectors.toList());
            Map<Integer, SeriesBaseInfo> seriesBaseInfoMap = commService.getSeriesBaseInfo(seriesIds);
            List<KeyValueDto<Integer, String>> gBrandAll = gBrandBaseService.getAll();
            //遍历
            for(ShowCarsViewEntity showCarsViewEntity : showCarsViewEntities){
                GetShowPicInfoByLevelListResponse.SeriesItem.Builder seriesItem = GetShowPicInfoByLevelListResponse.SeriesItem.newBuilder();
                seriesItem.setId(showCarsViewEntity.getSeriesId());
                SeriesBaseInfo seriesBaseInfo = seriesBaseInfoMap.get(showCarsViewEntity.getSeriesId());
                String seriesName = "";
                if(showCarsViewEntity.getSeriesId() < 10000){
                    seriesName = null != seriesBaseInfo ? seriesBaseInfo.getName() : "";
                }else{
                    seriesName = gBrandAll.stream().filter(keyValueDto -> keyValueDto.getKey() == showCarsViewEntity.getSeriesId()).
                            findFirst().map(KeyValueDto::getValue).orElse("概念车");
                }
                seriesItem.setName(seriesName);
                seriesItem.setLevelid(showCarsViewEntity.getLevelId());
                seriesItem.setPicid(showCarsViewEntity.getId());
                seriesItem.setFilepath(null != showCarsViewEntity ? ImageUtil.getFullImagePath(showCarsViewEntity.getSImg()) : "");
                seriesItem.setPicnum(showCarsViewEntity.getC());
                resultBuilder.addSeriesitems(seriesItem);
            }
        }
        resultBuilder.setShowid(showId);
        resultBuilder.setTotal(resultBuilder.getSeriesitemsCount());
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(resultBuilder)
                .build();
    }
    /**
     * 根据车展id,车系id 获取图片列表
     * @param request
     * @return
     */
    @Override
    public ShowPicInfoByShowIdSeriesIdResponse getShowPicInfoByShowIdSeriesId(ShowPicInfoByShowIdSeriesIdRequest request) {
        ShowPicInfoByShowIdSeriesIdResponse.Builder builder = ShowPicInfoByShowIdSeriesIdResponse.newBuilder();
        if(request.getSeriesid() == 0 || request.getShowid() == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<CarShowEntity> carShowEntities = showMapper.getShowPicInfoByShowIdSeriesId(request.getShowid(), request.getSeriesid());
        ShowPicInfoByShowIdSeriesIdResponse.Result.Builder result = ShowPicInfoByShowIdSeriesIdResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(carShowEntities)){
            for(CarShowEntity carShowEntity : carShowEntities){
                ShowPicInfoByShowIdSeriesIdResponse.CarShowPicItem.Builder carShowPicItem = ShowPicInfoByShowIdSeriesIdResponse.CarShowPicItem.newBuilder();
                carShowPicItem.setPicid(carShowEntity.getId());
                String bigfilepath = "";
                String smallfilepath = "";
                if(StringUtils.isNotBlank(carShowEntity.getPicPath())){
                    bigfilepath = ImageUtil.getFullImagePath(carShowEntity.getPicPath().replace("http://img.autohome.com.cn", "").replace("http://www.autoimg.cn", ""));
                }
                if(StringUtils.isNotBlank(carShowEntity.getSPicPath())){
                    smallfilepath = ImageUtil.getFullImagePath(carShowEntity.getSPicPath().replace("http://img.autohome.com.cn", "").replace("http://www.autoimg.cn", ""));
                }
                carShowPicItem.setBigfilepath(bigfilepath);
                carShowPicItem.setSmallfilepath(smallfilepath);
                carShowPicItem.setFctname(StringUtils.isNotBlank(carShowEntity.getFctName()) ? carShowEntity.getFctName() : "");
                carShowPicItem.setSeriesname(StringUtils.isNotBlank(carShowEntity.getSeriesName()) ? carShowEntity.getSeriesName() : "");
                result.addPicitems(carShowPicItem);
            }
            result.setTotal(carShowEntities.size());
        }
        result.setSeriesid(request.getSeriesid());
        result.setShowid(request.getShowid());
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }
    /**
     * 根据厂商id 获取参展中车系列表
     * @param request
     * @return
     */
    @Override
    public ShowSeriesByFctIdResponse getShowSeriesByFctId(ShowSeriesByFctIdRequest request) {
        ShowSeriesByFctIdResponse.Builder builder = ShowSeriesByFctIdResponse.newBuilder();
        int fctId = request.getFctid();
        if(fctId == 0){
            return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        List<KeyValueDto<Integer, String>> keyValueDtos = showMapper.getShowSeriesByFctId(fctId);
        ShowSeriesByFctIdResponse.Result.Builder result = ShowSeriesByFctIdResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(keyValueDtos)){
            for(KeyValueDto<Integer,String> keyValueDto : keyValueDtos){
                ShowSeriesByFctIdResponse.SeriesItem.Builder seriesItem = ShowSeriesByFctIdResponse.SeriesItem.newBuilder();
                seriesItem.setId(keyValueDto.getKey());
                seriesItem.setName(StringUtils.isNotBlank(keyValueDto.getValue()) ? keyValueDto.getValue() : "");
                result.addSeriesitems(seriesItem);
            }
            result.setTotal(keyValueDtos.size());
        }
        result.setFctid(fctId);
        return builder.setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();
    }
}
