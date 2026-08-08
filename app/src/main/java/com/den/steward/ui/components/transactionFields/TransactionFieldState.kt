package com.den.steward.ui.components.transactionFields

sealed class TransactionFieldState {
    data object Initial : TransactionFieldState()
    data object Error : TransactionFieldState()
    data object Success : TransactionFieldState()
}