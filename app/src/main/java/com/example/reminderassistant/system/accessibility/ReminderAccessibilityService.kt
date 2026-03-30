package com.example.reminderassistant.system.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.example.reminderassistant.domain.model.AccessibilitySuggestion
import com.example.reminderassistant.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAccessibilityService : AccessibilityService() {

    @Inject lateinit var eventRouter: AccessibilityEventRouter
    @Inject lateinit var importBridge: AccessibilityImportBridge

    private var overlayController: AccessibilityOverlayController? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlayController = AccessibilityOverlayController(this)
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED or
                AccessibilityEvent.TYPE_VIEW_SELECTED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val suggestion: AccessibilitySuggestion = eventRouter.onEvent(event, rootInActiveWindow) ?: return
        val snippet = suggestion.rawText.take(80)

        overlayController?.show(
            title = getString(com.example.reminderassistant.R.string.accessibility_quick_action_title),
            snippet = snippet,
            onReminder = {
                importBridge.launchImport(suggestion, Routes.REMINDER_EDITOR)
            },
            onCalendar = {
                importBridge.launchImport(suggestion, Routes.CALENDAR_EDITOR)
            },
            onDismiss = { /* no-op */ }
        )
    }

    override fun onInterrupt() {
        overlayController?.hide()
    }

    override fun onDestroy() {
        overlayController?.hide()
        super.onDestroy()
    }
}
