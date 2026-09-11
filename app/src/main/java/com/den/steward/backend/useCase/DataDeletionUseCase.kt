// Glory be to name of the Lord GOD of our LORD Jesus Christ
package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
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
        transactionId: String,
    ) {
        storageService.deleteTransaction(userId, transactionId)
    }

    suspend fun deleteFulfillment(
        transactionId: String,
        fulfillmentId: String,
        fulfillmentType: Transaction
    ) {
        storageService.deleteFulfillment(userId, transactionId, fulfillmentId, fulfillmentType)
    }
}