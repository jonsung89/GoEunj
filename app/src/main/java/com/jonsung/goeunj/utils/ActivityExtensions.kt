package com.jonsung.goeunj.utils

import android.app.Activity
import android.content.Context


fun Activity.getStringSharedPreferences(key: Int, defaultValue: String? = ""): String? {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    return sharedPref.getString(getString(key), "")
}

fun Activity.setStringSharedPreferences(key: Int, value: String) {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    with (sharedPref.edit()) {
        putString(getString(key), value)
        apply()
    }
}