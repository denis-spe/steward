package com.den.steward.backend.states.allTabState

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.states.SortBy
import java.time.LocalDate
import java.time.temporal.IsoFields

@Immutable
data class AllUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val periodType: PeriodType = PeriodType.WEEK,
    val isTransactionListSort: OrderBy = OrderBy.DESCENDING,
    val isPeriodTypeExpanded: Boolean = false,
    val weekNumber: Int? = selectedDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
    val selectedTransactionForView: Transaction? = null,
    val isTransactionListSortExpanded: Boolean = false,
    val selectedTransactionToDelete: Transaction? = null,
    val selectedTransactionToEdit: Transaction? = null,
    val openDeleteDialog: Boolean = false,
    val openEditDialog: Boolean = false
)