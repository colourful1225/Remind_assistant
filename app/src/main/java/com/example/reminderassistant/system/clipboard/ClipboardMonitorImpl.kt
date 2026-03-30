package com.example.reminderassistant.system.clipboard

import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentObserver: ClipboardContentObserver
) : ClipboardMonitor {

    override fun clipboardTextFlow(): Flow<String> = callbackFlow {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            val text = contentObserver.extractText(context, clipboardManager)
            if (!text.isNullOrBlank()) {
                trySend(text)
            }
        }
        clipboardManager.addPrimaryClipChangedListener(listener)
        awaitClose { clipboardManager.removePrimaryClipChangedListener(listener) }
    }
}
