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
        rawText: String,
        onCalendar: () -> Unit,
        onNotes: () -> Unit
    ) {
        if (overlayView != null) return

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 12, 16, 12)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        }

        val textView = TextView(context).apply {
            text = rawText.take(80)
            textSize = 14f
            setPadding(0, 8, 0, 8)
        }

        val buttonRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val calendarButton = Button(context).apply {
            text = "日历"
            setOnClickListener {
                onCalendar()
                hide()
            }
        }
        val notesButton = Button(context).apply {
            text = "备忘录"
            setOnClickListener {
                onNotes()
                hide()
            }
        }
        val closeButton = Button(context).apply {
            text = "关闭"
            setOnClickListener {
                hide()
            }
        }

        buttonRow.addView(calendarButton)
        buttonRow.addView(notesButton)
        buttonRow.addView(closeButton)

        container.addView(textView)
        container.addView(buttonRow)

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
