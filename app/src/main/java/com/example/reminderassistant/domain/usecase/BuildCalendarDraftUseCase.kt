package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.model.CalendarDraft
import javax.inject.Inject

class BuildCalendarDraftUseCase @Inject constructor() {

    fun build(
        title: String,
        description: String,
        startTime: Long?,
        endTime: Long?,
        allDay: Boolean,
        location: String?,
        sourceRawText: String?
    ): CalendarDraft {
        val resolvedTitle = title.ifBlank { "New Event" }
        val resolvedEnd = resolveEndTime(startTime, endTime, allDay)

        return CalendarDraft(
            title = resolvedTitle,
            description = description,
            startTime = startTime,
            endTime = resolvedEnd,
            allDay = allDay,
            location = location,
            sourceRawText = sourceRawText,
            suggestedDurationMinutes = if (startTime != null && resolvedEnd != null) {
                ((resolvedEnd - startTime) / 60000L).toInt()
            } else {
                null
            }
        )
    }

    private fun resolveEndTime(startTime: Long?, endTime: Long?, allDay: Boolean): Long? {
        if (startTime == null) return endTime
        val defaultDurationMs = if (allDay) 24 * 60 * 60 * 1000L else 60 * 60 * 1000L
        if (endTime == null || endTime <= startTime) {
            return startTime + defaultDurationMs
        }
        return endTime
    }
}
