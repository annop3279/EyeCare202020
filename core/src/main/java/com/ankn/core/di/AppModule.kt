package com.ankn.core.di

import android.content.Context
import androidx.work.WorkManager
import com.ankn.core.data.local.SharedPreferencesManager
import com.ankn.core.data.repository.setting.ReminderRepositoryImpl
import com.ankn.core.domain.repository.setting.ReminderRepository
import com.ankn.core.domain.usecase.setting.GetReminderSettingsUseCase
import com.ankn.core.domain.usecase.setting.UpdateReminderSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
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

    @Provides
    fun provideGetReminderSettingsUseCase(repository: ReminderRepository): GetReminderSettingsUseCase {
        return GetReminderSettingsUseCase(repository)
    }

    @Provides
    fun provideUpdateReminderSettingsUseCase(repository: ReminderRepository): UpdateReminderSettingsUseCase {
        return UpdateReminderSettingsUseCase(repository)
    }

 /*   @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context) = NotificationHelper(context)*/

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}
