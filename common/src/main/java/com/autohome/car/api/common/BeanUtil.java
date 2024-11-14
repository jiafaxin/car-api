package com.autohome.car.api.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {

    static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)  {
        BeanUtil.applicationContext = applicationContext;
    }

    public static <T> T get(Class<T> t) {
        return applicationContext.getBean(t);
    }
}
