package com.example.reminderassistant.domain.model

data class CalendarDraft(
    val title: String,
    val description: String = "",
    val startTime: Long? = null,
    val endTime: Long? = null,
    val allDay: Boolean = false,
    val location: String? = null,
    val sourceRawText: String? = null,
    val suggestedDurationMinutes: Int? = null
)
