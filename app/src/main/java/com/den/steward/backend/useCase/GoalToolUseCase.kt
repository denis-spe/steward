package com.den.steward.backend.useCase

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.repoInterfaces.Storage
import com.den.steward.backend.workers.GoalWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GoalToolUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    accountService: Account,
    private val storageService: Storage,
) {
    companion object {
        private const val TAG = "GoalToolUseCase"
    }
    val userId = accountService.currentUserId

    suspend fun addGoalAchieved(transaction: Transaction.Goal): Result<Unit> {
        return storageService.addGoalAchieved(userId, transaction)
    }

    suspend fun resetGoalAttain(transaction: Transaction.Goal): Result<Unit> {
        return storageService.resetGoalAttain(userId, transaction)
    }

    suspend fun schedule(transactionId: String, goal: Transaction.Goal? = null) {
        Log.d(TAG, "Attempting to schedule worker for Goal: $transactionId")

        // If goal is provided, use it directly. Otherwise fetch from storage.
        val transaction = goal ?: (storageService.getTransaction(userId, transactionId)
            .getOrNull() as? Transaction.Goal) ?: run {
                Log.e(TAG, "Failed to fetch Goal for scheduling: $transactionId")
                return
            }

        val now = System.currentTimeMillis()
        // Use a 1-second minimum delay for testing, but typically WorkManager handles delays > 10s better.
        val delay = (transaction.endAt - now).coerceAtLeast(1000L)

        Log.d(TAG, "Enqueuing Unique Work for $transactionId with delay ${delay/1000}s")

        val workRequest = OneTimeWorkRequestBuilder<GoalWorker>()
            .setInputData(
                workDataOf(
                    "transactionId" to transactionId
                )
            )
            .addTag("goal_worker_$transactionId")
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            transactionId,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d(TAG, "Goal $transactionId enqueued successfully. Run at: ${now + delay}")
    }
}
