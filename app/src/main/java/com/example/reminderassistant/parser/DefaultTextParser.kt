package com.example.reminderassistant.parser

import com.example.reminderassistant.domain.model.ParseResult
import javax.inject.Inject

class DefaultTextParser @Inject constructor(
    private val textCleaner: TextCleaner,
    private val timeParser: TimeParser,
    private val locationParser: LocationParser,
    private val titleGenerator: TitleGenerator,
    private val contentClassifier: ContentClassifier
) {
    fun parse(rawText: String): ParseResult {
        val cleaned = textCleaner.clean(rawText)
        val timeResult = timeParser.parse(cleaned)
        val location = locationParser.parse(cleaned)
        val title = titleGenerator.generate(cleaned)
        val type = contentClassifier.classify(cleaned, timeResult.timeMillis != null)

        val confidence = (
            0.2f +
                (if (timeResult.timeMillis != null) 0.35f else 0f) +
                (if (location != null) 0.15f else 0f) +
                (if (title.isNotBlank()) 0.1f else 0f) +
                timeResult.confidence
            ).coerceIn(0f, 0.95f)

        return ParseResult(
            rawText = rawText,
            cleanedText = cleaned,
            suggestedTitle = title,
            suggestedType = type,
            detectedTimeMillis = timeResult.timeMillis,
            detectedTimeText = timeResult.matchedText,
            detectedLocation = location,
            confidence = confidence
        )
    }
}
