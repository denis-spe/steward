package com.den.steward.backend.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.transactionScreenState.TransactionDataState
import com.den.steward.backend.states.transactionScreenState.TransactionMainSummary
import com.den.steward.backend.states.transactionScreenState.TransactionScreenUiState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import com.den.steward.helper.calculateFlow
import com.den.steward.helper.formattedDate
import com.den.steward.helper.incoming
import com.den.steward.helper.outgoing
import com.den.steward.helper.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {
    private val _transactionScreenUiState = MutableStateFlow(TransactionScreenUiState())
    val transactionScreenUiState = _transactionScreenUiState.asStateFlow()

    val dataUpdateState = updateTransaction.dataUpdateState
    val transactionDataState = dataFetchUseCase.fetchAllTransactions.map { state ->
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

                val now = LocalDate.now()

                val totalSum = transactions.sumOf { it.getAmountOrValue ?: 0.0 }

                val transactionMainSummary = TransactionMainSummary(
                    netFlow = transactions.calculateFlow,
                    incoming = transactions.incoming,
                    outgoing = transactions.outgoing,
                    incomingPercent = (transactions.incoming / totalSum) * 100,
                    outgoingPercent = (transactions.outgoing / totalSum) * 100
                )

                val transactionDataState = TransactionDataState(
                    transactions = transactions.groupBy {
                        val createdAt = it.createdAt.toLocalDateTime()
                        when {
                            now == createdAt.toLocalDate() -> "Today"
                            now.minusDays(1) == createdAt.toLocalDate() -> "Yesterday"
                            else -> createdAt.formattedDate
                        }
                    }.toImmutableMap(),
                    transactionMainSummary = transactionMainSummary
                )

                DataState.Success(transactionDataState)
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

    // ===================== Delete =====================
    fun deleteTransaction() {
        viewModelScope.launch {
            val transaction =
                _transactionScreenUiState.value.selectedTransactionToDelete ?: return@launch
            deletionUseCase.deleteTransaction(transaction)
        }

        // Reset the state
        updateSelectedTransactionToDelete(null)
        updateOpenDeleteDialog(false)
    }

    fun updateSelectedTransactionToDelete(transaction: Transaction?) {
        _transactionScreenUiState.update {
            it.copy(
                selectedTransactionToDelete = transaction,
                openDeleteDialog = transaction != null
            )
        }
    }

    fun updateOpenDeleteDialog(show: Boolean) {
        _transactionScreenUiState.update {
            it.copy(openDeleteDialog = show)
        }
    }


    // ===================== Update Transaction =========================
    fun onUpdateTransaction() {
        viewModelScope.launch {
            if (updateTransaction.updateTransaction()) {
                updateTransaction.onReset()
            }
        }
    }
}