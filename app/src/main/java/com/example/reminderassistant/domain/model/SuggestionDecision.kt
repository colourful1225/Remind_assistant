package com.example.reminderassistant.domain.model

enum class SuggestionBlockReason {
    DISABLED,
    TOO_SHORT,
    LOW_CONFIDENCE,
    LOW_VALUE,
    COOLDOWN,
    DUPLICATE,
    DISMISSED
}

sealed class SuggestionDecision {
    data class Allowed(val suggestion: ClipboardSuggestion) : SuggestionDecision()
    data class Blocked(val reason: SuggestionBlockReason) : SuggestionDecision()
}
