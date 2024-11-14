//package com.autohome.car.api.data.popauto.common;
//
//public class SpecStateUtil {
//
//    /// <summary>
//    ///0默认值
//    ///0X0001未上市
//    ///0X0002即将上市
//    ///0X0004在产在售
//    ///0X0008停产在售
//    ///0X0010停售
//    ///
//    ///两两结合
//    ///0X0001|0X0002=0X0003未售
//    ///0X0004|0X0008=0X000C在售
//    ///
//    ///三三结合
//    ///0X0002|0X0004|0X0008=0X000E即将上市+在售
//    ///0X0004|0X0008|0X0010=0X001C在售+停售
//    ///
//    ///四四结合
//    ///0X0001|0X0002|0X0004|0X0008=0X000F未售+在售
//    ///0X0002|0X0004|0X0008|0X0010=0X001E即将上市+在售+停售
//    ///
//    ///全部结合
//    ///0X0001|0X0002|0X0004|0X0008|0X0010=0X001F未售+在售+停售
//    /// </summary>
//    public static int getSpecState(String stateCode)
//    {
//        stateCode = stateCode.toLowerCase();
//        int returnState = SpecState.None.value;
//        switch (stateCode)
//        {
//            case "0x0001": returnState = SpecState.NoSell.value;
//                break;
//            case "0x0002": returnState = SpecState.WaitSell.value;
//                break;
//            case "0x0004": returnState = SpecState.Sell.value;
//                break;
//            case "0x0008": returnState = SpecState.SellInStop.value;
//                break;
//            case "0x0010": returnState = SpecState.StopSell.value;
//                break;
//            case "0x0003": returnState = SpecState.NoSell.value | SpecState.WaitSell.value;
//                break;
//            case "0x000c": returnState = SpecState.Sell.value | SpecState.SellInStop.value;
//                break;
//            case "0x000e": returnState = SpecState.WaitSell.value | SpecState.Sell.value | SpecState.SellInStop.value;
//                break;
//            case "0x001c": returnState = SpecState.Sell.value | SpecState.SellInStop.value | SpecState.StopSell.value;
//                break;
//            case "0x000f": returnState = SpecState.NoSell.value | SpecState.WaitSell.value | SpecState.Sell.value | SpecState.SellInStop.value;
//                break;
//            case "0x001e": returnState = SpecState.WaitSell.value | SpecState.Sell.value | SpecState.SellInStop.value | SpecState.StopSell.value;
//                break;
//            case "0x001f": returnState = SpecState.NoSell.value | SpecState.WaitSell.value | SpecState.Sell.value | SpecState.SellInStop.value | SpecState.StopSell.value;
//                break;
//        }
//        return returnState;
//    }
//
//}
