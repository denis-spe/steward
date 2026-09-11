// Bless be the LORD GOD
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.useCase.DataDeletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DataDeletionViewModel @Inject constructor(
    private val dataDeletionUseCase: DataDeletionUseCase
) : ViewModel() {

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction = _selectedTransaction.asStateFlow()

    private val _onDialogShow = MutableStateFlow(false)
    val onDialogShow = _onDialogShow.asStateFlow()


    fun deleteTransaction(
    ) {
        val transactionId = _selectedTransaction.value?.id ?: return

        viewModelScope.launch {
            dataDeletionUseCase.deleteTransaction(transactionId)
        }

        // Reset the state
        _selectedTransaction.value = null
        _onDialogShow.value = false
    }

    fun deleteFulfillment() {
        if (selectedTransaction.value == null) return
        val transactionId = when(selectedTransaction.value!!.type) {
            TransactionType.ATTAIN -> (selectedTransaction.value as Transaction.Attain).goal.id
            TransactionType.REPAYMENT -> (selectedTransaction.value as Transaction.Repayment).lent.id
            TransactionType.REFUND -> (selectedTransaction.value as Transaction.Refund).debt.id
            else -> return
        }
        val fulfillmentId = when(selectedTransaction.value!!.type) {
            TransactionType.ATTAIN -> (selectedTransaction.value as Transaction.Attain).id
            TransactionType.ACHIEVEMENT -> (selectedTransaction.value as Transaction.Achievement).id
            TransactionType.REPAYMENT -> (selectedTransaction.value as Transaction.Repayment).id
            TransactionType.REFUND -> (selectedTransaction.value as Transaction.Refund).id
            else -> return
        }
        val fulfillmentType = selectedTransaction.value ?: return

        viewModelScope.launch {
            dataDeletionUseCase.deleteFulfillment(transactionId, fulfillmentId, fulfillmentType)
        }

        // Reset the state
        _selectedTransaction.value = null
        _onDialogShow.value = false
    }

    fun updateSelectedTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
        _onDialogShow.value = true
    }

    fun updateOnDialogShow(value: Boolean) {
        _onDialogShow.value = value
    }
}