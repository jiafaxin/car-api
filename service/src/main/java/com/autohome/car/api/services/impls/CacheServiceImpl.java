package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v3.cache.ReqRefreshCacheRequest;
import autohome.rpc.car.car_api.v3.cache.ResRefreshCacheResponse;
import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.common.CommonUtils;
import com.autohome.car.api.common.LocationEnum;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.CacheService;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.series.PhotosService;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
import com.autohome.car.api.services.basic.series.SeriesSpecPicColorStatistics;
import com.autohome.car.api.services.basic.series.SeriesSpecPicInnerColorStatistics;
import com.autohome.car.api.services.basic.specs.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.LocationEnum.*;
import static com.autohome.car.api.common.ReturnMessageEnum.*;

/**
 * 更新缓存服务
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Resource
    private PhotosService photosService;

    @Resource
    private SeriesSpecPicColorStatistics seriesSpecPicColorStatistics;

    @Resource
    private SeriesSpecPicInnerColorStatistics seriesSpecPicInnerColorStatistics;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SeriesBaseService seriesBaseService;

    @Resource
    private SeriesSpecBaseService seriesSpecBaseService;

    @Resource
    private SeriesConfigService seriesConfigService;

    @Resource
    private SpecParamService specParamService;

    @Resource
    private SpecBaseService specBaseService;


    @Resource
    private SeriesSpecInfoBaseService seriesSpecInfoBaseService;

    @Resource
    private SpecConfigBagNewService specConfigBagNewService;

    @Resource
    private SpecConfigRelationService specConfigRelationService;

    @Resource
    private SpecConfigPriceService specConfigPriceService;

    @Resource
    private SpecConfigSubItemService specConfigSubItemService;

    @Resource
    private SpecInnerColorBaseService specInnerColorBaseService;

    @Resource
    private SpecSpecColorBaseService specSpecColorBaseService;

    @Resource
    private SpecColorService specColorService;

    @Resource
    private InnerColorSpecService innerColorSpecService;

    @Resource
    private SpecPicColorStatisticsBaseService specPicColorStatisticsBaseService;

    @Resource
    private SpecificConfigBaseService specificConfigBaseService;


    /**
     * 更新redis缓存和本地缓存
     * @param request
     * @return
     */
    @Override
    public ResRefreshCacheResponse refreshCache(ReqRefreshCacheRequest request) {
        ResRefreshCacheResponse.Builder builder = ResRefreshCacheResponse.newBuilder();
        //更新位置
        int locationId = request.getLocationid();
        //车系id
        int seriesId = request.getSeriesid();
        //车型
        int specId = request.getSpecid();
        log.info("=================car.api 更新缓存参数locationId：{};seriesId:{};specId:{}================",locationId,seriesId,specId);
        if(locationId <= 0){
            return builder
                    .setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode())
                    .setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg())
                    .build();
        }
        long start = System.currentTimeMillis();
        //车系列表的状态和价格
        if(locationId == LOCATION_ENUM1.getLocationId()){

        }else if(locationId == LOCATION_ENUM2.getLocationId()){//车系综述的状态和价格
            //更新车系综述页的redis数据
            this.refreshSeriesDetail(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.SERIES_DETAIL_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM3.getLocationId()){//车系综述的车型列表价格
            this.refreshSeriesSpecList(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.SERIES_SPEC_LIST_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM4.getLocationId()){//参数配置
            //更新参配的redis数据
            this.refreshParamConfig(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.PARAM_CONFIG_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM5.getLocationId()){//图片列表
            //更新图片列表的数据
            this.refreshPicList(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.PIC_LIST_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM6.getLocationId()){//车型综述的状态和价格
            //更新车型综述页redis数据
            this.refreshSpecDetail(specId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.SPEC_DETAIL_TOPIC,0,specId);
        }
        log.info("===========更新缓存和清除本地缓存耗时:{}",(System.currentTimeMillis()-start));
        return builder
                .setReturnCode(RETURN_MESSAGE_ENUM0.getReturnCode())
                .setReturnMsg(RETURN_MESSAGE_ENUM0.getReturnMsg())
                .build();
    }

    /**
     * task调用
     * @param locationId
     * @param seriesId
     * @param specId
     * @return
     */
    @Override
    public ApiResult taskRefreshCache(int locationId, int seriesId, int specId) {
        log.info("=================car.api 更新缓存参数locationId：{};seriesId:{};specId:{}================",locationId,seriesId,specId);
        if(locationId <= 0){
            return new ApiResult(null ,RETURN_MESSAGE_ENUM101);
        }
        //车系判断
        if(locationId == 2 || locationId == 3 || locationId == 4 || locationId == 5){
            if(seriesId <= 0){
                return new ApiResult(null ,RETURN_MESSAGE_ENUM102);
            }
        }
        //车型判断
        if(locationId == 6){
            if(specId <= 0){
                return new ApiResult(null ,RETURN_MESSAGE_ENUM102);
            }
        }
        //开始时间
        long start = System.currentTimeMillis();

        //车系列表的状态和价格
        if(locationId == LOCATION_ENUM1.getLocationId()){

        }else if(locationId == LOCATION_ENUM2.getLocationId()){//车系综述的状态和价格
            //更新车系综述页的redis数据
            this.refreshSeriesDetail(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.SERIES_DETAIL_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM3.getLocationId()){//车系综述的车型列表价格
            this.refreshSeriesSpecList(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.SERIES_SPEC_LIST_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM4.getLocationId()){//参数配置
            //更新参配的redis数据
            this.refreshParamConfig(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.PARAM_CONFIG_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM5.getLocationId()){//图片列表
            //更新图片列表的数据
            this.refreshPicList(seriesId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.PIC_LIST_TOPIC,seriesId,0);
        }else if(locationId == LOCATION_ENUM6.getLocationId()){//车型综述的状态和价格
            //更新车型综述页redis数据
            this.refreshSpecDetail(specId);
            //清除本地缓存，使用redis的发布订阅
            this.redisPublisher(CommonUtils.SPEC_DETAIL_TOPIC,0,specId);
        }
        log.info("===========当前更新：{} 更新缓存耗时:{}", LocationEnum.getLocationName(locationId),(System.currentTimeMillis()-start));
        return new ApiResult(null ,RETURN_MESSAGE_ENUM0);
    }

    /**
     * 更新车系综述页redis
     * http://car.api.autohome.com.cn/v2/carprice/series_parambyseriesid.ashx?_appid=app&seriesid={seriesid}
     * http://car.api.autohome.com.cn/v2/App/Spec_SpecItmesBySeriesId.ashx?_appid=app&seriesid={seriesid}&state=0x001f
     * @param seriesId
     */
    private void refreshSeriesDetail(int seriesId){
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(null == seriesBaseInfo){
            return;
        }
        Map<String,Object> seriesBaseParams = new HashMap<>();
        seriesBaseParams.put("seriesId",seriesId);
        seriesBaseService.refreshRedis(seriesBaseParams);
        Map<String, Object> seriesParams = new LinkedHashMap<>();
        seriesParams.put("seriesId", seriesId);


        seriesConfigService.refreshRedis(seriesParams);


        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        Map<String,Object> seriesSpecParams = new HashMap<>();
        seriesSpecParams.put("seriesid",seriesId);
        seriesSpecParams.put("iscv",isCV);

        seriesSpecBaseService.refreshRedis(seriesSpecParams);
        //车型
        List<SpecViewEntity> specs = seriesSpecBaseService.get(seriesId, isCV).join();
        List<Integer> specIds = specs.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
        List<CompletableFuture> specTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String,Object> specParams = new HashMap<>();
            specParams.put("specId",currSpecId);
            specTasks.add(CompletableFuture.runAsync(() -> {
                specBaseService.refreshRedis(specParams);
            }));
        }
        CompletableFuture.allOf(specTasks.toArray(new CompletableFuture[specTasks.size()])).join();
    }

    /**
     * http://car.api.autohome.com.cn/v2/carprice/series_parambyserieslist.ashx
     * http://car.api.autohome.com.cn/v2/App/Spec_SpecItmesBySeriesId.ashx?_appid=app&seriesid={seriesid}&state=0x001f
     * 车系综述的车型列表价格、状态
     * @param seriesId
     */
    private void refreshSeriesSpecList(int seriesId){
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(null == seriesBaseInfo){
            return;
        }
        Map<String,Object> seriesBaseParams = new HashMap<>();
        seriesBaseParams.put("seriesId",seriesId);
        seriesBaseService.refreshRedis(seriesBaseParams);

        Map<String, Object> seriesConfigParams = new LinkedHashMap<>();
        seriesConfigParams.put("seriesId", seriesId);
        seriesConfigService.refreshRedis(seriesConfigParams);

        Map<String,Object> seriesSpecParams = new HashMap<>();
        seriesSpecParams.put("seriesid",seriesId);
        seriesSpecParams.put("iscv",Level.isCVLevel(seriesBaseInfo.getLevelId()));
        seriesSpecBaseService.refreshRedis(seriesSpecParams);
        //车型
        List<SpecViewEntity> specs = seriesSpecBaseService.get(seriesId, Level.isCVLevel(seriesBaseInfo.getLevelId())).join();
        List<Integer> specIds = specs.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
        List<CompletableFuture> specTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String,Object> specParams = new HashMap<>();
            specParams.put("specId",currSpecId);
            specTasks.add(CompletableFuture.runAsync(() -> {
                specBaseService.refreshRedis(specParams);
            }));
        }
        CompletableFuture.allOf(specTasks.toArray(new CompletableFuture[specTasks.size()])).join();
    }

    /**
     * 更新参配页redis
     * http://car.api.autohome.com.cn/v1/app/Config_itemBaseInfo.ashx?_appid=app--------------------------
     *
     * http://car.api.autohome.com.cn/v1/Carprice/Spec_GetSpecInfoBySeriesId.ashx?_appid=app&seriesid=692---------需要
     *
     * http://car.api.autohome.com.cn/v2/car/Config_BagBySeriesId.ashx?_appid=app&seriesid=692------需要
     * http://car.api.autohome.com.cn/v1/carprice/spec_detailbyseriesId.ashx?_appid=app&seriesid=692&state=0X001F
     * http://car.api.autohome.com.cn/v3/CarPrice/Config_GetListBySeriesId.ashx?_appid=app&seriesid=692
     * //车系id获取
     * http://car.api.autohome.com.cn/v1/carprice/spec_innercolorlistbyseriesid.ashx?seriesid=692&_appid=app
     * http://car.api.autohome.com.cn/v1/carprice/spec_colorlistbyseriesid.ashx?seriesid=692&_appid=app
     * //车型id获取
     * http://car.api.autohome.com.cn/v1/carprice/spec_colorlistbyspecidList.ashx?_appid=car&specIdlist={ids}
     * http://car.api.autohome.com.cn/v1/carprice/Spec_InnerColorListBySpecIdList.ashx?_appid=car&specIdlist={ids}
     *
     * http://car.api.autohome.com.cn/v3/CarPrice/SpecificConfig_GetListBySpecList.ashx?_appid=app&speclist=59622,59623,59624,59625,59626,59627,65293,65294,65295,65000,65296
     *
     * http://car.api.autohome.com.cn/v3/config/configWithAiVideoForApp?seriesid=692
     * https://car.api.autohome.com.cn/v1/carprice/spec_infobyspeclist.ashx?_appid=app.iphone&isconfig=1&speclist=59881,65768
     * @param seriesId
     */
    private void refreshParamConfig(int seriesId){
        SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
        if(null == seriesBaseInfo){
            return ;
        }
        boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
        List<SpecViewEntity> list = seriesSpecInfoBaseService.get(seriesId,isCV).join();
        List<Integer> specIds = list.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
        List<CompletableFuture> specTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String,Object> params = new HashMap<>();
            params.put("specId",currSpecId);
            specTasks.add(CompletableFuture.runAsync(() -> {
                specBaseService.refreshRedis(params);
            }));
        }
        CompletableFuture.allOf(specTasks.toArray(new CompletableFuture[specTasks.size()])).join();
        Map<String,Object> seriesSpecInfoParams = new HashMap<>();
        seriesSpecInfoParams.put("seriesid",seriesId);
        seriesSpecInfoParams.put("iscv",isCV);
        seriesSpecInfoBaseService.refreshRedis(seriesSpecInfoParams);

        Map<String,Object> seriesSpecBasePrams = new HashMap<>();
        seriesSpecBasePrams.put("seriesid",seriesId);
        seriesSpecBasePrams.put("iscv",isCV);
        seriesSpecBaseService.refreshRedis(seriesSpecBasePrams);

        List<CompletableFuture> bagTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String, Object> bagParams = new LinkedHashMap<>();
            bagParams.put("specId", currSpecId);
            bagTasks.add(CompletableFuture.runAsync(() -> {
                specConfigBagNewService.refreshRedis(bagParams);
            }));
        }
        CompletableFuture.allOf(bagTasks.toArray(new CompletableFuture[bagTasks.size()])).join();

        List<CompletableFuture> configTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String,Object> specConfigParams = new LinkedHashMap<>();
            specConfigParams.put("specId",currSpecId);
            configTasks.add(CompletableFuture.runAsync(() -> {
                specConfigRelationService.refreshRedis(specConfigParams);
            }));
        }
        CompletableFuture.allOf(configTasks.toArray(new CompletableFuture[configTasks.size()])).join();

        List<CompletableFuture> priceTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String, Object> priceParams = new LinkedHashMap<>();
            priceParams.put("specId", currSpecId);
            priceTasks.add(CompletableFuture.runAsync(() -> {
                specConfigPriceService.refreshRedis(priceParams);
            }));
        }
        CompletableFuture.allOf(priceTasks.toArray(new CompletableFuture[priceTasks.size()])).join();

        List<CompletableFuture> configSubTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String,Object> specConfigSubParams = new LinkedHashMap<>();
            specConfigSubParams.put("specId",currSpecId);
            configSubTasks.add(CompletableFuture.runAsync(() -> {
                specConfigSubItemService.refreshRedis(specConfigSubParams);
            }));
        }
        CompletableFuture.allOf(configSubTasks.toArray(new CompletableFuture[configSubTasks.size()])).join();

        Map<String, Object> specColorParams = new HashMap<>();
        specColorParams.put("seriesId", seriesId);
        specInnerColorBaseService.refreshRedis(specColorParams);
        specSpecColorBaseService.refreshRedis(specColorParams);

        Map<String,Object> colorStatisticsParams = new HashMap<>();
        colorStatisticsParams.put("seriesid",seriesId);
        specPicColorStatisticsBaseService.refreshRedis(colorStatisticsParams);
        Map<String, Object> innerColorStatisticsParams = new LinkedHashMap<>();
        innerColorStatisticsParams.put("seriesId", seriesId);
        seriesSpecPicInnerColorStatistics.refreshRedis(innerColorStatisticsParams);

        List<CompletableFuture> colorTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String, Object> colorParams = new LinkedHashMap<>();
            colorParams.put("specId", currSpecId);
            colorTasks.add(CompletableFuture.runAsync(() -> {
                specColorService.refreshRedis(colorParams);
            }));
        }
        CompletableFuture.allOf(colorTasks.toArray(new CompletableFuture[colorTasks.size()])).join();

        List<CompletableFuture> innerColorTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String, Object> innerColorParams = new LinkedHashMap<>();
            innerColorParams.put("specId", currSpecId);
            innerColorTasks.add(CompletableFuture.runAsync(() -> {
                innerColorSpecService.refreshRedis(innerColorParams);
            }));
        }
        CompletableFuture.allOf(innerColorTasks.toArray(new CompletableFuture[innerColorTasks.size()])).join();

        List<CompletableFuture> specificConfigTasks = new ArrayList<>();
        for(Integer currSpecId : specIds){
            Map<String,Object> specificConfigParams = new HashMap<>();
            specificConfigParams.put("specId",currSpecId);
            specificConfigTasks.add(CompletableFuture.runAsync(() -> {
                specificConfigBaseService.refreshRedis(specificConfigParams);
            }));
        }
        CompletableFuture.allOf(specificConfigTasks.toArray(new CompletableFuture[specificConfigTasks.size()])).join();
    }

    /**
     * 更新图片列表redis
     * http://car.api.autohome.com.cn/v2/app/Pic_PictureItemsByCondition.ashx	车系车型图片 - 内饰
     * http://car.api.autohome.com.cn/v1/carpic/pic_allpictureitemsbycondition.ashx	车系车型图片
     * http://car.api.autohome.com.cn/v1/carprice/spec_parambyspecid.ashx	车型参数
     * http://car.api.autohome.com.cn/v1/carpic/piccolor_coloritemsbyseriesid.ashx	颜色
     * http://car.api.autohome.com.cn/v1/carpic/piccolor_innercoloritemsbyseriesid.ashx	内饰颜色
     * @param seriesId
     */
    private void refreshPicList(int seriesId){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("seriesId", seriesId);
        //刷新图片列表redis缓存
        photosService.refreshRedis(params);

        //刷新外观颜色和内饰颜色的redis 和本地缓存
        seriesSpecPicColorStatistics.refreshRedis(params);
        seriesSpecPicInnerColorStatistics.refreshRedis(params);
    }

    /**
     * 更新车型综述页redis
     * http://car.api.autohome.com.cn/v1/carprice/spec_parambyspecid.ashx?_appid=app.iphone&specid=51933
     * @param specId
     */
    private void refreshSpecDetail(int specId){
        Map<String,Object> param = new LinkedHashMap<>();
        param.put("specId",specId);
        specParamService.refreshRedis(param);
    }

    /**
     * redis 发布数据
     * @param topic
     * @param seriesId
     * @param specId
     */
    private void redisPublisher(String topic,int seriesId,int specId){
        if(seriesId > 0){
            redisTemplate.convertAndSend(topic,String.valueOf(seriesId));
        }

        if(specId > 0){
            redisTemplate.convertAndSend(topic,String.valueOf(specId));
        }
    }

}
