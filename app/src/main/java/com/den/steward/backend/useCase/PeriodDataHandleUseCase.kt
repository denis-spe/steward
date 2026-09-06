package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    private val fetchAllTransactions = dataFetchUseCase.fetchAllTransactions
    private val zoneId = ZoneId.systemDefault()

    // Configuration
    private val firstDayOfWeek = DayOfWeek.SUNDAY


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
        sort: Sort = Sort.ASCENDING,
        filter: Filter = Filter.ALL,
        sortType: SortType
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

                    val comparator = when (sortType) {
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

                    // 3. Apply sorting
                    val finalComparator = if (sort == Sort.DESCENDING) {
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
        sort: Sort = Sort.ASCENDING,
        filterForDayOfWeek: DayOfWeek? = null,
        filter: Filter = Filter.ALL,
        sortType: SortType
    ): Flow<DataState<List<Transaction>>> {
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        
        return if (filterForDayOfWeek != null) {
            val selectedDate = startOfWeek.with(TemporalAdjusters.nextOrSame(filterForDayOfWeek))
            getTransactionsInRange(
                selectedDate, selectedDate.plusDays(1),
                sort, filter, sortType = sortType)
        } else {
            getTransactionsInRange(
                startOfWeek,
                startOfWeek.plusDays(7),
                sort, filter,
                sortType = sortType
                )
        }
    }

    /**
     * Generic method to get transactions for a specific period type.
     */
    fun getTransactionsForPeriod(
        date: LocalDate,
        periodType: PeriodType,
        sort: Sort = Sort.ASCENDING,
        filter: Filter = Filter.ALL,
        sortType: SortType
    ): Flow<DataState<List<Transaction>>> {
        return when (periodType) {
            PeriodType.DAY -> getTransactionsInRange(
                date, date.plusDays(1), sort, filter, sortType = sortType
            )
            PeriodType.WEEK -> weeklyTransactions(date, sort, null, filter, sortType = sortType)
            PeriodType.MONTH -> {
                val start = date.with(TemporalAdjusters.firstDayOfMonth())
                val end = start.plusMonths(1)
                getTransactionsInRange(start, end, sort, filter, sortType = sortType)
            }
            PeriodType.YEAR -> {
                val start = date.with(TemporalAdjusters.firstDayOfYear())
                val end = start.plusYears(1)
                getTransactionsInRange(start, end, sort, filter, sortType = sortType)
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
            Filter.REFUND -> TransactionType.REFUND
            Filter.ATTAIN -> TransactionType.ATTAIN
            Filter.LENT -> TransactionType.LENT
            Filter.DEBT -> TransactionType.DEBT
            Filter.ALL -> null
        }
    }

    companion object {
        const val INITIAL_PAGE = 10_000
    }
}
