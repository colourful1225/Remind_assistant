package com.example.reminderassistant.ui.clipboard

import com.example.reminderassistant.domain.model.ParseResult
import com.example.reminderassistant.domain.model.ParsedType

data class ClipboardSuggestionUiState(
    val isVisible: Boolean = false,
    val rawText: String = "",
    val snippet: String = "",
    val suggestedType: ParsedType = ParsedType.UNKNOWN,
    val confidence: Float = 0f,
    val textHash: String = "",
    val parseResult: ParseResult? = null
)
