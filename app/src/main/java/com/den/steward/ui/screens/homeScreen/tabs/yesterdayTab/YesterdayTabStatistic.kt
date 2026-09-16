package com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.YesterdayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.charts.VicoBarChart
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayDonutChartErrorView
import kotlinx.coroutines.launch

@Composable
fun YesterdayTabStatisticView(
    yesterdayViewModel: YesterdayViewModel
) {
    val tabs = listOf(
        "Trend Chart",
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
                    0 -> YesterdayTabBarChart(
                        viewModel = yesterdayViewModel,
                        chartSize = 160.dp
                    )

                    1 -> YesterdaySummaryView(yesterdayViewModel = yesterdayViewModel)

                    2 -> YesterdayTabUnpaidLiabilitiesView()
                }
            }
        }

        YesterdayTabStatisticPanel(
            tabs = tabs,
            pager = pager
        )
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
        Text(
            text = tabs[pager.currentPage],
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedCard(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(0.2f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, title ->
                    val selected = pager.currentPage == index
                    val icon = when (title) {
                        "Trend Chart" -> if (selected) Icons.Filled.BarChart else Icons.Outlined.BarChart
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
        }
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
fun YesterdayTabBarChart(
    viewModel: YesterdayViewModel,
    chartSize: Dp = 160.dp
) {
    val chartDataCollection by viewModel.chartDataCollection.collectAsStateWithLifecycle()

    YesterdayStatisticLayout {
        when (val currentState = chartDataCollection) {
            is DataState.Success -> {
                if (currentState.data.chartData.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(chartSize)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PieChart,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No transactions available",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                } else {
                    VicoBarChart(
                        chartDataCollection = currentState.data,
                        modifier = Modifier.fillMaxSize(),
                        thickness = 3.dp,
                        xValueFormatter = { value -> "${value.toInt()}hr" }
                    )
                }
            }

            is DataState.Error -> TodayDonutChartErrorView(message = currentState.message)
            is DataState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(chartSize)
                            .clip(MaterialTheme.shapes.large)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun YesterdaySummaryView(
    yesterdayViewModel: YesterdayViewModel
) {
    val yesterdaySummaryTransactions by yesterdayViewModel.yesterdaySummaryTransactions.collectAsStateWithLifecycle()

    YesterdayStatisticLayout {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = yesterdaySummaryTransactions) {
                is DataState.Success -> {
                    YesterdaySummaryViewContent(
                        flow = state.data.flow,
                        transactionSize = state.data.transactionSize,
                        highTransactionActivityAmount = state.data.highTransactionActivityAmount,
                        highTransactionActivityLabel = state.data.highTransactionActivityLabel,
                        lowTransactionActivityAmount = state.data.lowTransactionActivityAmount,
                        lowTransactionActivityLabel = state.data.lowTransactionActivityLabel
                    )
                }

                is DataState.Loading -> Box(
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )

                is DataState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun YesterdaySummaryViewContent(
    flow: Double,
    transactionSize: Int,
    highTransactionActivityAmount: Double,
    highTransactionActivityLabel: String,
    lowTransactionActivityAmount: Double,
    lowTransactionActivityLabel: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Main metrics row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Yesterday's Net Flow",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val flowColor = if (flow >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    val flowIcon = if (flow >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
                    
                    Icon(
                        imageVector = flowIcon,
                        contentDescription = null,
                        tint = flowColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = flow.formatToAmount(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = flowColor
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = transactionSize.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (transactionSize == 1) "txn" else "txns",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Insight Section
        if (highTransactionActivityLabel.isNotEmpty()) {
            val highLabelFormatted = highTransactionActivityLabel.lowercase().replaceFirstChar { it.uppercase() }
            val lowLabelFormatted = lowTransactionActivityLabel.lowercase().replaceFirstChar { it.uppercase() }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Daily Insight",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val insightText = if (highTransactionActivityLabel != lowTransactionActivityLabel) {
                        "Your highest activity was in $highLabelFormatted (${highTransactionActivityAmount.formatToAmount()}), while $lowLabelFormatted had the lowest (${lowTransactionActivityAmount.formatToAmount()})."
                    } else {
                        "All of your yesterday's activity was categorized as $highLabelFormatted (${highTransactionActivityAmount.formatToAmount()})."
                    }

                    Text(
                        text = insightText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = TextUnit.Unspecified // Default or set small sp
                    )
                }
            }
        }
    }
}

@Composable
fun YesterdaySummaryActivityView(amount: Double, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

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

        // Main Card Shimmer
        YesterdayStatisticLayout {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        }

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )

        // Panel Shimmer
        OutlinedCard(
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.2f))
        ) {
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
        }
    }
}