package com.ankn.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ankn.core.domain.usecase.setting.GetReminderSettingsUseCase
import com.ankn.core.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val notificationName = params.inputData.getString(KEY_NOTIFICATION_NAME)
            val notificationDescription = params.inputData.getString(KEY_NOTIFICATION_DESCRIPTION)
            val delayedVibrationTimeMs = params.inputData.getLong(KEY_DELAYED_VIBRATION_TIME_MS, 30L)
            val settings = getReminderSettingsUseCase()
                if (notificationName != null && notificationDescription != null && settings.isEnabled) {
                    notificationHelper.createNotificationAndNotify(
                        notificationName = notificationName,
                        notificationDescription = notificationDescription,
                        delayedVibrationTimeMs = delayedVibrationTimeMs
                    )
                    Result.success()
                } else {
                    Result.failure()
                }
        }
    }

    companion object {
        const val KEY_NOTIFICATION_NAME = "KEY_NOTIFICATION_NAME"
        const val KEY_NOTIFICATION_DESCRIPTION = "KEY_NOTIFICATION_DESCRIPTION"
        const val KEY_DELAYED_VIBRATION_TIME_MS = "KEY_DELAYED_VIBRATION_TIME_MS"
    }
}
