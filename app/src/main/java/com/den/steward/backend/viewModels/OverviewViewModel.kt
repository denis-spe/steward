// Love the LORD your GOD with all your heart and with all soul and with all your might
// and love your neighbor as your self
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.OverviewUiState
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    private val _overviewUiState = MutableStateFlow(OverviewUiState())
    val overviewUiState = _overviewUiState.asStateFlow()

    val groupedTransactions: StateFlow<DataState<Map<String, List<Transaction>>>> = dataFetchUseCase.fetchAllTransactions
        .combine(_overviewUiState) { state, uiState ->
            when (state) {
                is DataState.Success -> {
                    val groupedData: Map<String, List<Transaction>> = state.data.groupBy { transaction ->
                        when (transaction.type) {
                            TransactionType.LENT -> "Lent"
                            TransactionType.DEBT -> "Debt"
                            TransactionType.GOAL -> "Goal"
                            else -> "Transactions"
                        }
                    }.toSortedMap(
                        compareBy { key ->
                            when (key) {
                                "Transactions" -> 0
                                "Goal" -> 1
                                "Debt" -> 2
                                "Lent" -> 3
                                else -> 4
                            }
                        }
                    ).mapValues { (_, transactions) ->
                        val comparator = when (uiState.sortBy) {
                            SortBy.TIME -> compareBy<Transaction> { it.createdAt }
                            SortBy.AMOUNT -> compareBy { it.getAmountOrValue ?: 0.0 }
                            SortBy.LABEL -> compareBy { it.getLabel.lowercase() }
                            SortBy.FULFILLED -> compareBy { transaction ->
                                when (transaction) {
                                    is Transaction.Lent -> transaction.remainingAmount
                                    is Transaction.Debt -> transaction.remainingAmount
                                    is Transaction.Goal -> transaction.remainingValue
                                    else -> 0.0
                                }
                            }
                        }

                        val finalComparator = if (uiState.orderBy == OrderBy.ASCENDING) {
                            comparator
                        } else {
                            comparator.reversed()
                        }

                        transactions.sortedWith(finalComparator)
                            .take(uiState.limitTransactionSize)
                    }
                    DataState.Success(groupedData)
                }

                is DataState.Error -> DataState.Error(state.message)
                is DataState.Loading -> DataState.Loading
            }
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    fun updateSort(orderBy: OrderBy) {
        _overviewUiState.update {
            it.copy(
                orderBy = orderBy
            )
        }
    }

    fun updateSortType(sortBy: SortBy) {
        _overviewUiState.update {
            it.copy(
                sortBy = sortBy
            )
        }
    }


}