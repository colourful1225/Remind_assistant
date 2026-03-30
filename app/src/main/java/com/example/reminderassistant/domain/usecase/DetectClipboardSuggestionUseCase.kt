package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.data.settings.SettingsRepository
import com.example.reminderassistant.data.suggestion.ClipboardSuggestionRepository
import com.example.reminderassistant.data.suggestion.ClipboardTextHasher
import com.example.reminderassistant.domain.model.ClipboardSuggestion
import com.example.reminderassistant.domain.model.ParsedType
import com.example.reminderassistant.domain.model.SuggestionBlockReason
import com.example.reminderassistant.domain.model.SuggestionDecision
import javax.inject.Inject

class DetectClipboardSuggestionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val suggestionRepository: ClipboardSuggestionRepository,
    private val parseIncomingTextUseCase: ParseIncomingTextUseCase,
    private val textHasher: ClipboardTextHasher
) {
    fun invoke(rawText: String): SuggestionDecision {
        if (!settingsRepository.getClipboardSuggestionsEnabled()) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.DISABLED)
        }

        val trimmed = rawText.trim()
        if (trimmed.length < MIN_LENGTH) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.TOO_SHORT)
        }

        val now = System.currentTimeMillis()
        val history = suggestionRepository.getHistory()
        val hash = textHasher.hash(trimmed)

        if (history.lastShownHash == hash && now - history.lastShownAt < DEDUPE_WINDOW_MS) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.DUPLICATE)
        }
        if (history.lastDismissedHash == hash && now - history.lastDismissedAt < DISMISS_WINDOW_MS) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.DISMISSED)
        }

        val cooldownMs = settingsRepository.getCooldownMinutes() * 60_000L
        if (now - history.lastShownAt < cooldownMs) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.COOLDOWN)
        }

        val parseResult = parseIncomingTextUseCase(trimmed)
        if (settingsRepository.getHighConfidenceOnly() && parseResult.confidence < CONFIDENCE_THRESHOLD) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.LOW_CONFIDENCE)
        }
        if (parseResult.suggestedType == ParsedType.UNKNOWN) {
            return SuggestionDecision.Blocked(SuggestionBlockReason.LOW_VALUE)
        }

        val suggestion = ClipboardSuggestion(
            rawText = trimmed,
            parseResult = parseResult,
            suggestedType = parseResult.suggestedType,
            confidence = parseResult.confidence,
            createdAt = now,
            textHash = hash
        )
        return SuggestionDecision.Allowed(suggestion)
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val CONFIDENCE_THRESHOLD = 0.6f
        private const val DEDUPE_WINDOW_MS = 2 * 60 * 1000L
        private const val DISMISS_WINDOW_MS = 10 * 60 * 1000L
    }
}
