package com.autohome.car.api.common.http;

/**
 *
 */

import java.util.UUID;

public class RequestID {
    /**
     * 请求全局唯一ID，跟踪请求全过程
     */
    private static ThreadLocal<String> requestID = new ThreadLocal<String>();
    /**
     * 请求开始时间，可用于统计请求响应时间
     */
    private static ThreadLocal<Long> requestTime = new ThreadLocal<Long>();

    /**
     * 请求模式
     */
    private static ThreadLocal<Boolean> debug = new ThreadLocal<Boolean>();

    public static void setRequestID(String requestId) {
        requestID.set(requestId);
        requestTime.set(System.currentTimeMillis());
    }

    public static void setRequestID() {
        requestID.set(UUID.randomUUID().toString());
        requestTime.set(System.currentTimeMillis());
    }

    public static void setDebug() {
        debug.set(true);
    }

    public static String getRequestID() {
        return requestID.get();
    }

    public static Long getRequestTime() {
        return requestTime.get();
    }

    public static boolean getDebug() {
        return debug.get();
    }

}
