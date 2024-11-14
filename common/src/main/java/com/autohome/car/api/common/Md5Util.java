package com.autohome.car.api.common;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class Md5Util {
    public static String get(Object obj){
        String json = JsonUtils.toString(obj);
        return DigestUtils.md5DigestAsHex(json.getBytes(StandardCharsets.UTF_8));
    }
}
