package com.autohome.car.api.common;


import org.apache.commons.text.StringEscapeUtils;

public class HtmlUtils {
    public static String encode(String html){
        return StringEscapeUtils.escapeHtml4(html);
    }


    public static String decode(String html){
        return StringEscapeUtils.unescapeHtml4(html);
    }
}
