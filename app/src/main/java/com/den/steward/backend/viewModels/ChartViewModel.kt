// Grace and truth came through JESUS CHRIST
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
}