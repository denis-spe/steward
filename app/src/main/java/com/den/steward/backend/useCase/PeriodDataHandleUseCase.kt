package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.helper.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/**
 * UseCase to handle data filtering and navigation based on time periods (Weeks, Months, etc.)
 */
class PeriodDataHandleUseCase @Inject constructor(
    dataFetchUseCase: DataFetchUseCase
) {
    val fetchAllTransactions = dataFetchUseCase.fetchAllTransactions
    private val zoneId = ZoneId.systemDefault()

    // Configuration
    private val firstDayOfWeek = DayOfWeek.SUNDAY

    fun transactionDayCount(
        date: LocalDate,
    ): Flow<DataState<Int>> {
        return fetchAllTransactions.map { state ->
            when (state) {
                is DataState.Success -> {
                    val count = state.data.count {
                        it.createdAt.toLocalDateTime().toLocalDate() == date
                    }
                    DataState.Success(count)
                }
                is DataState.Error -> DataState.Error(state.message)
                is DataState.Loading -> DataState.Loading
            }
        }
    }

    /**
     * Generates a list of dates representing a week for a specific pager page.
     */
    fun getWeekDaysForPage(page: Int, anchorDate: LocalDate = LocalDate.now()): List<LocalDate> {
        val weekOffset = (page - INITIAL_PAGE).toLong()
        val startOfTargetWeek = anchorDate
            .plusWeeks(weekOffset)
            .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        
        return (0..6).map { startOfTargetWeek.plusDays(it.toLong()) }
    }

    /**
     * Core filtering logic for any date range, sorting, and transaction type.
     */
    fun getTransactionsInRange(
        startDate: LocalDate,
        endDate: LocalDate, // Exclusive
        orderBy: OrderBy = OrderBy.ASCENDING,
        filter: Filter = Filter.ALL,
        sortBy: SortBy
    ): Flow<DataState<List<Transaction>>> {
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = endDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

        return fetchAllTransactions.map { state ->
            when (state) {
                is DataState.Success -> {
                    // 1. Filter by date range
                    var filtered = state.data.filter { it.createdAt in startMillis until endMillis }

                    // 2. Filter by transaction type
                    if (filter != Filter.ALL) {
                         val targetType = mapFilterToTransactionType(filter)
                        filtered = filtered.filter { it.type == targetType }
                    }

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

                    // 3. Apply sorting
                    val finalComparator = if (orderBy == OrderBy.DESCENDING) {
                        comparator.reversed()
                    } else {
                        comparator
                    }

                    filtered = filtered.sortedWith(finalComparator)

                    DataState.Success(filtered)
                }
                else -> state
            }
        }.flowOn(Dispatchers.Default)
    }

    /**
     * Specific helper for weekly views, supporting optional daily sub-filtering.
     */
    fun weeklyTransactions(
        now: LocalDate,
        orderBy: OrderBy = OrderBy.ASCENDING,
        filterForDayOfWeek: DayOfWeek? = null,
        filter: Filter = Filter.ALL,
        sortBy: SortBy
    ): Flow<DataState<List<Transaction>>> {
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        
        return if (filterForDayOfWeek != null) {
            val selectedDate = startOfWeek.with(TemporalAdjusters.nextOrSame(filterForDayOfWeek))
            getTransactionsInRange(
                selectedDate, selectedDate.plusDays(1),
                orderBy, filter, sortBy = sortBy)
        } else {
            getTransactionsInRange(
                startOfWeek,
                startOfWeek.plusDays(7),
                orderBy, filter,
                sortBy = sortBy
                )
        }
    }

    /**
     * Generic method to get transactions for a specific period type.
     */
    fun getTransactionsForPeriod(
        date: LocalDate,
        periodType: PeriodType,
        orderBy: OrderBy = OrderBy.ASCENDING,
        filter: Filter = Filter.ALL,
        sortBy: SortBy
    ): Flow<DataState<List<Transaction>>> {
        return when (periodType) {
            PeriodType.DAY -> getTransactionsInRange(
                date, date.plusDays(1), orderBy, filter, sortBy = sortBy
            )
            PeriodType.WEEK -> weeklyTransactions(date, orderBy, null, filter, sortBy = sortBy)
            PeriodType.MONTH -> {
                val start = date.with(TemporalAdjusters.firstDayOfMonth())
                val end = start.plusMonths(1)
                getTransactionsInRange(start, end, orderBy, filter, sortBy = sortBy)
            }
            PeriodType.YEAR -> {
                val start = date.with(TemporalAdjusters.firstDayOfYear())
                val end = start.plusYears(1)
                getTransactionsInRange(start, end, orderBy, filter, sortBy = sortBy)
            }
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

    companion object {
        const val INITIAL_PAGE = 10_000
    }
}
