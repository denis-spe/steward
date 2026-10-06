// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.allTabState.AllUiState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.DataUpdateState
import com.den.steward.backend.states.SortAndFilterState
import com.den.steward.ui.components.FilterBottomSheet
import com.den.steward.ui.components.OrderByBottomSheet
import com.den.steward.ui.components.PeriodTypeBottomSelector
import com.den.steward.ui.components.SortByBottomSheet
import com.den.steward.backend.states.allTabState.AllTabDataState
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.ui.components.TransactionViewDialog
import com.den.steward.ui.components.DataDeletionDialog
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import com.den.steward.ui.dataUpdate.UpdateTransactionInf
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.launch

@Composable
fun AllTabLazyList(
    allTabDataState: DataState<AllTabDataState>,
    allUiState: AllUiState,
    transactions: DataState<ImmutableMap<String, List<Transaction>>>,
    dataUpdateState: DataUpdateState,
    allViewModel: AllViewModel,
    sortAndFilterState: SortAndFilterState,
) {
    val (chartDataState, summaryState) = remember(allTabDataState) {
        when (allTabDataState) {
            is DataState.Success -> DataState.Success(allTabDataState.data.chartDataCollection) to DataState.Success(allTabDataState.data.allTransactionSummary)
            is DataState.Error -> DataState.Error(allTabDataState.message) to DataState.Error(allTabDataState.message)
            is DataState.Loading -> DataState.Loading to DataState.Loading
        }
    }
    var swipedState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    val scope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item(
                key = "summary",
                contentType = "summary_card" // FIX: distinct contentType for efficient recycling
            ) {
                AllTabSummaryCard(
                    allTransactionChartData = chartDataState,
                    allTransactionSummary = summaryState,
                    selectedDate = allUiState.selectedDate,
                    periodType = allUiState.periodType
                )
            }

            when (transactions) {
                is DataState.Success -> {
                    val groupedTransactions = transactions.data

                    if (groupedTransactions.isNotEmpty()) {
                        // FIX: renamed the inner destructured list from `transactions` to
                        // `dayTransactions` — it was shadowing the outer function parameter
                        // `transactions: DataState<Map<String, List<Transaction>>>`. It happened
                        // to work because nothing in this block needed the outer value, but it's
                        // a landmine for future edits that reference the wrong `transactions`.
                        groupedTransactions.forEach { (date, dayTransactions) ->
                            stickyHeader(key = "date_header_$date") {
                                AllTabLazyListStickyHeader(
                                    date = date
                                )
                            }

                            items(
                                count = dayTransactions.size,
                                key = { index -> "all_${date}_${dayTransactions[index].id}" },
                                // FIX: contentType lets Compose's recycling pool distinguish
                                // real transaction rows from shimmer/empty/error rows instead
                                // of treating every slot as interchangeable. Cheap to add,
                                // strictly improves recycling efficiency when the data state
                                // changes (e.g. Loading -> Success) while the list is visible.
                                contentType = { "transaction_row" }
                            ) { index ->
                                val transaction = dayTransactions[index]

                                val shape = remember(index, dayTransactions.size) {
                                    when (index) {
                                        0 -> if (dayTransactions.size == 1) RoundedCornerShape(16.dp) else RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        dayTransactions.lastIndex -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                                        else -> RectangleShape
                                    }
                                }

                                val padding = remember(index, dayTransactions.size) {
                                    when {
                                        index == dayTransactions.lastIndex -> 10.dp
                                        else -> 0.dp
                                    }
                                }

                                val dismissState = rememberSwipeToDismissBoxState(
                                    positionalThreshold = { distance: Float ->
                                        distance * 0.5f
                                    }
                                )

                                AllTabLazyListItem(
                                    modifier = Modifier
                                        .padding(bottom = padding)
                                        .animateItem(),
                                    shape = shape,
                                    dismissState = dismissState,
                                    transaction = transaction,
                                    onClick = {
                                        allViewModel.updateSelectedTransactionForView(transaction)
                                    },
                                    onUpdate = {
                                        swipedState = dismissState
                                        allViewModel.updateTransaction.updateSelectedTransaction(transaction)
                                    },
                                    onDelete = {
                                        swipedState = dismissState
                                        allViewModel.updateSelectedTransactionToDelete(transaction)
                                    }
                                )
                            }
                        }

                        item(
                            key = "Space up"
                        ) {
                            Spacer(
                                modifier = Modifier.height(55.dp)
                            )
                        }
                    }
                    else {
                        item(
                            key = "empty_state",
                            contentType = "empty_state" // FIX
                        ) {
                            AllTabLazyListEmpty()
                        }
                    }
                }


                is DataState.Loading -> {
                    items(
                        5,
                        key = { "shimmer_$it" },
                        contentType = { "shimmer_row" } // FIX
                    ) {
                        AllTabLazyListItemShimmer()
                    }
                }

                is DataState.Error -> {
                    item(
                        key = "error_state",
                        contentType = "error_state" // FIX
                    ) {
                        AllTabLazyListError(
                            message = transactions.message
                        )
                    }
                }
            }
        }
    }

    // --- Bottom Sheets & Dialogs ---
    FilterBottomSheet(
        isExpanded = sortAndFilterState.isFilterExpanded,
        selected = sortAndFilterState.filter,
        onFilterSelected = {
            allViewModel.updateFilter(it)
        },
        onDismiss = { allViewModel.updateIsFilterExpanded(false) }
    )

    OrderByBottomSheet(
        isExpanded = allUiState.isTransactionListSortExpanded,
        selected = allUiState.isTransactionListSort,
        isOrderByTimeLine = true,
        onSortSelected = {
            allViewModel.updateIsTransactionListOrder(it)
            allViewModel.updateIsTransactionListOrderExpanded(false)
        },
        onDismiss = { allViewModel.updateIsTransactionListOrderExpanded(false) }
    )

    OrderByBottomSheet(
        isExpanded = sortAndFilterState.isOrderByExpanded,
        selected = sortAndFilterState.orderBy,
        onSortSelected = {
            allViewModel.updateSort(it)
            allViewModel.updateIsOrderByExpanded(false)
        },
        onDismiss = { allViewModel.updateIsOrderByExpanded(false) }
    )

    SortByBottomSheet(
        isExpanded = sortAndFilterState.isSortByExpanded,
        selected = sortAndFilterState.sortBy,
        onSortSelected = {
            allViewModel.updateSortType(it)
            allViewModel.updateIsSortByExpanded(false)
        },
        onDismiss = { allViewModel.updateIsSortByExpanded(false) }
    )

    PeriodTypeBottomSelector(
        isExpanded = allUiState.isPeriodTypeExpanded,
        currentPeriodType = allUiState.periodType,
        onPeriodTypeChange = allViewModel::updatePeriodType,
        onDismiss = {
            allViewModel.updateIsPeriodTypeExpanded(false)
        }
    )

    allUiState.selectedTransactionForView?.let { transaction ->
        TransactionViewDialog(
            transaction = transaction,
            onShow = true,
            onDismissRequest = { allViewModel.updateSelectedTransactionForView(null) }
        )
    }



    DataDeletionDialog(
        transaction = allUiState.selectedTransactionToDelete,
        onDialogShow = allUiState.openDeleteDialog,
        onDelete = allViewModel::deleteTransaction,
        onDismissRequest = {
            allViewModel.updateOpenDeleteDialog(false)
            scope.launch {
                swipedState?.reset()
            }
        }
    )

    UpdateTransactionBottomDrawerSheet(
        dataUpdateState = dataUpdateState,
        updateCorrectNote = allViewModel.updateTransaction::updateCurrentNote,
        updateCorrectLabel = allViewModel.updateTransaction::updateCurrentLabel,
        updateSelectedIcon = allViewModel.updateTransaction::updateSelectedIcon,
        updatePaymentMethod = allViewModel.updateTransaction::updatePaymentMethod,
        updateCorrectAmount = allViewModel.updateTransaction::updateCurrentAmount,
        updateIsLabelCorrect = allViewModel.updateTransaction::updateIsLabelCorrect,
        updateIsAmountCorrect = allViewModel.updateTransaction::updateIsAmountCorrect,
        updateIsAffectingAmount = allViewModel.updateTransaction::updateIsAffectingAmount,
        onLocalTimeChange = allViewModel.updateTransaction::onLocalTimeChangeUpdate,
        onLocalDateChange = allViewModel.updateTransaction::onLocalDateChangeUpdate,
        onTransactionUpdate = {
            allViewModel.onUpdateTransaction()
            scope.launch {
                swipedState?.reset()
            }
        },
        reset =  {
            allViewModel.updateTransaction.onReset()
            scope.launch {
                swipedState?.reset()
            }
        }
    )
}

@Composable
private fun AllTabLazyListEmpty() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_empty_transactions),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    "No transactions found for this period.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}




@Composable
fun AllTabLazyListError(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Error: $message", color = MaterialTheme.colorScheme.error)
    }
}