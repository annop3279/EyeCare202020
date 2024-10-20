package com.ankn.core.data.repository.setting

import com.ankn.core.data.local.SharedPreferencesManager
import com.ankn.core.domain.model.ReminderSettings
import com.ankn.core.domain.repository.setting.ReminderRepository
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : ReminderRepository {
    override suspend fun getReminderSettings(): ReminderSettings =
        sharedPreferencesManager.getReminderSettings()

    override suspend fun updateReminderSettings(settings: ReminderSettings) =
        sharedPreferencesManager.updateReminderSettings(settings)
}