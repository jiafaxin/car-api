package com.autohome.car.api.common.cache;

import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MethodDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    public MethodDetail(){}
    public MethodDetail(Method method, Object[] args) {
        setClassName(method.getDeclaringClass().getName());
        setMethodName(method.getName());
        setParams(new ArrayList<>());
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];

            ParamInfo info = new ParamInfo();
            info.setName(parameter.getName());
            info.setValue(args[i]);
            info.setTypeName(parameter.getType().getName());

            getParams().add(info);
        }
    }

    private String className;
    private String methodName;
    private List<ParamInfo> params;


    public  String instanceKey(AutoCache cacheAnnotation) {
        StringBuilder key = new StringBuilder("redis:method:cache:");
        key.append(getClassName()).append(":").append(getMethodName());

        for (ParamInfo param : getParams()) {
            key.append(":").append(param.getName()).append("_").append(param.getValue());
        }

        key.append(":").append(cacheAnnotation.v());
        return DigestUtils.md5DigestAsHex(key.toString().getBytes(StandardCharsets.UTF_8));
    }

    public  String instanceKey(AutoL1L2Cache cacheAnnotation) {
        StringBuilder key = new StringBuilder("eh_redis:method:cache:");
        key.append(getClassName()).append(":").append(getMethodName());

        for (ParamInfo param : getParams()) {
            key.append(":").append(param.getName()).append("_").append(param.getValue());
        }

        key.append(":").append(cacheAnnotation.v());
        return DigestUtils.md5DigestAsHex(key.toString().getBytes(StandardCharsets.UTF_8));
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<ParamInfo> getParams() {
        return params;
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }
}
