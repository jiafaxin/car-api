package com.autohome.car.api.common;

public enum CarStructEnum {

    空(-1),
    全部(0),
    两厢(1),
    三厢(2),
    掀背(3),
    旅行版(4),
    硬顶敞篷车(5),
    软顶敞篷车(6),
    硬顶跑车(7),
    客车(8),
    货车(9),
    皮卡(10),
    MPV(11),
    SUV(12),
    跨界车(1000);

    private final int value;

    CarStructEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CarStructEnum fromValue(int value) {
        for (CarStructEnum cs : values()) {
            if (cs.getValue() == value) {
                return cs;
            }
        }
        return 空;
    }
}