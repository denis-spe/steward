package com.den.steward.backend.useCase

import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.helper.getStartOfDayMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class DataFilterUseCase @Inject constructor(
    dataFetchUseCase: DataFetchUseCase
) {
    private val fetchAllTransactions = dataFetchUseCase.fetchAllTransactions

    val todayTransactions: Flow<DataState<List<Transaction>>> = fetchAllTransactions.map { state ->
        when (state) {
            is DataState.Success -> {
                val startOfToday = getStartOfDayMillis(0)
                val startOfTomorrow = getStartOfDayMillis(1)

                val filtered = state.data.filter { transaction ->
                    transaction.createdAt in startOfToday until startOfTomorrow
                }


                DataState.Success(filtered)
            }
            else -> state // Pass along Loading or Error states
        }
    }.flowOn(Dispatchers.Default)

    // 3. Fetch yesterday's transactions (Midnight yesterday -> Midnight today)
    val yesterdayTransactions: Flow<DataState<List<Transaction>>> = fetchAllTransactions.map { state ->
        when (state) {
            is DataState.Success -> {
                val startOfYesterday = getStartOfDayMillis(-1)
                val startOfToday = getStartOfDayMillis(0)

                val filtered = state.data.filter { transaction ->
                    transaction.createdAt in startOfYesterday until startOfToday
                }

                DataState.Success(filtered)
            }
            else -> state
        }
    }.flowOn(Dispatchers.Default)
}