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
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {
    val transactions = dataFetchUseCase.fetchAllTransactions.filterNot { state ->
        when (state) {
            is DataState.Success -> state.data.all { transaction ->
                transaction.type in listOf(
                    TransactionType.DEBT,
                    TransactionType.LENT,
                    TransactionType.GOAL,
                    TransactionType.ATTAIN,
                    TransactionType.PLAN_FULFILLMENT,
                    TransactionType.ACHIEVEMENT
                )
            }

            else -> false
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )
}