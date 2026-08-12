// Glory be to LORD our GOD
package com.den.steward.backend.useCase

import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.repoInterfaces.Storage
import com.den.steward.backend.states.DataState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


class DataFetchUseCase @Inject constructor(
    accountService: Account,
    private val storageService: Storage
) {
    private val userId = accountService.currentUserId

    val fetchAllTransactions = storageService.fetchAllTransactions(userId)
        // Safety net: guarantees this flow emits something immediately, even if the
        // upstream StorageService flow stalls for some other reason (a new/uncached
        // Firestore listener, a slow cold start, etc). Without this, a stall upstream
        // means the collector (ViewModel/UI) never receives ANY value — not success,
        // not error — and is left on its initial loading state indefinitely.
        .onStart { emit(Result.success(emptyList())) }
        .map { result ->
            // 1. Rename lambda parameter to 'result' to avoid shadowing
            val originalTransactions = result.getOrThrow()

            val dataList = originalTransactions.flatMap { transaction ->
                // 2. Capture the sub-items from the 'when' statement
                val subItems = when (transaction) {
                    is Transaction.Loan -> transaction.repayment.map { it.copy(loan = transaction) }
                    is Transaction.Debt -> transaction.refund.map { it.copy(debt = transaction) }
                    is Transaction.Goal -> transaction.attain.map { it.copy(goal = transaction) }
                    else -> emptyList()
                }

                // 3. Return a combined list of the parent transaction + its sub-items
                listOf(transaction) + subItems
            }

            DataState.Success(dataList) as DataState<List<Transaction>>
        }
        .catch { e ->
            // 4. Provide a fallback for null messages
            emit(DataState.Error(e.message ?: "An unknown error occurred"))
        }

    suspend fun getTransaction(transactionId: String): Result<Transaction?> {
        return storageService.getTransaction(userId, transactionId)
    }
}