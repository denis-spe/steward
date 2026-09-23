
package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.services.service.Account
import com.den.steward.backend.services.service.Storage
import javax.inject.Inject

class UpdateDateUseCase @Inject constructor(
    accountService: Account,
    private val storageService: Storage,
) {
    private val user = accountService.currentUserId

    suspend fun updateTransactionFulfillment(
        transactionId: String,
        oldFulfillment: String,
        newFulfillment: Transaction,
    ) {
        storageService.updateTransactionFulfillment(
            user,
            transactionId = transactionId,
            oldFulfillmentId = oldFulfillment,
            newFulfillment = newFulfillment,
        )
    }

    suspend fun updateTransaction(
        transactionId: String,
        newTransaction: Transaction,
    ) {
        storageService.updateTransaction(
            user,
            transactionId = transactionId,
            newTransaction = newTransaction,
        )
    }
}