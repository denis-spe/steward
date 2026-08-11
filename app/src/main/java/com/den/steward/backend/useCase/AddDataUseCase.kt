package com.den.steward.backend.useCase

import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.repoInterfaces.Storage
import com.den.steward.backend.viewModels.DataTransferToViewModel
import javax.inject.Inject

class AddDataUseCase @Inject constructor(
    private val accountService: Account,
    private val storageService: Storage
) {
    val userId = accountService.currentUserId

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
                paymentMethod = dataTransferToViewModel.paymentMethod
            )
            TransactionType.EXPENSE -> Transaction.Expense(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod
            )
            TransactionType.LOAN -> Transaction.Loan(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod
            )
            TransactionType.DEBT -> Transaction.Debt(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod
            )
            TransactionType.SAVINGS -> Transaction.Savings(
                label = dataTransferToViewModel.label,
                amount = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                paymentMethod = dataTransferToViewModel.paymentMethod
            )
            TransactionType.GOAL -> Transaction.Goal(
                label = dataTransferToViewModel.label,
                value = amount,
                note = dataTransferToViewModel.note,
                createdAt = dataTransferToViewModel.createdAt,
                startedAt = dataTransferToViewModel.startedAt,
                endAt = dataTransferToViewModel.endAt // Will be updated if recurrence is added later
            )
            else -> null
        } ?: return

        storageService.addTransaction(userId, transaction)
    }
}