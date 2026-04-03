package com.example.reminderassistant.system.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.os.Handler
import android.os.Looper
import com.example.reminderassistant.parser.TimeParser
import com.example.reminderassistant.system.intent.XiaomiIntentCaller
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "ReminderA11yService"

@AndroidEntryPoint
class ReminderAccessibilityService : AccessibilityService() {

    @Inject lateinit var timeParser: TimeParser
    private lateinit var textExtractor: AccessibilityNodeTextExtractor
    private var overlayController: AccessibilityOverlayController? = null
    private lateinit var xiaomiIntentCaller: XiaomiIntentCaller
    private var lastHandledText: String = ""
    private var lastHandledTs: Long = 0L
    private var pendingCopiedText: String? = null
    private var pendingPackage: String? = null
    private var pendingTs: Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val clearPendingRunnable = Runnable {
        clearPending("pending_timeout")
    }
    private val selectionWatchdogRunnable = object : Runnable {
        override fun run() {
            if (overlayController?.isShowing() != true) return
            val root = rootInActiveWindow
            val stillSelecting = hasSelectionUi(root)
            if (!stillSelecting) {
                overlayController?.hide("selection_lost")
                clearPending("selection_lost")
                return
            }
            handler.postDelayed(this, 350L)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        textExtractor = AccessibilityNodeTextExtractor()
        overlayController = AccessibilityOverlayController(this)
        xiaomiIntentCaller = XiaomiIntentCaller(this)

        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_LONG_CLICKED or
                AccessibilityEvent.TYPE_VIEW_CLICKED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 120
        }
        Log.i(TAG, "accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString().orEmpty()
        if (pkg.isBlank() || pkg == packageName || isIgnoredPackage(pkg)) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                val candidate = textExtractor.extract(event, rootInActiveWindow)?.trim().orEmpty()
                if (candidate.isBlank()) return
                pendingCopiedText = candidate
                pendingPackage = pkg
                pendingTs = System.currentTimeMillis()
                handler.removeCallbacks(clearPendingRunnable)
                handler.postDelayed(clearPendingRunnable, 6_000L)
                startSelectionWatchdog()
                Log.i(TAG, "pending text cached pkg=$pkg text=$candidate")
            }

            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                if (!isCopyAction(event)) {
                    if (overlayController?.isShowing() == true) {
                        overlayController?.hide("non_copy_click")
                    }
                    return
                }
                val cached = pendingCopiedText ?: return
                if (System.currentTimeMillis() - pendingTs > 6_000L) return
                if (pendingPackage != null && pendingPackage != pkg) return
                if (shouldIgnoreDuplicate(cached)) return

                val timeResult = timeParser.parse(cached)
                lastHandledText = cached
                lastHandledTs = System.currentTimeMillis()
                Log.i(TAG, "copy confirmed pkg=$pkg text=$cached conf=${timeResult.confidence}")

                overlayController?.show(
                    rawText = cached,
                    parsedTimeMillis = timeResult.timeMillis,
                    onCalendar = {
                        xiaomiIntentCaller.startMiCalendar(
                            title = cached,
                            timeMillis = it,
                            description = cached
                        )
                    },
                    onNotes = {
                        xiaomiIntentCaller.startMiNotes(
                            title = cached,
                            content = cached
                        )
                    }
                )
                startSelectionWatchdog()
                clearPending("copy_consumed")
            }

            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (overlayController?.isShowing() == true && !hasSelectionUi(rootInActiveWindow)) {
                    overlayController?.hide("window_changed")
                    clearPending("window_changed")
                }
            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                if (overlayController?.isShowing() == true && !hasSelectionUi(rootInActiveWindow)) {
                    overlayController?.hide("selection_changed")
                    clearPending("selection_changed")
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "onInterrupt called")
    }

    override fun onDestroy() {
        overlayController?.hide()
        handler.removeCallbacks(clearPendingRunnable)
        handler.removeCallbacks(selectionWatchdogRunnable)
        super.onDestroy()
    }

    private fun shouldIgnoreDuplicate(text: String): Boolean {
        val now = System.currentTimeMillis()
        val isDuplicate = text == lastHandledText && (now - lastHandledTs) < 3000L
        return isDuplicate
    }

    private fun isCopyAction(event: AccessibilityEvent): Boolean {
        val t1 = event.text?.joinToString(" ")?.lowercase().orEmpty()
        val t2 = event.contentDescription?.toString()?.lowercase().orEmpty()
        val s = event.source
        val sourceText = s?.text?.toString()?.lowercase().orEmpty()
        val merged = "$t1 $t2 $sourceText"
        return merged.contains("复制") || merged.contains("拷贝") || merged.contains("copy")
    }

    private fun isIgnoredPackage(pkg: String): Boolean {
        return pkg.startsWith("com.android.systemui") ||
            pkg.startsWith("com.miui.home") ||
            pkg.startsWith("com.google.android.inputmethod") ||
            pkg.startsWith("com.sohu.inputmethod")
    }

    private fun clearPending(reason: String) {
        handler.removeCallbacks(clearPendingRunnable)
        pendingCopiedText = null
        pendingPackage = null
        pendingTs = 0L
        Log.i(TAG, "pending cleared: $reason")
    }

    private fun startSelectionWatchdog() {
        handler.removeCallbacks(selectionWatchdogRunnable)
        handler.postDelayed(selectionWatchdogRunnable, 250L)
    }

    private fun hasSelectionUi(root: AccessibilityNodeInfo?): Boolean {
        if (root == null) return false
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var scanned = 0
        while (queue.isNotEmpty() && scanned < 120) {
            val node = queue.removeFirst()
            scanned++
            val text = (node.text?.toString().orEmpty() + " " + node.contentDescription?.toString().orEmpty())
                .lowercase()
            if (text.contains("复制") || text.contains("拷贝") || text.contains("copy")) {
                return true
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let(queue::add)
            }
        }
        return false
    }
}
