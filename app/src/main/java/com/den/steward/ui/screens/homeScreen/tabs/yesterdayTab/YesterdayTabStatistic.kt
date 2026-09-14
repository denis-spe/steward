package com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.den.steward.backend.viewModels.YesterdayChartViewModel
import com.den.steward.backend.viewModels.YesterdayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayDonutChartEmptyView
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayDonutChartErrorView
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayDonutChartShimmerView
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayDonutChartView
import kotlinx.coroutines.launch

@Composable
fun YesterdayTabStatisticView(
    chartViewModel: YesterdayChartViewModel,
    yesterdayViewModel: YesterdayViewModel
) {
    val tabs = listOf(
        "Flow Chart",
        "Balances",
        "Liabilities"
    )

    val pager = rememberPagerState {
        tabs.size
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        YesterdayTabStatisticPanel(
            tabs = tabs,
            pager = pager
        )

        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 16.dp,
            verticalAlignment = Alignment.Top
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (page) {
                    0 -> YesterdayTabDonutChart(
                        viewModel = chartViewModel,
                        chartSize = 160.dp,
                        strokeWidth = 18.dp,
                        strokeCap = StrokeCap.Round
                    )

                    1 -> YesterdaySummaryView(yesterdayViewModel = yesterdayViewModel)

                    2 -> YesterdayTabUnpaidLiabilitiesView()
                }
            }
        }
    }
}

@Composable
fun YesterdayTabStatisticPanel(tabs: List<String>, pager: PagerState) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = pager.currentPage == index
                val icon = when (title) {
                    "Flow Chart" -> if (selected) Icons.Filled.DonutSmall else Icons.Outlined.DonutSmall
                    "Balances" -> if (selected) Icons.Filled.Wallet else Icons.Outlined.Wallet
                    "Liabilities" -> if (selected) Icons.Filled.Balance else Icons.Outlined.Balance
                    else -> Icons.Outlined.PieChart
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable {
                            scope.launch { pager.animateScrollToPage(index) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Text(
            text = tabs[pager.currentPage],
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun YesterdayStatisticLayout(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun YesterdayTabDonutChart(
    viewModel: YesterdayChartViewModel,
    chartSize: Dp = 160.dp,
    strokeWidth: Dp = 18.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val donutChartState by viewModel.donutChart.collectAsStateWithLifecycle()
    val donutChartCenterAmount by viewModel.donutChartCenterAmount.collectAsStateWithLifecycle()
    val donutStatusSummary by viewModel.donutStatusSummary.collectAsStateWithLifecycle()

    YesterdayStatisticLayout {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(chartSize)) {
                when (val currentState = donutChartState) {
                    is DataState.Success -> {
                        if (currentState.data.isEmpty()) {
                            TodayDonutChartEmptyView(chartSize = chartSize, strokeWidth = strokeWidth)
                        } else {
                            TodayDonutChartView(
                                donutChartData = currentState.data,
                                donutChartCenterAmount = donutChartCenterAmount,
                                chartSize = chartSize,
                                strokeWidth = strokeWidth,
                                strokeWidthSelected = 24.dp,
                                strokeCap = strokeCap
                            )
                        }
                    }
                    is DataState.Error -> TodayDonutChartErrorView(message = currentState.message)
                    is DataState.Loading -> TodayDonutChartShimmerView(chartSize = chartSize, strokeWidth = strokeWidth)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Distribution",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                when (val state = donutStatusSummary) {
                    is DataState.Success -> {
                        Text(
                            text = state.data,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.heightIn(min = 60.dp)
                        )
                    }
                    is DataState.Loading -> Box(Modifier.fillMaxWidth().height(40.dp).shimmerEffect())
                    is DataState.Error -> Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun YesterdaySummaryView(
    yesterdayViewModel: YesterdayViewModel
) {
    val currentAmountMapState by yesterdayViewModel.yesterdaySummaryTransactions.collectAsStateWithLifecycle()

    YesterdayStatisticLayout {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Yesterday's Net",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            when (val state = currentAmountMapState) {
                is DataState.Success -> {
                    val flow = remember(state.data) { yesterdayViewModel.calculateFlow(state.data) }
                    Text(
                        text = flow.formatToAmount(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (flow >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                is DataState.Loading -> Box(Modifier.fillMaxWidth().height(30.dp).shimmerEffect())
                is DataState.Error -> Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(Modifier.height(4.dp))
            
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Yesterday's financial activities summarized.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun YesterdayTabUnpaidLiabilitiesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Balance,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Liabilities for yesterday are included in the overall overview status.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun YesterdayTabStatisticShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Panel Shimmer
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
        }
        
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )

        // Main Card Shimmer
        YesterdayStatisticLayout {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        }
    }
}
