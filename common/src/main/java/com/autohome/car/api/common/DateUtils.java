package com.autohome.car.api.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date StringToDate(String str, String format, Date defaultValue) {
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }


    public static String DateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String DateToString(Date date,String format) {
        return new SimpleDateFormat(format).format(date);
    }

}
