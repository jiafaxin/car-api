package com.autohome.car.api.provider.subscriber;

import com.autohome.car.api.common.CommonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * 注册监听
 */
@Configuration
public class RedisMessageConfig {
    /**
     * 车系综述页
     */
    @Resource
    private SeriesDetailRedisMessageSubscriber seriesDetailRedisMessageSubscriber;

    /**
     * 车系综述的车型列表价格、状态
     */
    @Resource
    private SeriesSpecListRedisMessageSubscriber seriesSpecListRedisMessageSubscriber;

    /**
     * 参配页
     */
    @Resource
    private ParamConfigRedisMessageSubscriber paramConfigRedisMessageSubscriber;

    /**
     * 图片列表
     */
    @Resource
    private PicListRedisMessageSubscriber picListRedisMessageSubscriber;

    /**
     * 车型综述页
     */
    @Resource
    private SpecDetailRedisMessageSubscriber specDetailRedisMessageSubscriber;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 设置连接工厂，RedisConnectionFactory 可以直接从容器中取，也可以从 RedisTemplate 中取
        container.setConnectionFactory(factory);
        // 订阅名称叫 pic_list_topic 的通道, 类似 Redis 中的 subscribe 命令
        //Channel Topic 是针对特定频道的订阅，而 Pattern Topic 则是针对一类频道的订阅，通过模式匹配的方式进行订阅。
        //Pattern Topic 在需要同时订阅多个相关频道时非常有用，因为它允许订阅者使用通配符来简化订阅操作。
        container.addMessageListener(seriesDetailRedisMessageSubscriber, new ChannelTopic(CommonUtils.SERIES_DETAIL_TOPIC));
        container.addMessageListener(seriesSpecListRedisMessageSubscriber, new ChannelTopic(CommonUtils.SERIES_SPEC_LIST_TOPIC));
        container.addMessageListener(paramConfigRedisMessageSubscriber, new ChannelTopic(CommonUtils.PARAM_CONFIG_TOPIC));
        container.addMessageListener(picListRedisMessageSubscriber, new ChannelTopic(CommonUtils.PIC_LIST_TOPIC));
        container.addMessageListener(specDetailRedisMessageSubscriber, new ChannelTopic(CommonUtils.SPEC_DETAIL_TOPIC));

        return container;

    }



}
