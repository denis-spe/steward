package com.den.steward.backend.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.notification.NotificationEntity
import com.den.steward.backend.notification.NotificationSource
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.GoalToolUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GoalWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val goalToolUseCase: GoalToolUseCase,
    private val dataFetchUseCase: DataFetchUseCase,
    private val notificationSource: NotificationSource
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
            goalToolUseCase.addGoalAchieved(goal).getOrThrow()

            // 2. Reset the goal by clearing the goal attain list and updating timestamps
            val updatedGoal = goalToolUseCase.resetGoalAttain(goal).getOrThrow()

            // 3. Re-schedule if repeatable
            if (goal.repeatable != com.den.steward.backend.entitles.RecurrencePattern.NONE) {
                goalToolUseCase.schedule(transactionId, updatedGoal)
            }

             // 4. Display the notification
            notificationSource.showNotification(
                NotificationEntity(
                    title = "Goal Achieved",
                    message = when(data.status) {
                        GoalStatus.NOT_STARTED -> "Your goal has not started yet"
                        GoalStatus.IN_PROGRESS -> "You are still in progress"
                        GoalStatus.COMPLETED -> "You achieved your goal"
                        GoalStatus.FAILED -> "You failed to achieve your goal"
                    },
                    icon = data.type.icon,
                    largeIcon = data.selectedIcon
                )
            )

            Log.d(TAG, "Doing work completed successfully for $transactionId")
            Result.success()
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                Log.i(TAG, "GoalWorker for $transactionId was cancelled")
                throw e
            }
            Log.e(TAG, "Error occurred while doing work for $transactionId", e)
            Result.failure()
        }
    }
}