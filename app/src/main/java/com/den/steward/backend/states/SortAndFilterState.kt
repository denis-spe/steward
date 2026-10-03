// Grace and truth came through JESUS CHRIST
package com.den.steward.backend.states

import androidx.compose.runtime.Immutable

@Immutable
data class SortAndFilterState(
    val filter: List<Filter> = listOf(Filter.ALL),
    val orderBy: OrderBy = OrderBy.DESCENDING,
    val sortBy: SortBy = SortBy.TIME,
    val isFilterExpanded: Boolean = false,
    val isSortByExpanded: Boolean = false,
    val isOrderByExpanded: Boolean = false,
)
