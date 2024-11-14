package com.autohome.car.api.data.popauto.common;

public enum SpecState {
    /// 无
    None(0),
    /// 未上市
    NoSell(0x0001),
    /// 即将上市
    WaitSell(0x0002),
    /// 在产在售
    Sell(0x0004),
    /// 停产在售
    SellInStop(0x0008),
    /// 停售
    StopSell(0x0010);

    public int value;

    SpecState(int value){
        this.value = value;
    }
}
