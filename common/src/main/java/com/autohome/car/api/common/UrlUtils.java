package com.autohome.car.api.common;

import java.net.URLEncoder;

public class UrlUtils {
    public static String encode(String url, String charset){
        String encodedPath = "";
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < url.length(); i++) {
                char c = url.charAt(i);
                if (Character.isLetter(c) && Character.isUpperCase(c)) {
                    sb.append(c);
                } else if (c == '·') {
                    sb.append("%a1%a4");
                } else {
                    sb.append(URLEncoder.encode(String.valueOf(c), charset).toLowerCase());
                }
            }
            encodedPath = sb.toString();
        }catch (Exception ignored){
        }

        return encodedPath;
    }

    public static String customUrlEncode(String input) {
        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isSafe(ch)) {
                result.append(ch);
            } else if (ch == ' ') {
                result.append('+');
            } else {
                result.append('%');
                result.append(toHex(ch / 16));
                result.append(toHex(ch % 16));
            }
        }
        return result.toString();
    }

    private static boolean isSafe(char ch) {
        return ch >= 'A' && ch <= 'Z' ||
                ch >= 'a' && ch <= 'z' ||
                ch >= '0' && ch <= '9' ||
                ch == '-' || ch == '_' || ch == '.' || ch == '~';
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }


}
