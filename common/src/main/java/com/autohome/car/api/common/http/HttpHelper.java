package com.autohome.car.api.common.http;

import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP工具类，封装HttpClient4.3.x来对外提供简化的HTTP请求
 */
public class HttpHelper {

    private final static int SOCKET_TIMEOUT = 30000;
    private final static int CONNECT_TIMEOUT = 30000;
    private final static int CONNECTION_REQUEST_TIMEOUT = 30000;
    private final static int RETRY_COUNT = 0;
    private final static String ENCODING = "utf-8";


    public static ResponseContent getResponse(String url, String encoding)
            throws IOException, HttpException {
        return getResponse(url, ENCODING, CONNECTION_REQUEST_TIMEOUT);
    }

    public static ResponseContent getResponse(String url, String encoding, int timeout)
            throws IOException, HttpException {
        return getResponse(url, encoding, timeout, RETRY_COUNT);
    }

    public static ResponseContent getResponse(String url, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        return getResponse(url, null, null, encoding, timeout, retryCount);
    }


    public static ResponseContent getResponse(String url, Map<String, String> params, String encoding)
            throws IOException, HttpException {
        return getResponse(url, params, encoding, CONNECTION_REQUEST_TIMEOUT);
    }

    public static ResponseContent getResponse(String url, Map<String, String> params, String encoding, int timeout)
            throws IOException, HttpException {
        return getResponse(url, null, params, encoding, timeout, RETRY_COUNT);
    }

    public static ResponseContent getResponse(String url, Map<String, String> params, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        return getResponse(url, null, params, encoding, timeout, retryCount);
    }

    public static ResponseContent getResponse(String url, Map<String, String> header, Map<String, String> params, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        HttpClientWrapper hw = new HttpClientWrapper(timeout, CONNECT_TIMEOUT, SOCKET_TIMEOUT, retryCount);
        return hw.getResponse(url, header, params, encoding);
    }

    public static ResponseContent postResponse(String url, String encoding)
            throws IOException, HttpException {
        return postResponse(url, null, ENCODING);
    }

    public static ResponseContent postResponse(String url, Map<String, String> params, String encoding)
            throws IOException, HttpException {
        return postResponse(url, params, encoding, CONNECTION_REQUEST_TIMEOUT);
    }

    public static ResponseContent postResponse(String url, Map<String, String> params, String encoding, int timeout)
            throws IOException, HttpException {
        return postResponse(url, null, params, encoding, timeout, RETRY_COUNT);
    }

    public static ResponseContent postResponse(String url, Map<String, String> params, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        return postResponse(url, null, params, encoding, timeout, retryCount);
    }

    public static ResponseContent postResponse(String url, Map<String, String> header, Map<String, String> params, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        HttpClientWrapper hw = new HttpClientWrapper(timeout, CONNECT_TIMEOUT, SOCKET_TIMEOUT, retryCount);
        return hw.postResponse(url, header, params, encoding);
    }

    public static ResponseContent postJson(String url, Map<String, String> header, String json, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        HttpClientWrapper hw = new HttpClientWrapper(timeout, CONNECT_TIMEOUT, CONNECT_TIMEOUT, retryCount);
        return hw.postJson(url, header, json, encoding);
    }

    public static ResponseContent upload(String url, Map<String, String> header, Map<String, byte[]> params, String encoding, int timeout, int retryCount)
            throws IOException, HttpException {
        HttpClientWrapper hw = new HttpClientWrapper(timeout, CONNECT_TIMEOUT, SOCKET_TIMEOUT, retryCount);
        return hw.upload(url, header, params, encoding);
    }


}
