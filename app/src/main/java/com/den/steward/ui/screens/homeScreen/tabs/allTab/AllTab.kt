// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabListPanelButton

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.useCase.SortBy
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.helper.formattedDate
import com.den.steward.ui.components.FilterBottomSheet
import com.den.steward.ui.components.OrderByBottomSheet
import com.den.steward.ui.components.SortByBottomSheet
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AllTab(
    padding: PaddingValues,
    allViewModel: AllViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel
) {
    val pagerState = rememberPagerState(
        initialPage = PeriodDataHandleUseCase.INITIAL_PAGE,
        pageCount = { Int.MAX_VALUE }
    )

    val allUiState by allViewModel.allUiState.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(homeUiState.allTabFilter) {
        homeUiState.allTabFilter?.let {
            allViewModel.updateFilter(it)
            homeViewModel.clearAllTabFilter()
        }
    }

    val transactionsState by allViewModel.transactions.collectAsStateWithLifecycle()

    LaunchedEffect(pagerState.currentPage) {
        allViewModel.onPageChange(pagerState.currentPage)
    }

    val getWeekDaysForPage = remember(pagerState.currentPage) {
        allViewModel.getWeekDaysForPage(pagerState.currentPage)
    }
    val coroutineScope = rememberCoroutineScope()

    val filterIcon = when (allUiState.filter) {
        Filter.ALL -> R.drawable.filter
        Filter.EARNINGS -> R.drawable.ic_earnings
        Filter.EXPENSE -> R.drawable.ic_expense
        Filter.GOAL -> R.drawable.ic_finance_target
        Filter.SAVINGS -> R.drawable.ic_savings
        Filter.REPAYMENT -> R.drawable.ic_repayment
        Filter.SETTLEMENT -> R.drawable.ic_refund
        Filter.ATTAIN -> R.drawable.ic_attain
        Filter.LENT -> R.drawable.ic_loan
        Filter.DEBT -> R.drawable.ic_debt
    }

    val orderByIcon = when (allUiState.orderBy) {
        OrderBy.ASCENDING -> R.drawable.ascending_sort
        OrderBy.DESCENDING -> R.drawable.descending_sorting
    }

    val sortByIcon = when (allUiState.sortBy) {
        SortBy.TIME -> R.drawable.time
        SortBy.AMOUNT -> R.drawable.outline_amount
        SortBy.LABEL -> R.drawable.outline_label
        SortBy.FULFILLED -> R.drawable.ic_refund
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // --- Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeekView(
                pagerState = pagerState,
                weekDaysForPage = getWeekDaysForPage,
                selectedDate = allUiState.selectedDate
            ) {
                allViewModel.updateSelectedDate(it)
            }
        }

        // --- Controls Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Week ${allUiState.weekNumber ?: ""}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AllTabListPanelButtons(
                filterSelected = allUiState.filter != Filter.ALL,
                orderBySelected = allUiState.orderBy != OrderBy.ASCENDING,
                sortBySelected = allUiState.sortBy != SortBy.TIME,
                onFilterClick = { allViewModel.updateIsFilterExpanded(true) },
                onSortByClick = { allViewModel.updateIsSortByExpanded(true) },
                onOrderByClick = { allViewModel.updateIsOrderByExpanded(true) },
                filterIcon = filterIcon,
                orderByIcon = orderByIcon,
                sortByIcon = sortByIcon
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        AllTabLazyList(
            transactions = transactionsState,
            allViewModel = allViewModel,
            selectedDate = allUiState.selectedDate,
            periodType = allUiState.periodType
        )
    }

    // --- Bottom Sheets ---
    FilterBottomSheet(
        isExpanded = allUiState.isFilterExpanded,
        selected = allUiState.filter,
        onFilterSelected = {
            allViewModel.updateFilter(it)
            allViewModel.updateIsFilterExpanded(false)
        },
        onDismiss = { allViewModel.updateIsFilterExpanded(false) }
    )

    OrderByBottomSheet(
        isExpanded = allUiState.isOrderByExpanded,
        selected = allUiState.orderBy,
        onSortSelected = {
            allViewModel.updateSort(it)
            allViewModel.updateIsOrderByExpanded(false)
        },
        onDismiss = { allViewModel.updateIsOrderByExpanded(false) }
    )

    SortByBottomSheet(
        isExpanded = allUiState.isSortByExpanded,
        selected = allUiState.sortBy,
        onSortSelected = {
            allViewModel.updateSortType(it)
            allViewModel.updateIsSortByExpanded(false)
        },
        onDismiss = { allViewModel.updateIsSortByExpanded(false) }
    )
}

@Composable
fun AllTabSummaryCard(
    transactionsState: DataState<Map<String, List<Transaction>>>,
    allViewModel: AllViewModel,
    selectedDate: LocalDate,
    periodType: PeriodType
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when(periodType) {
                    PeriodType.DAY -> selectedDate.formattedDate
                    PeriodType.WEEK -> "Weekly Summary"
                    PeriodType.MONTH -> "Monthly Summary"
                    PeriodType.YEAR -> "Yearly Summary"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            when (transactionsState) {
                is DataState.Success -> {
                    val allList = remember(transactionsState.data) { 
                        transactionsState.data.values.flatten() 
                    }
                    val flow = remember(allList) { allViewModel.calculateFlow(allList) }
                    Text(
                        text = flow.formatToAmount(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (flow >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                is DataState.Loading -> {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(32.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                }
                is DataState.Error -> {
                    Text(text = "Error loading summary", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PeriodTypeSelector(
    currentPeriodType: PeriodType,
    onPeriodTypeChange: (PeriodType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeriodType.entries.forEach { type ->
            val isSelected = currentPeriodType == type
            AssistChip(
                onClick = { onPeriodTypeChange(type) },
                label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
    }
}

@Composable
fun AllTabListPanelButtons(
    filterSelected: Boolean,
    orderBySelected: Boolean,
    sortBySelected: Boolean,
    onFilterClick: () -> Unit,
    onSortByClick: () -> Unit,
    onOrderByClick: () -> Unit,
    filterIcon: Int,
    orderByIcon: Int,
    sortByIcon: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TodayTabListPanelButton(
            text = "Order",
            selected = orderBySelected,
            icon = {
                Icon(
                    painter = painterResource(orderByIcon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = onOrderByClick
        )
        TodayTabListPanelButton(
            text = "Sort",
            selected = sortBySelected,
            icon = {
                Icon(
                    painter = painterResource(sortByIcon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = onSortByClick
        )
        TodayTabListPanelButton(
            text = "Filter",
            selected = filterSelected,
            icon = {
                Icon(
                    painter = painterResource(filterIcon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = onFilterClick
        )
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
        modifier = Modifier.fillMaxWidth(0.95f),
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
