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

    suspend fun addTransaction(userId: String, transaction: Transaction): Result<String>
    suspend fun addFulfillment(userId: String, transactionId: String, fulfillment: Transaction): Result<Unit>
    suspend fun resetGoalAttain(userId: String, transaction: Transaction.Goal): Result<Unit>
    suspend fun addGoalAchieved(userId: String, transaction: Transaction.Goal): Result<Unit>
    suspend fun getTransaction(userId: String, transactionId: String): Result<Transaction?>
    suspend fun deleteTransaction(userId: String, transactionId: String): Result<Unit>
    suspend fun getTransactionFulfillment(
        userId: String,
        transactionId: String,
        fulfillment: Transaction
    ): Result<Transaction?>

    suspend fun deleteFulfillment(
        userId: String,
        transactionId: String,
        fulfillmentId: String,
        fulfillmentType: Class<out Transaction>
    ): Result<Unit>

    suspend fun updateTransaction(
        userId: String,
        transactionId: String,
        newTransaction: Transaction
    ): Result<Unit>

    suspend fun updateTransactionFulfillment(
        userId: String,
        transactionId: String,
        oldFulfillmentId: String,
        newFulfillment: Transaction
    ): Result<Unit>
}
