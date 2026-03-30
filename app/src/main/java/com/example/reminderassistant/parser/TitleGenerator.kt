package com.example.reminderassistant.parser

import javax.inject.Inject

class TitleGenerator @Inject constructor() {
    fun generate(cleanedText: String): String {
        if (cleanedText.isBlank()) {
            return "New Reminder"
        }

        val firstLine = cleanedText.split("\n").firstOrNull().orEmpty()
        val sentence = firstLine.split("，", "。", ".", "!", "?").firstOrNull().orEmpty()
        val trimmed = sentence.trim()

        return if (trimmed.length <= 24) trimmed else trimmed.take(24)
    }
}
