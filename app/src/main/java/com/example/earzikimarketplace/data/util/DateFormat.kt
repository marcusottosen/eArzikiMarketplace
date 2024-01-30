package com.example.earzikimarketplace.data.util
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDayMonth(date: Date): String {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.US)
    return dateFormat.format(date)
}

fun formatHourMinute(date: Date): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    return timeFormat.format(date)
}

fun formatYear(date: Date): String {
    val yearFormat = SimpleDateFormat("yyyy", Locale.US)
    return yearFormat.format(date)
}
