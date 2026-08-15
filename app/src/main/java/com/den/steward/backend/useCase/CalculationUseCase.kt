package com.den.steward.backend.useCase

import javax.inject.Inject

class CalculationUseCase @Inject constructor(
    dataFilterUseCase: DataFilterUseCase
){
    val todayTransactions = dataFilterUseCase.todayTransactions
}