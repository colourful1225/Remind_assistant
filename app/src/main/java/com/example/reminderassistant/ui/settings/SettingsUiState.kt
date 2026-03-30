package com.example.reminderassistant.ui.settings

data class SettingsUiState(
    val clipboardSuggestionsEnabled: Boolean = false,
    val accessibilityServiceEnabled: Boolean = false,
    val showSourceApp: Boolean = true
)
