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
package com.liangguo.tools.lsposed.utils.prefs

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.liangguo.lib.common.AppBuildConfig
import java.io.File
import java.io.FileNotFoundException

class SharedPrefsProvider : ContentProvider() {
    var prefs: SharedPreferences? = null

    override fun onCreate(): Boolean {
        try {
            prefs = PrefsUtils.getSharedPrefs(context, true)
            return true
        } catch (throwable: Throwable) {
            return false
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val parts = uri.pathSegments
        val cursor = MatrixCursor(arrayOf("data"))

        when (uriMatcher.match(uri)) {
            0 -> {
                cursor.newRow().add("data", prefs!!.getString(parts[1], ""))
                return cursor
            }

            1 -> {
                cursor.newRow().add("data", prefs!!.getString(parts[1], parts[2]))
                return cursor
            }

            2 -> {
                cursor.newRow().add("data", prefs!!.getInt(parts[1], parts[2].toInt()))
                return cursor
            }

            3 -> {
                cursor.newRow()
                    .add("data", if (prefs!!.getBoolean(parts[1], parts[2].toInt() == 1)) 1 else 0)
                return cursor
            }

            4 -> {
                val strings = prefs!!.getStringSet(parts[1], LinkedHashSet())
                for (str in strings!!) cursor.newRow().add("data", str)
                return cursor
            }
        }
        return null
    }

    @Throws(FileNotFoundException::class)
    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        if (context == null) return null

        val parts = uri.pathSegments
        if (uriMatcher.match(uri) == 5) {
            var filename: String? = null
            if ("0" == parts[1]) filename = "test0.png"
            else if ("1" == parts[1]) filename = "test1.mp3"
            else if ("2" == parts[1]) filename = "test2.mp4"
            else if ("3" == parts[1] || "5" == parts[1]) filename = "test3.txt"
            else if ("4" == parts[1]) filename = "test4.zip"

            var afd: AssetFileDescriptor? = null
            if (filename != null) try {
                afd = context!!.assets.openFd(filename)
            } catch (t: Throwable) {
                Log.i("afd", t.toString())
            }
            return afd
        } else if (uriMatcher.match(uri) == 6) {
            val context = context!!.createDeviceProtectedStorageContext()
            val file =
                File(context.filesDir.toString() + "/shortcuts/" + parts[1] + "_shortcut.png")
            if (!file.exists()) return null
            return AssetFileDescriptor(
                ParcelFileDescriptor.open(
                    file,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ), 0, AssetFileDescriptor.UNKNOWN_LENGTH
            )
        }

        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    companion object {
        const val AUTHORITY: String = "${AppBuildConfig.APPLICATION_ID}.provider.sharedprefs"
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, "string/*/", 0)
            uriMatcher.addURI(AUTHORITY, "string/*/*", 1)
            uriMatcher.addURI(AUTHORITY, "integer/*/*", 2)
            uriMatcher.addURI(AUTHORITY, "boolean/*/*", 3)
            uriMatcher.addURI(AUTHORITY, "stringset/*", 4)
            uriMatcher.addURI(AUTHORITY, "test/*", 5)
            uriMatcher.addURI(AUTHORITY, "shortcut_icon/*", 6)
        }
    }
}
