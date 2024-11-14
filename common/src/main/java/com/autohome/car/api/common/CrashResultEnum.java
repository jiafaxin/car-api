package com.autohome.car.api.common;

public enum CrashResultEnum {

    V1("G优秀"),
    V2("A良好"),
    V3("M一般"),
    V4("P较差");



    private final String value;

    CrashResultEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
