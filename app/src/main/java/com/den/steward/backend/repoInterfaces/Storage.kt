package com.den.steward.backend.repoInterfaces

import com.den.steward.backend.dataStructure.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface Storage {
    val firestore: FirebaseFirestore;
    suspend fun addLoan(userId: String, loan: Transaction.Loan): Result<Unit>
    fun fetchAllTransactions(userId: String): Flow<Result<List<Transaction>>>
    suspend fun addRepayment(userId: String, loanId: String, repayment: Transaction.Repayment): Result<Unit>
    fun fetchTransactionFulfillment(
        userId: String,
        transaction: Transaction
    ): Flow<Result<List<Transaction>>>

    suspend fun addEarnings(userId: String, earnings: Transaction.Earning): Result<Unit>
}
