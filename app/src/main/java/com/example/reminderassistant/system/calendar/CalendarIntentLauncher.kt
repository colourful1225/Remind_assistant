package com.example.reminderassistant.system.calendar

import android.content.Intent
import com.example.reminderassistant.domain.model.CalendarDraft

sealed class CalendarLaunchResult {
    data class Ready(val intent: Intent) : CalendarLaunchResult()
    data object NoCalendarApp : CalendarLaunchResult()
}

interface CalendarIntentLauncher {
    fun prepareLaunch(draft: CalendarDraft): CalendarLaunchResult
}
