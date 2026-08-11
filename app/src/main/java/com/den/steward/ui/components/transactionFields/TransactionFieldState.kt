package com.den.steward.ui.components.transactionFields

sealed class TransactionFieldState {
    data object Initial : TransactionFieldState()
    data class Error(val message: String) : TransactionFieldState()
    data object Success : TransactionFieldState()
}