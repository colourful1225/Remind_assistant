package com.example.reminderassistant.system.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.reminderassistant.data.settings.SettingsKeys
import com.example.reminderassistant.parser.TimeParser
import com.example.reminderassistant.system.intent.XiaomiIntentCaller
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "ReminderA11yService"

@AndroidEntryPoint
class ReminderAccessibilityService : AccessibilityService() {

    @Inject lateinit var timeParser: TimeParser
    private lateinit var textExtractor: AccessibilityNodeTextExtractor
    private lateinit var xiaomiIntentCaller: XiaomiIntentCaller
    private var overlayController: AccessibilityOverlayController? = null

    private val handler = Handler(Looper.getMainLooper())
    private var lastHandledText: String = ""
    private var lastHandledTs: Long = 0L
    private var showTimestamp: Long = 0L

    private val selectionWatchdogRunnable = object : Runnable {
        override fun run() {
            if (overlayController?.isShowing() != true) return
            if (overlayController?.isPanelShowing() == true) return
            val elapsed = System.currentTimeMillis() - showTimestamp
            if (elapsed < 1500L) {
                handler.postDelayed(this, 300L)
                return
            }
            if (!hasSelectionUi(rootInActiveWindow)) {
                overlayController?.hide("selection_lost")
                return
            }
            handler.postDelayed(this, 300L)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        textExtractor = AccessibilityNodeTextExtractor()
        xiaomiIntentCaller = XiaomiIntentCaller(this)
        overlayController = AccessibilityOverlayController(this)

        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_LONG_CLICKED or
                AccessibilityEvent.TYPE_VIEW_CLICKED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
        Log.i(TAG, "accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isAssistEnabled()) {
            if (overlayController?.isShowing() == true) overlayController?.hide("assist_disabled")
            return
        }

        val pkg = event.packageName?.toString().orEmpty()
        if (pkg.isBlank() || pkg == packageName || isIgnoredPackage(pkg)) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                val candidate = (
                    textExtractor.extract(event, rootInActiveWindow)
                        ?: readClipboardTextSafely()
                    )
                    ?.trim()
                    .orEmpty()
                if (candidate.isBlank()) return
                if (shouldIgnoreDuplicate(candidate)) return

                val parse = timeParser.parse(candidate)
                lastHandledText = candidate
                lastHandledTs = System.currentTimeMillis()
                showTimestamp = System.currentTimeMillis()

                overlayController?.show(
                    rawText = candidate,
                    parsedTimeMillis = parse.timeMillis,
                    onCalendar = { millis ->
                        xiaomiIntentCaller.startMiCalendar(
                            title = candidate,
                            timeMillis = millis,
                            description = candidate
                        )
                    },
                    onNotes = {
                        xiaomiIntentCaller.startMiNotes(
                            title = candidate,
                            content = candidate
                        )
                    }
                )
                startSelectionWatchdog()
                Log.i(TAG, "overlay show requested, pkg=$pkg, text=$candidate")
            }

            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                if (overlayController?.isShowing() != true) return
                if (overlayController?.isPanelShowing() == true) return
                val elapsed = System.currentTimeMillis() - showTimestamp
                if (elapsed < 1500L) return
                if (!hasSelectionUi(rootInActiveWindow)) {
                    overlayController?.hide("selection_changed")
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "onInterrupt")
    }

    override fun onDestroy() {
        overlayController?.hide()
        handler.removeCallbacks(selectionWatchdogRunnable)
        super.onDestroy()
    }

    private fun shouldIgnoreDuplicate(text: String): Boolean {
        val now = System.currentTimeMillis()
        return text == lastHandledText && (now - lastHandledTs) < 2500L
    }

    private fun startSelectionWatchdog() {
        handler.removeCallbacks(selectionWatchdogRunnable)
        handler.postDelayed(selectionWatchdogRunnable, 250L)
    }

    private fun isIgnoredPackage(pkg: String): Boolean {
        return pkg.startsWith("com.android.systemui") ||
            pkg.startsWith("com.miui.home") ||
            pkg.startsWith("com.google.android.inputmethod") ||
            pkg.startsWith("com.sohu.inputmethod")
    }

    private fun hasSelectionUi(root: AccessibilityNodeInfo?): Boolean {
        if (root == null) return false
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var scanned = 0
        while (queue.isNotEmpty() && scanned < 180) {
            val node = queue.removeFirst()
            scanned++

            val txt = node.text?.toString().orEmpty().lowercase()
            val desc = node.contentDescription?.toString().orEmpty().lowercase()
            val merged = "$txt $desc"
            if (
                merged.contains("复制") || merged.contains("拷贝") || merged.contains("copy") ||
                merged.contains("全选") || merged.contains("选择") || merged.contains("select")
            ) {
                return true
            }

            if (node.isFocused && node.isEditable) return true
            if (node.textSelectionStart >= 0 && node.textSelectionEnd > node.textSelectionStart) return true

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return false
    }

    private fun readClipboardTextSafely(): String? {
        return runCatching {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = manager.primaryClip ?: return null
            if (clip.itemCount <= 0) return null
            clip.getItemAt(0).coerceToText(this)?.toString()
        }.getOrNull()
    }

    private fun isAssistEnabled(): Boolean {
        val prefs = getSharedPreferences(SettingsKeys.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(SettingsKeys.KEY_A11Y_ASSIST_ENABLED, true)
    }
}
