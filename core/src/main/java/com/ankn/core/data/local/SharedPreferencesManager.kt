package com.ankn.core.data.local

import android.content.Context
import com.ankn.core.domain.model.ReminderSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

    fun getReminderSettings(): ReminderSettings {
        val isEnabled = prefs.getBoolean("is_enabled", false)
        val intervalMinutes = prefs.getInt("interval_minutes", 20)
        return ReminderSettings(isEnabled, intervalMinutes)
    }

    fun updateReminderSettings(settings: ReminderSettings) {
        prefs.edit()
            .putBoolean("is_enabled", settings.isEnabled)
            .putInt("interval_minutes", settings.intervalMinutes)
            .apply()
    }
}