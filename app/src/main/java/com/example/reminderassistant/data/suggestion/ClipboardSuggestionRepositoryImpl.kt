package com.example.reminderassistant.data.suggestion

import android.content.Context
import com.example.reminderassistant.data.suggestion.model.SuggestionHistoryRecord
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardSuggestionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ClipboardSuggestionRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getHistory(): SuggestionHistoryRecord {
        return SuggestionHistoryRecord(
            lastShownHash = prefs.getString(KEY_LAST_SHOWN_HASH, null),
            lastShownAt = prefs.getLong(KEY_LAST_SHOWN_AT, 0L),
            lastDismissedHash = prefs.getString(KEY_LAST_DISMISSED_HASH, null),
            lastDismissedAt = prefs.getLong(KEY_LAST_DISMISSED_AT, 0L)
        )
    }

    override fun recordShown(hash: String, timestamp: Long) {
        prefs.edit()
            .putString(KEY_LAST_SHOWN_HASH, hash)
            .putLong(KEY_LAST_SHOWN_AT, timestamp)
            .apply()
    }

    override fun recordDismissed(hash: String, timestamp: Long) {
        prefs.edit()
            .putString(KEY_LAST_DISMISSED_HASH, hash)
            .putLong(KEY_LAST_DISMISSED_AT, timestamp)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "clipboard_suggestion_history"
        private const val KEY_LAST_SHOWN_HASH = "last_shown_hash"
        private const val KEY_LAST_SHOWN_AT = "last_shown_at"
        private const val KEY_LAST_DISMISSED_HASH = "last_dismissed_hash"
        private const val KEY_LAST_DISMISSED_AT = "last_dismissed_at"
    }
}
