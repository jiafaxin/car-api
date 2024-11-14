package com.autohome.car.api.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        try {
            throwable.printStackTrace(pw);
            return stringWriter.toString();
        } finally {
            pw.close();
        }
    }
}
