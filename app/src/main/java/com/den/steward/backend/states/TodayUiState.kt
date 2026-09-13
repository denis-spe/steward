package com.den.steward.backend.states

import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy

data class TodayUiState(
    val filter: Filter = Filter.ALL,
    val orderBy: OrderBy = OrderBy.DESCENDING,
    val sortBy: SortBy = SortBy.TIME,
    val isFilterExpanded: Boolean = false,
    val isSortByExpanded: Boolean = false,
    val isOrderByExpanded: Boolean = false,
)
