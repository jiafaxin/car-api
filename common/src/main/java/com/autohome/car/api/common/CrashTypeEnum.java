package com.autohome.car.api.common;

public enum CrashTypeEnum {

    V1("耐撞性与维修经济性指数"),
    V2("车内乘员安全指数"),
    V3("车外行人安全"),
    V4("车辆辅助安全");



    private final String value;

    CrashTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
