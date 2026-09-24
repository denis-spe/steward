// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self
package com.den.steward.backend.viewModels

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PlanStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PlanTabUiState
import com.den.steward.backend.useCase.AddDataUseCase
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.DataFilterUseCase
import com.den.steward.backend.useCase.UpdateDateUseCase
import com.den.steward.helper.toEpochMillis
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanTabViewModel @Inject constructor(
    private val dataFetchUseCase: DataFetchUseCase,
    dataFilterUseCase: DataFilterUseCase,
    private val addDataUseCase: AddDataUseCase,
    private val dataDeletionUseCase: DataDeletionUseCase,
    private val updateDataUseCase: UpdateDateUseCase
) : ViewModel() {

    companion object {
        const val TAG = "PlanTabViewModel"
    }

    private val _planTabUiState = MutableStateFlow(PlanTabUiState())
    val planTabUiState: StateFlow<PlanTabUiState> = _planTabUiState

    val planTransactions: StateFlow<DataState<List<Transaction>>> = dataFilterUseCase.planTransactions
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    fun setSelectedTransaction(transaction: Transaction?) {
        _planTabUiState.value = _planTabUiState.value.copy(selectedParentTransaction = transaction)
    }

    fun updateSelectedFulfillmentTransactionType(
        transactionType: TransactionType
    ) {
        _planTabUiState.value = _planTabUiState.value.copy(selectedFulfillmentTransactionType = transactionType)
    }

    fun updateShowFulfillmentTransactionTypeBottomSheet(
        show: Boolean
    ) {
        _planTabUiState.value =
            _planTabUiState.value.copy(showFulfillmentTransactionTypeBottomSheet = show)

    }

    fun updateSelectedPlanFulfillmentType(
        transactionType: TransactionType
    ) {
        _planTabUiState.value = _planTabUiState.value.copy(selectedPlanFulfillmentType = transactionType)
    }

    val planFulfillmentTransactions: StateFlow<DataState<List<Transaction>>> = _planTabUiState
        .flatMapLatest { state ->
            val transaction = state.selectedParentTransaction

            if (transaction == null) flowOf(DataState.Success(emptyList()))
            else dataFetchUseCase.fetchAllFulfillment(transaction)
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DataState.Loading
        )

    fun updatePlanFulfillmentStatus(
        transactionId: String,
        planStatus: PlanStatus,
        fulfillment: Transaction.PlanFulfillment,
    ) {
        viewModelScope.launch {
            updateDataUseCase.updateTransactionFulfillment(
                transactionId,
                fulfillment.id,
                fulfillment.copy(
                    status = planStatus
                )
            )
        }
    }

    fun addPlanFulfillment() {
        _planTabUiState.update { it.copy(isFulfillmentValid = TransactionFieldState.Initial) }

        val currentState = _planTabUiState.value
        val labelText = currentState.label.text.toString()
        val amountText = currentState.amount.text.toString()
        val noteText = currentState.note.text.toString()

        if (currentState.isSaving || currentState.selectedParentTransaction == null) {
            _planTabUiState.update {
                it.copy(
                    isFulfillBtnClick = true,
                    isSaving = false,
                    isFulfillmentValid = TransactionFieldState.Error("Select transaction to fulfill")
                )
            }
            return
        }

        val amountValue = amountText.toDoubleOrNull()
        val isAmountInvalid = amountText.isEmpty() || amountValue == null || amountValue == 0.0

        if (isAmountInvalid) {
            _planTabUiState.update { it.copy(
                isAmountCorrect = TransactionFieldState.Error("Amount cannot be empty or 0")
            ) }
            return
        }

        val parent = currentState.selectedParentTransaction
        val createdAt = currentState.localDateCreatedAt.atTime(currentState.localTimeCreatedAt).toEpochMillis()

        val fulfillment = try {
            when (currentState.selectedFulfillmentTransactionType) {
                TransactionType.PLAN_FULFILLMENT -> {
                    if (parent !is Transaction.Plan) throw IllegalArgumentException("Parent must be Plan")
                    Transaction.PlanFulfillment(
                        value = amountValue,
                        createdAt = createdAt,
                        plan = parent,
                        label = labelText,
                        note = noteText,
                        status = PlanStatus.PENDING,
                        fulfillmentType = currentState.selectedPlanFulfillmentType
                    )
                }

                else -> throw IllegalArgumentException("Invalid fulfillment type")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error building fulfillment transaction", e)
            _planTabUiState.update {
                it.copy(
                    isSaving = false,
                    isFulfillmentValid = TransactionFieldState.Error(e.message ?: "Invalid fulfillment")
                )
            }
            return
        }

        // Immediately update state to indicate saving and close the sheet
        _planTabUiState.update { it.copy(
            isSaving = true,
        ) }

        viewModelScope.launch {
            try {
                // Perform the database operation
                addDataUseCase.addFulfillment(
                    parent.id,
                    fulfillment
                )

                _planTabUiState.update {
                    it.copy(
                        isSaving = true,
                        amount = TextFieldState(),
                        label = TextFieldState(),
                        note = TextFieldState(),
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error adding fulfillment transaction", e)
                _planTabUiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteFulfillmentPlan(
        fulfillmentId: String,
        transaction: Transaction
    ) {
        val id = _planTabUiState.value.selectedParentTransaction?.id ?: return

        viewModelScope.launch {
            dataDeletionUseCase.deleteFulfillment(
                id,
                fulfillmentId,
                transaction
            )
        }
    }
}
