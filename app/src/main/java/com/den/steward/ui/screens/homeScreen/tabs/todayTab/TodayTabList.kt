// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.TransactionViewDialog
import com.den.steward.ui.dataDeletion.DataDeletionDialog
import com.den.steward.ui.theme.ExtendedTheme

private val icon_size = 80.dp

@Composable
fun TodayTabList(
    modifier: Modifier = Modifier,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel,
    dataDeletionViewModel: DataDeletionViewModel
) {
    val transactionsState by todayViewModel.todayTransactions.collectAsStateWithLifecycle()
    val donutChartState by chartViewModel.donutChart.collectAsStateWithLifecycle()

    val combinedState = remember(transactionsState, donutChartState) {
        when {
            transactionsState is DataState.Loading || donutChartState is DataState.Loading -> DataState.Loading
            transactionsState is DataState.Error -> transactionsState
            donutChartState is DataState.Error -> donutChartState
            else -> transactionsState // Both Success
        }
    }

    Crossfade(
        targetState = combinedState,
        label = "TodayTabListCrossfade"
    ) { state ->
        when (state) {
            is DataState.Loading -> {
                TodayTabLazyListShimmer(
                    modifier = modifier,
                    numberOfShimmerItems = 5
                )
            }

            is DataState.Success -> {
                val transactions = remember(state) {
                    ((state as DataState.Success<*>).data as List<*>)
                        .filterIsInstance<Transaction>()
                }

                TodayTabLazyList(
                    modifier = modifier,
                    chartViewModel = chartViewModel,
                    todayViewModel = todayViewModel,
                    dataDeletionViewModel = dataDeletionViewModel,
                    transactions = transactions
                )
            }

            is DataState.Error -> {
                TodayTabListError()
            }
        }
    }
}

@Composable
fun TodayTabListHeader() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text(
                "Today's Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun TodayTabListPanelButtons(
    filter: Filter,
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

    val filterIcon = when (filter) {
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
fun TodayTabListPanelButtonsShimmer() {
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

@Composable
fun TodayTabListPanelButton(
    text: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else ExtendedTheme.colors.lightGray,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        ),
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 5.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        icon()
    }
}

@Composable
fun TodayTabLazyList(
    modifier: Modifier = Modifier,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel,
    dataDeletionViewModel: DataDeletionViewModel,
    transactions: List<Transaction>,
) {
    val todayUiState by todayViewModel.todayUiState.collectAsStateWithLifecycle()
    val selectedTransactionForView = remember { mutableStateOf<Transaction?>(null) }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        item {
            TodayTabStatisticView(
                chartViewModel = chartViewModel,
                todayViewModel = todayViewModel
            )
        }

        stickyHeader {
            TodayTabListHeader()
            TodayTabListPanelButtons(
                filter = todayUiState.filter,
                sortBy = todayUiState.sortBy,
                orderBy = todayUiState.orderBy,
                filterSelected = todayUiState.filter != Filter.ALL,
                orderBySelected = todayUiState.orderBy != OrderBy.DESCENDING,
                sortBySelected = todayUiState.sortBy != SortBy.TIME,
                onFilterClick = {
                    todayViewModel.updateIsFilterExpanded(true)
                },
                onSortByClick = {
                    todayViewModel.updateIsSortByExpanded(true)
                },
                onOrderByClick = {
                    todayViewModel.updateIsOrderExpanded(true)
                }
            )
        }

        if (transactions.isNotEmpty()) {
            items(
                transactions.size,
                key = { index -> "today_${transactions[index].id}" }
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
                    },
                    onClick = {
                        selectedTransactionForView.value = transaction
                    }
                )
            }
        } else {
            item {
                TodayTabListEmpty()
            }
        }
    }

    selectedTransactionForView.value?.let { transaction ->
        TransactionViewDialog(
            transaction = transaction,
            onShow = true,
            onDismissRequest = { selectedTransactionForView.value = null }
        )
    }

    DataDeletionDialog(
        viewModel = dataDeletionViewModel
    ) {
        dataDeletionViewModel.updateOnDialogShow(false)
    }
}

@Composable
fun TodayTabLazyListShimmer(
    modifier: Modifier = Modifier,
    numberOfShimmerItems: Int
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            TodayTabStatisticShimmer()
        }

        stickyHeader {
            TodayTabListHeader()
            TodayTabListPanelButtonsShimmer()
        }

        items(numberOfShimmerItems) {
            TodayTabLazyListItemShimmer()
        }
    }
}


@Composable
fun TodayTabListEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_empty_transactions),
            contentDescription = "No Transactions",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No Transactions Yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Add your first transaction to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TodayTabListError(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.failed),
            contentDescription = "Error",
            modifier = Modifier.size(icon_size)
        )
        Text(
            "Error",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
        )
    }
}
