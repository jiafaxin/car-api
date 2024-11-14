package com.autohome.car.api.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiResult<T> implements Serializable {
    int returncode;
    String message;
    T result;

    public ApiResult(){
    }
    public ApiResult (T result, ReturnMessageEnum returnMessageEnum) {
        this.returncode = returnMessageEnum.getReturnCode();
        this.message = returnMessageEnum.getReturnMsg();
        this.result = result;
    }

    public ApiResult(int returnCode,String message){
        this.returncode = returnCode;
        this.message = message;
    }
}
