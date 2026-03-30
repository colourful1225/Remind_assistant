package com.example.reminderassistant.system.accessibility.model

data class AccessibilityContext(
    val packageName: String,
    val className: String?,
    val eventType: Int,
    val timestamp: Long
)
