package com.example.reminderassistant.system.clipboard

import android.content.ClipboardManager
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 剪贴板监听服务
 * 监听系统剪贴板变化，提取文本时间信息
 */
@Singleton
class ClipboardMonitor @Inject constructor(
    private val context: Context
) {
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private var clipboardListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    fun startMonitoring(onTextCopied: (String) -> Unit) {
        clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
            val clip = clipboardManager.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString() ?: return@OnPrimaryClipChangedListener
                if (text.isNotEmpty()) {
                    onTextCopied(text)
                }
            }
        }
        clipboardManager.addPrimaryClipChangedListener(clipboardListener!!)
    }

    fun stopMonitoring() {
        if (clipboardListener != null) {
            clipboardManager.removePrimaryClipChangedListener(clipboardListener!!)
            clipboardListener = null
        }
    }
}
