//package com.autohome.car.api.provider.common;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.autohome.car.api.common.CommonUtils;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.common.constants.CommonConstants;
//import org.apache.dubbo.common.extension.Activate;
//import org.apache.dubbo.rpc.*;
//
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@Activate(group = {CommonConstants.PROVIDER},order = -99999)
//public class DubboAppIdFilter implements Filter {
//
//    @SneakyThrows
//    @Override
//    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//        Object[] arguments = invocation.getArguments();
//        String jsonString = JSON.toJSONString(arguments);
//        JSONArray jsonArray = JSONArray.parseArray(jsonString);
//        JSONObject jsonObject = jsonArray.getJSONObject(0);
//        String appid = jsonObject.getString(CommonUtils.APPID);
//
//        List<String> appIdList = Arrays.asList("car","club");
//        //方法名大写
//        String methodName = invocation.getMethodName();
//        //转小写
//        methodName = methodName.substring(0,1).toLowerCase() +  methodName.substring(1);
//        //反射拿到当前方法
//        Method method = invoker.getInterface().getMethod(methodName, invocation.getParameterTypes());
//        //根据当前方法获取返回值类型
//        Class<?> type = method.getReturnType();
//        if(StringUtils.isBlank(appid)){
//            log.warn("grpc-appid is not exist! interfaceName:{}; methodName:{}; parameter:{}", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), invocation.getArguments());
//            Object result = MessageUtil.toMessage(CommonUtils.getAppIdError(103), type);
//            return AsyncRpcResult.newDefaultAsyncResult(result,invocation);
//        }
//        if(!appIdList.contains(appid)){
//            log.warn("grpc-appid is not appIdList! interfaceName:{}; methodName:{}; parameter:{}", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), invocation.getArguments());
//            Object result = MessageUtil.toMessage(CommonUtils.getAppIdError(104), type);
//            return AsyncRpcResult.newDefaultAsyncResult(result,invocation);
//        }
//        // 没有检查到需要校验 或者 没有校验出错误 则调用业务逻辑
//        return invoker.invoke(invocation);
//    }
//
//}
