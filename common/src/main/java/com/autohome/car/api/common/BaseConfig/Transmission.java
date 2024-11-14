package com.autohome.car.api.common.BaseConfig;

public class Transmission {

    public static String shortName(String src) {
        src = src.replace("变速箱(MT)", "");
        src = src.replace("变速箱(AT)", "");
        src = src.replace("变速箱(CVT)", "");
        src = src.replace("变速箱(DCT)", "");
        src = src.replace("机械式自动变速箱(AMT)", "AMT");
        src = src.replace("电子无级变速箱(E-CVT)", "无级");
        src = src.replace("变速箱", "");
        return src;
    }

    public static String getCVTransmission(int transmissionId)
    {
        String returnStr = "";
        switch (transmissionId)
        {
            case 1: returnStr = "手动"; break;
            case 2: returnStr = "自动"; break;
        }
        return returnStr;
    }

}
