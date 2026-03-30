package com.example.reminderassistant.system.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminderassistant.domain.usecase.RestoreScheduledRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject lateinit var restoreScheduledRemindersUseCase: RestoreScheduledRemindersUseCase

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            restoreScheduledRemindersUseCase()
            pendingResult.finish()
        }
    }
}
