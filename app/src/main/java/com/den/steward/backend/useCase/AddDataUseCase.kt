package com.den.steward.backend.useCase

import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.repoInterfaces.Account
import com.den.steward.backend.repoInterfaces.Storage
import javax.inject.Inject

class AddDataUseCase @Inject constructor(
    private val accountService: Account,
    private val storageService: Storage
) {
    val userId = accountService.currentUserId

    suspend fun addLoan(loan: Transaction.Loan) {
        storageService.addLoan(userId, loan)
    }

    suspend fun addRepayment(loanId: String, repayment: Transaction.Repayment) {
        storageService.addRepayment(userId, loanId, repayment)
    }

    suspend fun addEarnings(earnings: Transaction.Earning) {
        storageService.addEarnings(userId, earnings)
    }
}