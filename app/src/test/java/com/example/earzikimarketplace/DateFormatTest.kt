package com.example.earzikimarketplace

import com.example.earzikimarketplace.data.util.formatDayMonth
import com.example.earzikimarketplace.data.util.formatHourMinute
import com.example.earzikimarketplace.data.util.formatYear
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class DateFormatTest {
    @Throws(ParseException::class)
    private fun createDate(dateStr: String): Date {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
        return sdf.parse(dateStr)
    }

    @Test
    @Throws(ParseException::class)
    fun testFormatDayMonth() {
        val testDate = createDate("25/12/2022 15:30")
        val formatted = formatDayMonth(testDate)
        Assertions.assertEquals("25/12", formatted)
    }

    @Test
    @Throws(ParseException::class)
    fun testFormatHourMinute() {
        val testDate = createDate("25/12/2022 15:30")
        val formatted = formatHourMinute(testDate)
        Assertions.assertEquals("15:30", formatted)
    }

    @Test
    @Throws(ParseException::class)
    fun testFormatYear() {
        val testDate = createDate("25/12/2022 15:30")
        val formatted = formatYear(testDate)
        Assertions.assertEquals("2022", formatted)
    }
}
