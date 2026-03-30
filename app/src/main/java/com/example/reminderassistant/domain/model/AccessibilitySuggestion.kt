package com.example.reminderassistant.domain.model

import com.example.reminderassistant.system.accessibility.model.AccessibilityTargetApp

data class AccessibilitySuggestion(
    val rawText: String,
    val parseResult: ParseResult,
    val suggestedType: ParsedType,
    val confidence: Float,
    val createdAt: Long,
    val textHash: String,
    val targetApp: AccessibilityTargetApp
)
