package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.AllTransactionSummary
import com.den.steward.backend.states.AllUiState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import com.den.steward.helper.formattedDate
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.temporal.IsoFields
import javax.inject.Inject

@HiltViewModel
class AllViewModel @Inject constructor(
    private val periodDataHandleUseCase: PeriodDataHandleUseCase,
    private val chartUseCase: ChartUseCase
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

    fun updateSelectedTransactionForView(transaction: Transaction?) {
        _allUiState.update {
            it.copy(
                selectedTransactionForView = transaction
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
                    TransactionType.DEBT,
                    TransactionType.REPAYMENT -> incoming += amount

                    TransactionType.EXPENSE,
                    TransactionType.LENT,
                    TransactionType.SAVINGS,
                    TransactionType.SETTLEMENT -> outgoing += amount
                    else -> {}
                }
            }
        }
        return incoming - outgoing
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = allUiState
        .map { Triple(it.selectedDate, it.periodType, Triple(it.orderBy, it.sortBy, it.filter)) }
        .distinctUntilChanged()
        .flatMapLatest { (selectedDate, periodType, queryParams) ->
            val (orderBy, sortBy, filter) = queryParams
            periodDataHandleUseCase.getTransactionsForPeriod(
                date = selectedDate,
                periodType = periodType,
                orderBy = orderBy,
                sortBy = sortBy,
                filter = filter
            )
                .distinctUntilChanged() // Avoid re-mapping if data is identical
                .map { stateResult ->
                    when (stateResult) {
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
                // FIX: ensures the toLocalDateTime()/groupBy work above never runs on Main,
                // regardless of what dispatcher getTransactionsForPeriod() emits on internally.
                // Defensive insurance against a main-thread stall coinciding with scroll,
                // not a confirmed fix for the reported jank — this is data-layer logic and
                // does not run on every scroll frame, only when the query params or
                // underlying data change.
                .flowOn(Dispatchers.Default)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val chartDataCollection: StateFlow<DataState<ChartDataCollection>> = allUiState
        .map { Triple(it.selectedDate, it.periodType, Triple(it.orderBy, it.sortBy, it.filter)) }
        .distinctUntilChanged()
        .flatMapLatest { (selectedDate, periodType, queryParams) ->
            val (orderBy, sortBy, filter) = queryParams
            val transactionsFlow = periodDataHandleUseCase.getTransactionsForPeriod(
                date = selectedDate,
                periodType = periodType,
                orderBy = orderBy,
                sortBy = sortBy,
                filter = filter
            ).distinctUntilChanged()

            val chartFlow = when (periodType) {
                PeriodType.DAY -> chartUseCase.chartDataCollection(transactionsFlow)
                PeriodType.WEEK -> chartUseCase.chartDataCollectionByWeekDay(transactionsFlow)
                PeriodType.MONTH -> chartUseCase.chartDataCollectionByMonthDay(transactionsFlow)
                PeriodType.YEAR -> chartUseCase.chartDataCollectionByMonth(transactionsFlow)
            }

            chartFlow
                .onStart { emit(DataState.Loading) }
                .flowOn(Dispatchers.Default)
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionSummary = allUiState
        .map { it.selectedDate to it.periodType }
        .distinctUntilChanged()
        .flatMapLatest { (date, periodType) ->
            periodDataHandleUseCase.getTransactionsForPeriod(
                date = date,
                periodType = periodType,
                orderBy = OrderBy.ASCENDING,
                sortBy = SortBy.TIME,
                filter = Filter.ALL
            )
                .distinctUntilChanged()
                .map { stateResult ->
                    when (stateResult) {
                        is DataState.Success -> {
                            val data = stateResult.data
                            val flow = calculateFlow(data)
                            val transactionSize = data.size
                            var totalReceived = 0.0
                            var totalSpent = 0.0
                            var totalSavings = 0.0

                            data.forEach {
                                if (it.getAffectAmount == "Yes") {
                                    when (it) {
                                        is Transaction.Earnings -> totalReceived += it.amount
                                        is Transaction.Savings -> totalSavings += it.amount
                                        is Transaction.Debt -> totalReceived += it.amount
                                        is Transaction.Repayment -> totalReceived += it.amount
                                        is Transaction.Expense -> totalSpent += it.amount
                                        is Transaction.Lent -> totalSpent += it.amount
                                        is Transaction.Settlement -> totalSpent += it.amount
                                        else -> {}
                                    }
                                }
                            }

                            val allTransactionSummary = AllTransactionSummary(
                                flow = flow,
                                transactionSize = transactionSize,
                                totalReceived = totalReceived,
                                totalSpent = totalSpent,
                                totalSavings = totalSavings
                            )

                            DataState.Success(allTransactionSummary)
                        }
                        is DataState.Error -> DataState.Error(stateResult.message)
                        is DataState.Loading -> DataState.Loading
                    }
                }
                // FIX: same defensive dispatcher guarantee for the summary calculation path.
                .flowOn(Dispatchers.Default)
                .onStart { emit(DataState.Loading) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )
}