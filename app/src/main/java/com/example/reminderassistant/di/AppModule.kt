package com.example.reminderassistant.di

import com.example.reminderassistant.data.repository.ReminderRepositoryImpl
import com.example.reminderassistant.domain.repository.ReminderRepository
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
}
