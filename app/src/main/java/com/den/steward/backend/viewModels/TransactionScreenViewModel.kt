package com.den.steward.backend.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    val transactions = dataFetchUseCase.fetchAllTransactions.map { state ->
        when (state) {
            is DataState.Success -> {
                val transactions = state.data.filterNot {
                    it.type in listOf(
                        TransactionType.ACHIEVEMENT, TransactionType.ATTAIN,
                        TransactionType.PLAN_FULFILLMENT, TransactionType.PLAN,
                        TransactionType.REPAYMENT, TransactionType.SETTLEMENT,
                        TransactionType.LENT, TransactionType.DEBT, TransactionType.GOAL
                    )
                }
                DataState.Success(transactions)
            }

            is DataState.Error -> DataState.Error(state.message)
            is DataState.Loading -> DataState.Loading
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )
}