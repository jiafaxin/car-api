package com.autohome.car.api.common;

public enum ParamTypeEnum {
    无(0),
    价格(1),
    排量(2),
    驱动(3),
    变速箱(4),
    结构(5),
    生产方式(6),
    燃料(7),
    国别(8),
    座位数(9),
    级别(10),
    参数配置(11);

    private final int value;

    ParamTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ParamTypeEnum fromValue(int value) {
        for (ParamTypeEnum pt : values()) {
            if (pt.getValue() == value) {
                return pt;
            }
        }
        return 无;
    }
}