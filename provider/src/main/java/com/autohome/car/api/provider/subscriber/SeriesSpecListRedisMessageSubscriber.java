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
 *http://car.api.autohome.com.cn/v2/carprice/series_parambyserieslist.ashx
 * http://car.api.autohome.com.cn/v2/App/Spec_SpecItmesBySeriesId.ashx?_appid=app&seriesid={seriesid}&state=0x001f
 * 车系综述的车型列表价格、状态
 */
@Component
@Slf4j
public class SeriesSpecListRedisMessageSubscriber implements MessageListener {


    @Resource
    private SeriesBaseService seriesBaseService;

    @Resource
    private SeriesSpecBaseService seriesSpecBaseService;

    @Resource
    private SeriesConfigService seriesConfigService;

    @Resource
    private SpecBaseService specBaseService;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        //通道
        String channel = new String(message.getChannel());
        //数据
        String messageBody = new String(message.getBody());
        log.info("==============SeriesSpecListRedisMessageSubscriber Received message: {} ; from channel:{}" ,messageBody, channel);
        if(StringUtils.isNotBlank(messageBody)){
            int seriesId = Integer.parseInt(messageBody);
            seriesConfigService.delECache(this.seriesConfigParam(seriesId));
            SeriesBaseInfo seriesBaseInfo = seriesBaseService.get(seriesId).join();
            if(null == seriesBaseInfo){
                return;
            }
            Map<String,Object> seriesBaseParams = new HashMap<>();
            seriesBaseParams.put("seriesId",seriesId);
            seriesBaseService.delECache(seriesBaseParams);

            boolean isCv = Level.isCVLevel(seriesBaseInfo.getLevelId());
            seriesSpecBaseService.delECache(this.seriesSpecParam(seriesId,isCv));
            //车型
            List<SpecViewEntity> specs = seriesSpecBaseService.get(seriesId, isCv).join();
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
        log.info("==============SeriesSpecListRedisMessageSubscriber end===============" );
    }

    private Map<String, Object> seriesConfigParam(int seriesId) {
        Map<String, Object> seriesConfigParam = new LinkedHashMap<>();
        seriesConfigParam.put("seriesId", seriesId);
        return seriesConfigParam;
    }

    /**
     * 参数
     * @param seriesId
     * @param isCv
     * @return
     */
    private Map<String, Object> seriesSpecParam(int seriesId,boolean isCv) {
        Map<String,Object> seriesSpecParams = new HashMap<>();
        seriesSpecParams.put("seriesid",seriesId);
        seriesSpecParams.put("iscv", isCv);
        return seriesSpecParams;
    }
}
