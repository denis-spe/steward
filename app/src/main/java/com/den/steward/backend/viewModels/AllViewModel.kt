package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.AllUiState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import com.den.steward.helper.formattedDate
import com.den.steward.helper.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.temporal.IsoFields
import javax.inject.Inject

@HiltViewModel
class AllViewModel @Inject constructor(
    private val periodDataHandleUseCase: PeriodDataHandleUseCase
) : ViewModel() {
    private val _allUiState = MutableStateFlow(AllUiState())
    val allUiState = _allUiState.asStateFlow()

    fun getWeekDaysForPage(page: Int): List<LocalDate> {
        return periodDataHandleUseCase.getWeekDaysForPage(
            page = page
        )
    }

    fun onPageChange(page: Int) {
        val localDates = getWeekDaysForPage(page)
        _allUiState.update {
            it.copy(
                selectedDate = localDates[0],
                periodType = PeriodType.WEEK,
                weekNumber = localDates[0].get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
            )
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        _allUiState.update {
            it.copy(
                selectedDate = date,
                periodType = PeriodType.DAY
            )
        }
    }

    fun updatePeriodType(periodType: PeriodType) {
        _allUiState.update {
            it.copy(
                periodType = periodType
            )
        }
    }

    fun updateSort(orderBy: OrderBy) {
        _allUiState.update {
            it.copy(
                orderBy = orderBy
            )
        }
    }

    fun updateFilter(filter: Filter) {
        _allUiState.update {
            it.copy(
                filter = filter
            )
        }
    }

    fun updateSortType(sortBy: SortBy) {
        _allUiState.update {
            it.copy(
                sortBy = sortBy
            )
        }
    }

    fun updateIsFilterExpanded(isExpanded: Boolean) {
        _allUiState.update { it.copy(isFilterExpanded = isExpanded) }
    }

    fun updateIsOrderByExpanded(isExpanded: Boolean) {
        _allUiState.update { it.copy(isOrderByExpanded = isExpanded) }
    }

    fun updateIsSortByExpanded(isExpanded: Boolean) {
        _allUiState.update { it.copy(isSortByExpanded = isExpanded) }
    }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = allUiState.flatMapLatest { state ->
        periodDataHandleUseCase.getTransactionsForPeriod(
            date = state.selectedDate,
            periodType = state.periodType,
            orderBy = state.orderBy,
            sortBy = state.sortBy,
            filter = state.filter
        )
            .distinctUntilChanged() // Avoid re-mapping if data is identical
            .map { stateResult ->
                when(stateResult) {
                    is DataState.Success -> {
                        val grouped = stateResult.data
                            .groupBy {
                                it.createdAt
                                    .toLocalDateTime()
                                    .toLocalDate()
                                    .formattedDate
                            }
                        DataState.Success(grouped)
                    }
                    is DataState.Error -> DataState.Error(stateResult.message)
                    is DataState.Loading -> DataState.Loading
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DataState.Loading
    )
}