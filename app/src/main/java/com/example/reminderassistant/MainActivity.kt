package com.example.reminderassistant

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.reminderassistant.data.settings.SettingsRepository
import com.example.reminderassistant.system.service.ClipboardMonitorService
import com.example.reminderassistant.ui.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var monitorEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitorEnabled = settingsRepository.getBackgroundMonitorEnabled()

        if (monitorEnabled) {
            startClipboardService()
        }

        setContent {
            MainScreen(
                monitorEnabled = monitorEnabled,
                onMonitorEnabledChange = { enabled ->
                    monitorEnabled = enabled
                    settingsRepository.setBackgroundMonitorEnabled(enabled)
                    if (enabled) {
                        startClipboardService()
                        ensureOverlayPermission()
                    } else {
                        stopClipboardService()
                    }
                },
                onHideToBackground = {
                    moveTaskToBack(true)
                    startActivity(
                        Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                },
                onOpenOverlayPermission = { ensureOverlayPermission() },
                onOpenAccessibilitySettings = {
                    startActivity(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                },
                onOpenAutostartSettings = { openAutostartSettings() },
                onOpenBatterySettings = { requestIgnoreBatteryOptimization() }
            )
        }
    }

    private fun startClipboardService() {
        val serviceIntent = Intent(this, ClipboardMonitorService::class.java).apply {
            action = ClipboardMonitorService.ACTION_START
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopClipboardService() {
        val serviceIntent = Intent(this, ClipboardMonitorService::class.java).apply {
            action = ClipboardMonitorService.ACTION_STOP
        }
        startService(serviceIntent)
    }

    private fun ensureOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (Settings.canDrawOverlays(this)) return
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
        )
    }

    private fun openAutostartSettings() {
        val candidates = listOf(
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            ),
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.appmanager.ApplicationsDetailsActivity"
                )
            ).putExtra("package_name", packageName)
        )

        val launched = candidates.any { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runCatching { startActivity(intent) }.isSuccess
        }

        if (!launched) {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    private fun requestIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) return

        startActivity(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
