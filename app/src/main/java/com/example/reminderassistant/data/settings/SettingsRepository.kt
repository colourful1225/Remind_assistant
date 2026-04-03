package com.example.reminderassistant.data.settings

interface SettingsRepository {
    fun getBackgroundMonitorEnabled(): Boolean
    fun setBackgroundMonitorEnabled(enabled: Boolean)

    fun getClipboardSuggestionsEnabled(): Boolean
    fun setClipboardSuggestionsEnabled(enabled: Boolean)

    fun getCooldownMinutes(): Int
    fun setCooldownMinutes(minutes: Int)

    fun getHighConfidenceOnly(): Boolean
    fun setHighConfidenceOnly(enabled: Boolean)
}
