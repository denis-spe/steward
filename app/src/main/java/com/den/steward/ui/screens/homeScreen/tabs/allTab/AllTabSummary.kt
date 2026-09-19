// Bless be the name of the LORD GOD
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.den.steward.R
import com.den.steward.backend.states.AllTransactionSummary
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PeriodType
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formattedDate
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.charts.VicoBarChart
import com.den.steward.ui.components.charts.VicoLineChart
import com.den.steward.ui.components.charts.collections.ChartDataCollection
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AllTabSummaryCard(
    allTransactionChartData: DataState<ChartDataCollection>,
    allTransactionSummary: DataState<AllTransactionSummary>,
    selectedDate: LocalDate,
    periodType: PeriodType
) {
    val tabs = listOf(
        "Summary",
        "Trend Chart",
    )

    val pager = rememberPagerState {
        tabs.size
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (periodType) {
                    PeriodType.DAY -> selectedDate.formattedDate
                    PeriodType.WEEK -> "Weekly Summary"
                    PeriodType.MONTH -> "Monthly Summary"
                    PeriodType.YEAR -> "Yearly Summary"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

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
                            0 -> AllTabSummary(allTransactionSummary = allTransactionSummary)
                            1 -> AllTabSummaryChart(
                                allTransactionChartData = allTransactionChartData,
                                periodType = periodType
                            )
                        }
                    }
                }

                AllTabSummarySelectorTabs(
                    tabs = tabs,
                    pager = pager
                )

            }

        }
    }
}

@Composable
fun AllTabSummaryChart(
    allTransactionChartData: DataState<ChartDataCollection>,
    periodType: PeriodType
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (allTransactionChartData) {
            is DataState.Success -> {
                val chartData = allTransactionChartData.data
                if (chartData.chartData.isEmpty()) {
                    AllTabSummaryChartEmptyView()
                } else {
                    VicoLineChart(
                        chartDataCollection = chartData,
                        modifier = Modifier.fillMaxSize(),
                        horizontalItemPlacer = remember {
                            HorizontalAxis.ItemPlacer.aligned(spacing = { 1 })
                        },
                        xValueFormatter = { value: Double ->
                            when (periodType) {
                                PeriodType.DAY -> "${value.toInt()}h"
                                PeriodType.WEEK -> {
                                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                    val index = value.toInt() - 1
                                    days.getOrElse(index) { " " }
                                }

                                PeriodType.MONTH -> value.toInt().toString()
                                PeriodType.YEAR -> {
                                    val months = listOf(
                                        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                                    )
                                    val index = value.toInt() - 1
                                    months.getOrElse(index) { " " }
                                }
                            }
                        },
                        yValueFormatter = { value: Double ->
                            value.toInt().formatToAmount()
                        }
                    )
                }
            }

            is DataState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }

            is DataState.Error -> {
                Text(
                    text = allTransactionChartData.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AllTabSummaryChartEmptyView() {
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
            text = "No transactions available for this period",
            style = MaterialTheme.typography.labelMedium,
            color = Color.LightGray
        )
    }
}

@Composable
fun AllTabSummarySelectorTabs(
    tabs: List<String>,
    pager: PagerState
) {
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(0.2f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, title ->
                    val selected = pager.currentPage == index

                    Box(
                        modifier = Modifier
                            .width(80.dp) // Increased width for text
                            .height(36.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                scope.launch { pager.animateScrollToPage(index) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AllTabSummary(
    allTransactionSummary: DataState<AllTransactionSummary>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        when (allTransactionSummary) {
            is DataState.Success -> {
                val allTransactionSummary = allTransactionSummary.data

                AllTabSummaryCardContent(
                    flow = allTransactionSummary.flow,
                    transactionSize = allTransactionSummary.transactionSize,
                    totalReceived = allTransactionSummary.totalReceived,
                    totalSpent = allTransactionSummary.totalSpent,
                    totalSavings = allTransactionSummary.totalSavings
                )

            }

            is DataState.Loading -> {
                AllTabSummaryCardContentShimmer()
            }

            is DataState.Error -> {
                Text(
                    text = "Error loading summary",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun AllTabSummaryCardContent(
    flow: Double,
    transactionSize: Int,
    totalReceived: Double,
    totalSpent: Double,
    totalSavings: Double
) {
    val error = MaterialTheme.colorScheme.error
    val green = colorResource(R.color.earnings)

    val flowIconColor = remember(flow) {
        if (flow >= 0) green  else error
    }
    val flowIcon = remember(flow) {
        if (flow >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 3.dp,
                    vertical = 3.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Net Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = flowIcon,
                    contentDescription = "trend",
                    tint = flowIconColor
                )
                Text(
                    text = flow.formatToAmount(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = flowIconColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AllTabSummaryTransactionCard(
                icon = Icons.Default.ArrowUpward,
                text = "Total Received",
                amount = totalReceived,
                color = colorResource(R.color.earnings),
            )
            Spacer(
                modifier = Modifier.width(3.dp)
            )

            AllTabSummaryTransactionCard(
                icon = Icons.Default.ArrowDownward,
                text = "Total Spent",
                amount = totalSpent,
                color = colorResource(R.color.expense),
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AllTabSummaryTransactionCard(
                icon = Icons.Default.Savings,
                text = "Savings",
                amount = totalSavings,
                color = colorResource(R.color.debt),
            )

            Spacer(
                modifier = Modifier.width(3.dp)
            )

            AllTabSummaryTransactionCard(
                icon = Icons.Default.AcUnit,
                text = "Transaction Count",
                amount = transactionSize.toDouble(),
                color = colorResource(R.color.purple_200),
            )
        }
    }
}

@Composable
private fun AllTabSummaryTransactionCard(
    icon: ImageVector,
    text: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val amountState = remember(amount) {
        amount.formatToAmount()
    }

    Surface(
        modifier = Modifier
            .width(130.dp)
            .height(100.dp),
        shape = MaterialTheme.shapes.large,
        shadowElevation = 1.dp,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color.copy(
                                alpha = 0.2f
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = color
                        )
                    }
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = amountState,
                style = MaterialTheme.typography.labelMedium
                    .copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun AllTabSummaryCardContentShimmer(
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Net Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier.width(55.dp)
                    .height(35.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AllTabSummaryTransactionCardShimmer()

            Spacer(
                modifier = Modifier.width(3.dp)
            )

            AllTabSummaryTransactionCardShimmer()
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AllTabSummaryTransactionCardShimmer()

            Spacer(
                modifier = Modifier.width(3.dp)
            )

            AllTabSummaryTransactionCardShimmer()
        }
    }
}

@Composable
private fun AllTabSummaryTransactionCardShimmer(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .width(130.dp)
            .height(100.dp),
        shape = MaterialTheme.shapes.large,
        shadowElevation = 1.dp,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                }

                Box(
                    modifier = Modifier.width(75.dp)
                        .height(16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier.width(40.dp)
                    .height(26.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }
    }
}