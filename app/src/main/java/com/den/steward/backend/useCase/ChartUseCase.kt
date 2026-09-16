package com.den.steward.backend.useCase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.charts.DonutChartData
import com.den.steward.ui.components.charts.collections.ChartData
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

class ChartUseCase @Inject constructor (
    @ApplicationContext private val context: Context,
    dataFilterUseCase: DataFilterUseCase
) {
    val donutChart: Flow<DataState<List<DonutChartData>>> = getDonutChartForTransactions(dataFilterUseCase.todayTransactions)

    val donutChartCenterAmount: Flow<Double> = getDonutChartCenterAmountForTransactions(dataFilterUseCase.todayTransactions)

    fun getDonutChartForTransactions(transactionsFlow: Flow<DataState<List<Transaction>>>): Flow<DataState<List<DonutChartData>>> {
        return transactionsFlow.map { state ->
            when (state) {
                is DataState.Success -> {
                    val transactions = state.data
                    val filterNoneAmount = transactions.filter {
                        it.type != TransactionType.GOAL &&
                                it.type != TransactionType.ATTAIN
                    }
                    val group = filterNoneAmount
                        .filter { (it.getAffectAmount?.lowercase() ?: "no") == "yes" }
                        .groupBy { it.type }

                    val data = group.map { (type, groupedTransactions) ->
                        val color = Color(ContextCompat.getColor(context, type.color))
                        val label = ContextCompat.getString(context, type.label)

                        DonutChartData(
                            amount = groupedTransactions.sumOf { it.getAmountOrValue ?: 0.0 }.toFloat(),
                            color = color,
                            title = label
                        )
                    }

                    DataState.Success(data)

                }
                is DataState.Loading -> DataState.Loading
                is DataState.Error -> DataState.Error(state.message)
            }
        }.flowOn(Dispatchers.Default)
    }

    fun getDonutChartCenterAmountForTransactions(transactionsFlow: Flow<DataState<List<Transaction>>>): Flow<Double> {
        return transactionsFlow.map { state ->
            var outgoing = 0.0
            var incoming = 0.0

            if (state is DataState.Success) {
                state.data.forEach { transaction ->
                    if (transaction.getAffectAmount == "Yes") {
                        when (transaction.type) {
                            TransactionType.EARNINGS,
                            TransactionType.DEBT,
                            TransactionType.SAVINGS,
                            TransactionType.REPAYMENT -> {
                                incoming += transaction.getAmountOrValue ?: 0.0
                            }

                            TransactionType.EXPENSE,
                            TransactionType.LENT,
                            TransactionType.SETTLEMENT -> {
                                outgoing += transaction.getAmountOrValue ?: 0.0
                            }
                            else -> {}
                        }
                    }
                }
            }
            incoming - outgoing
        }.flowOn(Dispatchers.Default)
    }

    fun chartDataCollection(transactions: Flow<DataState<List<Transaction>>>): Flow<DataState<ChartDataCollection>> {
        return transactions.map { state ->
            when (state) {
                is DataState.Success -> {
                    val transactions = state.data

                    // 1. Filter out GOAL and ATTAIN transactions and transactions that don't affect the amount
                    val filterNoneAmount = transactions.filter {
                        it.type != TransactionType.GOAL &&
                                it.type != TransactionType.ATTAIN
                    }.filter { (it.getAffectAmount?.lowercase() ?: "no") == "yes" }

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
                    val chartDataCollection = ChartDataCollection(
                        chartData = chartData
                    )

                    DataState.Success(chartDataCollection)

                }
                is DataState.Loading -> DataState.Loading
                is DataState.Error -> DataState.Error(state.message)
            }
        }.flowOn(Dispatchers.Default)
    }

}
