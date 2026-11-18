package com.den.steward.backend.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class GoalDetailScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    private val dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    private val transactionFlows = ConcurrentHashMap<String, StateFlow<DataState<Transaction>>>()

    fun getTransaction(transactionId: String): StateFlow<DataState<Transaction>> {
        return transactionFlows.getOrPut(transactionId) {
            dataFetchUseCase.fetchAllTransactions
                .map { dataState ->
                    when (dataState) {
                        is DataState.Success -> {
                            val found = dataState.data.firstOrNull { it.id == transactionId }
                            if (found != null) {
                                DataState.Success(found)
                            } else {
                                val fetched =
                                    dataFetchUseCase.getTransaction(transactionId).getOrNull()
                                if (fetched != null) {
                                    DataState.Success(fetched)
                                } else {
                                    DataState.Error("Transaction not found")
                                }
                            }
                        }

                        is DataState.Error -> DataState.Error(dataState.message)
                        is DataState.Loading -> DataState.Loading
                    }
                }
                .distinctUntilChanged()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = DataState.Loading
                )
        }
    }
}
