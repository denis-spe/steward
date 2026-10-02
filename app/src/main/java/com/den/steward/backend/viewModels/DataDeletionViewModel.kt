// Bless be the LORD GOD
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.Transaction
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
        viewModelScope.launch {
            val transactionId = _selectedTransaction.value ?: return@launch
            dataDeletionUseCase.deleteTransaction(transactionId)
        }

        // Reset the state
        _selectedTransaction.value = null
        _onDialogShow.value = false
    }

    fun deleteFulfillment() {
        viewModelScope.launch {
            _selectedTransaction.value?.let {
                dataDeletionUseCase.deleteTransaction(it)
            }
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