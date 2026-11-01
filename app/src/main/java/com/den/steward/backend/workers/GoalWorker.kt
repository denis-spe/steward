// Glory be to name of LORD GOD
package com.den.steward.backend.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.den.steward.R
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Recurrence
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.notification.NotificationEntity
import com.den.steward.backend.notification.NotificationSource
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.GoalToolUseCase
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@HiltWorker
class GoalWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val goalToolUseCase: GoalToolUseCase,
    private val dataFetchUseCase: DataFetchUseCase,
    private val notificationSource: NotificationSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "GoalWorker"
        private const val TRANSACTION_ID_KEY = "transactionId"

        private fun enqueueAt(
            context: Context,
            transactionId: String,
            targetEpochMillis: Long,
            policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
        ) {
            val delayMillis = (targetEpochMillis - System.currentTimeMillis())
                .coerceAtLeast(1_000L)

            val request = OneTimeWorkRequestBuilder<GoalWorker>()
                .setInputData(workDataOf(TRANSACTION_ID_KEY to transactionId))
                .addTag("goal_worker_${transactionId}_${targetEpochMillis}")
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                transactionId,
                policy,
                request
            )

            Log.d(
                TAG,
                "Scheduled goal=$transactionId at=${targetEpochMillis}ms; " +
                        "delay=${delayMillis / 1000}s; policy=$policy"
            )
        }

        /** Pure and unit-testable. Null means "nothing to schedule". */
        internal fun nextTriggerMillis(
            endAtMillis: Long,
            recurrence: Recurrence,
            now: Long = System.currentTimeMillis(),
            zone: ZoneId = ZoneId.systemDefault()
        ): Long? {
            if (endAtMillis > now) return endAtMillis      // deadline still in the future
            if (endAtMillis <= 0L) return null             // no deadline set

            val anchor = LocalDateTime.ofInstant(Instant.ofEpochMilli(endAtMillis), zone)

            // Prevent schedule drift by projecting strictly from anchor
            return recurrence.next(anchor, anchor)
                ?.atZone(zone)
                ?.toInstant()
                ?.toEpochMilli()
        }

        fun schedule(
            context: Context,
            transactionId: String,
            endAtMillis: Long,
            recurrence: Recurrence,
            policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE
        ) {
            val target = nextTriggerMillis(endAtMillis, recurrence) ?: run {
                Log.w(
                    TAG,
                    "No valid next occurrence for goal=$transactionId, recurrence=$recurrence"
                )
                return
            }

            Log.w(TAG, "Scheduled to happen at ${target.toLocalDateTime().formatedDateTime}")
            enqueueAt(context, transactionId, target, policy)
        }

        private fun scheduleAtDeadline(
            context: Context,
            transactionId: String,
            deadlineEpochMillis: Long
        ) {
            enqueueAt(
                context = context,
                transactionId = transactionId,
                targetEpochMillis = deadlineEpochMillis,
                policy = ExistingWorkPolicy.REPLACE
            )
        }
    }

    override suspend fun doWork(): Result {
        val transactionId = inputData.getString(TRANSACTION_ID_KEY)
        if (transactionId.isNullOrBlank()) {
            Log.e(TAG, "transactionId is missing")
            return Result.failure()
        }

        Log.i(TAG, "GoalWorker started for ID=$transactionId")

        return try {
            val fetched = dataFetchUseCase.getTransaction(transactionId)
                .getOrElse { throw it }

            if (fetched == null) {
                Log.e(TAG, "Transaction not found (deleted or missing): $transactionId")
                return Result.failure()
            }

            val goal = fetched as? Transaction.Goal ?: run {
                Log.e(TAG, "Transaction is missing or is not a Goal: $transactionId")
                return Result.failure()
            }

            val now = System.currentTimeMillis()
            if (goal.endAt > now + 1000L) {
                // Protect against early/stale work. The goal has not reached its deadline.
                scheduleAtDeadline(appContext, transactionId, goal.endAt)
                return Result.success()
            }

            // Guard against duplicate processing if this cycle was already processed
            val alreadyProcessed = goal.achievement.any { it.endAt == goal.endAt }
            if (alreadyProcessed) {
                Log.i(
                    TAG,
                    "Goal cycle for $transactionId at endAt=${goal.endAt} was already processed."
                )
                WorkManager.getInstance(appContext).cancelUniqueWork(transactionId)
                return Result.success()
            }

            // Execute atomic backend update prior to notifications
            val updatedGoal = goalToolUseCase.resetGoalAttain(goal).getOrThrow()

            // Compute actual achievement metrics from current goal data
            val totalAttain = goal.percentage.toInt()
            val finalStatus = if (totalAttain >= 100) GoalStatus.COMPLETED else GoalStatus.FAILED

            val message = when (finalStatus) {
                GoalStatus.COMPLETED -> appContext.getString(R.string.goal_achieved_message)
                GoalStatus.FAILED -> appContext.getString(R.string.goal_failed_message)
                else -> appContext.getString(R.string.goal_cycle_ended_message)
            }

            val bigMessage = when (finalStatus) {
                GoalStatus.COMPLETED -> appContext.getString(
                    R.string.goal_achieved_big_message,
                    totalAttain
                )
                GoalStatus.FAILED -> {
                    if (totalAttain == 0) {
                        appContext.getString(R.string.goal_failed_zero_attain)
                    } else {
                        appContext.getString(R.string.goal_failed_partial_attain, totalAttain)
                    }
                }
                else -> null
            }

            val notificationTitle =
                if (goal.label.isNotBlank()) "${goal.label.title} Goal Cycle" else "Goal Cycle"

            // Dispatch notification after state change succeeds
            notificationSource.showNotification(
                NotificationEntity(
                    title = notificationTitle,
                    message = message,
                    bigMessage = bigMessage,
                    icon = R.drawable.ic_launcher_foreground_sprout,
                    largeIcon = goal.selectedIcon
                )
            )

            if (updatedGoal.repeatable != Recurrence.Once) {
                schedule(
                    context = appContext,
                    transactionId = transactionId,
                    endAtMillis = updatedGoal.endAt,
                    recurrence = updatedGoal.repeatable,
                    policy = ExistingWorkPolicy.REPLACE
                )
            } else {
                WorkManager.getInstance(appContext).cancelUniqueWork(transactionId)
            }

            Log.i(TAG, "Goal cycle processed successfully: $transactionId")
            Result.success()
        } catch (e: kotlinx.coroutines.CancellationException) {
            Log.i(TAG, "GoalWorker cancelled for $transactionId")
            throw e
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Firestore error while processing goal=$transactionId, code=${e.code}", e)
            when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND,
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.failure()

                else -> Result.retry()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Network IO error while processing goal=$transactionId, retrying...", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process goal=$transactionId, retrying work...", e)
            Result.retry()
        }
    }
}