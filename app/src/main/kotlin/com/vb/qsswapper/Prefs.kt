package com.vb.qsswapper

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val FILE = "qsswapper"
    private const val KEY_ENABLED = "inversion_enabled"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
}
