package com.example.reminderassistant.di

import com.example.reminderassistant.data.repository.ReminderRepositoryImpl
import com.example.reminderassistant.data.settings.SettingsRepository
import com.example.reminderassistant.data.settings.SettingsRepositoryImpl
import com.example.reminderassistant.data.suggestion.ClipboardSuggestionRepository
import com.example.reminderassistant.data.suggestion.ClipboardSuggestionRepositoryImpl
import com.example.reminderassistant.domain.repository.ReminderRepository
import com.example.reminderassistant.system.calendar.CalendarIntentLauncher
import com.example.reminderassistant.system.calendar.CalendarIntentLauncherImpl
import com.example.reminderassistant.system.clipboard.ClipboardMonitor
import com.example.reminderassistant.system.clipboard.ClipboardMonitorImpl
import com.example.reminderassistant.system.notification.ReminderScheduler
import com.example.reminderassistant.system.notification.ReminderSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        impl: ReminderSchedulerImpl
    ): ReminderScheduler

    @Binds
    @Singleton
    abstract fun bindCalendarIntentLauncher(
        impl: CalendarIntentLauncherImpl
    ): CalendarIntentLauncher

    @Binds
    @Singleton
    abstract fun bindClipboardMonitor(
        impl: ClipboardMonitorImpl
    ): ClipboardMonitor

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindClipboardSuggestionRepository(
        impl: ClipboardSuggestionRepositoryImpl
    ): ClipboardSuggestionRepository
}
