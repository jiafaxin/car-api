package com.autohome.car.api.common;

import java.text.DecimalFormat;
import java.util.Formatter;

public class PriceUtils {

    public static String getStrPrice(int minPrice, int maxPrice) {
        if (minPrice == 0 || maxPrice == 0 )
        {
            return "暂无报价";
        }
        if(minPrice==maxPrice){
            Double d = minPrice / 10000.0;
            if(d>1000) {
                return new DecimalFormat("#.00").format(d) + "万";
            }else{
                return new DecimalFormat("#.00##").format(d) + "万";
            }
        }else{
            String min = getBaseStr(minPrice);
            String max = getBaseStr(maxPrice);
            return min+"-"+max+"万";
        }
    }

    /**
     * 价格除10000后的字符串：
     * > 1000 保留两位小数
     * 小数点后至少保留两位，至多保留4位
     * @param price
     * @return
     */
    static String getBaseStr(Integer price){
        Double d = price / 10000.0;
        String str = d + "";
        if(d > 1000){
           return  new DecimalFormat("#.##").format(d);
        }
        if(str.indexOf(".") < 0)
            return new Formatter().format("%.2f", d).toString();
        int sIndex = str.length() - str.indexOf(".") - 1;
        if(sIndex < 2){
            return new Formatter().format("%.2f", d).toString();
        }else if(sIndex > 4){
            return new Formatter().format("%.4f", d).toString();
        }else {
            return str;
        }

    }

}
