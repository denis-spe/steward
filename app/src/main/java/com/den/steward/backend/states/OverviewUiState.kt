package com.den.steward.backend.states

import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy

data class OverviewUiState(
    val orderBy: OrderBy = OrderBy.DESCENDING,
    val limitTransactionSize: Int = 10,
    val sortBy: SortBy = SortBy.TIME
)
