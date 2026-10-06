// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.Filter.Companion.icon
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.todayTabState.TodayTabDataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.TransactionViewDialog
import com.den.steward.ui.components.DataDeletionDialog
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import com.den.steward.ui.theme.ExtendedTheme
import kotlinx.coroutines.launch

private val icon_size = 80.dp

@Composable
fun TodayTabList(
    modifier: Modifier = Modifier,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel,
    dataDeletionViewModel: DataDeletionViewModel,
    todayTabDataState: DataState<TodayTabDataState>,
) {

    Crossfade(
        targetState = todayTabDataState,
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

                TodayTabLazyList(
                    modifier = modifier,
                    todayViewModel = todayViewModel,
                    todayTabDataState = state.data
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
    todayViewModel: TodayViewModel,
    todayTabDataState: TodayTabDataState
) {
    val sortAndFilterState by todayViewModel.sortAndFilterState.collectAsStateWithLifecycle()
    val todayUiState by todayViewModel.todayUiState.collectAsStateWithLifecycle()
    val selectedTransactionForView = remember { mutableStateOf<Transaction?>(null) }
    val dataUpdateState by todayViewModel.dataUpdateState.collectAsStateWithLifecycle()

    val (
        transactions,
        donutChartData,
        balanceStatStates,
        liabilitiesPaymentStatsState,
        donutSummaryStat
    ) = todayTabDataState

    val flow = remember(balanceStatStates) {
        balanceStatStates.flow.formatToAmount()
    }

    var swipedState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        item {
            TodayTabStatisticView(
                donutChartData = donutChartData,
                balanceStatStates = balanceStatStates,
                liabilitiesPaymentStatsState = liabilitiesPaymentStatsState,
                donutSummaryStat = donutSummaryStat,
                donutChartCenterAmount = flow
            )
        }

        stickyHeader {
            TodayTabListHeader()
            TodayTabListPanelButtons(
                filter = sortAndFilterState.filter,
                sortBy = sortAndFilterState.sortBy,
                orderBy = sortAndFilterState.orderBy,
                filterSelected = sortAndFilterState.filter != listOf(Filter.ALL),
                orderBySelected = sortAndFilterState.orderBy != OrderBy.DESCENDING,
                sortBySelected = sortAndFilterState.sortBy != SortBy.TIME,
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
                val dismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { distance: Float ->
                        distance * 0.5f
                    }
                )

                TodayTabLazyListItem(
                    dismissState = dismissState,
                    transaction = transaction,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(1000),
                    ),
                    onDelete = {
                        swipedState = dismissState
                        todayViewModel.updateSelectedTransactionToDelete(it)
                    },
                    onClick = {
                        selectedTransactionForView.value = transaction
                    },
                    onUpdate = {
                        swipedState = dismissState
                        todayViewModel.updateTransaction.updateSelectedTransaction(transaction)
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

    selectedTransactionForView.value?.let { transaction ->
        TransactionViewDialog(
            transaction = transaction,
            onShow = true,
            onDismissRequest = { selectedTransactionForView.value = null }
        )
    }

    DataDeletionDialog(
        transaction = todayUiState.selectedTransactionToDelete,
        onDialogShow = todayUiState.openDeleteDialog,
        onDelete = todayViewModel::deleteTransaction,
    ) {
        todayViewModel.updateOpenDeleteDialog(false)
        scope.launch {
            swipedState?.reset()
        }
    }

    UpdateTransactionBottomDrawerSheet(
        dataUpdateState = dataUpdateState,
        updateCorrectNote = todayViewModel.updateTransaction::updateCurrentNote,
        updateCorrectLabel = todayViewModel.updateTransaction::updateCurrentLabel,
        updateSelectedIcon = todayViewModel.updateTransaction::updateSelectedIcon,
        updatePaymentMethod = todayViewModel.updateTransaction::updatePaymentMethod,
        updateCorrectAmount = todayViewModel.updateTransaction::updateCurrentAmount,
        updateIsLabelCorrect = todayViewModel.updateTransaction::updateIsLabelCorrect,
        updateIsAmountCorrect = todayViewModel.updateTransaction::updateIsAmountCorrect,
        updateIsAffectingAmount = todayViewModel.updateTransaction::updateIsAffectingAmount,
        onLocalTimeChange = todayViewModel.updateTransaction::onLocalTimeChangeUpdate,
        onLocalDateChange = todayViewModel.updateTransaction::onLocalDateChangeUpdate,
        onTransactionUpdate = {
            todayViewModel.onUpdateTransaction()
            scope.launch {
                swipedState?.reset()
            }
        },
        reset =  {
            todayViewModel.updateTransaction.onReset()
            scope.launch {
                swipedState?.reset()
            }
        }
    )
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
