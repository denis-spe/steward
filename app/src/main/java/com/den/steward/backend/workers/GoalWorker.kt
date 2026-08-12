package com.den.steward.backend.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.GoalToolUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GoalWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val goalToolUseCase: GoalToolUseCase,
    private val dataFetchUseCase: DataFetchUseCase
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val TAG = "GoalWorker"
    }

    override suspend fun doWork(): Result{
        val transactionId = inputData.getString("transactionId") ?: return Result.failure()

        return try {
            val data = dataFetchUseCase.getTransaction(transactionId)
                .getOrNull()

            val goal = data as? Transaction.Goal ?: run {
                Log.e(TAG, "Transaction is not a Goal: $transactionId")
                return Result.failure()
            }

            // 1. Add the achievement to the goal
            goalToolUseCase.addGoalAchieved(goal)

            // 2. Reset the goal by clearing the goal attain list
            goalToolUseCase.resetGoalAttain(goal)

            // 3. Re-schedule if repeatable
            if (goal.repeatable != com.den.steward.backend.dataStructure.RecurrencePattern.NONE) {
                goalToolUseCase.schedule(transactionId)
            }

            Log.d(TAG, "Doing work completed successfully for $transactionId")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred while doing work", e)
            Result.failure()
        }
    }
}