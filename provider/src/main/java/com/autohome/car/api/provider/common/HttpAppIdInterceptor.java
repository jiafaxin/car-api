//package com.autohome.car.api.provider.common;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.autohome.car.api.common.CommonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//public class HttpAppIdInterceptor implements HandlerInterceptor{
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String appid = request.getParameter(CommonUtils._APPID);
//        if(StringUtils.isBlank(appid)){
//            log.warn("http-appid is not exist! url:{}; methodType:{}; parameter:{}", request.getRequestURL(),request.getMethod(),request.getQueryString());
//            String jsonObjectStr = JSONObject.toJSONString(CommonUtils.getAppIdError(103));
//            returnJson(response, jsonObjectStr);
//            return false;
//        }
//
//        List<String> appIdList = Arrays.asList("car","club");
//        if(!appIdList.contains(appid)){
//            log.warn("http-appid is not appIdList! url:{}; methodType:{}; parameter:{}", request.getRequestURL(),request.getMethod(),request.getQueryString());
//            String jsonObjectStr = JSONObject.toJSONString(CommonUtils.getAppIdError(104));
//            returnJson(response, jsonObjectStr);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 写入客户端
//     */
//    private void returnJson(HttpServletResponse response, String json) {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json");
//        try (PrintWriter writer = response.getWriter()) {
//            writer.print(json);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }
//}
