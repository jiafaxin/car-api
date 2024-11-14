package com.autohome.car.api.provider.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class JsonpInterceptor implements HandlerInterceptor {

    final static String paramName = "_callback";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String callback = request.getParameter(paramName);
            if (StringUtils.isNotBlank(callback)) {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.print(callback + "(");
            }
        }catch (Exception e){

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        try {
            String callback = request.getParameter(paramName);
            if (StringUtils.isNotBlank(callback)) {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.print(")");
            }
        }catch (Exception e){

        }
    }

}
