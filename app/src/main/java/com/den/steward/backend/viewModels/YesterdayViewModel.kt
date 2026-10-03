package com.den.steward.backend.viewModels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.Affected
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.yesterdayTabState.YesterdayTransactionSummary
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortAndFilterState
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.yesterdayTabState.YesterdayTabState
import com.den.steward.backend.states.yesterdayTabState.YesterdayUiState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.helper.calculateFlow
import com.den.steward.helper.filterAndSortTodayTransactions
import com.den.steward.helper.getStartOfDayMillis
import com.den.steward.helper.incoming
import com.den.steward.helper.outgoing
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.charts.collections.ChartData
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import com.den.steward.backend.useCase.DataFetchUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YesterdayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataDeletionUseCase: DataDeletionUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    private val _yesterdayUiState = MutableStateFlow(YesterdayUiState())
    val yesterdayUiState = _yesterdayUiState.asStateFlow()

    private val _sortAndFilterState = MutableStateFlow(SortAndFilterState())
    val sortAndFilterState = _sortAndFilterState.asStateFlow()

    private val filterCriteriaFlow = _sortAndFilterState
        .map { Triple(it.filter, it.orderBy, it.sortBy) }
        .distinctUntilChanged()

    val yesterdayTabDataState: StateFlow<DataState<YesterdayTabState>> = combine(
        dataFetchUseCase.fetchAllTransactions,
        filterCriteriaFlow
    ) { state, (filter, orderBy, sortBy) ->
        when (state) {
            is DataState.Success -> {
                val transactions = state.data
                val startOfYesterday = getStartOfDayMillis(-1)
                val startOfToday = getStartOfDayMillis(0)

                val yesterdayTransactions = filterAndSortTodayTransactions(
                    transactions = transactions,
                    startOfToday = startOfToday,
                    startOfTomorrow = startOfYesterday,
                    filter = filter,
                    orderBy = orderBy,
                    sortBy = sortBy
                )

                val summary = handleSummary(yesterdayTransactions)
                val chartDataCollection = handleChartDataCollection(yesterdayTransactions)

                DataState.Success(
                    YesterdayTabState(
                        transactions = yesterdayTransactions,
                        summary = summary,
                        chartDataCollection = chartDataCollection
                    )
                )
            }

            is DataState.Error -> DataState.Error(state.message)
            is DataState.Loading -> DataState.Loading
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    private fun handleSummary(transaction: List<Transaction>): YesterdayTransactionSummary {
        val transactions = transaction
            .filter {
                it.type != TransactionType.GOAL ||
                        it.type != TransactionType.ATTAIN ||
                        it.type != TransactionType.ACHIEVEMENT
            }

        val group = transactions.groupBy { it.type }
            .mapValues { (_, value) -> value.sumOf { it.getAmountOrValue ?: 0.0 } }

        val flow = transactions.calculateFlow
        val highTransaction = group.maxByOrNull { it.value }
        val lowTransaction = group.minByOrNull { it.value }

        val totalTransactionAmount = transactions
            .sumOf { it.getAmountOrValue ?: 0.0 }

        val incoming = transactions.incoming
        val outgoing = transactions.outgoing

        return YesterdayTransactionSummary(
            flow = flow,
            transactionSize = transactions.size,
            highTransactionActivityAmount = highTransaction?.value ?: 0.0,
            highTransactionActivityLabel = highTransaction?.key?.name ?: "",
            lowTransactionActivityAmount = lowTransaction?.value ?: 0.0,
            lowTransactionActivityLabel = lowTransaction?.key?.name ?: "",
            incoming = incoming,
            outgoing = outgoing,
            incomingPercentage = ((incoming / totalTransactionAmount) * 100).toInt(),
            outgoingPercentage = ((outgoing / totalTransactionAmount) * 100).toInt()
        )
    }
    private fun handleChartDataCollection(transactions: List<Transaction>): ChartDataCollection {
        // 1. Filter out GOAL and ATTAIN transactions and transactions that don't affect the amount
        val filterNoneAmount = transactions.filter {
            it.type != TransactionType.GOAL &&
                    it.type != TransactionType.ATTAIN
        }.filter { (it.getAffectedAmount ?: Affected.NEUTRAL) == Affected.AFFECTED }

        // 2. Find all unique hours that have any activity across any transaction type
        val allUniqueHours = filterNoneAmount.map {
            it.createdAt.toLocalDateTime().hour
        }.distinct().sorted()

        // 3. Group by type and create a ChartData for each group with aligned X values
        val chartData = filterNoneAmount.groupBy {
            it.type
        }
            .map { (type, groupedTransactions) ->
                val color = Color(ContextCompat.getColor(context, type.color))
                val label = ContextCompat.getString(context, type.label)

                val hourlyData = groupedTransactions.groupBy {
                    it.createdAt.toLocalDateTime().hour
                }

                val x = allUniqueHours.map { it.toDouble() }
                val y = allUniqueHours.map { hour ->
                    hourlyData[hour]?.sumOf { it.getAmountOrValue ?: 0.0 } ?: 0.0
                }

                ChartData(
                    x = x,
                    y = y,
                    label = label,
                    color = color
                )
            }


        // 3. Create a ChartDataCollection with the list of ChartData
        return ChartDataCollection(
            chartData = chartData.toPersistentList()
        )
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

    fun updateIsFilterExpanded(isExpanded: Boolean) {
        _sortAndFilterState.value = _sortAndFilterState.value.copy(
            isFilterExpanded = isExpanded
        )
    }

    fun updateSortBy(sortBy: SortBy) {
        _sortAndFilterState.value = _sortAndFilterState.value.copy(
            sortBy = sortBy,
            isSortByExpanded = false
        )
    }
    fun updateIsSortByExpanded(isExpanded: Boolean) {
        _sortAndFilterState.value = _sortAndFilterState.value.copy(
            isSortByExpanded = isExpanded
        )
    }

    fun updateOrderBy(orderBy: OrderBy) {
        _sortAndFilterState.value = _sortAndFilterState.value.copy(
            orderBy = orderBy,
            isOrderByExpanded = false
        )
    }

    fun updateIsOrderExpanded(isExpanded: Boolean) {
        _sortAndFilterState.value = _sortAndFilterState.value.copy(
            isOrderByExpanded = isExpanded
        )
    }

    // ===================== Delete =====================
    fun deleteTransaction() {
        viewModelScope.launch {
            val transaction = _yesterdayUiState.value.selectedTransactionToDelete ?: return@launch
            dataDeletionUseCase.deleteTransaction(transaction)
        }

        // Reset the state
        updateSelectedTransactionToDelete(null)
        updateOpenDeleteDialog(false)
    }

    fun updateSelectedTransactionToDelete(transaction: Transaction?) {
        _yesterdayUiState.update {
            it.copy(
                selectedTransactionToDelete = transaction,
                openDeleteDialog = transaction != null
            )
        }
    }

    fun updateOpenDeleteDialog(show: Boolean) {
        _yesterdayUiState.update {
            it.copy(openDeleteDialog = show)
        }
    }
}
