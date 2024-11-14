package com.autohome.car.api.common.BaseConfig;

public class Stuct {
    public static String getName(String _struct) {
        String returnStr = _struct;
        switch (_struct) {
            case "两厢车":
                returnStr = "两厢";
                break;
            case "三厢车":
                returnStr = "三厢";
                break;
            case "掀背车":
                returnStr = "掀背";
                break;
            case "旅行车":
                returnStr = "旅行版";
                break;
            case "三厢跨界车":
                returnStr = "三厢跨界车";
                break;
            case "两厢跨界车":
                returnStr = "两厢跨界车";
                break;
            case "旅行跨界车":
                returnStr = "旅行跨界车";
                break;
            case "SUV跨界车":
                returnStr = "SUV跨界车";
                break;
        }
        return returnStr;
    }

    public static String getCVName(int StructId) {
        String returnStr = "";
        switch (StructId) {
            case 8:
                returnStr = "客车";
                break;
            case 9:
                returnStr = "货车";
                break;
            case 10:
                returnStr = "皮卡";
                break;
        }
        return returnStr;
    }

    public static int StructOrderBy(String _struct,boolean isCV)
    {
        if(isCV){
            return Integer.parseInt(_struct);
        }
        int returnInt = 0;
        switch (_struct)
        {
            case "两厢车": returnInt = 1; break;
            case "三厢车": returnInt = 2; break;
            case "掀背车": returnInt = 3; break;
            case "旅行车": returnInt = 4; break;
            case "硬顶敞篷车": returnInt = 5; break;
            case "软顶敞篷车": returnInt = 6; break;
            case "硬顶跑车": returnInt = 7; break;
            case "皮卡": returnInt = 10; break;
            case "MPV": returnInt = 11; break;
            case "SUV": returnInt = 12; break;
            case "三厢跨界车": returnInt = 13; break;
            case "两厢跨界车": returnInt = 14; break;
            case "旅行跨界车": returnInt = 15; break;
            case "SUV跨界车": returnInt = 16; break;
        }
        return returnInt;
    }
}
