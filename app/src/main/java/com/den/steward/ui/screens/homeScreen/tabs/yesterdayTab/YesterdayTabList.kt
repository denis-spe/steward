// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortAndFilterState
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.yesterdayTabState.YesterdayTabState
import com.den.steward.backend.states.yesterdayTabState.YesterdayTransactionSummary
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.YesterdayViewModel
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import com.den.steward.ui.dataDeletion.DataDeletionDialog
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabLazyListItem
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabLazyListItemShimmer
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabListEmpty
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabListError
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabListPanelButton
import kotlinx.collections.immutable.ImmutableList

@Composable
fun YesterdayTabList(
    modifier: Modifier = Modifier,
    dataDeletionViewModel: DataDeletionViewModel,
    yesterdayTabState: DataState<YesterdayTabState>,
    sortAndFilterState: SortAndFilterState,
    updateIsFilterExpanded: (isExpanded: Boolean) -> Unit,
    updateIsSortByExpanded: (isExpanded: Boolean) -> Unit,
    updateIsOrderExpanded: (isExpanded: Boolean) -> Unit
) {

    Crossfade(
        targetState = yesterdayTabState,
        label = "YesterdayTabListCrossfade"
    ) { state ->
        when (state) {
            is DataState.Loading -> {
                YesterdayTabLazyListShimmer(
                    modifier = modifier,
                    numberOfShimmerItems = 5
                )
            }

            is DataState.Success -> {
                val (
                    yesterdayTransactionSummary,
                    transactions,
                    chartDataCollection
                ) = state.data

                YesterdayTabLazyList(
                    modifier = modifier,
                    dataDeletionViewModel = dataDeletionViewModel,
                    transactions = transactions,
                    yesterdayTransactionSummary = yesterdayTransactionSummary,
                    chartDataCollection = chartDataCollection,
                    sortAndFilterState = sortAndFilterState,
                    updateIsOrderExpanded = updateIsOrderExpanded,
                    updateIsFilterExpanded = updateIsFilterExpanded,
                    updateIsSortByExpanded = updateIsSortByExpanded
                )
            }

            is DataState.Error -> {
                TodayTabListError()
            }
        }
    }
}

@Composable
fun YesterdayTabListHeader() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text(
                "Yesterday's Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun YesterdayTabLazyList(
    modifier: Modifier = Modifier,
    dataDeletionViewModel: DataDeletionViewModel,
    transactions: ImmutableList<Transaction>,
    yesterdayTransactionSummary: YesterdayTransactionSummary,
    chartDataCollection: ChartDataCollection,
    sortAndFilterState: SortAndFilterState,
    updateIsFilterExpanded: (isExpanded: Boolean) -> Unit,
    updateIsSortByExpanded: (isExpanded: Boolean) -> Unit,
    updateIsOrderExpanded: (isExpanded: Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        item {
            YesterdayTabStatisticView(
                yesterdayTransactionSummary = yesterdayTransactionSummary,
                chartDataCollection = chartDataCollection
            )
        }

        stickyHeader {
            YesterdayTabListHeader()
            YesterdayTabListPanelButtons(
                filter = sortAndFilterState.filter,
                sortBy = sortAndFilterState.sortBy,
                orderBy = sortAndFilterState.orderBy,
                filterSelected = sortAndFilterState.filter != listOf(Filter.ALL),
                orderBySelected = sortAndFilterState.orderBy != OrderBy.DESCENDING,
                sortBySelected = sortAndFilterState.sortBy != SortBy.TIME,
                onFilterClick = {
                    updateIsFilterExpanded(true)
                },
                onSortByClick = {
                    updateIsSortByExpanded(true)
                },
                onOrderByClick = {
                    updateIsOrderExpanded(true)
                }
            )
        }

        if (transactions.isNotEmpty()) {
            items(
                transactions.size,
                key = { index -> "yesterday_${transactions[index].id}" }
            ) { index ->
                val transaction = transactions[index]

                TodayTabLazyListItem(
                    transaction = transaction,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(1000),
                    ),
                    onDelete = {
                        dataDeletionViewModel.updateSelectedTransaction(transaction)
                    }
                )
            }
        } else {
            item {
                TodayTabListEmpty()
            }
        }
    }


    DataDeletionDialog(
        viewModel = dataDeletionViewModel
    ) {
        dataDeletionViewModel.updateOnDialogShow(false)
    }
}

@Composable
fun YesterdayTabListPanelButtons(
    filter: List<Filter>,
    sortBy: SortBy,
    orderBy: OrderBy,
    filterSelected: Boolean,
    orderBySelected: Boolean,
    sortBySelected: Boolean,
    onFilterClick: () -> Unit,
    onSortByClick: () -> Unit,
    onOrderByClick: () -> Unit,
) {
    val iconSize = 20.dp

    val filterIcon = if (filter.size == 1) {
        when (filter.first()) {
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

    val orderByIcon = when (orderBy) {
        OrderBy.ASCENDING -> R.drawable.ascending_sort
        OrderBy.DESCENDING -> R.drawable.descending_sorting
    }

    val sortByIcon = when (sortBy) {
        SortBy.TIME -> R.drawable.time
        SortBy.AMOUNT -> R.drawable.outline_amount
        SortBy.LABEL -> R.drawable.outline_label
        SortBy.FULFILLED -> R.drawable.ic_refund
    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LazyRow (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item(key = "Order By") {
                TodayTabListPanelButton(
                    text = "Order",
                    selected = orderBySelected,
                    icon = {
                        Icon(
                            painter = painterResource(orderByIcon),
                            contentDescription = "Order",
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    onClick = onOrderByClick
                )
            }
            item(key = "Sort By") {
                TodayTabListPanelButton(
                    text = "Sort",
                    selected = sortBySelected,
                    icon = {
                        Icon(
                            painter = painterResource(sortByIcon),
                            contentDescription = "SortBy",
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    onClick = onSortByClick
                )
            }

            item(key = "Filter") {
                TodayTabListPanelButton(
                    text = "Filter",
                    selected = filterSelected,
                    icon = {
                        Icon(
                            painter = painterResource(filterIcon),
                            contentDescription = "filter",
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    onClick = onFilterClick
                )
            }
        }
    }
}

@Composable
fun YesterdayTabLazyListShimmer(
    modifier: Modifier = Modifier,
    numberOfShimmerItems: Int
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            YesterdayTabStatisticShimmer()
        }

        stickyHeader {
            YesterdayTabListHeader()
            YesterdayTabListPanelButtonsShimmer()
        }

        items(numberOfShimmerItems) {
            TodayTabLazyListItemShimmer()
        }
    }
}

@Composable
fun YesterdayTabListPanelButtonsShimmer() {
    val height = 46.dp

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LazyRow (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item(key = "Order By") {
                Box(
                    modifier = Modifier
                        .size(80.dp, height)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
            item(key = "Sort By") {
                Box(
                    modifier = Modifier
                        .size(90.dp, height)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }

            item(key = "Filter") {
                Box(
                    modifier = Modifier
                        .size(96.dp, height)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
        }
    }
}
