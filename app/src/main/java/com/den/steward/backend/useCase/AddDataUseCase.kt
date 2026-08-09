package com.den.steward.backend.useCase

import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.repoInterfaces.Storage
import javax.inject.Inject

class AddDataUseCase @Inject constructor(
    private val accountService: Account,
    private val storageService: Storage
) {
    val userId = accountService.currentUserId

    suspend fun addFulfillment(transactionId: String, fulfillment: Transaction) {
        storageService.addFulfillment(userId, transactionId, fulfillment)
    }

    suspend fun addTransaction(transaction: Transaction) {
        storageService.addTransaction(userId, transaction)
    }
}