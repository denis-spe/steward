// Love the LORD your GOD with all your heart and with all soul
// and with all your might and love your neighbor as your self
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DataFetchViewModel @Inject constructor(
    dataFetchUseCase: DataFetchUseCase,
    dataFilterUseCase: DataFilterUseCase,
) : ViewModel() {

    /**
     * Fetch all transactions from the database
     */
    val fetchAllTransactions: StateFlow<DataState<List<Transaction>>> = dataFetchUseCase.fetchAllTransactions
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    /**
     * Fetch all transactions from the database that are today
     */
    val todayTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.todayTransactions
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    /**
     * Fetch all transactions from the database that are yesterday
     */
    val yesterdayTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.yesterdayTransactions
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )
}