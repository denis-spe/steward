package com.den.steward.backend.states

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import java.time.LocalDate
import java.time.temporal.IsoFields

@Immutable
data class AllUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val periodType: PeriodType = PeriodType.WEEK,
    val orderBy: OrderBy = OrderBy.ASCENDING,
    val sortBy: SortBy = SortBy.TIME,
    val filter: Filter = Filter.ALL,
    val isFilterExpanded: Boolean = false,
    val isOrderByExpanded: Boolean = false,
    val isSortByExpanded: Boolean = false,
    val isPeriodTypeExpanded: Boolean = false,
    val weekNumber: Int? = selectedDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
    val selectedTransactionForView: Transaction? = null
)