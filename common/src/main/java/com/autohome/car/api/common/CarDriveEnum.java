package com.autohome.car.api.common;

public enum CarDriveEnum {

    空(-1),
    全部(0),
    前驱(1),
    后驱(2),
    四驱(3);

    private final int value;

    CarDriveEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CarDriveEnum fromValue(int value) {
        for (CarDriveEnum dr : values()) {
            if (dr.getValue() == value) {
                return dr;
            }
        }
        return 空;
    }
}