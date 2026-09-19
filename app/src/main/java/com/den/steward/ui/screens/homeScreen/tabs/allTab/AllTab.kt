// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.useCase.SortBy
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AllTab(
    padding: PaddingValues,
    allViewModel: AllViewModel = hiltViewModel(),
    dataDeletionViewModel: DataDeletionViewModel = hiltViewModel(),
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
    val allTransactionSummary by allViewModel.transactionSummary.collectAsStateWithLifecycle()
    val chartDataCollection by allViewModel.chartDataCollection.collectAsStateWithLifecycle()

    LaunchedEffect(pagerState.currentPage) {
        allViewModel.onPageChange(pagerState.currentPage)
    }

    val getWeekDaysForPage = remember(pagerState.currentPage) {
        allViewModel.getWeekDaysForPage(pagerState.currentPage)
    }

    val transactionCountsState by allViewModel.transactionCounts.collectAsStateWithLifecycle()

    val countList = remember(transactionCountsState, pagerState.currentPage) {
        val state = transactionCountsState
        if (state is DataState.Success) {
            val localDates = allViewModel.getWeekDaysForPage(pagerState.currentPage)
            localDates.map { date -> state.data[date] ?: 0 }
        } else {
            emptyList()
        }
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

    val periodTypeIcon = when (allUiState.periodType) {
        PeriodType.WEEK -> R.drawable.ic_week
        PeriodType.MONTH -> R.drawable.ic_month
        PeriodType.YEAR -> R.drawable.ic_year
        PeriodType.DAY -> R.drawable.ic_day
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
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            WeekView(
                pagerState = pagerState,
                weekDaysForPage = getWeekDaysForPage,
                selectedDate = allUiState.selectedDate,
                weekNumber = allUiState.weekNumber,
                counts = countList,
                onResetClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(PeriodDataHandleUseCase.INITIAL_PAGE)
                    }
                },

            ) {
                allViewModel.updateSelectedDate(it)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AllTabListPanelButtons(
                    filterSelected = allUiState.filter != Filter.ALL,
                    orderBySelected = allUiState.orderBy != OrderBy.ASCENDING,
                    sortBySelected = allUiState.sortBy != SortBy.TIME,
                    onFilterClick = { allViewModel.updateIsFilterExpanded(true) },
                    onSortByClick = { allViewModel.updateIsSortByExpanded(true) },
                    onOrderByClick = { allViewModel.updateIsOrderByExpanded(true) },
                    periodType = allUiState.periodType,
                    filterIcon = filterIcon,
                    orderByIcon = orderByIcon,
                    sortByIcon = sortByIcon,
                    onPeriodTypeClick = { allViewModel.updateIsPeriodTypeExpanded(true) },
                    periodTypeIcon = periodTypeIcon,
                    periodTypeSelected = allUiState.periodType != PeriodType.WEEK
                )
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        AllTabLazyList(
            transactions = transactionsState,
            allTransactionChartData = chartDataCollection,
            allTransactionSummary = allTransactionSummary,
            selectedDate = allUiState.selectedDate,
            periodType = allUiState.periodType,
            allUiState = allUiState,
            updateFilter = allViewModel::updateFilter,
            updateSort = allViewModel::updateSort,
            updateSortType = allViewModel::updateSortType,
            updateIsFilterExpanded = allViewModel::updateIsFilterExpanded,
            updateIsOrderByExpanded = allViewModel::updateIsOrderByExpanded,
            updateIsSortByExpanded = allViewModel::updateIsSortByExpanded,
            updateSelectedTransactionForView = allViewModel::updateSelectedTransactionForView,
            updateIsPeriodTypeExpanded = allViewModel::updateIsPeriodTypeExpanded,
            updatePeriodType = allViewModel::updatePeriodType,
            dataDeletionViewModel = dataDeletionViewModel
        )
    }
}


@Composable
fun AllTabListPanelButtons(
    filterSelected: Boolean,
    orderBySelected: Boolean,
    sortBySelected: Boolean,
    periodTypeSelected: Boolean,
    onFilterClick: () -> Unit,
    onSortByClick: () -> Unit,
    onOrderByClick: () -> Unit,
    onPeriodTypeClick: () -> Unit,
    periodType: PeriodType,
    filterIcon: Int,
    orderByIcon: Int,
    sortByIcon: Int,
    periodTypeIcon: Int
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item(key = "Period Type") {
            AllTabListPanelButton(
                text = periodType.name.lowercase().replaceFirstChar { it.uppercase() },
                isSelect = periodTypeSelected,
                icon = periodTypeIcon,
                onClick = onPeriodTypeClick
            )
        }

        item(key = "Order By") {
            AllTabListPanelButton(
                text = "Order",
                isSelect = orderBySelected,
                icon = orderByIcon,
                onClick = onOrderByClick
            )
        }
        item(key = "Sort By") {
            AllTabListPanelButton(
                text = "Sort",
                isSelect = sortBySelected,
                icon = sortByIcon,
                onClick = onSortByClick
            )
        }

        item(key = "Filter") {
            AllTabListPanelButton(
                text = "Filter",
                isSelect = filterSelected,
                icon = filterIcon,
                onClick = onFilterClick
            )
        }
    }
}

@Composable
fun AllTabListPanelButton(
    text: String,
    icon: Int,
    isSelect: Boolean = false,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.onSurface

    val selectedColor = remember(
        isSelect,
    ) { if (isSelect) primaryColor.copy(alpha = 0.2f) else Color.Transparent }

    val iconColor = remember(
        isSelect
    ) {
        if (isSelect) primaryColor else surfaceColor
    }

    val borderColor = remember(
        isSelect,
    ) { if (isSelect) primaryColor else null }

    val border = if (borderColor != null) {
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        } else {
            null
        }

    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults
            .outlinedButtonColors()
            .copy(
                containerColor = selectedColor,
            ),
        border = border,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


