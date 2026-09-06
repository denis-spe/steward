// I Worship the LORD GOD of hosts
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.screens.homeScreen.transactionCharts.TransactionDonutChartEmptyView
import com.den.steward.ui.screens.homeScreen.transactionCharts.TransactionDonutChartErrorView
import com.den.steward.ui.screens.homeScreen.transactionCharts.TransactionDonutChartShimmerView
import com.den.steward.ui.screens.homeScreen.transactionCharts.TransactionDonutChartView
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TodayStatisticView(
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel
) {
    val tabs = listOf(
        "Chart",
        "Summary",
        "Unpaid Liabilities"
    )

    val pager = rememberPagerState {
        tabs.size
    }

    HorizontalPager(
        state = pager,
        pageSpacing = 16.dp
    ) {
        when (it) {
            0 -> TodayDonutChart(
                viewModel = chartViewModel,
                chartSize = 200.dp,
                strokeWidth = 20.dp,
                strokeCap = StrokeCap.Round
            )

            1 -> TodaySummaryView(todayViewModel = todayViewModel)
        }
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
fun TodayDonutChart(
    viewModel: ChartViewModel,
    chartSize: Dp = 350.dp,
    strokeWidth: Dp = 20.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val donutChartState by viewModel.donutChart.collectAsStateWithLifecycle()
    val donutChartCenterAmount by viewModel.donutChartCenterAmount.collectAsStateWithLifecycle()

    TodayStatisticLayout {
        when (val currentState = donutChartState) {
            is DataState.Success -> {
                val donutChartData = currentState.data
                if (donutChartData.isEmpty()) {
                    TransactionDonutChartEmptyView(
                        chartSize = chartSize,
                        strokeWidth = strokeWidth
                    )
                } else {
                    TransactionDonutChartView(
                        donutChartData = donutChartData,
                        donutChartCenterAmount = donutChartCenterAmount,
                        chartSize = chartSize,
                        strokeWidth = strokeWidth,
                        strokeWidthSelected = strokeWidth * 2,
                        strokeCap = strokeCap
                    )
                }
            }

            is DataState.Error -> {
                TransactionDonutChartErrorView(message = currentState.message)
            }

            is DataState.Loading -> {
                TransactionDonutChartShimmerView(
                    chartSize = chartSize,
                    strokeWidth = strokeWidth
                )
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

                else -> {}
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
                }

                is DataState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}




@Composable
fun TodayUnpaidLiabilitiesView() {

}