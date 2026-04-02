package com.example.reminderassistant.system.intent

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 启动系统备忘录应用
 * 支持Google Keep、Samsung Notes等系统备忘录应用
 */
@Singleton
class NotesIntentLauncher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun launch(text: String, timeMillis: Long?) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = "vnd.android.cursor.dir/note"
            putExtra(Intent.EXTRA_TEXT, text)
            if (timeMillis != null) {
                putExtra("time_ms", timeMillis)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        // 尝试发送Intent，可能系统没有notes应用
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // 备用方案：尝试打开Google Keep
            tryGoogleKeep(text, timeMillis)
        }
    }

    private fun tryGoogleKeep(text: String, timeMillis: Long?) {
        val googleKeepIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.google.android.keep")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(googleKeepIntent)
        } catch (e: Exception) {
            // 如果都没有，静默失败
            e.printStackTrace()
        }
    }
}
