package com.example.reminderassistant.system.accessibility

import com.example.reminderassistant.system.accessibility.model.AccessibilityTargetApp

class AccessibilityTargetMatcher {
    fun match(packageName: String?): AccessibilityTargetApp? {
        if (packageName.isNullOrBlank()) return null
        return AccessibilityTargetApp.fromPackage(packageName)
    }
}
