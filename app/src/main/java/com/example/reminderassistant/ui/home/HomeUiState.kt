package com.example.reminderassistant.ui.home

import com.example.reminderassistant.domain.model.ReminderItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val reminders: List<ReminderItem> = emptyList()
)
