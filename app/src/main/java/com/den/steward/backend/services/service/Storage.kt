package com.den.steward.backend.services.service

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface Storage {
    val firestore: FirebaseFirestore;
    fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>>

    suspend fun addTransaction(userId: String, transaction: Transaction): Result<String>
    suspend fun addFulfillment(userId: String, transactionId: String, fulfillment: Transaction): Result<String>
    suspend fun resetGoalAttain(userId: String, transaction: Transaction.Goal): Result<Transaction.Goal>
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
        fulfillmentType: Transaction
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

    suspend fun deletingMultipleTransaction(
        userId: String,
        transactionIds: List<String>
    ): Result<Unit>

    fun fetchTransactionFulfillment(
        userId: String,
        transaction: Transaction,
        fulfillmentType: TransactionType?
    ): Flow<Result<List<Transaction>>>
}
