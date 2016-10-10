package com.sromku.simple.fb.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    public static boolean DEBUG = false;
    public static boolean DEBUG_WITH_STACKTRACE = false;

    public static <T> void logInfo(Class<T> cls, String message) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();



            if (DEBUG_WITH_STACKTRACE) {

            }
        }
    }

    public static <T> void logWarning(Class<T> cls, String message) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();


            if (DEBUG_WITH_STACKTRACE) {

            }
        }
    }

    public static <T> void logError(Class<T> cls, String message) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();


            if (DEBUG_WITH_STACKTRACE) {

            }
        }
    }

    public static <T> void logError(Class<T> cls, String message, Throwable e) {
        if (DEBUG || DEBUG_WITH_STACKTRACE) {
            String tag = cls.getName();


            if (DEBUG_WITH_STACKTRACE) {

            }
        }
    }

    private enum LogType {
        INFO,
        WARNING,
        ERROR
    }

    private static String getStackTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        new Throwable().printStackTrace(pw);
        return sw.toString();
    }
}
