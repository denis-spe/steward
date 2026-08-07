package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.AuthorizationUseCase
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import com.den.steward.helper.yesterdayMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authorizationUseCase: AuthorizationUseCase,
    private val addDataUseCase: AddDataUseCase,
    private val dataFetchUseCase: DataFetchUseCase,
    private val dataFilterUseCase: DataFilterUseCase
) : ViewModel(){
    val userState: StateFlow<AuthState> = authorizationUseCase.userState


    val fetchAllTransactions: StateFlow<DataState<List<Transaction>>> = dataFetchUseCase.fetchAllTransactions
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


    fun addLoan() {
        val loan = Transaction.Loan(
            label = "Loan",
            note = "test",
            amount = 1000.0,
            createdAt = yesterdayMillis()
        )
        viewModelScope.launch {
            addDataUseCase.addLoan(loan)
        }
    }

    fun addRepayment(loanId: String, repayment: Transaction.Repayment) {
        viewModelScope.launch {
            addDataUseCase.addRepayment(loanId, repayment)
        }
    }
}