package com.example.reminderassistant.domain.model

data class ParseResult(
    val rawText: String,
    val cleanedText: String,
    val suggestedTitle: String,
    val suggestedType: ParsedType,
    val detectedTimeMillis: Long? = null,
    val detectedTimeText: String? = null,
    val detectedLocation: String? = null,
    val confidence: Float = 0f
)
