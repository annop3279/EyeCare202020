package com.ankn.core.di

import android.content.Context
import com.ankn.core.data.local.SharedPreferencesManager
import com.ankn.core.data.repository.setting.ReminderRepositoryImpl
import com.ankn.core.domain.repository.setting.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideReminderRepository(sharedPreferencesManager: SharedPreferencesManager): ReminderRepository {
        return ReminderRepositoryImpl(sharedPreferencesManager)
    }
}
