package com.autohome.car.api.provider.common;

import com.autohome.car.api.common.ApiResult;
import com.autohome.car.api.common.ReturnMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * grpc 调用异常处理
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER})
public class CarApiRpcExceptionFilter implements Filter, BaseFilter.Listener{

    //处理异常返回信息
    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        // 当 Dubbo 调用成功返回时触发该方法
        if(appResponse.hasException() && GenericService.class != invoker.getInterface()){
            Throwable exception = appResponse.getException();
            Object result = null;
            try {
                String methodName = invocation.getMethodName();
                //转小写
                //methodName = methodName.substring(0,1).toLowerCase() +  methodName.substring(1);
                //反射拿到当前方法,这种也可以
                //Method method = invoker.getInterface().getMethod(methodName, invocation.getParameterTypes());、
                //拿到当前方法
                Method method = getMethod(invoker.getInterface(), methodName, invocation.getParameterTypes());
                //获取返回对象类
                Class<?> returnClass = method.getReturnType();
                if(returnClass != Void.class){
                    result = MessageUtil.toMessage(new ApiResult<>(ReturnMessageEnum.RETURN_MESSAGE_ENUM500.getReturnCode(),exception.toString()), returnClass);
                }
            }catch (Exception e){
                log.error("CarApiRpcExceptionFilter onResponse Exception :{}",e.getMessage());
                throw new RuntimeException(e);
            }
            appResponse.setValue(result);
            appResponse.setException(null);
            log.error("carApiRpc invoking exception message :{}",exception.getMessage());
        }
    }

    /**
     * 获取当前反射方法
     * @param zClass
     * @param methodName
     * @param parameterTypes
     * @return
     */
    private Method getMethod(Class<?> zClass,String methodName,Class<?>... parameterTypes){
        Method[] methods = zClass.getMethods();
        for(Method method : methods){
            if(method.getName().equalsIgnoreCase(methodName) && Arrays.equals(method.getParameterTypes() ,parameterTypes)){
                return method;
            }
        }
        throw new RuntimeException("carApiRpc not fond method :" + methodName);
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        // 当 Dubbo 调用发生异常时触发该方法
        log.error("Dubbo service invoking failed:{}",t.getMessage());
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }
}
