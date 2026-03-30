package com.example.reminderassistant.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeFormatter {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun format(timestamp: Long): String {
        return formatter.format(Date(timestamp))
    }
}
