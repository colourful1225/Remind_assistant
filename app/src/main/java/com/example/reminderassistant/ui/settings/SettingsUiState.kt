package com.example.reminderassistant.ui.settings

data class SettingsUiState(
    val clipboardSuggestionsEnabled: Boolean = false,
    val clipboardCooldownMinutes: Int = 10,
    val highConfidenceOnly: Boolean = true,
    val accessibilityServiceEnabled: Boolean = false,
    val showSourceApp: Boolean = true
)
