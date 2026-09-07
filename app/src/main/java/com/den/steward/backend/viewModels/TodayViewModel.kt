package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.TodayUiState
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.Sort
import com.den.steward.backend.useCase.SortType
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

                    val comparator = when (uiState.sortType) {
                        SortType.DATE -> compareBy<Transaction> { it.createdAt }
                        SortType.AMOUNT -> compareBy { it.getAmountOrValue ?: 0.0 }
                        SortType.NAME -> compareBy { it.getLabel.lowercase() }
                        SortType.FULFILLED -> compareBy { transaction ->
                            when (transaction) {
                                is Transaction.Lent -> transaction.remainingAmount
                                is Transaction.Debt -> transaction.remainingAmount
                                is Transaction.Goal -> transaction.remainingValue
                                else -> 0.0
                            }
                        }
                    }

                    val finalComparator = if (uiState.sort == Sort.ASCENDING) {
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
                                TransactionType.REFUND -> current - amount

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
}
