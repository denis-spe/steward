// Glory be to name of the Lord GOD of our LORD Jesus Christ
package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.services.AccountService
import com.den.steward.backend.services.StorageService
import javax.inject.Inject


class DataDeletionUseCase @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val goalToolUseCase: GoalToolUseCase
) {
    /**
     * Get the current user ID.
     */
    private val userId get() = accountService.currentUserId

    suspend fun deleteTransaction(
        transaction: Transaction
    ) {
        if (transaction is Transaction.Goal) {
            goalToolUseCase.cancel(transaction.id)
        }

        val transactionId = when (transaction) {
            is Transaction.Attain -> transaction.goal.id.ifBlank { null }
            is Transaction.Achievement -> transaction.goal.id.ifBlank { null }
            is Transaction.Settlement -> transaction.debt.id.ifBlank { null }
            is Transaction.Repayment -> transaction.lent.id.ifBlank { transaction.lentId.ifBlank { null } }
            is Transaction.PlanFulfillment -> transaction.plan.id.ifBlank { null }
            else -> null
        }
        val fulfillmentId = when (transaction) {
            is Transaction.Attain -> transaction.id.ifBlank { null }
            is Transaction.Achievement -> transaction.id.ifBlank { null }
            is Transaction.Settlement -> transaction.id.ifBlank { null }
            is Transaction.Repayment -> transaction.id.ifBlank { null }
            is Transaction.PlanFulfillment -> transaction.id.ifBlank { null }
            else -> null
        }

        val result = if (transactionId.isNullOrBlank() || fulfillmentId.isNullOrBlank()) {
            storageService.deleteTransaction(userId, transaction.id)
        } else {
            storageService.deleteFulfillment(userId, transactionId, fulfillmentId, transaction)
        }
        result.getOrThrow()
    }
}