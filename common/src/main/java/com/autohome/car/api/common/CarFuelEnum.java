package com.autohome.car.api.common;

public enum CarFuelEnum {

    空(-1),
    全部(0),
    汽油(1),
    柴油(2),
    油电混合(3),
    纯电动(4),
    插电式混动(5),
    增程式(6),
    氢燃料(7),
    汽油48V轻混系统(8),
    汽油24V轻混系统(9),
    全部新能源(701),
    全部轻混系统(801);

    private final int value;

    CarFuelEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CarFuelEnum fromValue(int value) {
        for (CarFuelEnum cf : values()) {
            if (cf.getValue() == value) {
                return cf;
            }
        }
        return 空;
    }
}