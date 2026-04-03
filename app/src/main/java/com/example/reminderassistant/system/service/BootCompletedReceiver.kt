package com.example.reminderassistant.system.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.reminderassistant.data.settings.SettingsKeys

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val shouldCheck =
            action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED

        if (!shouldCheck) return

        val prefs = context.getSharedPreferences(SettingsKeys.PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(SettingsKeys.KEY_BACKGROUND_MONITOR_ENABLED, false)
        if (!enabled) return

        val serviceIntent = Intent(context, ClipboardMonitorService::class.java).apply {
            this.action = ClipboardMonitorService.ACTION_START
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
