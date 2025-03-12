/*
 * This file is part of "+ XP_TAG+".

 * "+ XP_TAG+" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2024 "+ XP_TAG+" Contributions
 */
package com.liangguo.tools.lsposed.utils.log;

import static com.liangguo.tools.lsposed.XposedInitKt.XP_TAG;

import de.robv.android.xposed.XposedBridge;


public class XposedLogUtils {

    /**
     * 日志输出等级，默认输出全部日志，可以修改
     */
    static int logLevel;

    public static void logI(String msg) {
        if (logLevel < 3) return;
        XposedBridge.log("[" + XP_TAG + "][I]: " + msg);
    }

    public static void logI(String tagOpkg, String msg) {
        if (logLevel < 3) return;
        XposedBridge.log("[" + XP_TAG + "][I][" + tagOpkg + "]: " + msg);
    }

    public static void logI(String tag, String pkg, String msg) {
        if (logLevel < 3) return;
        XposedBridge.log("[" + XP_TAG + "][I][" + pkg + "][" + tag + "]: " + msg);
    }

    public static void logW(String msg) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W]: " + msg);
    }

    public static void logW(String tag, String pkg, String msg) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + pkg + "][" + tag + "]: " + msg);
    }

    public static void logW(String tag, String pkg, Throwable log) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + pkg + "][" + tag + "]: " + log);
    }

    public static void logW(String tag, String pkg, String msg, Exception exp) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + pkg + "][" + tag + "]: " + msg + ", by: " + exp);
    }

    public static void logW(String tag, String pkg, String msg, Throwable log) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + pkg + "][" + tag + "]: " + msg + ", by: " + log);
    }

    public static void logW(String tag, String msg) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + tag + "]: " + msg);
    }

    public static void logW(String tag, Throwable log) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + tag + "]: " + log);
    }

    public static void logW(String tag, String msg, Exception exp) {
        if (logLevel < 2) return;
        XposedBridge.log("[" + XP_TAG + "][W][" + tag + "]: " + msg + ", by: " + exp);
    }

    public static void logE(String tag, String msg) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + tag + "]: " + msg);
    }

    public static void logE(String msg) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E]: " + msg);
    }

    public static void logE(String tag, Throwable log) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + tag + "]: " + log);
    }

    public static void logE(String tag, String pkg, String msg) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + pkg + "][" + tag + "]: " + msg);
    }

    public static void logE(String tag, String pkg, Throwable log) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + pkg + "][" + tag + "]: " + log);
    }

    public static void logE(String tag, String pkg, Exception exp) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + pkg + "][" + tag + "]: " + exp);
    }

    public static void logE(String tag, String pkg, String msg, Throwable log) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + pkg + "][" + tag + "]: " + msg + ", by: " + log);
    }

    public static void logE(String tag, String pkg, String msg, Exception exp) {
        if (logLevel < 1) return;
        XposedBridge.log("[" + XP_TAG + "][E][" + pkg + "][" + tag + "]: " + msg + ", by: " + exp);
    }

    public static void logD(String msg) {
        if (logLevel < 4) return;
        XposedBridge.log("[" + XP_TAG + "][D]: " + msg);
    }

    public static void logD(String tag, String pkg, String msg) {
        if (logLevel < 4) return;
        XposedBridge.log("[" + XP_TAG + "][D][" + pkg + "][" + tag + "]: " + msg);
    }

}
