package com.example.reminderassistant.system.calendar

import android.content.Context
import com.example.reminderassistant.domain.model.CalendarDraft
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarIntentLauncherImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CalendarIntentLauncher {

    override fun prepareLaunch(draft: CalendarDraft): CalendarLaunchResult {
        val intent = CalendarIntentFactory.createInsertIntent(draft)
        val canHandle = intent.resolveActivity(context.packageManager) != null
        return if (canHandle) {
            CalendarLaunchResult.Ready(intent)
        } else {
            CalendarLaunchResult.NoCalendarApp
        }
    }
}
