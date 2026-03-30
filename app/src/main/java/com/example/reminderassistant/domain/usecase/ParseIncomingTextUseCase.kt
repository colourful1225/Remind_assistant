package com.example.reminderassistant.domain.usecase

import com.example.reminderassistant.domain.model.ParseResult
import com.example.reminderassistant.parser.DefaultTextParser
import javax.inject.Inject

class ParseIncomingTextUseCase @Inject constructor(
    private val parser: DefaultTextParser = DefaultTextParser()
) {
    operator fun invoke(rawText: String): ParseResult {
        return parser.parse(rawText)
    }
}
