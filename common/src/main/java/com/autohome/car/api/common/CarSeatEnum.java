package com.autohome.car.api.common;

public enum CarSeatEnum {

    空(-1),
    全部(0),
    Seat2(2),
    Seat4(4),
    Seat5(5),
    Seat6(6),
    Seat7(7),
    SeatMax(8);

    private final int value;

    CarSeatEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CarSeatEnum fromValue(int value) {
        for (CarSeatEnum cs : values()) {
            if (cs.getValue() == value) {
                return cs;
            }
        }
        return 空;
    }
}