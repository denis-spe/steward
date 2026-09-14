package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.YesterdayUiState
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class YesterdayViewModel @Inject constructor(
    dataFilterUseCase: DataFilterUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {
    private val _yesterdayUiState = MutableStateFlow(YesterdayUiState())
    val yesterdayUiState = _yesterdayUiState.asStateFlow()

    val yesterdayTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.yesterdayTransactions
        .combine(_yesterdayUiState) { state, uiState ->
            when (state) {
                is DataState.Success -> {
                    var transactions = state.data

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

                    val targetType = mapFilterToTransactionType(uiState.filter)
                    transactions = transactions.sortedWith(finalComparator)
                        .filter { targetType == null || it.type == targetType }

                    DataState.Success(transactions)
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

    val yesterdaySummaryTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.yesterdayTransactions
        .combine(_yesterdayUiState) { state, _ ->
            when (state) {
                is DataState.Success -> {
                    var transactions = state.data

                    DataState.Success(transactions)
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

    fun calculateFlow(transactions: List<Transaction>): Double {
        var incoming = 0.0
        var outgoing = 0.0

        transactions.forEach { transaction ->
            if (transaction.getAffectAmount == "Yes") {
                val amount = transaction.getAmountOrValue ?: 0.0
                when (transaction.type) {
                    TransactionType.EARNINGS,
                    TransactionType.SAVINGS,
                    TransactionType.DEBT,
                    TransactionType.REPAYMENT -> incoming += amount

                    TransactionType.EXPENSE,
                    TransactionType.LENT,
                    TransactionType.REFUND -> outgoing += amount
                    else -> {}
                }
            }
        }
        return incoming - outgoing
    }

    private fun mapFilterToTransactionType(filter: Filter): TransactionType? {
        return when (filter) {
            Filter.EARNINGS -> TransactionType.EARNINGS
            Filter.EXPENSE -> TransactionType.EXPENSE
            Filter.GOAL -> TransactionType.GOAL
            Filter.SAVINGS -> TransactionType.SAVINGS
            Filter.REPAYMENT -> TransactionType.REPAYMENT
            Filter.REFUND -> TransactionType.REFUND
            Filter.ATTAIN -> TransactionType.ATTAIN
            Filter.LENT -> TransactionType.LENT
            Filter.DEBT -> TransactionType.DEBT
            Filter.ALL -> null
        }
    }

    fun updateFilter(filter: Filter) {
        _yesterdayUiState.value = _yesterdayUiState.value.copy(
            filter = filter,
            isFilterExpanded = false
        )
    }

    fun updateIsFilterExpanded(isExpanded: Boolean) {
        _yesterdayUiState.value = _yesterdayUiState.value.copy(
            isFilterExpanded = isExpanded
        )
    }

    fun updateSortBy(sortBy: SortBy) {
        _yesterdayUiState.value = _yesterdayUiState.value.copy(
            sortBy = sortBy,
            isSortByExpanded = false
        )
    }
    fun updateIsSortByExpanded(isExpanded: Boolean) {
        _yesterdayUiState.value = _yesterdayUiState.value.copy(
            isSortByExpanded = isExpanded
        )
    }

    fun updateOrderBy(orderBy: OrderBy) {
        _yesterdayUiState.value = _yesterdayUiState.value.copy(
            orderBy = orderBy,
            isOrderByExpanded = false
        )
    }

    fun updateIsOrderExpanded(isExpanded: Boolean) {
        _yesterdayUiState.value = _yesterdayUiState.value.copy(
            isOrderByExpanded = isExpanded
        )
    }
}
