package com.den.steward.backend.alarmSchedular

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import com.den.steward.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val ALARM_ACTION = "com.den.steward.ALARM_ACTION"
        const val EXTRA_TASK = "extra_task_id"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ${intent?.action}")
        
        context?.let {
            Toast.makeText(it, it.getString(R.string.app_name), Toast.LENGTH_SHORT).show()
        }

        if (intent?.action == ALARM_ACTION) {
            val powerManager = context?.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val wakelock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "steward:alarm_receiver_wakelock"
            )
            wakelock?.acquire(3000L)
        }
        
        GlobalScope.launch {
            handleIntent(intent)
        }
    }

    private suspend fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            ALARM_ACTION -> {
                val alarmId = getAlarmId(intent)
                Log.d(TAG, "Handling alarm for ID: $alarmId")
                // TODO: Perform required action and use case
            }
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "System event received: ${intent.action}. Rescheduling alarms if needed.")
            }
            else -> {
                Log.e(TAG, "Action not found: ${intent?.action}")
            }
        }
    }

    private fun getAlarmId(intent: Intent?) = intent?.getLongExtra(EXTRA_TASK, 0L) ?: 0L
}
