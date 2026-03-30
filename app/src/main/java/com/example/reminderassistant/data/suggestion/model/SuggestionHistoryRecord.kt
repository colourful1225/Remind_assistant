package com.example.reminderassistant.data.suggestion.model

data class SuggestionHistoryRecord(
    val lastShownHash: String? = null,
    val lastShownAt: Long = 0L,
    val lastDismissedHash: String? = null,
    val lastDismissedAt: Long = 0L
)
