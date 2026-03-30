package com.example.reminderassistant.system.accessibility

import android.content.Context
import android.content.Intent
import com.example.reminderassistant.MainActivity
import com.example.reminderassistant.domain.model.AccessibilitySuggestion
import com.example.reminderassistant.domain.usecase.BuildAccessibilityImportRequestUseCase
import com.example.reminderassistant.system.share.ImportSessionStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityImportBridge @Inject constructor(
    @ApplicationContext private val context: Context,
    private val buildAccessibilityImportRequestUseCase: BuildAccessibilityImportRequestUseCase,
    private val importSessionStore: ImportSessionStore
) {
    fun launchImport(suggestion: AccessibilitySuggestion, preferredRoute: String) {
        val session = buildAccessibilityImportRequestUseCase.invoke(suggestion)
        importSessionStore.set(session, preferredRoute)
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    }
}
