package com.den.steward.backend.alarmSchedular

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class AlarmScheduler @Inject constructor (
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "AlarmScheduler"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleAlarm(alarmId: Long, triggerAtMillis: Long) {
        val intentId = alarmId.toInt()
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ALARM_ACTION
            putExtra(AlarmReceiver.EXTRA_TASK, alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            intentId,
            alarmIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val currentTime = System.currentTimeMillis()
        if (triggerAtMillis <= currentTime) {
            Log.w(TAG, "It is not possible to set alarm in the past")
            return
        }

        alarmManager?.let {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || it.canScheduleExactAlarms()) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    it,
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.d(TAG, "Alarm scheduled for $alarmId at $triggerAtMillis")
            } else {
                Log.e(TAG, "Cannot schedule exact alarms: Permission missing")
                // Fallback or request permission
            }
        }
    }

    fun cancelAlarm(alarmId: Long) {
        val intentId = alarmId.toInt()
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ALARM_ACTION
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            intentId,
            alarmIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_NO_CREATE
            }
        )

        if (pendingIntent != null) {
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarm cancelled for $alarmId")
        }
    }
}
