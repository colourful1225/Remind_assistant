package com.example.reminderassistant.system.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import javax.inject.Inject

class ClipboardContentObserver @Inject constructor() {
    fun extractText(context: Context, clipboardManager: ClipboardManager): String? {
        val clipData: ClipData = clipboardManager.primaryClip ?: return null
        if (clipData.itemCount <= 0) return null
        val item = clipData.getItemAt(0)
        return item.text?.toString() ?: item.coerceToText(context).toString()
    }
}
