package com.ankn.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ankn.core.domain.usecase.setting.GetReminderSettingsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getReminderSettingsUseCase: GetReminderSettingsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = getReminderSettingsUseCase()
        if (settings.isEnabled) {
            // Show notification
            showNotification()
            Log.d("ReminderWorker", "Reminder notification shown")
        }
        return Result.success()
    }

    private suspend fun showNotification() {
        /*withContext(Dispatchers.IO) {
            val notificationName = params.inputData.getString(KEY_NOTIFICATION_NAME)
            val notificationDescription = params.inputData.getString(KEY_NOTIFICATION_DESCRIPTION)
            if (notificationName != null && notificationDescription != null) {
                notificationHelper.createNotificationAndNotify(
                    notificationId = notificationHelper.generateNotificationRandomId(),
                    notificationName = notificationName,
                    notificationDescription = notificationDescription
                )
                Result.success()
            } else {
                Result.failure()
            }
        }*/
    }
}