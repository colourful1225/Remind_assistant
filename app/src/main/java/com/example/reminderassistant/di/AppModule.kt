package com.example.reminderassistant.di

import android.content.Context
import com.example.reminderassistant.data.settings.SettingsRepository
import com.example.reminderassistant.data.settings.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideApplicationContext(
            @ApplicationContext context: Context
        ): Context = context
    }
}
