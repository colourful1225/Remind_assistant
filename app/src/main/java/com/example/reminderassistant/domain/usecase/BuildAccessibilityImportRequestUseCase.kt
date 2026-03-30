package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.model.ImportRequest
import com.example.reminderassistant.domain.model.ImportSession
import com.example.reminderassistant.domain.model.SourceContext
import com.example.reminderassistant.domain.model.SourceType
import com.example.reminderassistant.domain.model.AccessibilitySuggestion
import javax.inject.Inject

class BuildAccessibilityImportRequestUseCase @Inject constructor() {
    fun invoke(suggestion: AccessibilitySuggestion): ImportSession {
        val request = ImportRequest(
            text = suggestion.rawText,
            sourceContext = SourceContext(
                sourceType = SourceType.ACCESSIBILITY,
                sourceAppPackage = suggestion.targetApp.packageNames.firstOrNull(),
                sourceAppName = suggestion.targetApp.displayName
            )
        )
        return ImportSession(request = request, parseResult = suggestion.parseResult)
    }
}
