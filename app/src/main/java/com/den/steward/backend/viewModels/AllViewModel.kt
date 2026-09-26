package com.den.steward.backend.viewModels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.allTabState.AllTransactionSummary
import com.den.steward.backend.states.allTabState.AllUiState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.backend.states.Filter
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortAndFilterState
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.allTabState.AllTabDataState
import com.den.steward.helper.calculateFlow
import com.den.steward.helper.formattedDate
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.charts.collections.ChartData
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.IsoFields
import javax.inject.Inject
import kotlin.collections.sumOf

/**
 * Pairs a transaction with its already-parsed LocalDateTime so downstream
 * grouping/filtering never has to call [toLocalDateTime] more than once
 * per transaction per emission.
 */
private data class TimedTransaction(
    val transaction: Transaction,
    val dateTime: LocalDateTime
)

private data class SortFilterControllers(
    val selectedDate: LocalDate,
    val periodType: PeriodType,
    val triple: Triple<List<Filter>, OrderBy, SortBy>,
    val isTransactionListSort: OrderBy
)

@HiltViewModel
class AllViewModel @Inject constructor(
    private val periodDataHandleUseCase: PeriodDataHandleUseCase,
    @ApplicationContext private val context: Context,
    private val chartUseCase: ChartUseCase
) : ViewModel() {
    private val _allUiState = MutableStateFlow(AllUiState())
    val allUiState = _allUiState.asStateFlow()

    private val _sortAndFilterState = MutableStateFlow(SortAndFilterState())
    val sortAndFilterState = _sortAndFilterState.asStateFlow()

    // Extract ONLY query criteria to avoid re-triggering calculations on sheet expansion toggles
    private val filterCriteriaFlow = _sortAndFilterState
        .map { Triple(it.filter, it.orderBy, it.sortBy) }
        .distinctUntilChanged()

    private val allUiStateCriteriaFlow = _allUiState
        .map { Triple(it.selectedDate, it.periodType, it.weekNumber) }
        .distinctUntilChanged()

    private val allUiStateCriteriaFlowForTransaction = _allUiState
        .map { Triple(it.selectedDate, it.periodType, it.isTransactionListSort) }
        .distinctUntilChanged()

    // Color/label resolution for each TransactionType is fixed for the lifetime of the
    // ViewModel (context doesn't change), so resolve it once instead of on every chart
    // recomputation.
    private val typeStyle: Map<TransactionType, Pair<Color, String>> by lazy {
        TransactionType.entries.associateWith { type ->
            Color(ContextCompat.getColor(context, type.color)) to
                    ContextCompat.getString(context, type.label)
        }
    }

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
        _sortAndFilterState.update {
            it.copy(
                orderBy = orderBy
            )
        }
    }

    fun updateFilter(filter: Filter) {
        _sortAndFilterState.update { state ->
            val currentFilters = state.filter.toMutableList()
            val newFilters = if (filter == Filter.ALL) {
                listOf(Filter.ALL)
            } else {
                currentFilters.remove(Filter.ALL)
                if (currentFilters.contains(filter)) {
                    currentFilters.remove(filter)
                } else {
                    currentFilters.add(filter)
                }
                if (currentFilters.isEmpty()) {
                    listOf(Filter.ALL)
                } else {
                    currentFilters
                }
            }
            state.copy(filter = newFilters)
        }
    }

    fun updateIsTransactionListOrderExpanded(isExpanded: Boolean) {
        _allUiState.update {
            it.copy(
                isTransactionListSortExpanded = isExpanded
            )
        }
    }
    fun updateIsTransactionListOrder(order: OrderBy) {
        _allUiState.update {
            it.copy(
                isTransactionListSort = order
            )
        }
    }

    fun updateSortType(sortBy: SortBy) {
        _sortAndFilterState.update {
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
        _sortAndFilterState.update { it.copy(isFilterExpanded = isExpanded) }
    }

    fun updateIsOrderByExpanded(isExpanded: Boolean) {
        _sortAndFilterState.update { it.copy(isOrderByExpanded = isExpanded) }
    }

    fun updateIsSortByExpanded(isExpanded: Boolean) {
        _sortAndFilterState.update { it.copy(isSortByExpanded = isExpanded) }
    }


    fun updateIsPeriodTypeExpanded(expend: Boolean) {
        _allUiState.update { it.copy(isPeriodTypeExpanded = expend) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTabDataState: StateFlow<DataState<AllTabDataState>> = combine(
        allUiStateCriteriaFlow, filterCriteriaFlow
    ) { (selectedDate, periodType, _), (filter, orderBy, sortBy) ->
        Triple(selectedDate, periodType, Triple(filter, orderBy, sortBy))
    }.flatMapLatest { (selectedDate, periodType, sortAndFilter) ->
        val (filter, orderBy, sortBy) = sortAndFilter
        periodDataHandleUseCase.getTransactionsForPeriod(
            date = selectedDate,
            periodType = periodType,
            orderBy = orderBy,
            sortBy = sortBy,
            filter = filter
        )
            .distinctUntilChanged()
            .map { stateResult ->
                when (stateResult) {
                    is DataState.Success -> {
                        val data = stateResult.data

                        // Parse each transaction's date/time exactly once, then reuse
                        // it for grouping, the summary calc, and the chart builder
                        // instead of re-parsing per pass.
                        val timedTransactions = data.map {
                            TimedTransaction(it, it.createdAt.toLocalDateTime())
                        }

                        val transactions = timedTransactions
                            .groupBy { it.dateTime.toLocalDate() }
                            .toSortedMap()
                            .mapKeys{
                                (key, value) ->
                                    val today = LocalDate.now()
                                    val yesterday = today.minusDays(1)

                                    when (key) {
                                        today -> "Today"
                                        yesterday -> "Yesterday"
                                        else -> key.formattedDate
                                    }
                            }
                            .mapValues { (_, grouped) -> grouped.map { it.transaction } }
                            .toImmutableMap()

                        val chartDataCollection = when (periodType) {
                            PeriodType.DAY -> chartDataCollectionByHour(timedTransactions)
                            PeriodType.WEEK -> chartDataCollectionByWeekDay(timedTransactions)
                            PeriodType.MONTH -> chartDataCollectionByMonthDay(timedTransactions)
                            PeriodType.YEAR -> chartDataCollectionByMonth(timedTransactions)
                        }

                        val allTransactionSummary = handleAllTabSummary(timedTransactions)

                        DataState.Success(
                            AllTabDataState(
                                transactions = transactions,
                                chartDataCollection = chartDataCollection,
                                allTransactionSummary = allTransactionSummary
                            )
                        )
                    }
                    is DataState.Error -> DataState.Error(stateResult.message)
                    is DataState.Loading -> DataState.Loading
                }
            }.onStart { emit(DataState.Loading) }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<DataState<ImmutableMap<String, List<Transaction>>>> = combine(
        allUiStateCriteriaFlowForTransaction, filterCriteriaFlow
    ) { (selectedDate, periodType, isTransactionListSort), (filter, orderBy, sortBy) ->
        SortFilterControllers(
            selectedDate,
            periodType,
            Triple(filter, orderBy, sortBy),
            isTransactionListSort
        )
    }.flatMapLatest { (selectedDate, periodType, sortAndFilter, isTransactionListSort) ->
        val (filter, orderBy, sortBy) = sortAndFilter
        periodDataHandleUseCase.getTransactionsForPeriod(
            date = selectedDate,
            periodType = periodType,
            orderBy = orderBy,
            sortBy = sortBy,
            filter = filter
        )
            .distinctUntilChanged()
            .map { stateResult ->
                when (stateResult) {
                    is DataState.Success -> {
                        val data = stateResult.data

                        // Parse each transaction's date/time exactly once, then reuse
                        // it for grouping, the summary calc, and the chart builder
                        // instead of re-parsing per pass.
                        val timedTransactions = data.map {
                            TimedTransaction(it, it.createdAt.toLocalDateTime())
                        }

                        val transactions = timedTransactions
                            .groupBy { it.dateTime.toLocalDate() }
                            .toSortedMap {
                                    date1, date2 ->
                                if (isTransactionListSort == OrderBy.DESCENDING) {
                                    date2.compareTo(date1)
                                } else {
                                    date1.compareTo(date2)
                                }
                            }
                            .mapKeys{
                                    (key, value) ->
                                val today = LocalDate.now()
                                val yesterday = today.minusDays(1)

                                when (key) {
                                    today -> "Today"
                                    yesterday -> "Yesterday"
                                    else -> key.formattedDate
                                }
                            }
                            .mapValues { (_, grouped) -> grouped.map { it.transaction } }
                            .toImmutableMap()

                        DataState.Success(
                            transactions
                        )
                    }
                    is DataState.Error -> DataState.Error(stateResult.message)
                    is DataState.Loading -> DataState.Loading
                }
            }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    // Bounded, access-ordered cache: only keeps the most recently used pages'
    // count flows alive. Without a cap this map (and every flow chain inside it)
    // grows for the lifetime of the ViewModel as the user swipes through weeks.
    private val maxCachedCountPages = 15

    private val countsCache = object : LinkedHashMap<Int, StateFlow<ImmutableList<Pair<Int?, Int>>>>(
        maxCachedCountPages, 0.75f, true
    ) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<Int, StateFlow<ImmutableList<Pair<Int?, Int>>>>
        ): Boolean = size > maxCachedCountPages
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun transactionCounts(page: Int): StateFlow<ImmutableList<Pair<Int?, Int>>> =
        countsCache.getOrPut(page) {
            filterCriteriaFlow.flatMapLatest { (filter, orderBy, sortBy) ->
                val weekDate = getWeekDaysForPage(page).first()
                periodDataHandleUseCase.weeklyTransactions(
                    now = weekDate, orderBy = orderBy, filter = filter, sortBy = sortBy
                ).distinctUntilChanged().map { stateResult ->
                    when (stateResult) {
                        is DataState.Success -> {
                            val groupedTransaction = stateResult.data
                                .groupBy { it.createdAt.toLocalDateTime().toLocalDate() }
                            val counts = groupedTransaction.mapValues { it.value.size }
                            val flow = groupedTransaction.mapValues {
                                if (it.value.calculateFlow < 0) -1 else 1
                            }

                            getWeekDaysForPage(page).map { date -> flow[date] to (counts[date] ?: 0) }.toImmutableList()
                        }
                        is DataState.Error, is DataState.Loading -> persistentListOf()
                    }
                }
            }.flowOn(Dispatchers.Default)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    persistentListOf()
                )
        }

    private fun handleAllTabSummary(timedTransactions: List<TimedTransaction>): AllTransactionSummary {
        val transactions = timedTransactions.map { it.transaction }
        val flow = transactions.calculateFlow
        val transactionSize = timedTransactions.size
        var totalReceived = 0.0
        var totalSpent = 0.0
        var totalSavings = 0.0

        timedTransactions.forEach { (transaction, _) ->
            if (transaction.getAffectAmount == "Yes") {
                when (transaction) {
                    is Transaction.Earnings -> totalReceived += transaction.amount
                    is Transaction.Savings -> totalSavings += transaction.amount
                    is Transaction.Debt -> totalReceived += transaction.amount
                    is Transaction.Repayment -> totalReceived += transaction.amount
                    is Transaction.Expense -> totalSpent += transaction.amount
                    is Transaction.Lent -> totalSpent += transaction.amount
                    is Transaction.Settlement -> totalSpent += transaction.amount
                    else -> {}
                }
            }
        }

        val dayCountOfTransaction = timedTransactions
            .groupBy { it.dateTime.toLocalDate() }
            .mapValues { it.value.size }
            .toImmutableMap()

        return AllTransactionSummary(
            flow = flow,
            transactionSize = transactionSize,
            totalReceived = totalReceived,
            totalSpent = totalSpent,
            totalSavings = totalSavings,
            dayCountOfTransaction = dayCountOfTransaction
        )
    }

    /**
     * Shared implementation for all four "chart data by X" builders. Only the
     * key extracted from each transaction's LocalDateTime differs between them
     * (hour / day-of-week / day-of-month / month), so the filter -> group ->
     * series-build pipeline is written once instead of four times.
     */
    private fun buildChartDataCollection(
        timedTransactions: List<TimedTransaction>,
        keyOf: (LocalDateTime) -> Int
    ): ChartDataCollection {
        // Single pass: drop GOAL/ATTAIN and non-amount-affecting transactions together,
        // instead of two separate filter() passes over the list.
        val filtered = timedTransactions.filter { (transaction, _) ->
            transaction.type != TransactionType.GOAL &&
                    transaction.type != TransactionType.ATTAIN &&
                    transaction.getAffectAmount.equals("yes", ignoreCase = true)
        }

        val allUniqueKeys = filtered.map { keyOf(it.dateTime) }.distinct().sorted()

        val chartData = filtered.groupBy { it.transaction.type }
            .map { (type, grouped) ->
                val (color, label) = typeStyle.getValue(type)

                val keyedData = grouped.groupBy { keyOf(it.dateTime) }

                val x = allUniqueKeys.map { it.toDouble() }
                val y = allUniqueKeys.map { key ->
                    keyedData[key]?.sumOf { it.transaction.getAmountOrValue ?: 0.0 } ?: 0.0
                }

                ChartData(
                    x = x,
                    y = y,
                    label = label,
                    color = color
                )
            }
            .toImmutableList()

        return ChartDataCollection(chartData = chartData)
    }

    private fun chartDataCollectionByHour(timedTransactions: List<TimedTransaction>): ChartDataCollection =
        buildChartDataCollection(timedTransactions) { it.hour }

    private fun chartDataCollectionByWeekDay(timedTransactions: List<TimedTransaction>): ChartDataCollection =
        buildChartDataCollection(timedTransactions) { it.dayOfWeek.value }

    private fun chartDataCollectionByMonthDay(timedTransactions: List<TimedTransaction>): ChartDataCollection =
        buildChartDataCollection(timedTransactions) { it.dayOfMonth }

    private fun chartDataCollectionByMonth(timedTransactions: List<TimedTransaction>): ChartDataCollection =
        buildChartDataCollection(timedTransactions) { it.monthValue }




}