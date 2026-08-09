package com.den.steward.backend.repoInterfaces

import com.den.steward.backend.dataStructure.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface Storage {
    val firestore: FirebaseFirestore;
    fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>>
    fun fetchTransactionFulfillment(
        userId: String,
        transaction: Transaction
    ): Flow<Result<List<Transaction>>>

    suspend fun addTransaction(userId: String, transaction: Transaction): Result<Unit>
    suspend fun addFulfillment(userId: String, transactionId: String, fulfillment: Transaction): Result<Unit>
}
