package com.den.steward.backend.states

import androidx.compose.runtime.Immutable
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.Sort
import java.time.LocalDate
import java.time.temporal.IsoFields

@Immutable
data class AllUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val periodType: PeriodType = PeriodType.WEEK,
    val sort: Sort = Sort.ASCENDING,
    val filter: Filter = Filter.ALL,
    val weekNumber: Int = selectedDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
)
