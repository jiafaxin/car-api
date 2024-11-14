package com.autohome.car.api.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCache {

    /**
     * 多久过期，单位分钟
     * @return
     */
    int removeIn() default 10;

    /**
     * 缓存有效期
     * @return
     */
    int expireIn();

    /**
     * 版本
     * @return
     */
    String v() default "v0";

}
