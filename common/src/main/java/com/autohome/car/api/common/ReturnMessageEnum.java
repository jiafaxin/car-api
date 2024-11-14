package com.autohome.car.api.common;

import lombok.Getter;

@Getter
public enum ReturnMessageEnum {
    RETURN_MESSAGE_ENUM0(0,"成功"),
    RETURN_MESSAGE_ENUM101(101,"缺少必要的请求参数"),
    RETURN_MESSAGE_ENUM102(102,"请求参数格式错误"),
    RETURN_MESSAGE_ENUM103(103,"缺少参数_appid"),
    RETURN_MESSAGE_ENUM104(104,"该_appid不存在"),
    RETURN_MESSAGE_ENUM105(105,"该_appid已停用"),
    RETURN_MESSAGE_ENUM106(106,"缺少签名"),
    RETURN_MESSAGE_ENUM107(107,"签名错误"),
    RETURN_MESSAGE_ENUM108(108,"缺少参数_timestamp"),
    RETURN_MESSAGE_ENUM109(109,"请求已过期"),
    RETURN_MESSAGE_ENUM110(110,"请求重发"),
    RETURN_MESSAGE_ENUM111(111,"HTTP请求非get方式"),
    RETURN_MESSAGE_ENUM112(112,"HTTP请求非post方式"),
    RETURN_MESSAGE_ENUM113(113,"接口版本不存在"),
    RETURN_MESSAGE_ENUM114(114,"没有接口访问权限"),
    RETURN_MESSAGE_ENUM115(115,"接口已停用"),
    RETURN_MESSAGE_ENUM116(116,"接口不处于上线状态"),
    RETURN_MESSAGE_ENUM117(117,"已超限频阀值"),
    RETURN_MESSAGE_ENUM118(118,"已超限流阀值"),
    RETURN_MESSAGE_ENUM119(119,"ip限制不能访问资源"),
    RETURN_MESSAGE_ENUM120(120,"参数过长"),
    RETURN_MESSAGE_ENUM403(403,"访问被禁止"),
    RETURN_MESSAGE_ENUM404(404,"页面找不到"),
    RETURN_MESSAGE_ENUM500(500,"服务器错误");

    private int returnCode;

    private String returnMsg;

    ReturnMessageEnum(int returnCode,String returnMsg){
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

}
