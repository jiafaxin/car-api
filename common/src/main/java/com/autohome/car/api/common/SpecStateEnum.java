package com.autohome.car.api.common;

public enum SpecStateEnum {

    NONE(0),
    //未上市
    NO_SELL(0x0001),
    //即将上市
    WAIT_SELL(0x0002),
    //在产在售
    SELL(0x0004),
    //停产在售
    SELL_IN_STOP(0x0008),
    //停售
    STOP_SELL(0x0010),


    //未售
    SELL_3(0x0003),
    //在售
    SELL_12(0x000c),
    //即将上市+在售
    SELL_14(0x000e),
    //在售+停售
    SELL_28(0x001c),
    //未售+在售
    SELL_15(0x000f),
    //即将上市+在售+停售
    SELL_30(0x001e),
    //未售+在售+停售
    SELL_31(0x001f),

    SELL_22(0x0004+0x0008+0x0010),
    SELL_24(0x0002+0x0004+0x0008+0x0010);


    private final int value;

    SpecStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
