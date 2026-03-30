package com.example.reminderassistant.system.accessibility.model

data class AccessibilityCandidate(
    val rawText: String,
    val targetApp: AccessibilityTargetApp,
    val context: AccessibilityContext
)
