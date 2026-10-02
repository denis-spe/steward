// Glory be to name of the Lord GOD of our LORD Jesus Christ
package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.services.AccountService
import com.den.steward.backend.services.StorageService
import javax.inject.Inject


class DataDeletionUseCase @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) {
    /**
     * Get the current user ID.
     */
    private val userId get() = accountService.currentUserId

    suspend fun deleteTransaction(
        transaction: Transaction
    ) {
        val transactionId = when(transaction.type) {
            TransactionType.ATTAIN -> (transaction as Transaction.Attain).goal.id
            TransactionType.ACHIEVEMENT -> (transaction as Transaction.Achievement).goal.id
            TransactionType.SETTLEMENT -> (transaction as Transaction.Settlement).debt.id
            TransactionType.REPAYMENT -> (transaction as Transaction.Repayment).lent.id
            TransactionType.PLAN_FULFILLMENT -> (transaction as Transaction.PlanFulfillment).plan.id
            else -> null
        }
        val fulfillmentId = when(transaction.type) {
            TransactionType.ATTAIN -> (transaction as Transaction.Attain).id
            TransactionType.ACHIEVEMENT -> (transaction as Transaction.Achievement).id
            TransactionType.SETTLEMENT -> (transaction as Transaction.Settlement).id
            TransactionType.REPAYMENT -> (transaction as Transaction.Repayment).id
            TransactionType.PLAN_FULFILLMENT -> (transaction as Transaction.PlanFulfillment).id
            else -> null
        }

        if (transactionId == null || fulfillmentId == null) {
            storageService.deleteTransaction(userId, transaction.id)
            return
        }
        storageService.deleteFulfillment(userId, transactionId, fulfillmentId, transaction)
    }
}