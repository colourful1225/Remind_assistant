package com.example.reminderassistant.domain.model

data class ImportSession(
    val request: ImportRequest,
    val parseResult: ParseResult
)
