// Glory be to LORD our GOD
package com.den.steward.backend.useCase

import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.repoInterfaces.Storage
import com.den.steward.backend.states.DataState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataFetchUseCase @Inject constructor(
    accountService: Account,
    storageService: Storage
) {
    private val userId = accountService.currentUserId

    val fetchAllTransactions = storageService.fetchAllTransactions(userId)
        .map { result ->
            // 1. Rename lambda parameter to 'result' to avoid shadowing
            val originalTransactions = result.getOrThrow()

            val dataList = originalTransactions.flatMap { transaction ->
                // 2. Capture the sub-items from the 'when' statement
                val subItems = when (transaction) {
                    is Transaction.Loan -> transaction.repayment
                    is Transaction.Debt -> transaction.refund
                    is Transaction.TargetAmount -> transaction.attain
                    is Transaction.CountTarget -> transaction.attain
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
}