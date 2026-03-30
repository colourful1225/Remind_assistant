package com.example.reminderassistant.data.settings

interface SettingsRepository {
    fun getClipboardSuggestionsEnabled(): Boolean
    fun setClipboardSuggestionsEnabled(enabled: Boolean)

    fun getCooldownMinutes(): Int
    fun setCooldownMinutes(minutes: Int)

    fun getHighConfidenceOnly(): Boolean
    fun setHighConfidenceOnly(enabled: Boolean)
}
