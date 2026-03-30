package com.example.reminderassistant.system.accessibility

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityStatusProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isServiceEnabled(): Boolean {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = manager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        val targetId = "${context.packageName}/.system.accessibility.ReminderAccessibilityService"
        return enabledServices.any { it.id == targetId }
    }
}
