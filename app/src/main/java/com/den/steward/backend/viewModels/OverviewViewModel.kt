// Love the LORD your GOD with all your heart and with all soul and with all your might
// and love your neighbor as your self
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.OverviewUiState
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.overviewTabState.OverviewSummary
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.helper.calculateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class OverviewData(
    val summary: OverviewSummary,
    val groupedTransactions: Map<String, List<Transaction>>
)

@HiltViewModel
class OverviewViewModel @Inject constructor(
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    private val _overviewUiState = MutableStateFlow(OverviewUiState())
    val overviewUiState = _overviewUiState.asStateFlow()

    val overviewSummary: StateFlow<DataState<OverviewSummary>> =
        dataFetchUseCase.fetchAllTransactions
            .map { state ->
                when (state) {
                    is DataState.Success -> {
                        val transactions = state.data
                        val liabilities = listOf(
                            TransactionType.SETTLEMENT,
                            TransactionType.REPAYMENT,
                            TransactionType.DEBT,
                            TransactionType.LENT
                        )

                        val netFlow = transactions.filterNot { transaction ->
                            transaction.type in liabilities
                        }.calculateFlow

                        val liabilityFlow = transactions.filter { transaction ->
                            transaction.type in liabilities
                        }.calculateFlow

                        val goalAchieved = transactions.filter {
                            it is Transaction.Achievement &&
                                    it.status == GoalStatus.COMPLETED
                        }
                            .size
                        val goalFailed = transactions.filter {
                            it is Transaction.Achievement &&
                                    it.status == GoalStatus.FAILED
                        }
                            .size

                        DataState.Success(
                            OverviewSummary(
                                netFlow = netFlow.toString(),
                                liabilities = liabilityFlow.toString(),
                                goalAchieved = goalAchieved.toString(),
                                goalFailed = goalFailed.toString()
                            )
                        )

                    }

                    is DataState.Error -> DataState.Error(state.message)
                    is DataState.Loading -> DataState.Loading
                }
            }.flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DataState.Loading
            )

    val groupedTransactions: StateFlow<DataState<Map<String, List<Transaction>>>> = dataFetchUseCase.fetchAllTransactions
        .combine(_overviewUiState) { state, uiState ->
            when (state) {
                is DataState.Success -> {
                    val groupedData: Map<String, List<Transaction>> = state.data
                        .filter { transaction ->
                            when (transaction.type) {
                                TransactionType.ACHIEVEMENT,
                                TransactionType.ATTAIN,
                                TransactionType.PLAN_FULFILLMENT,
                                TransactionType.PLAN,
                                TransactionType.REPAYMENT,
                                TransactionType.SETTLEMENT -> false

                                else -> true
                            }
                        }
                        .groupBy { transaction ->
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

    val overviewData: StateFlow<DataState<OverviewData>> =
        combine(overviewSummary, groupedTransactions) { summaryState, transactionsState ->
            when {
                summaryState is DataState.Error -> DataState.Error(summaryState.message)
                transactionsState is DataState.Error -> DataState.Error(transactionsState.message)
                summaryState is DataState.Success && transactionsState is DataState.Success -> {
                    DataState.Success(
                        OverviewData(
                            summary = summaryState.data,
                            groupedTransactions = transactionsState.data
                        )
                    )
                }

                else -> DataState.Loading
            }
        }.distinctUntilChanged()
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