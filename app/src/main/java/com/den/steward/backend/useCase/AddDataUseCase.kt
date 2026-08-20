package com.den.steward.backend.useCase

import android.util.Log
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.services.service.Account
import com.den.steward.backend.services.service.Storage
import com.den.steward.backend.viewModels.DataTransferToViewModel
import javax.inject.Inject

class AddDataUseCase @Inject constructor(
    accountService: Account,
    private val storageService: Storage,
    private val goalToolUseCase: GoalToolUseCase
) {
    val userId = accountService.currentUserId

    companion object {
        private const val TAG = "AddDataUseCase"
    }

    suspend fun addFulfillment(transactionId: String, fulfillment: Transaction) {
        storageService.addFulfillment(userId, transactionId, fulfillment)
    }

    suspend fun addTransaction(dataTransferToViewModel: DataTransferToViewModel) {
        val amount = dataTransferToViewModel.amount.toDoubleOrNull() ?: 0.0
        val transaction = when (dataTransferToViewModel.transactionType) {
            TransactionType.EARNINGS -> Transaction.Earnings(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod,
                affectAmount = dataTransferToViewModel.isAffectingAmount ?: false
            )
            TransactionType.EXPENSE -> Transaction.Expense(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod,
                affectAmount = dataTransferToViewModel.isAffectingAmount ?: false
            )
            TransactionType.LENT -> Transaction.Lent(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod,
                affectAmount = dataTransferToViewModel.isAffectingAmount ?: false
            )
            TransactionType.DEBT -> Transaction.Debt(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod,
                affectAmount = dataTransferToViewModel.isAffectingAmount ?: false
            )
            TransactionType.SAVINGS -> Transaction.Savings(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod,
                affectAmount = dataTransferToViewModel.isAffectingAmount ?: false
            )
            TransactionType.GOAL -> Transaction.Goal(
                label = dataTransferToViewModel.label,
                value = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                startedAt = dataTransferToViewModel.startedAt,
                endAt = dataTransferToViewModel.endAt,
                repeatable = dataTransferToViewModel.repeatable,
            ).calculateStatus(System.currentTimeMillis())

            else -> null
        } ?: return

        val result = try {
            Log.d(TAG, "Starting addTransaction for goal: ${transaction.id}")
            storageService.addTransaction(userId, transaction)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in addTransaction", e)
            Result.failure(e)
        }
        
        if (result.isSuccess) {
            val transactionId = result.getOrThrow()
            Log.i(TAG, "Transaction saved successfully: $transactionId")
            if (transaction is Transaction.Goal) {
                Log.i(TAG, "Scheduling Goal worker for $transactionId")
                goalToolUseCase.schedule(transactionId, transaction)
            }
        } else {
            Log.e(TAG, "Failed to save transaction: ${result.exceptionOrNull()?.message}")
        }
    }
}