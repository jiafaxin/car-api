package com.autohome.car.api.services.basic;

import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 二维码服务
 */
@Component
public class QRCodeService {

    @Autowired
    StringRedisTemplate redisTemplate;

    public CompletableFuture<String> series(int seriesId){
        return get(String.format("objid=%s&type=2&_appid=car",seriesId));
    }

    public CompletableFuture<String> spec(int seriesId, int specId) {
        return get(String.format("objid=%s&specid=%s&type=3&_appid=car",seriesId,specId));
    }


    CompletableFuture<String> get(String params) {
        String key = "qrcode:".concat(DigestUtils.md5DigestAsHex(params.getBytes(StandardCharsets.UTF_8)));
        String val = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(val))
            return CompletableFuture.completedFuture(val);

        return getQRCode(params).thenApply(x -> {
            if (x != null) {
                redisTemplate.opsForValue().set(key, x, 30, TimeUnit.DAYS);
            }else{
                //无法获取的时候，返回默认值
                return "https://car3.autoimg.cn/cardfs/activity/g29/M0A/F0/95/100x100_autohomecar__ChsEn1wgUjyAEX6yAAC57BUfG1o773.jpg";
            }
            return x;
        }).exceptionally(x->{
            return "https://car3.autoimg.cn/cardfs/activity/g29/M0A/F0/95/100x100_autohomecar__ChsEn1wgUjyAEX6yAAC57BUfG1o773.jpg";
        });
    }

    CompletableFuture<String> getQRCode(String params) {
        String url = "http://wxcarlq.api.autohome.com.cn/api/Article/GetWxQRcode".concat("?").concat(params);
        return HttpClient.getResult(url, new TypeReference<ApiResult<String>>() {
        }).thenApply(result -> {
            if (result.getReturncode() == 0) {
                return result.getResult();
            }
            return null;
        });
    }
}
