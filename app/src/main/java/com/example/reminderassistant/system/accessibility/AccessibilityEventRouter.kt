package com.example.reminderassistant.system.accessibility

import android.view.accessibility.AccessibilityEvent
import com.example.reminderassistant.domain.usecase.DetectAccessibilityCandidateUseCase
import com.example.reminderassistant.system.accessibility.model.AccessibilityCandidate
import com.example.reminderassistant.system.accessibility.model.AccessibilityContext
import javax.inject.Inject

class AccessibilityEventRouter @Inject constructor(
    private val targetMatcher: AccessibilityTargetMatcher,
    private val textExtractor: AccessibilityNodeTextExtractor,
    private val detectAccessibilityCandidateUseCase: DetectAccessibilityCandidateUseCase
) {
    private var lastEventTime = 0L

    fun onEvent(event: AccessibilityEvent, root: android.view.accessibility.AccessibilityNodeInfo?): com.example.reminderassistant.domain.model.AccessibilitySuggestion? {
        val now = System.currentTimeMillis()
        if (now - lastEventTime < THROTTLE_MS) return null
        lastEventTime = now

        val targetApp = targetMatcher.match(event.packageName?.toString()) ?: return null
        val rawText = textExtractor.extract(event, root)?.trim().orEmpty()
        if (rawText.isBlank()) return null

        val context = AccessibilityContext(
            packageName = event.packageName?.toString().orEmpty(),
            className = event.className?.toString(),
            eventType = event.eventType,
            timestamp = now
        )
        val candidate = AccessibilityCandidate(
            rawText = rawText,
            targetApp = targetApp,
            context = context
        )
        return detectAccessibilityCandidateUseCase.invoke(candidate)
    }

    companion object {
        private const val THROTTLE_MS = 800L
    }
}
