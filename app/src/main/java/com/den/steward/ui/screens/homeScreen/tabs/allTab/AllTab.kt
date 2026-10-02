// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import kotlinx.coroutines.launch

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
    val sortAndFilterState by allViewModel.sortAndFilterState.collectAsStateWithLifecycle()
    val allTabDataState by allViewModel.allTabDataState.collectAsStateWithLifecycle()
    val transactions by allViewModel.transactions.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(homeUiState.allTabFilter) {
        homeUiState.allTabFilter?.let {
            allViewModel.updateFilter(it)
            homeViewModel.clearAllTabFilter()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .collect { page ->
                allViewModel.onPageChange(page)
            }
    }

    val settledPage = pagerState.settledPage
    val weekDaysForPage = remember(settledPage) { allViewModel.getWeekDaysForPage(settledPage) }
    val transactionCountsState by remember(settledPage) {
        allViewModel.transactionCounts(settledPage)
    }.collectAsStateWithLifecycle()
    val transactionCountsByDate by allViewModel.transactionCountsByDate.collectAsStateWithLifecycle()



    val coroutineScope = rememberCoroutineScope()

    val filterIcon = if (sortAndFilterState.filter.size == 1) {
        when (sortAndFilterState.filter.first()) {
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
            Filter.PLAN -> R.drawable.ic_plan
        }
    } else {
        R.drawable.filter
    }

    val orderByIcon = when (sortAndFilterState.orderBy) {
        OrderBy.ASCENDING -> R.drawable.ascending_sort
        OrderBy.DESCENDING -> R.drawable.descending_sorting
    }

    val sortByIcon = when (sortAndFilterState.sortBy) {
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

    val transactionListSortName = when(allUiState.isTransactionListSort) {
        OrderBy.ASCENDING -> "Oldest"
        OrderBy.DESCENDING -> "Latest"
    }

    val transactionListSortIcon = when(allUiState.isTransactionListSort) {
        OrderBy.DESCENDING -> R.drawable.ic_sort_latest
        OrderBy.ASCENDING -> R.drawable.ascending_sort
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
                weekDaysForPage = weekDaysForPage,
                selectedDate = allUiState.selectedDate,
                weekNumber = allUiState.weekNumber,
                counts = transactionCountsState,
                periodType = allUiState.periodType,
                moreInfo = { day ->
                    val (flow, count) = transactionCountsByDate
                    val dayCount = count[day.date]
                    val dayFlow = flow[day.date]
                    val color = if (dayFlow == -1)
                        MaterialTheme.colorScheme.error
                    else Color(0xFF3BA429)


                    if (dayCount != null) {
                        Card(
                            colors = CardDefaults.cardColors().copy(
                                containerColor = color.copy(0.3f).compositeOver(
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    dayCount.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = color
                                )
                            }
                        }
                    }
                },
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
                    filterSelected = sortAndFilterState.filter != listOf(Filter.ALL),
                    orderBySelected = sortAndFilterState.orderBy != OrderBy.DESCENDING,
                    sortBySelected = sortAndFilterState.sortBy != SortBy.TIME,
                    onFilterClick = { allViewModel.updateIsFilterExpanded(true) },
                    onSortByClick = { allViewModel.updateIsSortByExpanded(true) },
                    onOrderByClick = { allViewModel.updateIsOrderByExpanded(true) },
                    periodType = allUiState.periodType,
                    filterIcon = filterIcon,
                    orderByIcon = orderByIcon,
                    sortByIcon = sortByIcon,
                    onPeriodTypeClick = { allViewModel.updateIsPeriodTypeExpanded(true) },
                    periodTypeIcon = periodTypeIcon,
                    periodTypeSelected = allUiState.periodType != PeriodType.WEEK,
                    isTransactionListSort = allUiState.isTransactionListSort != OrderBy.DESCENDING,
                    transactionListSortName = transactionListSortName,
                    transactionListSortIcon = transactionListSortIcon,
                    onTransactionListSort = { allViewModel.updateIsTransactionListOrderExpanded(isExpanded = true) }
                )
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        AllTabLazyList(
            allTabDataState = allTabDataState,
            transactions = transactions,
            selectedDate = allUiState.selectedDate,
            periodType = allUiState.periodType,
            allUiState = allUiState,
            sortAndFilterState = sortAndFilterState,
            updateFilter = allViewModel::updateFilter,
            updateSort = allViewModel::updateSort,
            updateIsTransactionListOrder = allViewModel::updateIsTransactionListOrder,
            updateIsTransactionListOrderExpanded = allViewModel::updateIsTransactionListOrderExpanded,
            updateSortType = allViewModel::updateSortType,
            updateIsFilterExpanded = allViewModel::updateIsFilterExpanded,
            updateIsOrderByExpanded = allViewModel::updateIsOrderByExpanded,
            updateIsSortByExpanded = allViewModel::updateIsSortByExpanded,
            updateSelectedTransactionForView = allViewModel::updateSelectedTransactionForView,
            updateIsPeriodTypeExpanded = allViewModel::updateIsPeriodTypeExpanded,
            updatePeriodType = allViewModel::updatePeriodType,
            updateSelectedTransactionToDelete = allViewModel::updateSelectedTransactionToDelete,
            updateOpenDeleteDialog = allViewModel::updateOpenDeleteDialog,
            onDeleteTransaction = allViewModel::deleteTransaction
        )
    }
}


@Composable
fun AllTabListPanelButtons(
    filterSelected: Boolean,
    orderBySelected: Boolean,
    sortBySelected: Boolean,
    periodTypeSelected: Boolean,
    isTransactionListSort: Boolean,
    onFilterClick: () -> Unit,
    onSortByClick: () -> Unit,
    onOrderByClick: () -> Unit,
    onPeriodTypeClick: () -> Unit,
    onTransactionListSort: () -> Unit,
    periodType: PeriodType,
    transactionListSortName: String,
    transactionListSortIcon: Int,
    filterIcon: Int,
    orderByIcon: Int,
    sortByIcon: Int,
    periodTypeIcon: Int,

) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item(key = "Transaction list sort") {
            AllTabListPanelButton(
                text = transactionListSortName,
                isSelect = isTransactionListSort,
                icon = transactionListSortIcon,
                onClick = onTransactionListSort
            )
        }

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


