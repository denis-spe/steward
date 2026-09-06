// Grace and truth came through JESUS CHRIST
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    chartUseCase: ChartUseCase
) : ViewModel() {
    val donutChart: StateFlow<DataState<List<DonutChartData>>> = chartUseCase.donutChart
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )
    val donutChartCenterAmount: StateFlow<Double> = chartUseCase.donutChartCenterAmount
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )
    val donutStatusSummary: StateFlow<DataState<String>> = chartUseCase.donutChart
        .map { state ->
            when (state) {
                is DataState.Success -> {
                    val data = state.data
                    if (data.isEmpty()) {
                        DataState.Success("No transaction data available.")
                    } else if (data.size == 1) {
                        val item = data.first()
                        DataState.Success("Today all your transactions are in the \"${item.title}\" category, totaling ${item.amount.formatToAmount()}.")
                    } else {
                        val total = data.sumOf { it.amount.toDouble() }
                        val max = data.maxBy { it.amount }
                        val min = data.minBy { it.amount }

                        val str = """
                            Today you have a total volume of ${total.formatToAmount()} across ${data.size} categories.
                            Your largest category is "${max.title}" at ${max.amount.formatToAmount()},
                             and your smallest is "${min.title}" at ${min.amount.formatToAmount()}.
                        """.trimIndent()

                        DataState.Success(str)
                    }
                }

                is DataState.Error -> DataState.Error(state.message)
                is DataState.Loading -> DataState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )
}