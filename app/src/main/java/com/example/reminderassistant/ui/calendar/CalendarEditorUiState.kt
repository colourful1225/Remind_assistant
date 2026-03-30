package com.example.reminderassistant.ui.calendar

enum class CalendarLaunchError {
    NO_CALENDAR_APP
}

data class CalendarEditorUiState(
    val title: String = "",
    val location: String = "",
    val description: String = "",
    val startTime: Long? = null,
    val endTime: Long? = null,
    val allDay: Boolean = false,
    val rawText: String? = null,
    val launchError: CalendarLaunchError? = null,
    val isSaving: Boolean = false
)
