package com.example.reminderassistant.system.share

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.reminderassistant.MainActivity
import com.example.reminderassistant.domain.model.ImportRequest
import com.example.reminderassistant.domain.model.ImportSession
import com.example.reminderassistant.domain.model.SourceContext
import com.example.reminderassistant.domain.model.SourceType
import com.example.reminderassistant.domain.usecase.ParseIncomingTextUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ShareReceiverActivity : ComponentActivity() {

    @Inject lateinit var parseIncomingTextUseCase: ParseIncomingTextUseCase
    @Inject lateinit var importSessionStore: ImportSessionStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        val rawText = intent?.getStringExtra(Intent.EXTRA_TEXT)
        if (rawText.isNullOrBlank()) {
            finish()
            return
        }

        lifecycleScope.launch {
            val parseResult = withContext(Dispatchers.Default) {
                parseIncomingTextUseCase(rawText)
            }
            val sourceContext = SourceContext(
                sourceType = SourceType.SHARE,
                sourceAppPackage = referrer?.host ?: callingPackage
            )
            importSessionStore.set(
                ImportSession(
                    request = ImportRequest(text = rawText, sourceContext = sourceContext),
                    parseResult = parseResult
                )
            )

            startActivity(
                Intent(this@ShareReceiverActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            )
            finish()
        }
    }
}
