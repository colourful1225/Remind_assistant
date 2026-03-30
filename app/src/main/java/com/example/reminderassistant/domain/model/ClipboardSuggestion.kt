package com.example.reminderassistant.domain.model

data class ClipboardSuggestion(
    val rawText: String,
    val parseResult: ParseResult,
    val suggestedType: ParsedType,
    val confidence: Float,
    val createdAt: Long,
    val textHash: String
)
