package com.example.reminderassistant.system.accessibility.model

enum class AccessibilityTargetApp(val packageNames: Set<String>, val displayName: String) {
    WECHAT(setOf("com.tencent.mm"), "WeChat"),
    SMS(setOf("com.android.mms", "com.google.android.apps.messaging", "com.android.messaging"), "SMS");

    companion object {
        fun fromPackage(packageName: String): AccessibilityTargetApp? {
            return entries.firstOrNull { packageName in it.packageNames }
        }
    }
}
