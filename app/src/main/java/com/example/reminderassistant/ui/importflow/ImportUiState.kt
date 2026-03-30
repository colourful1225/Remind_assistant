package com.example.reminderassistant.ui.importflow

import com.example.reminderassistant.domain.model.ParsedType

data class ImportUiState(
    val rawText: String = "",
    val cleanedText: String = "",
    val suggestedTitle: String = "",
    val suggestedType: ParsedType = ParsedType.UNKNOWN,
    val detectedTimeText: String? = null,
    val detectedTimeMillis: Long? = null,
    val detectedLocation: String? = null,
    val confidence: Float = 0f,
    val isEmpty: Boolean = true
)
