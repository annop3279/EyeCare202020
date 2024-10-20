package com.ankn.core.domain.repository.setting

import com.ankn.core.domain.model.ReminderSettings

interface ReminderRepository {
    suspend fun getReminderSettings(): ReminderSettings
    suspend fun updateReminderSettings(settings: ReminderSettings)
}