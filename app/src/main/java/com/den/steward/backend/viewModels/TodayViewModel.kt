package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.LiabilitiesPaymentStatsState
import com.den.steward.backend.states.TodayUiState
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
class TodayViewModel @Inject constructor(
    dataFilterUseCase: DataFilterUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {
    private val _todayUiState = MutableStateFlow(TodayUiState())
    val todayUiState = _todayUiState.asStateFlow()

    val todayTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.todayTransactions
        .combine(_todayUiState) { state, uiState ->
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

    val todaySummaryTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.todayTransactions
        .combine(_todayUiState) { state, _ ->
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

    val currentAmountForDifferentMethods: StateFlow<DataState<Map<PaymentMethod, Double>>> = dataFetchUseCase.fetchAllTransactions
        .map { state ->
            when (state) {
                is DataState.Success -> {
                    val currentAmountMap = mutableMapOf<PaymentMethod, Double>()

                    state.data.forEach { transaction ->
                        if (transaction.getAffectAmount == "Yes") {
                            val method = transaction.getPaymentMethodOrNull ?: return@forEach
                            val amount = transaction.getAmountOrValue ?: 0.0
                            val current = currentAmountMap.getOrDefault(method, 0.0)

                            val updated = when (transaction.type) {
                                TransactionType.EARNINGS,
                                TransactionType.SAVINGS,
                                TransactionType.DEBT,
                                TransactionType.REPAYMENT -> current + amount

                                TransactionType.EXPENSE,
                                TransactionType.LENT,
                                TransactionType.SETTLEMENT -> current - amount

                                else -> current
                            }
                            currentAmountMap[method] = updated
                        }
                    }

                    DataState.Success(currentAmountMap)
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

    val liabilitiesPaymentStats: StateFlow<DataState<LiabilitiesPaymentStatsState>> = dataFetchUseCase.fetchAllTransactions
        .map { state ->
            when (state) {
                is DataState.Success -> {
                    var totalLoan = 0.0
                    var totalDebt = 0.0
                    var unPaidLoan = 0.0
                    var unPaidDebt = 0.0
                    var paidCount = 0.0
                    var unPaidCount = 0.0

                    state.data.forEach { transaction ->
                        totalLoan += when (transaction) {
                            is Transaction.Lent -> transaction.amount
                            else -> 0.0
                        }
                        totalDebt += when (transaction) {
                            is Transaction.Debt -> transaction.amount
                            else -> 0.0
                        }

                        unPaidLoan += when (transaction) {
                            is Transaction.Lent -> transaction.remainingAmount
                            else -> 0.0
                        }
                        unPaidDebt += when (transaction) {
                            is Transaction.Debt -> transaction.remainingAmount
                            else -> 0.0
                        }

                        paidCount += when (transaction) {
                            is Transaction.Debt -> if (transaction.remainingAmount == 0.0) 1.0 else 0.0
                            is Transaction.Lent -> if (transaction.remainingAmount == 0.0) 1.0 else 0.0
                            else -> 0.0
                        }

                        unPaidCount += when (transaction) {
                            is Transaction.Debt -> if (transaction.remainingAmount != 0.0) 1.0 else 0.0
                            is Transaction.Lent -> if (transaction.remainingAmount != 0.0) 1.0 else 0.0
                            else -> 0.0
                        }
                    }

                    val liabilitiesPayAmount = LiabilitiesPaymentStatsState(
                        totalLoan = totalLoan,
                        totalDebt = totalDebt,
                        unPaidLoan = unPaidLoan,
                        unPaidDebt = unPaidDebt,
                        paidCount = paidCount,
                        unPaidCount = unPaidCount
                    )

                    DataState.Success(liabilitiesPayAmount)
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
                    TransactionType.SETTLEMENT -> outgoing += amount
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
            Filter.SETTLEMENT -> TransactionType.SETTLEMENT
            Filter.ATTAIN -> TransactionType.ATTAIN
            Filter.LENT -> TransactionType.LENT
            Filter.DEBT -> TransactionType.DEBT
            Filter.ALL -> null
        }
    }

    fun updateFilter(filter: Filter) {
        _todayUiState.value = _todayUiState.value.copy(
            filter = filter,
            isFilterExpanded = false
        )
    }

    fun updateIsFilterExpanded(isExpanded: Boolean) {
        _todayUiState.value = _todayUiState.value.copy(
            isFilterExpanded = isExpanded
        )
    }

    fun updateSortBy(sortBy: SortBy) {
        _todayUiState.value = _todayUiState.value.copy(
            sortBy = sortBy,
            isSortByExpanded = false
        )
    }
    fun updateIsSortByExpanded(isExpanded: Boolean) {
        _todayUiState.value = _todayUiState.value.copy(
            isSortByExpanded = isExpanded
        )
    }

    fun updateOrderBy(orderBy: OrderBy) {
        _todayUiState.value = _todayUiState.value.copy(
            orderBy = orderBy,
            isOrderByExpanded = false
        )
    }

    fun updateIsOrderExpanded(isExpanded: Boolean) {
        _todayUiState.value = _todayUiState.value.copy(
            isOrderByExpanded = isExpanded
        )
    }
}
