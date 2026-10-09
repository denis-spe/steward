// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.Filter.Companion.icon
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortAndFilterState
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.yesterdayTabState.YesterdayTabState
import com.den.steward.backend.states.yesterdayTabState.YesterdayTransactionSummary
import com.den.steward.backend.viewModels.YesterdayViewModel
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.DataDeletionDialog
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabListEmpty
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTabListError
import com.den.steward.ui.theme.ExtendedTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun YesterdayTabList(
    modifier: Modifier = Modifier,
    yesterdayViewModel: YesterdayViewModel,
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
                    yesterdayViewModel = yesterdayViewModel,
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
    yesterdayViewModel: YesterdayViewModel,
    transactions: ImmutableList<Transaction>,
    yesterdayTransactionSummary: YesterdayTransactionSummary,
    chartDataCollection: ChartDataCollection,
    sortAndFilterState: SortAndFilterState,
    updateIsFilterExpanded: (isExpanded: Boolean) -> Unit,
    updateIsSortByExpanded: (isExpanded: Boolean) -> Unit,
    updateIsOrderExpanded: (isExpanded: Boolean) -> Unit
) {
    var swipedState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    val scope = rememberCoroutineScope()
    val yesterdayUiState by yesterdayViewModel.yesterdayUiState.collectAsStateWithLifecycle()
    val dataUpdateState by yesterdayViewModel.dataUpdateState.collectAsStateWithLifecycle()

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
                val yesterdayItemDismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { distance: Float ->
                        distance * 0.5f
                    }
                )

                val shadowElevation = when (index) {
                    0 -> 4.dp
                    transactions.lastIndex -> 4.dp
                    else -> 2.dp
                }

                YesterdayTabLazyListItem(
                    dismissState = yesterdayItemDismissState,
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(1000),
                    ),
                    shadowElevation = shadowElevation,
                    transaction = transaction,
                    onDelete = {
                        swipedState = yesterdayItemDismissState
                        yesterdayViewModel.updateSelectedTransactionToDelete(it)
                    },
                    onUpdate = {
                        swipedState = yesterdayItemDismissState
                        yesterdayViewModel.updateTransaction.updateSelectedTransaction(transaction)
                    }
                )
            }
            item(
                key = "Space up"
            ) {
                Spacer(
                    modifier = Modifier.height(55.dp)
                )
            }
        } else {
            item {
                TodayTabListEmpty()
            }
        }
    }

    DataDeletionDialog(
        transaction = yesterdayUiState.selectedTransactionToDelete,
        onDialogShow = yesterdayUiState.openDeleteDialog,
        onDelete = yesterdayViewModel::deleteTransaction,
        onDismissRequest = {
            yesterdayViewModel.updateOpenDeleteDialog(false)
            scope.launch {
                swipedState?.reset()
            }
        }
    )

    UpdateTransactionBottomDrawerSheet(
        dataUpdateState = dataUpdateState,
        updateCorrectNote = yesterdayViewModel.updateTransaction::updateCurrentNote,
        updateCorrectLabel = yesterdayViewModel.updateTransaction::updateCurrentLabel,
        updateSelectedIcon = yesterdayViewModel.updateTransaction::updateSelectedIcon,
        updatePaymentMethod = yesterdayViewModel.updateTransaction::updatePaymentMethod,
        updateCorrectAmount = yesterdayViewModel.updateTransaction::updateCurrentAmount,
        updateIsLabelCorrect = yesterdayViewModel.updateTransaction::updateIsLabelCorrect,
        updateIsAmountCorrect = yesterdayViewModel.updateTransaction::updateIsAmountCorrect,
        updateIsAffectingAmount = yesterdayViewModel.updateTransaction::updateIsAffectingAmount,
        onLocalTimeChange = yesterdayViewModel.updateTransaction::onLocalTimeChangeUpdate,
        onLocalDateChange = yesterdayViewModel.updateTransaction::onLocalDateChangeUpdate,
        onExchange = yesterdayViewModel.updateTransaction::onExchange,
        onTransactionUpdate = {
            yesterdayViewModel.onUpdateTransaction()
            scope.launch {
                swipedState?.reset()
            }
        },
        reset =  {
            yesterdayViewModel.updateTransaction.onReset()
            scope.launch {
                swipedState?.reset()
            }
        }
    )
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
        filter.first().icon
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
                YesterdayTabListPanelButton(
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
                YesterdayTabListPanelButton(
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
                YesterdayTabListPanelButton(
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
fun YesterdayTabListPanelButton(
    text: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary
    else ExtendedTheme.colors.lightGray
    val contentColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
        colors = ButtonDefaults.textButtonColors().copy(
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Surface(
                color = backgroundColor,
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    icon()
                }
            }
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
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
            YesterdayTabLazyListItemShimmer()
        }
    }
}

@Composable
fun YesterdayTabListPanelButtonsShimmer() {
    val height = 40.dp

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
