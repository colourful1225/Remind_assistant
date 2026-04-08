package com.example.reminderassistant.system.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.reminderassistant.R
import com.example.reminderassistant.data.settings.SettingsKeys
import com.example.reminderassistant.data.settings.SettingsRepository
import com.example.reminderassistant.parser.TimeParser
import com.example.reminderassistant.system.clipboard.ClipboardMonitor
import com.example.reminderassistant.system.intent.XiaomiIntentCaller
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "ClipboardMonitorService"
private const val CHANNEL_ID = "clipboard_monitor_channel"
private const val NOTIFICATION_ID = 1001

@AndroidEntryPoint
class ClipboardMonitorService : Service() {

    companion object {
        const val ACTION_START = "com.example.reminderassistant.action.START_MONITOR"
        const val ACTION_STOP = "com.example.reminderassistant.action.STOP_MONITOR"
        const val ACTION_TEST_TRIGGER = "com.example.reminderassistant.action.TEST_TRIGGER"
        const val ACTION_REFRESH_NOTIFICATION = "com.example.reminderassistant.action.REFRESH_NOTIFICATION"
        const val ACTION_TOGGLE_ASSIST = "com.example.reminderassistant.action.TOGGLE_ASSIST"
    }

    @Inject
    lateinit var clipboardMonitor: ClipboardMonitor

    @Inject
    lateinit var timeParser: TimeParser

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private lateinit var xiaomiIntentCaller: XiaomiIntentCaller
    private lateinit var windowManager: WindowManager

    private var overlayView: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var autoDismissTask: Runnable? = null

    private var monitoringStarted = false
    private var lastCopiedText: String = ""

    override fun onCreate() {
        super.onCreate()
        if (!settingsRepository.getBackgroundMonitorEnabled()) {
            Log.i(TAG, "background monitor switch is off, stop service")
            stopSelf()
            return
        }

        xiaomiIntentCaller = XiaomiIntentCaller(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startClipboardMonitoringIfNeeded()
        Log.i(TAG, "ClipboardMonitorService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!settingsRepository.getBackgroundMonitorEnabled()) {
            Log.i(TAG, "switch turned off, ignore action and stop")
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_REFRESH_NOTIFICATION -> {
                updateForegroundNotification()
            }
            ACTION_TEST_TRIGGER -> {
                val copiedText = intent.getStringExtra("copiedText").orEmpty()
                val timeMillis = intent.getLongExtra("timeMillis", 0L)
                if (copiedText.isNotBlank() && timeMillis > 0L) {
                    Log.i(TAG, "receive ACTION_TEST_TRIGGER, show overlay")
                    showBottomOverlay(copiedText, timeMillis)
                }
            }
            else -> {
                startClipboardMonitoringIfNeeded()
            }
        }

        return START_STICKY
    }

    private fun startClipboardMonitoringIfNeeded() {
        if (monitoringStarted) return
        monitoringStarted = true
        Log.i(TAG, "clipboard monitoring started")

        clipboardMonitor.startMonitoring { copiedText ->
            if (copiedText == lastCopiedText) return@startMonitoring
            lastCopiedText = copiedText

            val parseResult = timeParser.parse(copiedText)
            if (parseResult.timeMillis != null && parseResult.confidence > 0.4f) {
                Log.i(TAG, "time parsed, show overlay text=$copiedText")
                showBottomOverlay(copiedText, parseResult.timeMillis)
            }
        }
    }

    private fun showBottomOverlay(copiedText: String, timeMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Log.w(TAG, "Overlay permission not granted")
            return
        }

        dismissBottomOverlay()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F2FFFFFF"))
            setPadding(28, 20, 28, 20)
            elevation = 24f
        }

        val title = TextView(this).apply {
            text = "检测到时间信息"
            textSize = 16f
            setTextColor(Color.BLACK)
        }

        val content = TextView(this).apply {
            text = copiedText.take(30)
            textSize = 13f
            setTextColor(Color.DKGRAY)
            setPadding(0, 10, 0, 14)
        }

        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val btnCalendar = Button(this).apply {
            text = "小米日历"
            contentDescription = "悬浮窗_小米日历"
            setOnClickListener {
                xiaomiIntentCaller.startMiCalendar(
                    title = copiedText,
                    timeMillis = timeMillis,
                    description = copiedText
                )
                dismissBottomOverlay()
            }
        }

        val btnTodo = Button(this).apply {
            text = "小米笔记待办"
            contentDescription = "悬浮窗_小米笔记待办"
            setOnClickListener {
                xiaomiIntentCaller.startMiNotes(
                    title = copiedText,
                    content = copiedText
                )
                dismissBottomOverlay()
            }
        }

        val btnDismiss = Button(this).apply {
            text = "关闭"
            contentDescription = "悬浮窗_关闭"
            setOnClickListener { dismissBottomOverlay() }
        }

        actionRow.addView(btnCalendar, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        actionRow.addView(btnTodo, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        actionRow.addView(btnDismiss, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f))

        root.addView(title)
        root.addView(content)
        root.addView(actionRow)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM
            y = 36
        }

        overlayView = root
        windowManager.addView(root, params)
        Log.i(TAG, "overlay added to window manager")

        val dismissRunnable = Runnable { dismissBottomOverlay() }
        autoDismissTask = dismissRunnable
        mainHandler.postDelayed(dismissRunnable, 12000)
    }

    private fun dismissBottomOverlay() {
        autoDismissTask?.let { mainHandler.removeCallbacks(it) }
        autoDismissTask = null

        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        overlayView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissBottomOverlay()
        clipboardMonitor.stopMonitoring()
        monitoringStarted = false
        Log.i(TAG, "ClipboardMonitorService destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!settingsRepository.getBackgroundMonitorEnabled()) return

        val restartIntent = Intent(this, ClipboardMonitorService::class.java).apply {
            action = ACTION_START
        }
        val pendingIntent = PendingIntent.getService(
            this,
            1101,
            restartIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1500L,
            pendingIntent
        )
        Log.w(TAG, "onTaskRemoved -> schedule service restart")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "剪贴板监听服务",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val assistEnabled = isAssistEnabled()
        val toggleTitle = if (assistEnabled) "关闭" else "开启"
        val toggleBroadcastIntent = Intent(this, AssistToggleReceiver::class.java).apply {
            action = ACTION_TOGGLE_ASSIST
        }
        val toggleIntent = PendingIntent.getBroadcast(
            this,
            2201,
            toggleBroadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("提醒助手运行中")
            .setContentText(if (assistEnabled) "状态：已开启（点此可关闭）" else "状态：已关闭（点此可开启）")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(toggleIntent)
            .addAction(0, toggleTitle, toggleIntent)
            .build()
    }

    private fun updateForegroundNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun isAssistEnabled(): Boolean {
        val prefs = getSharedPreferences(SettingsKeys.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(SettingsKeys.KEY_A11Y_ASSIST_ENABLED, true)
    }
}
