package com.example.reminderassistant.data.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val prefs = context.getSharedPreferences(SettingsKeys.PREFS_NAME, Context.MODE_PRIVATE)

    override fun getBackgroundMonitorEnabled(): Boolean {
        return prefs.getBoolean(SettingsKeys.KEY_BACKGROUND_MONITOR_ENABLED, false)
    }

    override fun setBackgroundMonitorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(SettingsKeys.KEY_BACKGROUND_MONITOR_ENABLED, enabled).apply()
    }

    override fun getClipboardSuggestionsEnabled(): Boolean {
        return prefs.getBoolean(SettingsKeys.KEY_CLIPBOARD_ENABLED, true)
    }

    override fun setClipboardSuggestionsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(SettingsKeys.KEY_CLIPBOARD_ENABLED, enabled).apply()
    }

    override fun getCooldownMinutes(): Int {
        return prefs.getInt(SettingsKeys.KEY_COOLDOWN_MINUTES, 10)
    }

    override fun setCooldownMinutes(minutes: Int) {
        prefs.edit().putInt(SettingsKeys.KEY_COOLDOWN_MINUTES, minutes).apply()
    }

    override fun getHighConfidenceOnly(): Boolean {
        return prefs.getBoolean(SettingsKeys.KEY_HIGH_CONF_ONLY, true)
    }

    override fun setHighConfidenceOnly(enabled: Boolean) {
        prefs.edit().putBoolean(SettingsKeys.KEY_HIGH_CONF_ONLY, enabled).apply()
    }
}
