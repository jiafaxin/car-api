package com.autohome.car.api.common;

import org.apache.commons.lang3.StringUtils;

public class StringIntegerUtils {

    public static int getIntegerByTenString(String str){
        if(StringUtils.isBlank(str)){
            return 0;
        }
        if(str.substring(0,2).toLowerCase().equals("0x")){
            return Integer.parseInt(str.substring(2),16);
        }else {
            return Integer.parseInt(str,16);
        }

    }

    public static int[] convertToInt32(String str, String sepertor, int defVal) {
        String[] strArray = str.split(sepertor);
        int[] returnValue = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            try {
                returnValue[i] = Integer.parseInt(strArray[i]);
            } catch (NumberFormatException nfe) {
                returnValue[i] = defVal;
            }
        }
        return returnValue;
    }

    public static int getIntOrDefault(String str,int defVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
           return defVal;
        }
    }

}
