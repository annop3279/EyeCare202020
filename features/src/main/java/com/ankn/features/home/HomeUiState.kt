package com.ankn.features.home

data class HomeUiState(
    val isReminderEnabled: Boolean = false,
    val statusText: String = "Reminders are inactive",
    val hasNotificationPermission: Boolean = false
)