package com.den.steward.backend.useCase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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
                            TransactionType.REFUND -> {
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

    val yesterdayTransactions = dataFilterUseCase.yesterdayTransactions
}
