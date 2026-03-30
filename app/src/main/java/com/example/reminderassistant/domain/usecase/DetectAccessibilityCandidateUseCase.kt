package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.data.suggestion.ClipboardTextHasher
import com.example.reminderassistant.domain.model.AccessibilitySuggestion
import com.example.reminderassistant.domain.model.ParsedType
import com.example.reminderassistant.system.accessibility.CandidateCooldownStore
import com.example.reminderassistant.system.accessibility.model.AccessibilityCandidate
import javax.inject.Inject

class DetectAccessibilityCandidateUseCase @Inject constructor(
    private val parseIncomingTextUseCase: ParseIncomingTextUseCase,
    private val textHasher: ClipboardTextHasher,
    private val cooldownStore: CandidateCooldownStore
) {
    fun invoke(candidate: AccessibilityCandidate): AccessibilitySuggestion? {
        val trimmed = candidate.rawText.trim()
        if (trimmed.length < MIN_LENGTH || trimmed.length > MAX_LENGTH) return null

        val now = System.currentTimeMillis()
        val hash = textHasher.hash(trimmed)
        if (cooldownStore.shouldBlock(hash, now)) return null

        val parseResult = parseIncomingTextUseCase(trimmed)
        if (parseResult.suggestedType == ParsedType.UNKNOWN || parseResult.confidence < CONFIDENCE_THRESHOLD) {
            return null
        }

        val suggestion = AccessibilitySuggestion(
            rawText = trimmed,
            parseResult = parseResult,
            suggestedType = parseResult.suggestedType,
            confidence = parseResult.confidence,
            createdAt = now,
            textHash = hash,
            targetApp = candidate.targetApp
        )
        cooldownStore.recordShown(hash, now)
        return suggestion
    }

    companion object {
        private const val MIN_LENGTH = 6
        private const val MAX_LENGTH = 180
        private const val CONFIDENCE_THRESHOLD = 0.6f
    }
}
