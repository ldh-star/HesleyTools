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

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import com.liangguo.tools.lsposed.configs.XpPrefs

class PrefsChangeObserver @JvmOverloads constructor(
    context: Context,
    handler: Handler?,
    autoApplyChange: Boolean = false,
    type: PrefType = PrefType.Any,
    name: String? = null,
    private val def: Any? = null
) : ContentObserver(handler) {
    private val autoApplyChange: Boolean
    private val prefType: PrefType
    private val context: Context
    private val name: String?

    constructor(
        context: Context,
        handler: Handler?,
        autoApplyChange: Boolean,
        name: String?
    ) : this(context, handler, autoApplyChange, PrefType.StringSet, name, null)

    constructor(
        context: Context,
        handler: Handler?,
        type: PrefType,
        name: String?,
        def: Any?
    ) : this(context, handler, false, type, name, def)

    init {
        var uri: Uri? = null
        this.name = name
        prefType = type
        this.context = context
        this.autoApplyChange = autoApplyChange
        uri = when (type) {
            PrefType.Any -> PrefToUri.anyPrefToUri()
            PrefType.String -> PrefToUri.stringPrefToUri(name, def as String?)
            PrefType.StringSet -> PrefToUri.stringSetPrefToUri(name)
            PrefType.Integer -> PrefToUri.intPrefToUri(name, (def as Int?)!!)
            PrefType.Boolean -> PrefToUri.boolPrefToUri(name, def as Boolean)
        }
        context.contentResolver.registerContentObserver(uri, type == PrefType.Any, this)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (selfChange) return
        if (autoApplyChange) {
            if (prefType == PrefType.Any) return
            applyChange()
        }
        if (prefType == PrefType.Any) onChange(
            when (uri!!.pathSegments[1]) {
                "string" -> PrefType.String
                "stringset" -> PrefType.StringSet
                "integer" -> PrefType.Integer
                "boolean" -> PrefType.Boolean
                else -> PrefType.Any
            }, uri, uri.pathSegments[2], def
        )
        else onChange(prefType, uri, name, def)
    }

    /**
     * @param type 类型
     * @param uri  uri
     * @param name 完整 key
     * @param def  传入的默认值
     */
    fun onChange(type: PrefType?, uri: Uri?, name: String?, def: Any?) {
    }

    private fun applyChange() {
        name ?: return
        XpPrefs.prefsMap[name] = when (prefType) {
            PrefType.String -> PrefsUtils.getSharedStringPrefs(context, name, def as String?)
            PrefType.StringSet -> PrefsUtils.getSharedStringSetPrefs(context, name)
            PrefType.Integer -> PrefsUtils.getSharedIntPrefs(context, name, (def as Int?)!!)
            PrefType.Boolean -> PrefsUtils.getSharedBoolPrefs(context, name, def as Boolean)
            else -> null
        }
    }

    object PrefToUri {
        fun stringPrefToUri(name: String?, defValue: String?): Uri {
            return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/string/" + name + "/" + defValue)
        }

        fun stringSetPrefToUri(name: String?): Uri {
            return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/stringset/" + name)
        }

        fun intPrefToUri(name: String?, defValue: Int): Uri {
            return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/integer/" + name + "/" + defValue)
        }

        fun boolPrefToUri(name: String?, defValue: Boolean): Uri {
            return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/boolean/" + name + "/" + (if (defValue) '1' else '0'))
        }

        fun shortcutIconPrefToUri(name: String): Uri {
            return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/shortcut_icon/" + name)
        }

        fun anyPrefToUri(): Uri {
            return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/pref/")
        }
    }

    companion object {
        private const val TAG = "PrefsChangeObserver"
    }
}
