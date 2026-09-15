// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.AllTransactionSummary
import com.den.steward.backend.states.AllUiState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.ui.components.FilterBottomSheet
import com.den.steward.ui.components.OrderByBottomSheet
import com.den.steward.ui.components.SortByBottomSheet
import com.den.steward.ui.components.TransactionViewDialog
import com.den.steward.ui.dataDeletion.DataDeletionDialog
import java.time.LocalDate

@Composable
fun AllTabLazyList(
    allUiState: AllUiState,
    allTransactionSummary: DataState<AllTransactionSummary>,
    transactions: DataState<Map<String, List<Transaction>>>,
    selectedDate: LocalDate,
    periodType: PeriodType,
    updateFilter: (Filter) -> Unit,
    updateSort: (OrderBy) -> Unit,
    updateSortType: (SortBy) -> Unit,
    updateIsFilterExpanded: (Boolean) -> Unit,
    updateIsOrderByExpanded: (Boolean) -> Unit,
    updateIsSortByExpanded: (Boolean) -> Unit,
    dataDeletionViewModel: DataDeletionViewModel
) {
    val selectedTransactionForView = remember { mutableStateOf<Transaction?>(null) }

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
                key = "summary"
            ) {
                AllTabSummaryCard(
                    allTransactionSummary = allTransactionSummary,
                    selectedDate = selectedDate,
                    periodType = periodType
                )
            }

            when (transactions) {
                is DataState.Success -> {
                    val groupedTransactions = transactions.data

                    if (groupedTransactions.isNotEmpty()) {
                        groupedTransactions.forEach { (date, transactions) ->
                            stickyHeader(key = "date_header_$date") {
                                AllTabLazyListStickyHeader(
                                    date = date
                                )
                            }

                            items(
                                count = transactions.size,
                                key = { index -> "all_${date}_${transactions[index].id}" }
                            ) { index ->
                                val transaction = transactions[index]

                                val shape = remember(index, transactions.size) {
                                    when (index) {
                                        0 -> if (transactions.size == 1) RoundedCornerShape(16.dp) else RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        transactions.lastIndex -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                                        else -> RectangleShape
                                    }
                                }

                                val padding = remember(index, transactions.size) {
                                    when {
                                        index == transactions.lastIndex -> 10.dp
                                        else -> 0.dp
                                    }
                                }

                                AllTabLazyListItem(
                                    modifier = Modifier
                                        .padding(bottom = padding)
                                        .animateItem(),
                                    shape = shape,
                                    transaction = transaction,
                                    onClick = {
                                        selectedTransactionForView.value = transaction
                                    },
                                    onDelete = {
                                        dataDeletionViewModel.updateSelectedTransaction(transaction)
                                    }
                                )
                            }
                        }
                    }
                    else {
                        item(key = "empty_state") {
                            AllTabLazyListEmpty()
                        }
                    }
                }


                is DataState.Loading -> {
                    items(5, key = { "shimmer_$it" }) {
                        AllTabLazyListItemShimmer()
                    }
                }

                is DataState.Error -> {
                    item(key = "error_state") {
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
        isExpanded = allUiState.isFilterExpanded,
        selected = allUiState.filter,
        onFilterSelected = {
            updateFilter(it)
            updateIsFilterExpanded(false)
        },
        onDismiss = { updateIsFilterExpanded(false) }
    )

    OrderByBottomSheet(
        isExpanded = allUiState.isOrderByExpanded,
        selected = allUiState.orderBy,
        onSortSelected = {
            updateSort(it)
            updateIsOrderByExpanded(false)
        },
        onDismiss = { updateIsOrderByExpanded(false) }
    )

    SortByBottomSheet(
        isExpanded = allUiState.isSortByExpanded,
        selected = allUiState.sortBy,
        onSortSelected = {
            updateSortType(it)
            updateIsSortByExpanded(false)
        },
        onDismiss = { updateIsSortByExpanded(false) }
    )

    selectedTransactionForView.value?.let { transaction ->
        TransactionViewDialog(
            transaction = transaction,
            onShow = true,
            onDismissRequest = { selectedTransactionForView.value = null }
        )
    }

    DataDeletionDialog(
        viewModel = dataDeletionViewModel,
        onDismissRequest = {
            dataDeletionViewModel.updateOnDialogShow(false)
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
