package com.example.reminderassistant.system.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.provider.CalendarContract
import android.view.accessibility.AccessibilityEvent
import com.example.reminderassistant.parser.TimeParser
import com.example.reminderassistant.system.accessibility.model.TextExtractor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAccessibilityService : AccessibilityService() {

    @Inject lateinit var timeParser: TimeParser
    private lateinit var textExtractor: TextExtractor
    private var overlayController: AccessibilityOverlayController? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        textExtractor = TextExtractor()
        overlayController = AccessibilityOverlayController(this)
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_LONG_CLICKED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) return
        
        val rawText = textExtractor.extract(event, rootInActiveWindow)?.trim() ?: return
        if (rawText.isBlank()) return
        
        val timeResult = timeParser.parse(rawText)
        overlayController?.show(
            rawText = rawText,
            onCalendar = { launchCalendarIntent(rawText, timeResult.timeMillis) },
            onNotes = { launchNotesIntent(rawText, timeResult.timeMillis) }
        )
    }

    private fun launchCalendarIntent(text: String, timeMillis: Long?) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, text)
            if (timeMillis != null) {
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, timeMillis)
            }
        }
        startActivity(intent)
    }

    private fun launchNotesIntent(text: String, timeMillis: Long?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, text)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        overlayController?.hide()
    }

    override fun onDestroy() {
        overlayController?.hide()
        super.onDestroy()
    }
}
