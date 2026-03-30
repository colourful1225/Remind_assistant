package com.example.reminderassistant.data.suggestion

import com.example.reminderassistant.data.suggestion.model.SuggestionHistoryRecord

interface ClipboardSuggestionRepository {
    fun getHistory(): SuggestionHistoryRecord
    fun recordShown(hash: String, timestamp: Long)
    fun recordDismissed(hash: String, timestamp: Long)
}
