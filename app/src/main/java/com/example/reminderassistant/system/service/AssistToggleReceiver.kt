package com.example.reminderassistant.system.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminderassistant.data.settings.SettingsKeys

class AssistToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val prefs = context.getSharedPreferences(SettingsKeys.PREFS_NAME, Context.MODE_PRIVATE)

        when (action) {
            ClipboardMonitorService.ACTION_TOGGLE_ASSIST -> {
                val current = prefs.getBoolean(SettingsKeys.KEY_A11Y_ASSIST_ENABLED, true)
                prefs.edit().putBoolean(SettingsKeys.KEY_A11Y_ASSIST_ENABLED, !current).apply()
            }

            else -> return
        }

        context.startService(
            Intent(context, ClipboardMonitorService::class.java).apply {
                this.action = ClipboardMonitorService.ACTION_REFRESH_NOTIFICATION
            }
        )
    }
}
