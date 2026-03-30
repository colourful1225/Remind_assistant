package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.data.suggestion.ClipboardSuggestionRepository
import javax.inject.Inject

class RecordSuggestionShownUseCase @Inject constructor(
    private val suggestionRepository: ClipboardSuggestionRepository
) {
    fun invoke(hash: String, timestamp: Long) {
        suggestionRepository.recordShown(hash, timestamp)
    }
}
