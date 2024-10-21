package com.ankn.core.di

import com.ankn.core.domain.repository.setting.ReminderRepository
import com.ankn.core.domain.usecase.setting.GetReminderSettingsUseCase
import com.ankn.core.domain.usecase.setting.UpdateReminderSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideGetReminderSettingsUseCase(repository: ReminderRepository): GetReminderSettingsUseCase {
        return GetReminderSettingsUseCase(repository)
    }

    @Provides
    fun provideUpdateReminderSettingsUseCase(repository: ReminderRepository): UpdateReminderSettingsUseCase {
        return UpdateReminderSettingsUseCase(repository)
    }
}