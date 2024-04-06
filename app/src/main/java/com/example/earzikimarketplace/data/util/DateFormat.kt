package com.example.earzikimarketplace.data.util
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 05/12
fun formatDayMonth(date: Date): String {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.US)
    return dateFormat.format(date)
}

// 13:45
fun formatHourMinute(date: Date): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    return timeFormat.format(date)
}

// 2023
fun formatYear(date: Date): String {
    val yearFormat = SimpleDateFormat("yyyy", Locale.US)
    return yearFormat.format(date)
}
