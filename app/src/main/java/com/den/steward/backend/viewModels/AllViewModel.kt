package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.AllUiState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.useCase.Sort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.IsoFields
import javax.inject.Inject

@HiltViewModel
class AllViewModel @Inject constructor(
    private val periodDataHandleUseCase: PeriodDataHandleUseCase
) : ViewModel() {
    private val _allUiState = MutableStateFlow(AllUiState())
    val allUiState = _allUiState.asStateFlow()

    fun getWeekDaysForPage(page: Int): List<LocalDate> {
        return periodDataHandleUseCase.getWeekDaysForPage(
            page = page
        )
    }

    fun onPageChange(page: Int) {
        val localDates = getWeekDaysForPage(page)
        _allUiState.update {
            it.copy(
                selectedDate = localDates[0],
                periodType = PeriodType.WEEK,
                weekNumber = localDates[0].get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
            )
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        _allUiState.update {
            it.copy(
                selectedDate = date,
                periodType = PeriodType.DAY
            )
        }
    }

    fun updatePeriodType(periodType: PeriodType) {
        _allUiState.update {
            it.copy(
                periodType = periodType
            )
        }
    }

    fun updateSort(sort: Sort) {
        _allUiState.update {
            it.copy(
                sort = sort
            )
        }
    }

    fun updateFilter(filter: Filter) {
        _allUiState.update {
            it.copy(
                filter = filter
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = allUiState.flatMapLatest { state ->
        periodDataHandleUseCase.getTransactionsForPeriod(
            date = state.selectedDate,
            periodType = state.periodType,
            sort = state.sort,
            filter = state.filter
        ).distinctUntilChanged()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DataState.Loading
    )
}