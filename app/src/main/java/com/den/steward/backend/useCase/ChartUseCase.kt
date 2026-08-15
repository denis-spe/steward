package com.den.steward.backend.useCase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChartUseCase @Inject constructor (
    @ApplicationContext context: Context,
    dataFilterUseCase: DataFilterUseCase
) {
    val donutChart: Flow<DataState<List<DonutChartData>>> = dataFilterUseCase
        .todayTransactions
        .map { state ->
            when (state) {
                is DataState.Success -> {
                    val transactions = state.data
                    val filterNoneAmount = transactions.filter {
                        it.type != TransactionType.GOAL &&
                        it.type != TransactionType.ATTAIN
                    }
                    val group = filterNoneAmount.groupBy { it.type }

                    val data = group.map { (type, groupedTransactions) ->
                        val color = Color(ContextCompat.getColor(context, type.color))
                        val label = ContextCompat.getString(context, type.label)

                        DonutChartData(
                            amount = groupedTransactions.sumOf { it.getAmountOrValue ?: 0.0 }.toFloat(),
                            color = color,
                            title = label
                        )
                    }

                    if (data.isEmpty()) {
                        DataState.Empty
                    } else {
                        DataState.Success(data)
                    }

                }
                is DataState.Loading -> DataState.Loading
                is DataState.Empty -> DataState.Empty
                is DataState.Error -> DataState.Error(state.message)
            }
        }
    val yesterdayTransactions = dataFilterUseCase.yesterdayTransactions
}