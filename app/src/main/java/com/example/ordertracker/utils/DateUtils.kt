package com.example.ordertracker.utils

import java.util.Calendar
import java.util.Date

object DateUtils {

    fun getStartOfDayTimestamp(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getEndOfDayTimestamp(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    // Overload for convenience if a Long timestamp is passed
    fun getStartOfDayTimestamp(timestamp: Long): Long {
        return getStartOfDayTimestamp(Date(timestamp))
    }

    fun getEndOfDayTimestamp(timestamp: Long): Long {
        return getEndOfDayTimestamp(Date(timestamp))
    }

    fun getStartOfMonthTimestamp(year: Int, month: Int /* 1-12 */): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar month is 0-indexed
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDayTimestamp(calendar.time)
    }

    fun getEndOfMonthTimestamp(year: Int, month: Int /* 1-12 */): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar month is 0-indexed
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return getEndOfDayTimestamp(calendar.time)
    }
}
