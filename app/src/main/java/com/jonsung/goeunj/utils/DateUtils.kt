package com.jonsung.goeunj.utils

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

const val DEFAULT_DATE_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy"

fun Date.convertDateToString(pattern: String? = null): String {
    val formatter = SimpleDateFormat(pattern ?: "MM/dd/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(this)
}

fun String.convertStringToDate(pattern: String? = null): Date? {
    if (this.isEmpty()) return null
    val formatter = SimpleDateFormat(pattern ?: "MM/dd/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.parse(this)
}

fun String.convertDefaultStringToDate(): Date? {
    if (this.isEmpty()) return null
    val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.parse(this)
}

fun Date.isDateInCurrentWeek(): Boolean {
    val currentCalendar = Calendar.getInstance()
    val week = currentCalendar[Calendar.WEEK_OF_YEAR]
    val year = currentCalendar[Calendar.YEAR]
    val targetCalendar = Calendar.getInstance()
    targetCalendar.time = this
    val targetWeek = targetCalendar[Calendar.WEEK_OF_YEAR]
    val targetYear = targetCalendar[Calendar.YEAR]
    return week == targetWeek && year == targetYear
}

/**
 * get the day of the week of a given date
 * @sample  "Sun Sep 05 00:00:00 CDT 2021".getDayOfWeek()
 * @return  Int (ex. Calendar.SUNDAY)
 */
fun Date.getDayOfWeek(): Int {
    val currentCalendar = Calendar.getInstance()
    currentCalendar.time = this
    return currentCalendar.get(Calendar.DAY_OF_WEEK)
}

fun Date.getDayOfMonth(): Int {
    val currentCalendar = Calendar.getInstance()
    currentCalendar.time = this
    return currentCalendar.get(Calendar.DAY_OF_MONTH)
}

/**
 * is given date the given day
 * @params  dayOfWeek: Int
 * @sample  "Sun Sep 05 00:00:00 CDT 2021".isDayOfWeek(Calendar.SUNDAY)
 * @return  Boolean
 */
fun Date.isDayOfWeek(dayOfWeek: Int): Boolean {
    val currentCalendar = Calendar.getInstance()
    currentCalendar.time = this
    return currentCalendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
}

fun Date.isSameDay(date2: Date): Boolean {
    val sdf = SimpleDateFormat("yyMMdd", Locale.getDefault())
    return sdf.format(this) == sdf.format(date2)
}

fun getAllDatesOfCurrentWeek(): List<String?> {
    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.SUNDAY
    calendar[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY

    val days = arrayOfNulls<String>(7)
    for (i in 0..6) {
        days[i] = format.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days.toList()
}