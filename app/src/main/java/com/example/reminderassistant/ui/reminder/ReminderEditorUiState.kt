package com.example.reminderassistant.ui.reminder

data class ReminderEditorUiState(
    val title: String = "",
    val note: String = "",
    val reminderTime: Long? = null,
    val isSaving: Boolean = false
)
