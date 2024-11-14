package com.autohome.car.api.common;

public enum CrashTypeEnum2020 {

    V5("耐撞性与维修经济性指数"),
    V6("车内乘员安全指数"),
    V7("车外行人安全"),
    V8("车辆辅助安全");



    private final String value;

    CrashTypeEnum2020(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
