package com.example.reminderassistant.data.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getClipboardSuggestionsEnabled(): Boolean {
        return prefs.getBoolean(KEY_CLIPBOARD_ENABLED, true)
    }

    override fun setClipboardSuggestionsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CLIPBOARD_ENABLED, enabled).apply()
    }

    override fun getCooldownMinutes(): Int {
        return prefs.getInt(KEY_COOLDOWN_MINUTES, 10)
    }

    override fun setCooldownMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_COOLDOWN_MINUTES, minutes).apply()
    }

    override fun getHighConfidenceOnly(): Boolean {
        return prefs.getBoolean(KEY_HIGH_CONF_ONLY, true)
    }

    override fun setHighConfidenceOnly(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HIGH_CONF_ONLY, enabled).apply()
    }

    companion object {
        private const val PREFS_NAME = "reminder_settings"
        private const val KEY_CLIPBOARD_ENABLED = "clipboard_enabled"
        private const val KEY_COOLDOWN_MINUTES = "clipboard_cooldown_minutes"
        private const val KEY_HIGH_CONF_ONLY = "clipboard_high_confidence"
    }
}
