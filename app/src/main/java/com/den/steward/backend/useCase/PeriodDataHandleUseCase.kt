package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.states.SortBy
import com.den.steward.helper.filterByListOfFilters
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime
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
        filter: List<Filter> = emptyList(),
        sortBy: SortBy,
        search: String
    ): Flow<DataState<List<Transaction>>> {
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = endDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

        return fetchAllTransactions.map { state ->
            when (state) {
                is DataState.Success -> {
                    // 1. Filter by date range
                    var filtered = state.data.filter { it.createdAt in startMillis until endMillis }

                    // 2. Filter by transaction type
                    filtered = filtered.filterByListOfFilters(filter)
                        .filter { transaction ->
                            transaction.getLabel.contains(search, ignoreCase = true) ||
                            transaction.getNote.contains(search, ignoreCase = true) ||
                            transaction.getAmountOrValue?.toString()?.contains(search, ignoreCase = true) == true ||
                            transaction.getPaymentMethodOrNull?.label?.contains(search, ignoreCase = true) == true ||
                            transaction.getAffectedAmount?.label?.contains(search, ignoreCase = true) == true ||
                            transaction.getStatus?.contains(search, ignoreCase = true) == true ||
                            transaction.type.toString().contains(search, ignoreCase = true)
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
        filter: List<Filter> = emptyList(),
        sortBy: SortBy,
        search: String
    ): Flow<DataState<List<Transaction>>> {
        val startOfWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        
        return if (filterForDayOfWeek != null) {
            val selectedDate = startOfWeek.with(TemporalAdjusters.nextOrSame(filterForDayOfWeek))
            getTransactionsInRange(
                selectedDate, selectedDate.plusDays(1),
                orderBy, filter, sortBy = sortBy, search = search
            )
        } else {
            getTransactionsInRange(
                startOfWeek,
                startOfWeek.plusDays(7),
                orderBy, filter,
                sortBy = sortBy,
                search = search
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
        filter: List<Filter> = emptyList(),
        sortBy: SortBy,
        search: String
    ): Flow<DataState<List<Transaction>>> {
        return when (periodType) {
            PeriodType.DAY -> getTransactionsInRange(
                date, date.plusDays(1), orderBy, filter, sortBy = sortBy, search = search
            )
            PeriodType.WEEK -> weeklyTransactions(date, orderBy, null, filter, sortBy = sortBy, search = search)
            PeriodType.MONTH -> {
                val start = date.with(TemporalAdjusters.firstDayOfMonth())
                val end = start.plusMonths(1)
                getTransactionsInRange(start, end, orderBy, filter, sortBy = sortBy, search = search)
            }
            PeriodType.YEAR -> {
                val start = date.with(TemporalAdjusters.firstDayOfYear())
                val end = start.plusYears(1)
                getTransactionsInRange(start, end, orderBy, filter, sortBy = sortBy, search = search)
            }
        }
    }

    companion object {
        const val INITIAL_PAGE = 10_000
    }
}
