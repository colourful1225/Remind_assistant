package com.example.reminderassistant.system.accessibility

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class AccessibilityOverlayController(
    private val context: Context
) {
    companion object {
        private const val TAG = "A11yOverlay"
        private const val MINI_TIMEOUT_MS = 12_000L
        private const val PANEL_TIMEOUT_MS = 18_000L
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val handler = Handler(Looper.getMainLooper())
    private var miniView: View? = null
    private var panelView: View? = null

    private val miniDismissRunnable = Runnable { hideMini("mini_timeout") }
    private val panelDismissRunnable = Runnable { hidePanel("panel_timeout") }

    fun show(
        rawText: String,
        parsedTimeMillis: Long?,
        onCalendar: (Long?) -> Unit,
        onNotes: () -> Unit
    ) {
        hideAll("show_replace")
        showMiniButton(rawText, parsedTimeMillis, onCalendar, onNotes)
    }

    fun hide(reason: String = "manual") {
        hideAll(reason)
    }

    fun isShowing(): Boolean = miniView != null || panelView != null
    fun isMiniShowing(): Boolean = miniView != null
    fun isPanelShowing(): Boolean = panelView != null

    private fun showMiniButton(
        rawText: String,
        parsedTimeMillis: Long?,
        onCalendar: (Long?) -> Unit,
        onNotes: () -> Unit
    ) {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(14), dp(8), dp(14), dp(8))
            background = roundedBg(fill = "#EAF8F8F8", stroke = "#BBD2D2D2", radius = 22f)
            elevation = dp(8).toFloat()
        }

        val button = TextView(context).apply {
            text = "提醒"
            setTextColor(Color.parseColor("#202124"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setOnClickListener {
                hideMini("mini_clicked")
                showChoicePanel(rawText, parsedTimeMillis, onCalendar, onNotes)
            }
        }
        root.addView(button)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            x = dp(18)
            y = dp(148)
        }

        miniView = root
        runCatching { windowManager.addView(root, params) }
            .onSuccess {
                Log.i(TAG, "mini show")
                handler.postDelayed(miniDismissRunnable, MINI_TIMEOUT_MS)
            }
            .onFailure {
                miniView = null
                Log.e(TAG, "mini show failed: ${it.message}")
            }
    }

    private fun showChoicePanel(
        rawText: String,
        parsedTimeMillis: Long?,
        onCalendar: (Long?) -> Unit,
        onNotes: () -> Unit
    ) {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(16))
            background = roundedBg(fill = "#ECFFFFFF", stroke = "#A8D0D0D0", radius = 26f)
            elevation = dp(14).toFloat()
        }

        val title = TextView(context).apply {
            text = "保存到哪里？"
            setTextColor(Color.parseColor("#202124"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
        val content = TextView(context).apply {
            text = rawText.take(80)
            setTextColor(Color.parseColor("#5F6368"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setPadding(0, dp(8), 0, dp(12))
        }
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val calendarButton = Button(context).apply {
            text = "小米日历"
            setOnClickListener {
                onCalendar(parsedTimeMillis)
                hideAll("calendar_clicked")
            }
        }
        val notesButton = Button(context).apply {
            text = "小米笔记待办"
            setOnClickListener {
                onNotes()
                hideAll("notes_clicked")
            }
        }
        val closeButton = Button(context).apply {
            text = "关闭"
            setOnClickListener { hideAll("close_clicked") }
        }

        row.addView(calendarButton, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(notesButton, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(closeButton, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f))

        root.addView(title)
        root.addView(content)
        root.addView(row)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM
            y = dp(90)
        }

        panelView = root
        runCatching { windowManager.addView(root, params) }
            .onSuccess {
                Log.i(TAG, "panel show")
                handler.postDelayed(panelDismissRunnable, PANEL_TIMEOUT_MS)
            }
            .onFailure {
                panelView = null
                Log.e(TAG, "panel show failed: ${it.message}")
            }
    }

    private fun hideMini(reason: String) {
        handler.removeCallbacks(miniDismissRunnable)
        miniView?.let {
            runCatching { windowManager.removeView(it) }
            Log.i(TAG, "mini hide: $reason")
        }
        miniView = null
    }

    private fun hidePanel(reason: String) {
        handler.removeCallbacks(panelDismissRunnable)
        panelView?.let {
            runCatching { windowManager.removeView(it) }
            Log.i(TAG, "panel hide: $reason")
        }
        panelView = null
    }

    private fun hideAll(reason: String) {
        hideMini(reason)
        hidePanel(reason)
    }

    private fun roundedBg(fill: String, stroke: String, radius: Float): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radius.toInt()).toFloat()
            setColor(Color.parseColor(fill))
            setStroke(dp(1), Color.parseColor(stroke))
        }
    }

    private fun dp(v: Int): Int {
        val density = context.resources.displayMetrics.density
        return (v * density).toInt()
    }
}
