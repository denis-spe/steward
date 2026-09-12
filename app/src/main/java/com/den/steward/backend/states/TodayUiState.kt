package com.den.steward.backend.states

import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.Sort
import com.den.steward.backend.useCase.SortType

data class TodayUiState(
    val filter: Filter = Filter.ALL,
    val sort: Sort = Sort.DESCENDING,
    val sortType: SortType = SortType.DATE,
    val isFilterExpanded: Boolean = false,
    val isSortExpanded: Boolean = false,
    val isSortTypeExpanded: Boolean = false,
)
