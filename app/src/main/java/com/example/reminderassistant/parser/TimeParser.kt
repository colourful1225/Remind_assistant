package com.example.reminderassistant.parser

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class TimeParseResult(
    val timeMillis: Long?,
    val matchedText: String?,
    val confidence: Float
)

class TimeParser @Inject constructor() {

    fun parse(text: String, nowProvider: () -> LocalDateTime = { LocalDateTime.now() }): TimeParseResult {
        val normalized = text.trim()
        if (normalized.isEmpty()) return TimeParseResult(null, null, 0f)

        val now = nowProvider()
        val (date, dateText) = parseDate(normalized, now.toLocalDate())
        val (time, timeText, timeConfidence) = parseTime(normalized)

        if (date == null && time == null) {
            return TimeParseResult(null, null, 0f)
        }

        val resolvedDate = date ?: now.toLocalDate()
        val resolvedTime = time ?: LocalTime.of(9, 0)
        var resolved = LocalDateTime.of(resolvedDate, resolvedTime)

        if (date == null && resolved.isBefore(now.minusMinutes(2))) {
            resolved = resolved.plusDays(1)
        }

        val matched = listOfNotNull(dateText, timeText).joinToString(" ").ifBlank { null }
        val baseConfidence = when {
            date != null && time != null -> 0.78f
            time != null -> 0.58f
            else -> 0.42f
        }

        return TimeParseResult(
            timeMillis = resolved.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            matchedText = matched,
            confidence = (baseConfidence + timeConfidence).coerceAtMost(0.92f)
        )
    }

    private fun parseDate(text: String, today: LocalDate): Pair<LocalDate?, String?> {
        if (text.contains("今天")) return today to "今天"
        if (text.contains("明天")) return today.plusDays(1) to "明天"
        if (text.contains("后天")) return today.plusDays(2) to "后天"
        if (text.contains("大后天")) return today.plusDays(3) to "大后天"

        val dateRegex = Regex("(\\d{1,2})月(\\d{1,2})(?:日|号)?")
        val dateMatch = dateRegex.find(text)
        if (dateMatch != null) {
            val month = dateMatch.groupValues[1].toIntOrNull()
            val day = dateMatch.groupValues[2].toIntOrNull()
            if (month != null && day != null) {
                var year = today.year
                val candidate = runCatching { LocalDate.of(year, month, day) }.getOrNull()
                if (candidate == null) return null to null
                if (candidate.isBefore(today)) year += 1
                return runCatching { LocalDate.of(year, month, day) to dateMatch.value }.getOrDefault(null to null)
            }
        }

        val weekday = parseWeekday(text)
        if (weekday != null) {
            val nextDate = when {
                text.contains("下周") || text.contains("下星期") -> today.with(TemporalAdjusters.next(weekday))
                text.contains("本周") || text.contains("这周") || text.contains("本星期") || text.contains("这星期") ->
                    today.with(TemporalAdjusters.nextOrSame(weekday))
                else -> today.with(TemporalAdjusters.nextOrSame(weekday))
            }
            return nextDate to "周${weekdayToCn(weekday)}"
        }

        return null to null
    }

    private fun parseTime(text: String): Triple<LocalTime?, String?, Float> {
        val special = parseSpecialTime(text)
        if (special != null) return special

        val timeRegex = Regex("(上午|下午|晚上|中午|早上|凌晨)?\\s*([零一二三四五六七八九十两\\d]{1,3})\\s*(点|:|：)\\s*([零一二三四五六七八九十\\d]{1,2})?\\s*(分|半)?")
        val match = timeRegex.find(text)
        if (match != null) {
            val period = match.groupValues[1]
            val hourRaw = match.groupValues[2]
            val minuteRaw = match.groupValues[4]
            val suffix = match.groupValues[5]

            val hour = parseNumber(hourRaw) ?: return Triple(null, null, 0f)
            var minute = parseNumber(minuteRaw) ?: 0
            if (suffix == "半") minute = 30

            var adjustedHour = hour
            when (period) {
                "下午", "晚上" -> if (hour in 1..11) adjustedHour = hour + 12
                "中午" -> adjustedHour = when (hour) {
                    12 -> 12
                    in 1..11 -> hour + 12
                    else -> hour
                }
                "凌晨" -> if (hour == 12) adjustedHour = 0
                "上午", "早上" -> adjustedHour = if (hour == 12) 0 else hour
            }

            val safeHour = adjustedHour.coerceIn(0, 23)
            val safeMinute = minute.coerceIn(0, 59)
            val confidenceBoost = if (period.isNotBlank() || suffix == "半") 0.16f else 0.08f
            return Triple(LocalTime.of(safeHour, safeMinute), match.value.trim(), confidenceBoost)
        }

        val simpleRegex = Regex("(上午|下午|晚上|中午|早上|凌晨)?\\s*([零一二三四五六七八九十两\\d]{1,3})点")
        val simple = simpleRegex.find(text) ?: return Triple(null, null, 0f)
        val period = simple.groupValues[1]
        val hour = parseNumber(simple.groupValues[2]) ?: return Triple(null, null, 0f)
        var adjustedHour = hour
        when (period) {
            "下午", "晚上" -> if (hour in 1..11) adjustedHour = hour + 12
            "中午" -> adjustedHour = if (hour in 1..11) hour + 12 else hour
            "凌晨" -> if (hour == 12) adjustedHour = 0
            "上午", "早上" -> adjustedHour = if (hour == 12) 0 else hour
        }
        return Triple(LocalTime.of(adjustedHour.coerceIn(0, 23), 0), simple.value.trim(), 0.12f)
    }

    private fun parseSpecialTime(text: String): Triple<LocalTime?, String?, Float>? {
        if (text.contains("今晚")) return Triple(LocalTime.of(20, 0), "今晚", 0.12f)
        if (text.contains("今早")) return Triple(LocalTime.of(8, 0), "今早", 0.1f)
        if (text.contains("明早")) return Triple(LocalTime.of(8, 0), "明早", 0.1f)
        if (text.contains("中午")) return Triple(LocalTime.of(12, 0), "中午", 0.1f)

        val quarterRegex = Regex("(上午|下午|晚上|中午|早上|凌晨)?\\s*([零一二三四五六七八九十两\\d]{1,3})点(一刻|三刻|半)")
        val m = quarterRegex.find(text) ?: return null
        val period = m.groupValues[1]
        val hour = parseNumber(m.groupValues[2]) ?: return null
        val minute = when (m.groupValues[3]) {
            "一刻" -> 15
            "三刻" -> 45
            "半" -> 30
            else -> 0
        }
        var adjustedHour = hour
        when (period) {
            "下午", "晚上" -> if (hour in 1..11) adjustedHour = hour + 12
            "中午" -> adjustedHour = if (hour in 1..11) hour + 12 else hour
            "凌晨" -> if (hour == 12) adjustedHour = 0
            "上午", "早上" -> adjustedHour = if (hour == 12) 0 else hour
        }
        return Triple(LocalTime.of(adjustedHour.coerceIn(0, 23), minute), m.value, 0.16f)
    }

    private fun parseWeekday(text: String): DayOfWeek? {
        return when {
            text.contains("周一") || text.contains("星期一") -> DayOfWeek.MONDAY
            text.contains("周二") || text.contains("星期二") -> DayOfWeek.TUESDAY
            text.contains("周三") || text.contains("星期三") -> DayOfWeek.WEDNESDAY
            text.contains("周四") || text.contains("星期四") -> DayOfWeek.THURSDAY
            text.contains("周五") || text.contains("星期五") -> DayOfWeek.FRIDAY
            text.contains("周六") || text.contains("星期六") -> DayOfWeek.SATURDAY
            text.contains("周日") || text.contains("周天") || text.contains("星期日") || text.contains("星期天") -> DayOfWeek.SUNDAY
            else -> null
        }
    }

    private fun weekdayToCn(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.MONDAY -> "一"
            DayOfWeek.TUESDAY -> "二"
            DayOfWeek.WEDNESDAY -> "三"
            DayOfWeek.THURSDAY -> "四"
            DayOfWeek.FRIDAY -> "五"
            DayOfWeek.SATURDAY -> "六"
            DayOfWeek.SUNDAY -> "日"
        }
    }

    private fun parseNumber(raw: String): Int? {
        if (raw.isBlank()) return null
        raw.toIntOrNull()?.let { return it }

        val src = raw.replace("两", "二")
        val map = mapOf(
            '零' to 0, '一' to 1, '二' to 2, '三' to 3, '四' to 4,
            '五' to 5, '六' to 6, '七' to 7, '八' to 8, '九' to 9
        )

        if (src == "十") return 10
        if (!src.contains("十")) return src.firstOrNull()?.let { map[it] }

        val parts = src.split("十")
        val tens = when {
            parts[0].isBlank() -> 1
            else -> parts[0].firstOrNull()?.let { map[it] } ?: return null
        }
        val ones = when {
            parts.size < 2 || parts[1].isBlank() -> 0
            else -> parts[1].firstOrNull()?.let { map[it] } ?: return null
        }
        return tens * 10 + ones
    }
}
