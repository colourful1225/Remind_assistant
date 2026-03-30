package com.example.reminderassistant.system.accessibility

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.reminderassistant.R

class AccessibilityOverlayController(
    private val context: Context
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoDismissRunnable = Runnable { hide() }

    fun show(
        title: String,
        snippet: String,
        onReminder: () -> Unit,
        onCalendar: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (overlayView != null) return

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        }

        val titleView = TextView(context).apply {
            text = title
            textSize = 16f
        }
        val snippetView = TextView(context).apply {
            text = snippet
            textSize = 14f
            setPadding(0, 12, 0, 12)
        }

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val reminderButton = Button(context).apply {
            text = context.getString(R.string.accessibility_add_reminder)
            setOnClickListener {
                onReminder()
                hide()
            }
        }
        val calendarButton = Button(context).apply {
            text = context.getString(R.string.accessibility_add_calendar)
            setOnClickListener {
                onCalendar()
                hide()
            }
        }
        val dismissButton = Button(context).apply {
            text = context.getString(R.string.accessibility_dismiss)
            setOnClickListener {
                onDismiss()
                hide()
            }
        }

        row.addView(reminderButton)
        row.addView(calendarButton)
        row.addView(dismissButton)

        container.addView(titleView)
        container.addView(snippetView)
        container.addView(row)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 120
        }

        overlayView = container
        windowManager.addView(container, params)
        handler.postDelayed(autoDismissRunnable, AUTO_DISMISS_MS)
    }

    fun hide() {
        handler.removeCallbacks(autoDismissRunnable)
        overlayView?.let { view ->
            windowManager.removeView(view)
        }
        overlayView = null
    }

    companion object {
        private const val AUTO_DISMISS_MS = 12_000L
    }
}
