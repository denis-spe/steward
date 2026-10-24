package com.den.steward.backend.useCase

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.den.steward.backend.entitles.Recurrence
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.services.service.Account
import com.den.steward.backend.services.service.Storage
import com.den.steward.backend.workers.GoalWorker
import dagger.hilt.android.qualifiers.ApplicationContext
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

    suspend fun resetGoalAttain(transaction: Transaction.Goal): Result<Transaction.Goal> {
        return storageService.resetGoalAttain(userId, transaction)
    }

    fun schedule(transactionId: String, endAtMillis: Long, recurrence: Recurrence) {
        Log.d(
            TAG,
            "Attempting to schedule worker for Goal: $transactionId at $endAtMillis, recurrence=$recurrence"
        )
        GoalWorker.schedule(
            context = context,
            transactionId = transactionId,
            endAtMillis = endAtMillis,
            recurrence = recurrence,
            policy = ExistingWorkPolicy.REPLACE
        )
    }

    fun cancel(transactionId: String) {
        Log.d(TAG, "Cancelling scheduled worker for Goal: $transactionId")
        WorkManager.getInstance(context).cancelUniqueWork(transactionId)
    }
}
