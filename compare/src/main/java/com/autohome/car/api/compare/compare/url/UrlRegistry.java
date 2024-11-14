package com.autohome.car.api.compare.compare.url;


import com.autohome.car.api.compare.compare.param.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class UrlRegistry implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public static Map<String, Param> map = new HashMap<>();
    @PostConstruct
    public void initMap() {
        Map<String, Url> beansOfType = applicationContext.getBeansOfType(Url.class);
        List<Url> baseUrls = new ArrayList<>(beansOfType.values());
        if (!CollectionUtils.isEmpty(baseUrls)) {
            for (Url baseUrl : baseUrls) {
                if (baseUrl.isSupport()) {
                    map.putAll(baseUrl.getUrl());
                }
            }
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<String, Param> getUrlMap() {
        return map;
    }

}
