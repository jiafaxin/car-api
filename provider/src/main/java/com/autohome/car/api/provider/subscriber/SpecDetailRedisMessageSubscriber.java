package com.autohome.car.api.provider.subscriber;

import com.autohome.car.api.services.basic.specs.SpecParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * 监听车型综述页的redis信息
 */
@Component
@Slf4j
public class SpecDetailRedisMessageSubscriber implements MessageListener {


    @Resource
    private SpecParamService specParamService;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        //通道
        String channel = new String(message.getChannel());
        //数据
        String messageBody = new String(message.getBody());
        log.info("===========SpecDetailRedisMessageSubscriber Received message: {} ; from channel:{}" ,messageBody, channel);
        if(StringUtils.isNotBlank(messageBody)){
            int specId = Integer.parseInt(messageBody);
            specParamService.delECache(this.makeParam(specId));
        }
        log.info("===========SpecDetailRedisMessageSubscriber end===========");

    }

    /**
     * 参数
     * @param specId
     * @return
     */
    private Map<String, Object> makeParam(int specId) {
        Map<String,Object> param = new LinkedHashMap<>();
        param.put("specId",specId);
        return param;
    }
}
