package com.ankn.features.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ankn.core.domain.usecase.setting.GetReminderSettingsUseCase
import com.ankn.core.domain.usecase.setting.UpdateReminderSettingsUseCase
import com.ankn.core.domain.model.ReminderSettings
import com.ankn.core.domain.util.ResourceProvider
import com.ankn.core.util.NotificationHelper
import com.ankn.core.worker.ReminderWorker
import com.ankn.core.worker.ReminderWorker.Companion.KEY_DELAYED_VIBRATION_TIME_MS
import com.ankn.core.worker.ReminderWorker.Companion.KEY_NOTIFICATION_DESCRIPTION
import com.ankn.core.worker.ReminderWorker.Companion.KEY_NOTIFICATION_NAME
import com.ankn.features.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase,
    private val updateReminderSettingsUseCase: UpdateReminderSettingsUseCase,
    private val workManager: WorkManager,
    private val notificationHelper: NotificationHelper,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        viewModelScope.launch {
            try {
                val initialSettings = getReminderSettingsUseCase()
                updateUiState(initialSettings)
            } catch (e: Exception) {
                // Handle error (e.g., log it or update UI state with error message)
            }
        }
    }

    fun updateSettings(isEnabled: Boolean, intervalMinutes: Int) {
        viewModelScope.launch {
            try {
                val newSettings = ReminderSettings(isEnabled, intervalMinutes)
                updateReminderSettingsUseCase(newSettings)
                updateUiState(newSettings)
                if (_uiState.value.hasNotificationPermission) {
                    setupReminderWork(newSettings)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateNotificationPermissionState(isGranted: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                hasNotificationPermission = isGranted,
                statusText = getStatusText(currentState.isReminderEnabled, isGranted)
            )
        }
        if (isGranted && _uiState.value.isReminderEnabled) {
            setupReminderWork(_uiState.value.reminderSettings)
        } else if (!isGranted) {
            workManager.cancelUniqueWork("reminder_work")
        }
    }

    private fun updateUiState(settings: ReminderSettings) {
        _uiState.update { currentState ->
            currentState.copy(
                reminderSettings = settings,
                isReminderEnabled = settings.isEnabled,
                statusText = getStatusText(
                    settings.isEnabled,
                    currentState.hasNotificationPermission
                )
            )
        }
    }

    private fun getStatusText(isEnabled: Boolean, hasPermission: Boolean): String {
        return when {
            isEnabled && hasPermission -> "Reminders are active"
            isEnabled -> "Reminders are inactive (Permission required)"
            else -> "Reminders are inactive"
        }
    }

    private fun setupReminderWork(settings: ReminderSettings) {
        if (settings.isEnabled) {
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val inputData = Data.Builder()
                .putString(KEY_NOTIFICATION_NAME, resourceProvider.getString(R.string.notification_title))
                .putString(KEY_NOTIFICATION_DESCRIPTION, resourceProvider.getString(R.string.notification_description))
                .putLong(KEY_DELAYED_VIBRATION_TIME_MS, 10000L)
                .build()

            val initialDelay = settings.intervalMinutes.toLong()

            val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(
                initialDelay, TimeUnit.MINUTES
            )
                .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .setInputData(inputData)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "reminder_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                reminderWork
            )
        } else {
            workManager.cancelUniqueWork("reminder_work")
        }
    }

    fun showNotificationTest() {
        notificationHelper.createNotificationAndNotify(
            notificationName = resourceProvider.getString(R.string.notification_title),
            notificationDescription = resourceProvider.getString(R.string.notification_description),
            10000L
        )
    }
}
