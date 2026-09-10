// I Worship the LORD GOD of hosts
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TodayTabStatisticView(
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel
) {
    val tabs = listOf(
        "Donut Chart",
        "Summary",
        "Liabilities Stats"
    )

    val pager = rememberPagerState {
        tabs.size
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TodayTabStatisticPanel(
            tabs = tabs,
            pager = pager
        )

        HorizontalPager(
            state = pager,
            pageSpacing = 16.dp
        ) {
            when (it) {
                0 -> TodayTabDonutChart(
                    viewModel = chartViewModel,
                    chartSize = 170.dp,
                    strokeWidth = 20.dp,
                    strokeCap = StrokeCap.Round
                )

                1 -> TodaySummaryView(todayViewModel = todayViewModel)

                2 -> TodayTabUnpaidLiabilitiesView(todayViewModel = todayViewModel)
            }
        }
    }
}

@Composable
fun TodayTabStatisticPanel(tabs: List<String>, pager: PagerState) {
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, title ->
                TodayTabStatisticPanelButton(
                    icon = when (title) {
                        "Donut Chart" -> {
                            if (pager.currentPage == index) {
                                androidx.compose.material.icons.Icons.Filled.DonutSmall
                            } else {
                                androidx.compose.material.icons.Icons.Outlined.DonutSmall
                            }
                        }

                        "Summary" -> {
                            if (pager.currentPage == index) {
                                androidx.compose.material.icons.Icons.Filled.Wallet
                            } else {
                                androidx.compose.material.icons.Icons.Outlined.Wallet
                            }
                        }

                        "Liabilities Stats" -> {
                            if (pager.currentPage == index) {
                                androidx.compose.material.icons.Icons.Filled.Balance
                            } else {
                                androidx.compose.material.icons.Icons.Outlined.Balance
                            }
                        }

                        else -> {
                            if (pager.currentPage == index) {
                                androidx.compose.material.icons.Icons.Filled.PieChart
                            } else {
                                androidx.compose.material.icons.Icons.Outlined.PieChart
                            }
                        }
                    },
                    selected = pager.currentPage == index
                ) {
                    scope.launch {
                        pager.animateScrollToPage(index)
                    }
                }
            }
        }

        Text(
            text = tabs[pager.currentPage],
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TodayTabStatisticPanelButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
){
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
        )
    }
}


@Composable
fun TodayStatisticLayout(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
fun TodayTabDonutChart(
    viewModel: ChartViewModel,
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 16.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val donutChartState by viewModel.donutChart.collectAsStateWithLifecycle()
    val donutChartCenterAmount by viewModel.donutChartCenterAmount.collectAsStateWithLifecycle()
    val donutStatusSummary by viewModel.donutStatusSummary.collectAsStateWithLifecycle()

    TodayStatisticLayout {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (val currentState = donutChartState) {
                is DataState.Success -> {
                    val donutChartData = currentState.data
                    if (donutChartData.isEmpty()) {
                        TodayDonutChartEmptyView(
                            chartSize = chartSize,
                            strokeWidth = strokeWidth
                        )
                    } else {
                        TodayDonutChartView(
                            donutChartData = donutChartData,
                            donutChartCenterAmount = donutChartCenterAmount,
                            chartSize = chartSize,
                            strokeWidth = strokeWidth,
                            strokeWidthSelected = 30.dp,
                            strokeCap = strokeCap
                        )
                    }
                }

                is DataState.Error -> {
                    TodayDonutChartErrorView(message = currentState.message)
                }

                is DataState.Loading -> {
                    TodayDonutChartShimmerView(
                        chartSize = chartSize,
                        strokeWidth = strokeWidth
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(2.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = donutStatusSummary) {
                    is DataState.Success -> {
                        Text(
                            text = state.data,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Start
                        )
                    }
                    is DataState.Error -> {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    is DataState.Loading -> {
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodaySummaryView(
    todayViewModel: TodayViewModel
) {
    val currentAmountMapState by todayViewModel.currentAmountForDifferentMethods.collectAsStateWithLifecycle()
    val todayTransactionsState by todayViewModel.todayTransactions.collectAsStateWithLifecycle()

    TodayStatisticLayout {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Total Flow Today ---
            when (val state = todayTransactionsState) {
                is DataState.Success -> {
                    val flow = todayViewModel.calculateFlow(state.data)
                    Column {
                        Text(text = "Today's Flow", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = flow.formatToAmount(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (flow >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }

                is DataState.Loading -> {
                    Column {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(25.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmerEffect()
                        )

                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(25.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmerEffect()
                        )
                    }
                }

                is DataState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            // --- Account Balances ---
            when (val state = currentAmountMapState) {
                is DataState.Success -> {
                    val balances = state.data
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Account Balances", style = MaterialTheme.typography.titleSmall)
                        balances.forEach { (method, amount) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = method.label,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = amount.formatToAmount(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                is DataState.Loading -> {
                    // Shimmer or loading indicator
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(25.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmerEffect()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(25.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .shimmerEffect()
                            )

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(25.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .shimmerEffect()
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(25.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .shimmerEffect()
                            )

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(25.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                is DataState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}




@Composable
fun TodayTabUnpaidLiabilitiesView(todayViewModel: TodayViewModel) {
    val statsState by todayViewModel.liabilitiesPaymentStats.collectAsStateWithLifecycle()

    TodayStatisticLayout {
        when (val state = statsState) {
            is DataState.Success -> {
                val stats = state.data
                val loans = stats["Loans"] ?: 0.0
                val debts = stats["Debts"] ?: 0.0
                val paid = stats["Paid"] ?: 0.0
                val unpaid = stats["Unpaid"] ?: 0.0

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "What people owe you", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = loans.formatToAmount(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "What you owe", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = debts.formatToAmount(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Status Summary: ${paid.toInt()} paid items, ${unpaid.toInt()} unpaid items.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            is DataState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(50.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmerEffect()
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(50.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmerEffect()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                }
            }

            is DataState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
