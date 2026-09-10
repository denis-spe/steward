// Glory be to LORD our GOD
package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.services.service.Account
import com.den.steward.backend.services.service.Storage
import com.den.steward.backend.states.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
        .distinctUntilChanged()
        .map { result ->
            val originalTransactions = result.getOrThrow()

            val dataList = buildList {
                originalTransactions.forEach { transaction ->
                    add(transaction)
                    when (transaction) {
                        is Transaction.Lent -> {
                            transaction.repayment.forEach { add(it.copy(lent = transaction)) }
                        }
                        is Transaction.Debt -> {
                            transaction.refund.forEach { add(it.copy(debt = transaction)) }
                        }
                        is Transaction.Goal -> {
                            transaction.attain.forEach { add(it.copy(goal = transaction)) }
                        }
                        else -> {}
                    }
                }
            }

            DataState.Success(dataList) as DataState<List<Transaction>>
        }
        .catch { e ->
            // 4. Provide a fallback for null messages
            emit(DataState.Error(e.message ?: "An unknown error occurred"))
        }.flowOn(Dispatchers.IO)

    suspend fun getTransaction(transactionId: String): Result<Transaction?> {
        return storageService.getTransaction(userId, transactionId)
    }
}