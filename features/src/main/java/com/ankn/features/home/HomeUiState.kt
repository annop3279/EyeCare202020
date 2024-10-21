package com.ankn.features.home

import com.ankn.core.domain.model.ReminderSettings

data class HomeUiState(
    val reminderSettings: ReminderSettings = ReminderSettings(false, 15),
    val isReminderEnabled: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val statusText: String = "Reminders are inactive"
)