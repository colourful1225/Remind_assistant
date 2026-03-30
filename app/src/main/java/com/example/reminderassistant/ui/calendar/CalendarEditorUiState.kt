package com.example.reminderassistant.ui.calendar

data class CalendarEditorUiState(
    val title: String = "",
    val location: String = "",
    val description: String = "",
    val startTime: Long? = null,
    val endTime: Long? = null,
    val allDay: Boolean = false,
    val isSaving: Boolean = false
)
