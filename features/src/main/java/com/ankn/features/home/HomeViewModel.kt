package com.ankn.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ankn.core.domain.usecase.setting.GetReminderSettingsUseCase
import com.ankn.core.domain.usecase.setting.UpdateReminderSettingsUseCase
import com.ankn.core.domain.model.ReminderSettings
import com.ankn.core.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase,
    private val updateReminderSettingsUseCase: UpdateReminderSettingsUseCase,
    private val workManager: WorkManager
) : ViewModel() {

    private val _reminderSettings = MutableStateFlow(ReminderSettings(false, 1))
    open val reminderSettings: StateFlow<ReminderSettings> = _reminderSettings.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _reminderSettings.value = getReminderSettingsUseCase()
            setupReminderWork(_reminderSettings.value)
        }
    }

    fun updateSettings(isEnabled: Boolean, intervalMinutes: Int) {
        viewModelScope.launch {
            val newSettings = ReminderSettings(isEnabled, intervalMinutes)
            updateReminderSettingsUseCase(newSettings)
            _uiState.update { it.copy(
                isReminderEnabled = isEnabled,
                statusText = if (isEnabled) "Reminders are active" else "Reminders are inactive"
            ) }
            setupReminderWork(newSettings)
        }
    }

    fun updateNotificationPermissionState(isGranted: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = isGranted) }
    }

    private fun setupReminderWork(settings: ReminderSettings) {
        if (settings.isEnabled) {
            val constraint = Constraints.Builder().apply {
                setRequiredNetworkType(NetworkType.CONNECTED)
            }.build()

            val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(
                settings.intervalMinutes.toLong(), TimeUnit.MINUTES
            ).apply {
                setConstraints(constraint)
            }.build()

            workManager.enqueueUniquePeriodicWork(
                "reminder_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                reminderWork
            )
        } else {
            workManager.cancelUniqueWork("reminder_work")
        }
    }
}