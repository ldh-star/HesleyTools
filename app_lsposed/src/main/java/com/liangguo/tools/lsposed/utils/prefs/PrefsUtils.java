/*
 * This file is part of HyperCeiler.

 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2024 HyperCeiler Contributions
 */
package com.liangguo.tools.lsposed.utils.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.liangguo.lib.common.BuildConfig;
import com.liangguo.tools.lsposed.configs.XpPrefs;
import com.liangguo.tools.lsposed.utils.log.XposedLogUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;

public class PrefsUtils {

    public static SharedPreferences mSharedPreferences = null;

    private static String mPrefsPathCurrent = null;
    private static String mPrefsFileCurrent = null;
    public static String PREFS_NAME = "hesleytools_prefs";
    private static final String mPrefsPath = "/data/user_de/0/" + BuildConfig.APPLICATION_ID + "/shared_prefs";
    public static String prefsFile = mPrefsPath + "/" + PREFS_NAME + ".xml";


    public static SharedPreferences getSharedPrefs(Context context, boolean multiProcess) {
        context = context.createDeviceProtectedStorageContext();
        try {
            return context.getSharedPreferences(PREFS_NAME, multiProcess ? Context.MODE_MULTI_PROCESS | Context.MODE_WORLD_READABLE : Context.MODE_WORLD_READABLE);
        } catch (Throwable t) {
            return context.getSharedPreferences(PREFS_NAME, multiProcess ? Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE : Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getSharedPrefs(Context context) {
        return getSharedPrefs(context, false);
    }


    public static String getSharedPrefsPath() {
        if (mPrefsPathCurrent == null) try {
            Field mFile = mSharedPreferences.getClass().getDeclaredField("mFile");
            mFile.setAccessible(true);
            mPrefsPathCurrent = ((File) mFile.get(mSharedPreferences)).getParentFile().getAbsolutePath();
            return mPrefsPathCurrent;
        } catch (Throwable t) {
            System.out.print("Test" + t);
            return mPrefsPath;
        }
        else return mPrefsPathCurrent;
    }

    public static String getSharedPrefsFile() {
        if (mPrefsFileCurrent == null) try {
            Field fFile = mSharedPreferences.getClass().getDeclaredField("mFile");
            fFile.setAccessible(true);
            mPrefsFileCurrent = ((File) fFile.get(mSharedPreferences)).getAbsolutePath();
            System.out.println("Test: mPrefsFileCurrent");
            return mPrefsFileCurrent;
        } catch (Throwable t) {
            System.out.println("Test: mPrefsFile" + t);
            return prefsFile;
        }
        else
            System.out.println("Test: mPrefsFileCurrent2");
        return mPrefsFileCurrent;
    }


    public static boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    public static SharedPreferences.Editor editor() {
        return mSharedPreferences.edit();
    }

    public static void putString(String key, String defValue) {
        mSharedPreferences.edit().putString(key, defValue).apply();
    }

    public static String getSharedStringPrefs(Context context, String name, String defValue) {
        Uri uri = PrefsChangeObserver.PrefToUri.INSTANCE.stringPrefToUri(name, defValue);
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String prefValue = cursor.getString(0);
                cursor.close();
                return prefValue;
            } else XposedLogUtils.logI("ContentResolver", "[" + name + "] Cursor fail: " + cursor);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        if (XpPrefs.INSTANCE.getPrefsMap().containsKey(name))
            return (String) XpPrefs.INSTANCE.getPrefsMap().getOrDefault(name, defValue);
        else return defValue;
    }

    public static Set<String> getSharedStringSetPrefs(Context context, String name) {
        Uri uri = PrefsChangeObserver.PrefToUri.INSTANCE.stringSetPrefToUri(name);
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                Set<String> prefValue = new LinkedHashSet<>();
                while (cursor.moveToNext()) {
                    prefValue.add(cursor.getString(0));
                }
                cursor.close();
                return prefValue;
            } else {
                XposedLogUtils.logI("ContentResolver", "[" + name + "] Cursor fail: null");
            }
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        LinkedHashSet<String> empty = new LinkedHashSet<>();
        if (XpPrefs.INSTANCE.getPrefsMap().containsKey(name)) {
            return (Set<String>) XpPrefs.INSTANCE.getPrefsMap().getOrDefault(name, empty);
        } else {
            return empty;
        }
    }


    public static int getSharedIntPrefs(Context context, String name, int defValue) {
        Uri uri = PrefsChangeObserver.PrefToUri.INSTANCE.intPrefToUri(name, defValue);
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int prefValue = cursor.getInt(0);
                cursor.close();
                return prefValue;
            } else XposedLogUtils.logI("ContentResolver", "[" + name + "] Cursor fail: " + cursor);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        if (XpPrefs.INSTANCE.getPrefsMap().containsKey(name))
            return (int) XpPrefs.INSTANCE.getPrefsMap().getOrDefault(name, defValue);
        else return defValue;
    }


    public static boolean getSharedBoolPrefs(Context context, String name, boolean defValue) {
        Uri uri = PrefsChangeObserver.PrefToUri.INSTANCE.boolPrefToUri(name, defValue);
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int prefValue = cursor.getInt(0);
                cursor.close();
                return prefValue == 1;
            } else XposedLogUtils.logI("ContentResolver", "[" + name + "] Cursor fail: " + cursor);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }

        if (XpPrefs.INSTANCE.getPrefsMap().containsKey(name))
            return (boolean) XpPrefs.INSTANCE.getPrefsMap().getOrDefault(name, false);
        else
            return defValue;
    }
}
