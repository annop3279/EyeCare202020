package com.ankn.core.domain.usecase.setting

import com.ankn.core.domain.repository.setting.ReminderRepository
import com.ankn.core.domain.model.ReminderSettings
import javax.inject.Inject

class GetReminderSettingsUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(): ReminderSettings = repository.getReminderSettings()
}