// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.Sort
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.dataDeletion.DataDeletionDialog
import kotlinx.coroutines.android.awaitFrame

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
        targetState = combinedState
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
    filterSelected: Boolean,
    sortSelected: Boolean,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit
){
    val iconSize = 20.dp

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodayTabListPanelButton(
                text = "Type",
                selected = sortSelected,
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Sort",
                        modifier = Modifier.size(iconSize)
                    )
                },
                onClick = onSortClick
            )

            TodayTabListPanelButton(
                text = "Filter",
                selected = filterSelected,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.filter),
                        contentDescription = "filter",
                        modifier = Modifier.size(iconSize)
                    )
                },
                onClick = onFilterClick
            )
            TodayTabListPanelButton(
                text = "Sort",
                selected = sortSelected,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.size(iconSize)
                    )
                },
                onClick = onSortClick
            )
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
            else Color.Gray.copy(alpha = 0.1f),
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        )
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
                filterSelected = todayUiState.filter != Filter.ALL,
                sortSelected = todayUiState.sort != Sort.DESCENDING,
                onFilterClick = {},
                onSortClick = {}
            )
        }

        if (transactions.isNotEmpty()) {
            items(
                transactions.size,
                key = { index -> transactions[index].id }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }

        stickyHeader {
            TodayTabListHeader()
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
