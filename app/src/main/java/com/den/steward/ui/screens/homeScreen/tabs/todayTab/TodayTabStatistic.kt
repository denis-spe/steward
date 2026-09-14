// I Worship the LORD GOD of hosts
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import kotlinx.coroutines.launch

@Composable
fun TodayTabStatisticView(
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel
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
        TodayTabStatisticPanel(
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
                    0 -> TodayTabDonutChart(
                        viewModel = chartViewModel,
                        chartSize = 160.dp,
                        strokeWidth = 18.dp,
                        strokeCap = StrokeCap.Round
                    )

                    1 -> TodaySummaryView(todayViewModel = todayViewModel)

                    2 -> TodayTabUnpaidLiabilitiesView(todayViewModel = todayViewModel)
                }
            }
        }
    }
}

@Composable
private fun TodayTabStatisticPanel(tabs: List<String>, pager: PagerState) {
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
private fun TodayStatisticLayout(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
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
private fun TodayTabDonutChart(
    viewModel: ChartViewModel,
    chartSize: Dp = 160.dp,
    strokeWidth: Dp = 18.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val donutChartState by viewModel.donutChart.collectAsStateWithLifecycle()
    val donutChartCenterAmount by viewModel.donutChartCenterAmount.collectAsStateWithLifecycle()
    val donutStatusSummary by viewModel.donutStatusSummary.collectAsStateWithLifecycle()

    TodayStatisticLayout {
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
private fun TodaySummaryView(
    todayViewModel: TodayViewModel
) {
    val currentAmountMapState by todayViewModel.currentAmountForDifferentMethods.collectAsStateWithLifecycle()
    val todayTransactionsState by todayViewModel.todaySummaryTransactions.collectAsStateWithLifecycle()

    TodayStatisticLayout {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Total Flow ---
            Column(
                modifier = Modifier.weight(0.45f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Today's Net",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                when (val state = todayTransactionsState) {
                    is DataState.Success -> {
                        val flow = remember(state.data) { todayViewModel.calculateFlow(state.data) }
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
                        text = "Transactions today show your spending patterns.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // --- Account Balances ---
            Column(
                modifier = Modifier.weight(0.55f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Account Balances",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                when (val state = currentAmountMapState) {
                    is DataState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.data.forEach { (method, amount) ->
                                Column (
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Image(
                                            painter = painterResource(id = method.icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = method.label,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Text(
                                        text = amount.formatToAmount(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    is DataState.Loading -> repeat(2) { Box(Modifier.fillMaxWidth().height(24.dp).shimmerEffect()) }
                    is DataState.Error -> Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun TodayTabUnpaidLiabilitiesView(todayViewModel: TodayViewModel) {
    val statsState by todayViewModel.liabilitiesPaymentStats.collectAsStateWithLifecycle()

    TodayStatisticLayout {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Liability Breakdown",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (val state = statsState) {
                is DataState.Success -> {
                    val totalLoans = remember(state.data) { state.data.totalLoan }
                    val unpaidLoans = remember(state.data) { state.data.unPaidLoan }
                    val totalDebts = remember(state.data) { state.data.totalDebt }
                    val unpaidDebts = remember(state.data) { state.data.unPaidDebt }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LiabilityCard(
                            modifier = Modifier.weight(1f),
                            title = "Receivable",
                            amount = totalLoans,
                            unpaidAmount = unpaidLoans,
                            color = MaterialTheme.colorScheme.primary,
                            progressTitle = "loans"
                        )
                        LiabilityCard(
                            modifier = Modifier.weight(1f),
                            title = "Payable",
                            amount = totalDebts,
                            unpaidAmount = unpaidDebts,
                            color = MaterialTheme.colorScheme.error,
                            progressTitle = "debts"
                        )
                    }
                }
                is DataState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LiabilityCardShimmer(modifier = Modifier.weight(1f))
                        LiabilityCardShimmer(modifier = Modifier.weight(1f))
                    }
                }
                is DataState.Error -> Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun LiabilityCardProgress(
    modifier: Modifier = Modifier,
    title: String,
    totalAmount: Double,
    unpaidAmount: Double,
    color: Color
) {
    val progress = remember(totalAmount, unpaidAmount) {
        if (totalAmount > 0) ((totalAmount - unpaidAmount) / totalAmount).toFloat().coerceIn(0f, 1f) else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "LiabilityProgress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = modifier
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = color.copy(alpha = 0.7f)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun LiabilityCardShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Title
            Box(Modifier.size(60.dp, 14.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Remaining section
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(50.dp, 10.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())
                    Box(Modifier.size(80.dp, 20.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())
                }
                // Total section
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(40.dp, 10.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())
                    Box(Modifier.size(70.dp, 16.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())
                }
            }

            // Progress bar section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).shimmerEffect())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(Modifier.size(40.dp, 10.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())
                    Box(Modifier.size(30.dp, 10.dp).clip(MaterialTheme.shapes.extraSmall).shimmerEffect())
                }
            }
        }
    }
}

@Composable
private fun LiabilityCard(
    modifier: Modifier = Modifier,
    title: String,
    progressTitle: String,
    amount: Double,
    color: Color,
    unpaidAmount: Double
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color.copy(alpha = 0.05f))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Column {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = color.copy(alpha = 0.6f)
                    )
                    Text(
                        text = unpaidAmount.formatToAmount(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = color
                    )
                }
                Column {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = color.copy(alpha = 0.6f)
                    )
                    Text(
                        text = amount.formatToAmount(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = color.copy(alpha = 0.8f)
                    )
                }
            }

            LiabilityCardProgress(
                modifier = Modifier.fillMaxWidth(),
                title = progressTitle,
                totalAmount = amount,
                unpaidAmount = unpaidAmount,
                color = color
            )
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Box(
        modifier
            .fillMaxHeight()
            .width(thickness)
            .background(color = color)
    )
}

@Composable
fun TodayTabStatisticShimmer() {
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
        TodayStatisticLayout {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        }
    }
}
