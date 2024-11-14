package com.autohome.car.api.provider.subscriber;

import com.autohome.car.api.services.basic.series.PhotosService;
import com.autohome.car.api.services.basic.series.SeriesSpecPicColorStatistics;
import com.autohome.car.api.services.basic.series.SeriesSpecPicInnerColorStatistics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * http://car.api.autohome.com.cn/v2/app/Pic_PictureItemsByCondition.ashx	车系车型图片 - 内饰
 * http://car.api.autohome.com.cn/v1/carpic/pic_allpictureitemsbycondition.ashx	车系车型图片
 * http://car.api.autohome.com.cn/v1/carprice/spec_parambyspecid.ashx	车型参数
 * http://car.api.autohome.com.cn/v1/carpic/piccolor_coloritemsbyseriesid.ashx	颜色
 * http://car.api.autohome.com.cn/v1/carpic/piccolor_innercoloritemsbyseriesid.ashx	内饰颜色
 * 图片列表相关订阅
 * 处理本地缓存
 */
@Component
@Slf4j
public class PicListRedisMessageSubscriber implements MessageListener {

    @Resource
    private PhotosService photosService;

    @Resource
    private SeriesSpecPicColorStatistics seriesSpecPicColorStatistics;

    @Resource
    private SeriesSpecPicInnerColorStatistics seriesSpecPicInnerColorStatistics;



    @Override
    public void onMessage(Message message, byte[] pattern) {
        //通道
        String channel = new String(message.getChannel());
        //数据
        String messageBody = new String(message.getBody());
        log.info("=========PicListRedisMessageSubscriber Received message: {} ; from channel:{}" ,messageBody, channel);
        if(StringUtils.isNotBlank(messageBody)){
            int seriesId = Integer.parseInt(messageBody);
            photosService.delECache(this.makeParam(seriesId));
            //清除外观颜色本地缓存
            seriesSpecPicColorStatistics.delECache(this.makeParam(seriesId));
            //清除内饰颜色本地缓存
            seriesSpecPicInnerColorStatistics.delECache(this.makeParam(seriesId));
        }
        log.info("=========PicListRedisMessageSubscriber end=========");
    }

    /**
     * 参数
     * @param seriesId
     * @return
     */
    private Map<String, Object> makeParam(int seriesId) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("seriesId", seriesId);
        return param;
    }
}
