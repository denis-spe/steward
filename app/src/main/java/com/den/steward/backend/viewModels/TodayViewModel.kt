package com.den.steward.backend.viewModels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.TodayUiState
import com.den.steward.backend.states.todayTabState.BalanceStatStates
import com.den.steward.backend.states.todayTabState.LiabilitiesPaymentStatsState
import com.den.steward.backend.states.todayTabState.TodayTabDataState
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.helper.calculateFlow
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.getStartOfDayMillis
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
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
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    private val _todayUiState = MutableStateFlow(TodayUiState())
    val todayUiState = _todayUiState.asStateFlow()

    // Extract ONLY query criteria to avoid re-triggering calculations on sheet expansion toggles
    private val filterCriteriaFlow = _todayUiState
        .map { Triple(it.filter, it.orderBy, it.sortBy) }
        .distinctUntilChanged()

    val todayTabDataState: StateFlow<DataState<TodayTabDataState>> = combine(
        dataFetchUseCase.fetchAllTransactions,
        filterCriteriaFlow
    ) { state, (filter, orderBy, sortBy) ->
        when (state) {
            is DataState.Success -> {
                val transactions = state.data
                val startOfToday = getStartOfDayMillis(0)
                val startOfTomorrow = getStartOfDayMillis(1)

                val todayTransactions = filterAndSortTodayTransactions(
                    transactions = transactions,
                    startOfToday = startOfToday,
                    startOfTomorrow = startOfTomorrow,
                    filter = filter,
                    orderBy = orderBy,
                    sortBy = sortBy
                )

                val balanceStatStates = handleBalanceStatStates(
                    todayTransaction = todayTransactions,
                    transactions = transactions
                )
                val liabilitiesStats = handleLiabilitiesPaymentStats(transactions)
                val donutCharts = handleDonutChart(todayTransactions)
                val donutSummary = handleDonutSummary(donutCharts)

                DataState.Success(
                    TodayTabDataState(
                        transactions = todayTransactions,
                        balanceStatStates = balanceStatStates,
                        liabilitiesPaymentStatsState = liabilitiesStats,
                        donutChartData = donutCharts,
                        donutSummaryStat = donutSummary
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

    private fun filterAndSortTodayTransactions(
        transactions: List<Transaction>,
        startOfToday: Long,
        startOfTomorrow: Long,
        filter: Filter,
        orderBy: OrderBy,
        sortBy: SortBy
    ): ImmutableList<Transaction> {
        val targetType = mapFilterToTransactionType(filter)

        val comparator = when (sortBy) {
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

        val finalComparator = if (orderBy == OrderBy.ASCENDING) comparator else comparator.reversed()

        return transactions
            .asSequence()
            .filter { it.createdAt in startOfToday until startOfTomorrow }
            .filter { targetType == null || it.type == targetType }
            .sortedWith(finalComparator)
            .toImmutableList()
    }

    private fun handleBalanceStatStates(
        todayTransaction: List<Transaction>,
        transactions: List<Transaction>
    ): BalanceStatStates {
        val paymentMethodStat = mutableMapOf<PaymentMethod, Double>()
        val flow = calculateFlow(todayTransaction)

        transactions.forEach { transaction ->
            if (transaction.getAffectAmount == "Yes") {
                val method = transaction.getPaymentMethodOrNull ?: return@forEach
                val amount = transaction.getAmountOrValue ?: 0.0
                val current = paymentMethodStat.getOrDefault(method, 0.0)

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
                paymentMethodStat[method] = updated
            }
        }

        return BalanceStatStates(
            flow = flow,
            paymentState = paymentMethodStat.toImmutableMap()
        )
    }

    private fun handleLiabilitiesPaymentStats(transactions: List<Transaction>): LiabilitiesPaymentStatsState {
        var totalLoan = 0.0
        var totalDebt = 0.0
        var unPaidLoan = 0.0
        var unPaidDebt = 0.0
        var paidCount = 0.0
        var unPaidCount = 0.0

        transactions.forEach { transaction ->
            when (transaction) {
                is Transaction.Lent -> {
                    totalLoan += transaction.amount
                    unPaidLoan += transaction.remainingAmount
                    if (transaction.remainingAmount == 0.0) paidCount++ else unPaidCount++
                }
                is Transaction.Debt -> {
                    totalDebt += transaction.amount
                    unPaidDebt += transaction.remainingAmount
                    if (transaction.remainingAmount == 0.0) paidCount++ else unPaidCount++
                }
                else -> {}
            }
        }

        return LiabilitiesPaymentStatsState(
            totalLoan = totalLoan,
            totalDebt = totalDebt,
            unPaidLoan = unPaidLoan,
            unPaidDebt = unPaidDebt,
            paidCount = paidCount,
            unPaidCount = unPaidCount
        )
    }

    private fun handleDonutChart(todayTransactions: List<Transaction>): ImmutableList<DonutChartData> {
        return todayTransactions
            .filter { it.type != TransactionType.GOAL && it.type != TransactionType.ATTAIN }
            .filter { (it.getAffectAmount?.lowercase() ?: "no") == "yes" }
            .groupBy { it.type }
            .map { (type, groupedTransactions) ->
                val color = Color(ContextCompat.getColor(context, type.color))
                val label = ContextCompat.getString(context, type.label)

                DonutChartData(
                    amount = groupedTransactions.sumOf { it.getAmountOrValue ?: 0.0 }.toFloat(),
                    color = color,
                    title = label
                )
            }
            .toImmutableList()
    }

    private fun handleDonutSummary(donutChartData: ImmutableList<DonutChartData>): String {
        return if (donutChartData.isEmpty()) {
            "No transaction data \navailable."
        } else if (donutChartData.size == 1) {
            val item = donutChartData.first()
            "Today all your transactions are in the \"${item.title}\" category, totaling ${item.amount.formatToAmount()}."
        } else {
            val total = donutChartData.sumOf { it.amount.toDouble() }
            val max = donutChartData.maxBy { it.amount }
            val min = donutChartData.minBy { it.amount }

            """
            Today you have a total volume of ${total.formatToAmount()} across ${donutChartData.size} categories.
            Your largest category is `${max.title}` at ${max.amount.formatToAmount()},
             and your smallest is `${min.title}` at ${min.amount.formatToAmount()}.
            """.trimIndent()
        }
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
            Filter.PLAN -> TransactionType.PLAN
            Filter.ALL -> null
        }
    }

    fun updateFilter(filter: Filter) {
        _todayUiState.value = _todayUiState.value.copy(filter = filter, isFilterExpanded = false)
    }

    fun updateIsFilterExpanded(isExpanded: Boolean) {
        _todayUiState.value = _todayUiState.value.copy(isFilterExpanded = isExpanded)
    }

    fun updateSortBy(sortBy: SortBy) {
        _todayUiState.value = _todayUiState.value.copy(sortBy = sortBy, isSortByExpanded = false)
    }

    fun updateIsSortByExpanded(isExpanded: Boolean) {
        _todayUiState.value = _todayUiState.value.copy(isSortByExpanded = isExpanded)
    }

    fun updateOrderBy(orderBy: OrderBy) {
        _todayUiState.value = _todayUiState.value.copy(orderBy = orderBy, isOrderByExpanded = false)
    }

    fun updateIsOrderExpanded(isExpanded: Boolean) {
        _todayUiState.value = _todayUiState.value.copy(isOrderByExpanded = isExpanded)
    }
}
