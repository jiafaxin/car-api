package com.autohome.car.api.common;

public enum CarGearBoxEnum {

    空(-1),
    全部(0),
    手动(1),
    自动(2);

    private final int value;

    CarGearBoxEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CarGearBoxEnum fromValue(int value) {
        for (CarGearBoxEnum gearbox : values()) {
            if (gearbox.getValue() == value) {
                return gearbox;
            }
        }
        return 空;
    }
}