package com.jonsung.goeunj.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.convertDateToString(): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(this)
}

fun String.convertStringToDate(): Date? {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.parse(this)
}