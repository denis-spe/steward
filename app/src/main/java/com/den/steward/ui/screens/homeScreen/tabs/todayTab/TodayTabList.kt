// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.ui.componentExtenison.shimmerEffect

private val icon_size = 80.dp

@Composable
fun TodayTabList(
    modifier: Modifier = Modifier,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel
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
                val transactions = ((state as DataState.Success<*>).data as List<*>)
                    .filterIsInstance<Transaction>()
                if (state.isEmpty) {
                    TodayTabListEmpty(
                        modifier = modifier
                    )
                } else {
                    TodayTabLazyList(
                        modifier = modifier,
                        chartViewModel = chartViewModel,
                        todayViewModel = todayViewModel,
                        transactions = transactions
                    )
                }
            }

            is DataState.Error -> {
                TodayTabListError()
            }
        }
    }
}

@Composable
fun TodayTabLazyList(
    modifier: Modifier = Modifier,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel,
    transactions: List<Transaction>
) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    "Today's Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        items(
            transactions.size,
            key = { index -> transactions[index].id }
        ) { index ->
            val transaction = transactions[index]

            TodayTabLazyListItem(
                transaction = transaction,
                color = MaterialTheme.colorScheme.surface,
            )
        }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    "Today's Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_list),
            contentDescription = "No Transactions",
            modifier = Modifier.size(icon_size)
        )
        Text(
            "No Transactions",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
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
