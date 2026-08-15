package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.AuthorizationUseCase
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.backend.useCase.ChartUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import com.den.steward.ui.components.charts.DonutChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authorizationUseCase: AuthorizationUseCase,
    private val addDataUseCase: AddDataUseCase,
    private val dataFetchUseCase: DataFetchUseCase,
    private val dataFilterUseCase: DataFilterUseCase,
    private val chartUseCase: ChartUseCase
) : ViewModel(){
    val userState: StateFlow<AuthState> = authorizationUseCase.userState


    val fetchAllTransactions: StateFlow<DataState<List<Transaction>>> = dataFetchUseCase.fetchAllTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    val donutChart: StateFlow<DataState<List<DonutChartData>>> = chartUseCase.donutChart
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )


    val todayTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.todayTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    val yesterdayTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.yesterdayTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )


    fun addFulfillment(transactionId: String, fulfillment: Transaction) {
        viewModelScope.launch {
            addDataUseCase.addFulfillment(transactionId, fulfillment)
        }
    }

    fun transactionDataSubmission(dataTransferToViewModel: DataTransferToViewModel) {
        viewModelScope.launch {
            addDataUseCase.addTransaction(dataTransferToViewModel = dataTransferToViewModel)
        }
    }
}