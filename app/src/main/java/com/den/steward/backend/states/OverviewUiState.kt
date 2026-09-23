package com.den.steward.backend.states

data class OverviewUiState(
    val orderBy: OrderBy = OrderBy.DESCENDING,
    val limitTransactionSize: Int = 10,
    val sortBy: SortBy = SortBy.TIME
)
