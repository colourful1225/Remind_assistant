package com.example.reminderassistant.parser

import javax.inject.Inject

class TextCleaner @Inject constructor() {
    fun clean(rawText: String): String {
        return rawText
            .replace("\r\n", "\n")
            .replace(Regex("[\\t ]+"), " ")
            .replace(Regex("\\n{2,}"), "\n")
            .trim()
    }
}
