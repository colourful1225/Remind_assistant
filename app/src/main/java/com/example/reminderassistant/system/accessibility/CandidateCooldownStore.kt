package com.example.reminderassistant.system.accessibility

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CandidateCooldownStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun shouldBlock(hash: String, now: Long): Boolean {
        val lastHash = prefs.getString(KEY_LAST_HASH, null)
        val lastTime = prefs.getLong(KEY_LAST_TIME, 0L)
        if (lastHash == hash && now - lastTime < DUPLICATE_WINDOW_MS) {
            return true
        }
        if (now - lastTime < GLOBAL_COOLDOWN_MS) {
            return true
        }
        return false
    }

    fun recordShown(hash: String, timestamp: Long) {
        prefs.edit()
            .putString(KEY_LAST_HASH, hash)
            .putLong(KEY_LAST_TIME, timestamp)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "accessibility_suggestion_cooldown"
        private const val KEY_LAST_HASH = "last_hash"
        private const val KEY_LAST_TIME = "last_time"
        private const val DUPLICATE_WINDOW_MS = 2 * 60 * 1000L
        private const val GLOBAL_COOLDOWN_MS = 30 * 1000L
    }
}
