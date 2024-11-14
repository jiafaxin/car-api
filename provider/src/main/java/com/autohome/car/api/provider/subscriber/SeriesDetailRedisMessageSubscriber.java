package com.autohome.car.api.provider.subscriber;

import com.autohome.car.api.common.BaseConfig.Level;
import com.autohome.car.api.data.popauto.entities.SpecViewEntity;
import com.autohome.car.api.services.basic.SeriesBaseService;
import com.autohome.car.api.services.basic.SeriesSpecBaseService;
import com.autohome.car.api.services.basic.SpecBaseService;
import com.autohome.car.api.services.basic.models.SeriesBaseInfo;
import com.autohome.car.api.services.basic.series.SeriesConfigService;
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
 * http://car.api.autohome.com.cn/v2/carprice/series_parambyseriesid.ashx?_appid=app&seriesid={seriesid}
 * http://car.api.autohome.com.cn/v2/App/Spec_SpecItmesBySeriesId.ashx?_appid=app&seriesid={seriesid}&state=0x001f
 * 监听车系综述页的redis信息
 * 清除本地缓存
 * 测试完成
 */
@Component
@Slf4j
public class SeriesDetailRedisMessageSubscriber implements MessageListener {

    @Resource
    private SeriesConfigService seriesConfigService;

    @Resource
    private SeriesSpecBaseService seriesSpecBaseService;


    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private SpecBaseService specBaseService;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        //通道
        String channel = new String(message.getChannel());
        //数据
        String messageBody = new String(message.getBody());
        log.info("==================SeriesDetailRedisMessageSubscriber Received message: {} ; from channel:{}" ,messageBody, channel);
        if(StringUtils.isNotBlank(messageBody)){
            int seriesId = Integer.parseInt(messageBody);
            seriesConfigService.delECache(this.seriesConfigParam(seriesId));
            SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
            if(seriesBaseInfo==null){
                return ;
            }
            Map<String,Object> seriesBaseParams = new HashMap<>();
            seriesBaseParams.put("seriesId",seriesId);
            seriesBaseService.delECache(seriesBaseParams);

            boolean isCV = Level.isCVLevel(seriesBaseInfo.getLevelId());
            seriesSpecBaseService.delECache(this.seriesSpecParam(seriesId,isCV));
            //车型
            List<SpecViewEntity> specs = seriesSpecBaseService.get(seriesId, isCV).join();
            List<Integer> specIds = specs.stream().map(SpecViewEntity::getSpecId).distinct().collect(Collectors.toList());
            List<CompletableFuture> specTasks = new ArrayList<>();
            for(Integer currSpecId : specIds){
                Map<String,Object> specParams = new HashMap<>();
                specParams.put("specId",currSpecId);
                specTasks.add(CompletableFuture.runAsync(() -> {
                    specBaseService.delECache(specParams);
                }));
            }
            CompletableFuture.allOf(specTasks.toArray(new CompletableFuture[specTasks.size()])).join();

        }
        log.info("==================SeriesDetailRedisMessageSubscriber end ===============================");
    }

    /**
     * seriesConfigService参数
     * @param seriesId
     * @return
     */
    private Map<String, Object> seriesConfigParam(int seriesId) {
        Map<String, Object> seriesConfigParam = new LinkedHashMap<>();
        seriesConfigParam.put("seriesId", seriesId);
        return seriesConfigParam;
    }


    /**
     * seriesSpecBaseService参数
     * @param seriesId
     * @param isCv
     * @return
     */
    private Map<String, Object> seriesSpecParam(int seriesId,boolean isCv) {
        Map<String,Object> seriesSpecParam = new HashMap<>();
        seriesSpecParam.put("seriesid",seriesId);
        seriesSpecParam.put("iscv",isCv);
        return seriesSpecParam;
    }
}
