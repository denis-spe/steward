package com.den.steward.backend.states

import com.den.steward.backend.useCase.Sort
import com.den.steward.backend.useCase.SortType

data class OverviewUiState(
    val sort: Sort = Sort.DESCENDING,
    val limitTransactionSize: Int = 10,
    val sortType: SortType = SortType.DATE
)
