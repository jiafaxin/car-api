package com.autohome.car.api.provider.subscriber;

import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesSpecPicInnerColorStatistics;
import com.autohome.car.api.services.basic.specs.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 *http://car.api.autohome.com.cn/v1/app/Config_itemBaseInfo.ashx?_appid=app--------------------------
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
 * 监听参配的redis信息
 */
@Component
@Slf4j
public class ParamConfigRedisMessageSubscriber implements MessageListener {

    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private SeriesSpecInfoBaseService seriesSpecInfoBaseService;

    @Resource
    private SpecBaseService specBaseService;

    @Resource
    private SeriesSpecBaseService seriesSpecBaseService;

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
    private SpecPicColorStatisticsBaseService specPicColorStatisticsBaseService;

    @Resource
    private SeriesSpecPicInnerColorStatistics seriesSpecPicInnerColorStatistics;

    @Resource
    private SpecColorService specColorService;

    @Resource
    private InnerColorSpecService innerColorSpecService;

    @Resource
    private SpecificConfigBaseService specificConfigBaseService;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        //通道
        String channel = new String(message.getChannel());
        //数据
        String messageBody = new String(message.getBody());
        log.info("============ParamConfigRedisMessageSubscriber Received message: {} ; from channel:{}" ,messageBody, channel);
        long startTime = System.currentTimeMillis();
        if(StringUtils.isNotBlank(messageBody)){
            //车系id
            int seriesId = Integer.parseInt(messageBody);
            SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
            if(null == seriesBaseInfo){
                return ;
            }
            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            List<SpecViewEntity> list = seriesSpecInfoBaseService.get(seriesId,isCV).join();
            List<Integer> specIds = list.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
            //车型基本信息
            List<CompletableFuture> specTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String,Object> params = new HashMap<>();
                params.put("specId",currSpecId);
                specTasks.add(CompletableFuture.runAsync(() -> {
                    specBaseService.delECache(params);
                }));
            }
            CompletableFuture.allOf(specTasks.toArray(new CompletableFuture[specTasks.size()])).join();

            Map<String,Object> seriesSpecInfoParams = new HashMap<>();
            seriesSpecInfoParams.put("seriesid",seriesId);
            seriesSpecInfoParams.put("iscv",isCV);
            seriesSpecInfoBaseService.delECache(seriesSpecInfoParams);

            Map<String,Object> seriesSpecBasePrams = new HashMap<>();
            seriesSpecBasePrams.put("seriesid",seriesId);
            seriesSpecBasePrams.put("iscv",isCV);
            seriesSpecBaseService.delECache(seriesSpecBasePrams);

            List<CompletableFuture> bagTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String, Object> bagParams = new LinkedHashMap<>();
                bagParams.put("specId", currSpecId);
                bagTasks.add(CompletableFuture.runAsync(() -> {
                    specConfigBagNewService.delECache(bagParams);
                }));
            }
            CompletableFuture.allOf(bagTasks.toArray(new CompletableFuture[bagTasks.size()])).join();

            List<CompletableFuture> configTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String,Object> specConfigParams = new LinkedHashMap<>();
                specConfigParams.put("specId",currSpecId);
                configTasks.add(CompletableFuture.runAsync(() -> {
                    specConfigRelationService.delECache(specConfigParams);
                }));
            }
            CompletableFuture.allOf(configTasks.toArray(new CompletableFuture[configTasks.size()])).join();

            List<CompletableFuture> priceTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String, Object> priceParams = new LinkedHashMap<>();
                priceParams.put("specId", currSpecId);
                priceTasks.add(CompletableFuture.runAsync(() -> {
                    specConfigPriceService.delECache(priceParams);
                }));
            }
            CompletableFuture.allOf(priceTasks.toArray(new CompletableFuture[priceTasks.size()])).join();

            List<CompletableFuture> configSubTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String,Object> specConfigSubParams = new LinkedHashMap<>();
                specConfigSubParams.put("specId",currSpecId);
                configSubTasks.add(CompletableFuture.runAsync(() -> {
                    specConfigSubItemService.delECache(specConfigSubParams);
                }));
            }
            CompletableFuture.allOf(configSubTasks.toArray(new CompletableFuture[configSubTasks.size()])).join();

            Map<String, Object> specColorParams = new HashMap<>();
            specColorParams.put("seriesId", seriesId);
            specInnerColorBaseService.delECache(specColorParams);
            specSpecColorBaseService.delECache(specColorParams);

            Map<String,Object> colorStatisticsParams = new HashMap<>();
            colorStatisticsParams.put("seriesid",seriesId);
            specPicColorStatisticsBaseService.delECache(colorStatisticsParams);

            Map<String, Object> innerColorStatisticsParams = new LinkedHashMap<>();
            innerColorStatisticsParams.put("seriesId", seriesId);
            seriesSpecPicInnerColorStatistics.delECache(innerColorStatisticsParams);

            List<CompletableFuture> colorTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String, Object> colorParams = new LinkedHashMap<>();
                colorParams.put("specId", currSpecId);
                colorTasks.add(CompletableFuture.runAsync(() -> {
                    specColorService.delECache(colorParams);
                }));
            }
            CompletableFuture.allOf(colorTasks.toArray(new CompletableFuture[colorTasks.size()])).join();

            List<CompletableFuture> innerColorTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String, Object> innerColorParams = new LinkedHashMap<>();
                innerColorParams.put("specId", currSpecId);
                innerColorTasks.add(CompletableFuture.runAsync(() -> {
                    innerColorSpecService.delECache(innerColorParams);
                }));
            }
            CompletableFuture.allOf(innerColorTasks.toArray(new CompletableFuture[innerColorTasks.size()])).join();

            List<CompletableFuture> specificConfigTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String,Object> specificConfigParams = new HashMap<>();
                specificConfigParams.put("specId",currSpecId);
                specificConfigTasks.add(CompletableFuture.runAsync(() -> {
                    specificConfigBaseService.delECache(specificConfigParams);
                }));
            }
            CompletableFuture.allOf(specificConfigTasks.toArray(new CompletableFuture[specificConfigTasks.size()])).join();
        }

        log.info("============ParamConfigRedisMessageSubscriber end============,耗时：{}",System.currentTimeMillis()-startTime);

    }
}
