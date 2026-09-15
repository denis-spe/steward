// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.states.AllTransactionSummary
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.useCase.SortBy
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formattedDate
import com.den.steward.ui.componentExtenison.shimmerEffect
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeekView(
                pagerState = pagerState,
                weekDaysForPage = getWeekDaysForPage,
                selectedDate = allUiState.selectedDate
            ) {
                allViewModel.updateSelectedDate(it)
            }

            PeriodTypeSelector(
                currentPeriodType = allUiState.periodType,
                onPeriodTypeChange = allViewModel::updatePeriodType
            )

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

                Spacer(modifier = Modifier.width(4.dp))

                AllTabListPanelButtons(
                    filterSelected = allUiState.filter != Filter.ALL,
                    orderBySelected = allUiState.orderBy != OrderBy.ASCENDING,
                    sortBySelected = allUiState.sortBy != SortBy.TIME,
                    onFilterClick = { allViewModel.updateIsFilterExpanded(true) },
                    onSortByClick = { allViewModel.updateIsSortByExpanded(true) },
                    onOrderByClick = { allViewModel.updateIsOrderByExpanded(true) },
                    onResetClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(PeriodDataHandleUseCase.INITIAL_PAGE)
                        }
                    },
                    filterIcon = filterIcon,
                    orderByIcon = orderByIcon,
                    sortByIcon = sortByIcon
                )
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        AllTabLazyList(
            transactions = transactionsState,
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
            dataDeletionViewModel = dataDeletionViewModel
        )
    }
}

@Composable
fun AllTabSummaryCard(
    allTransactionSummary: DataState<AllTransactionSummary>,
    selectedDate: LocalDate,
    periodType: PeriodType
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (periodType) {
                    PeriodType.DAY -> selectedDate.formattedDate
                    PeriodType.WEEK -> "Weekly Summary"
                    PeriodType.MONTH -> "Monthly Summary"
                    PeriodType.YEAR -> "Yearly Summary"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            when (allTransactionSummary) {
                is DataState.Success -> {
                    val allTransactionSummary = allTransactionSummary.data

                    AllTabSummaryCardContent(
                        flow = allTransactionSummary.flow,
                        transactionSize = allTransactionSummary.transactionSize,
                        totalReceived = allTransactionSummary.totalReceived,
                        totalSpent = allTransactionSummary.totalSpent,
                        totalSavings = allTransactionSummary.totalSavings
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
fun AllTabSummaryCardContent(
    flow: Double,
    transactionSize: Int,
    totalReceived: Double,
    totalSpent: Double,
    totalSavings: Double
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Net Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = flow.formatToAmount(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (flow >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AllTabSummaryTransactionCard(
                icon = Icons.Default.ArrowUpward,
                text = "Total Received",
                amount = totalReceived,
                color = colorResource(R.color.earnings),
            )
            Spacer(
                modifier = Modifier.width(3.dp)
            )

            AllTabSummaryTransactionCard(
                icon = Icons.Default.ArrowDownward,
                text = "Total Spent",
                amount = totalSpent,
                color = colorResource(R.color.expense),
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AllTabSummaryTransactionCard(
                icon = Icons.Default.Savings,
                text = "Savings",
                amount = totalSavings,
                color = colorResource(R.color.debt),
            )

            Spacer(
                modifier = Modifier.width(3.dp)
            )

            AllTabSummaryTransactionCard(
                icon = Icons.Default.AcUnit,
                text = "Transaction Count",
                amount = transactionSize.toDouble(),
                color = colorResource(R.color.purple_200),
            )
        }
    }
}

@Composable
fun AllTabSummaryTransactionCard(
    icon: ImageVector,
    text: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val amountState = remember(amount) {
        amount.formatToAmount()
    }

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        shape = MaterialTheme.shapes.large,
        shadowElevation = 1.dp,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color.copy(
                                alpha = 0.2f
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = color
                        )
                    }
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = amountState,
                style = MaterialTheme.typography.labelMedium
                    .copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )
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
    sortByIcon: Int,
    onResetClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        stickyHeader {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
            ) {
                AllTabListPanelButton(
                    text = "Reset",
                    icon = R.drawable.filled_refund,
                    onClick = onResetClick
                )
            }
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

    val selectedColor = remember(
        isSelect,
    ) { if (isSelect) primaryColor.copy(alpha = 0.2f) else Color.Transparent }

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
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
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
