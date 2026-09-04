// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.helper.formattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AllTab(
    padding: PaddingValues,
    dataFetchViewModel: DataFetchViewModel,
    chartViewModel: ChartViewModel
) {
    val allViewModel: AllViewModel = hiltViewModel()

    val pagerState = rememberPagerState(
        initialPage = PeriodDataHandleUseCase.INITIAL_PAGE,
        pageCount = { Int.MAX_VALUE }
    )

    val allUiState by allViewModel.allUiState.collectAsStateWithLifecycle()

    LaunchedEffect(pagerState.currentPage) {
        allViewModel.onPageChange(pagerState.currentPage)
    }

    val getWeekDaysForPage = remember(pagerState.currentPage) {
        allViewModel.getWeekDaysForPage(pagerState.currentPage)
    }
    val coroutineScope = rememberCoroutineScope()

    val transactions by allViewModel.transactions.collectAsStateWithLifecycle()

    val onPeriodTypeChange = when(allUiState.periodType) {
        PeriodType.DAY -> Icons.Default.CalendarViewDay
        PeriodType.WEEK -> Icons.Default.CalendarViewWeek
        PeriodType.MONTH -> Icons.Default.CalendarViewMonth
        PeriodType.YEAR -> Icons.Default.CalendarMonth
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                allUiState.selectedDate.formattedDate,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeekView(
                pagerState = pagerState,
                weekDaysForPage = getWeekDaysForPage,
                selectedDate = allUiState.selectedDate
            ) {
                allViewModel.updateSelectedDate(it)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "week ${allUiState.weekNumber}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(PeriodDataHandleUseCase.INITIAL_PAGE)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = onPeriodTypeChange,
                        contentDescription = "period type",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = "sort",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "filter",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }


        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 3.dp)
        )
        Surface {
            when (val transactionsState = transactions) {
                is DataState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(transactionsState.data.size) { index ->
                            Text(transactionsState.data[index].toString())
                        }
                    }
                }

                is DataState.Loading -> {
                    Text("Loading")
                }

                is DataState.Error -> {
                    Text("Error")
                }
            }
        }
    }
}


@Composable
fun WeekView(
    pagerState: PagerState,
    weekDaysForPage: List<LocalDate>,
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    HorizontalPager(
        modifier = Modifier.fillMaxWidth(0.9f),
        state = pagerState
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDaysForPage.forEach { day ->
                    WeekDayView(
                        day = day,
                        isSelected = day == selectedDate
                    ) {
                        onDayClick(day)
                    }
                }
            }
        }
    }
}

@Composable
fun WeekDayView(
    day: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val materialTheme = MaterialTheme.colorScheme
    val isToday = remember(day) { day == LocalDate.now() }

    val textColor = when {
        isSelected -> materialTheme.onPrimary
        isToday -> materialTheme.secondary
        else -> materialTheme.onSurfaceVariant
    }

    val backgroundColor = if (isSelected) materialTheme.primary else Color.Transparent

    Column(
        modifier = Modifier
            .width(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = day.dayOfWeek.name.take(3),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )

        HorizontalDivider(
            color = textColor,
        )

        Text(
            text = day.dayOfMonth.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
        )
    }
}
