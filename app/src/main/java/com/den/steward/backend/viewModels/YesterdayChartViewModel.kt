package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
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
class YesterdayChartViewModel @Inject constructor(
    chartUseCase: ChartUseCase,
    dataFilterUseCase: DataFilterUseCase
) : ViewModel() {
    private val yesterdayTransactions = dataFilterUseCase.yesterdayTransactions

    val donutChart: StateFlow<DataState<List<DonutChartData>>> = chartUseCase.getDonutChartForTransactions(yesterdayTransactions)
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    val donutChartCenterAmount: StateFlow<Double> = chartUseCase.getDonutChartCenterAmountForTransactions(yesterdayTransactions)
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0
        )

    val donutStatusSummary: StateFlow<DataState<String>> = donutChart
        .map { state ->
            when (state) {
                is DataState.Success -> {
                    val data = state.data
                    if (data.isEmpty()) {
                        DataState.Success("No transaction data \navailable for yesterday.")
                    } else if (data.size == 1) {
                        val item = data.first()
                        DataState.Success("Yesterday all your transactions were in the \"${item.title}\" category, totaling ${item.amount.formatToAmount()}.")
                    } else {
                        val total = data.sumOf { it.amount.toDouble() }
                        val max = data.maxBy { it.amount }
                        val min = data.minBy { it.amount }

                        val str = """
                            Yesterday you had a total volume of ${total.formatToAmount()} across ${data.size} categories.
                            Your largest category was `${max.title}` at ${max.amount.formatToAmount()},
                             and your smallest was `${min.title}` at ${min.amount.formatToAmount()}.
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
