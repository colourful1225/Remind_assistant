package com.example.reminderassistant.parser

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class TimeParseResult(
    val timeMillis: Long?,
    val matchedText: String?,
    val confidence: Float
)

class TimeParser @Inject constructor() {
    fun parse(text: String, nowProvider: () -> LocalDateTime = { LocalDateTime.now() }): TimeParseResult {
        val now = nowProvider()
        val (date, dateText) = parseDate(text, now.toLocalDate())
        val (time, timeText, timeConfidence) = parseTime(text)

        if (date == null && time == null) {
            return TimeParseResult(null, null, 0f)
        }

        val resolvedDate = date ?: now.toLocalDate()
        val resolvedTime = time ?: LocalTime.of(9, 0)
        val resolved = LocalDateTime.of(resolvedDate, resolvedTime)

        val matchedText = listOfNotNull(dateText, timeText).joinToString(" ")
        val confidence = if (date != null && time != null) 0.75f else 0.45f + timeConfidence

        return TimeParseResult(
            timeMillis = resolved.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
            matchedText = matchedText.ifBlank { null },
            confidence = confidence.coerceAtMost(0.9f)
        )
    }

    private fun parseDate(text: String, today: LocalDate): Pair<LocalDate?, String?> {
        if (text.contains("今天")) return today to "今天"
        if (text.contains("明天")) return today.plusDays(1) to "明天"
        if (text.contains("后天")) return today.plusDays(2) to "后天"

        val dateRegex = Regex("(\\d{1,2})月(\\d{1,2})[日号]?")
        val dateMatch = dateRegex.find(text)
        if (dateMatch != null) {
            val month = dateMatch.groupValues[1].toIntOrNull()
            val day = dateMatch.groupValues[2].toIntOrNull()
            if (month != null && day != null) {
                val year = today.year
                return LocalDate.of(year, month, day) to dateMatch.value
            }
        }

        val weekday = parseWeekday(text)
        if (weekday != null) {
            val next = today.with(TemporalAdjusters.nextOrSame(weekday))
            return next to "周${weekdayToText(weekday)}"
        }

        return null to null
    }

    private fun parseTime(text: String): Triple<LocalTime?, String?, Float> {
        val timeRegex = Regex("(上午|下午|晚上|中午)?\\s*(\\d{1,2})(?:[:点](\\d{1,2}))?")
        val match = timeRegex.find(text) ?: return Triple(null, null, 0f)

        val period = match.groupValues[1]
        val hour = match.groupValues[2].toIntOrNull() ?: return Triple(null, null, 0f)
        val minute = match.groupValues[3].toIntOrNull() ?: 0

        var adjustedHour = hour
        if (period.isNotBlank()) {
            if ((period == "下午" || period == "晚上") && hour in 1..11) {
                adjustedHour = hour + 12
            }
            if (period == "中午" && hour in 1..11) {
                adjustedHour = hour + 12
            }
        }

        val time = LocalTime.of(adjustedHour.coerceIn(0, 23), minute.coerceIn(0, 59))
        val matched = match.value.trim()
        return Triple(time, matched, if (period.isNotBlank()) 0.15f else 0f)
    }

    private fun parseWeekday(text: String): DayOfWeek? {
        return when {
            text.contains("周一") -> DayOfWeek.MONDAY
            text.contains("周二") -> DayOfWeek.TUESDAY
            text.contains("周三") -> DayOfWeek.WEDNESDAY
            text.contains("周四") -> DayOfWeek.THURSDAY
            text.contains("周五") -> DayOfWeek.FRIDAY
            text.contains("周六") -> DayOfWeek.SATURDAY
            text.contains("周日") || text.contains("周天") -> DayOfWeek.SUNDAY
            else -> null
        }
    }

    private fun weekdayToText(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "一"
            DayOfWeek.TUESDAY -> "二"
            DayOfWeek.WEDNESDAY -> "三"
            DayOfWeek.THURSDAY -> "四"
            DayOfWeek.FRIDAY -> "五"
            DayOfWeek.SATURDAY -> "六"
            DayOfWeek.SUNDAY -> "日"
        }
    }
}
