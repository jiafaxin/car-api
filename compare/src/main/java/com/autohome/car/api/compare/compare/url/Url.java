package com.autohome.car.api.compare.compare.url;

import com.autohome.car.api.compare.compare.param.Param;

import java.util.Map;

public interface Url {
    Map<String, Param> getUrl();

    /**
     * 是否支持
     */
    boolean isSupport();
}
