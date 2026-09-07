// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.charts.DonutChartData
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayDonutChartShimmerView

// ============== Transactions ==============
@Composable
fun OverviewTransactionItem(
    transaction: Transaction
) {
    val color = colorResource(transaction.type.color)
    val amount = (transaction.getAmountOrValue ?: 0.0).formatToAmount()
    val icon = painterResource(transaction.getIcon ?: R.drawable.label)
    val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime

    Card(
        modifier = Modifier.size(200.dp, 100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = transaction.type.icon),
                    contentDescription = transaction.getLabel,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(color)
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Text(
                    transaction.getLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color.copy(alpha = 0.2f)
                        )
                    ,
                ) {
                    Image(
                        painter = icon,
                        contentDescription = transaction.getLabel,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                    )
                }

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Column(
                ) {
                    Text(
                        amount,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    )
                    Text(
                        createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewTransactionItemShimmer() {
    Card(
        modifier = Modifier.size(200.dp, 100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for the transaction type icon
                Box(
                    modifier = Modifier.size(24.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                // Placeholder for the label
                Box(
                    modifier = Modifier.size(100.dp, 20.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for the transaction icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Column(
                ) {
                    // Placeholder for the amount
                    Box(
                        modifier = Modifier.size(100.dp, 22.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )

                    Spacer(
                        modifier = Modifier.size(3.dp)
                    )
                    // Placeholder for the date
                    Box(
                        modifier = Modifier.size(130.dp, 20.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

// ============ Goal ================
@Composable
fun OverviewGoalItem(
    transaction: Transaction
) {
    if (transaction !is Transaction.Goal) return

    val color = colorResource(transaction.type.color)
    val amount = (transaction.getAmountOrValue ?: 0.0).formatToAmount()
    val icon = painterResource(transaction.getIcon ?: R.drawable.label)
    val repeatableName = transaction.repeatable.name

    Card(
        modifier = Modifier.fillMaxWidth()
            .height(100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = icon,
                        contentDescription = transaction.getLabel,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(
                        modifier = Modifier.size(4.dp)
                    )
                    Text(
                        transaction.getLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                    )
                }

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        amount,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    )
                    Text(
                        "Remaining: ${transaction.remainingValue.formatToAmount()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                OverviewDonutChart(
                    donutChartData = listOf(
                        DonutChartData(
                            title = "Attained",
                            amount = transaction.totalAttain.toFloat(),
                            color = color
                        ),
                        DonutChartData(
                            title = "Remaining",
                            amount = transaction.remainingValue.coerceAtLeast(0.0).toFloat(),
                            color = color.copy(alpha = 0.1f)
                        )
                    ),
                    donutChartCenterAmount = transaction.totalAttain / transaction.value,
                    chartSize = 60.dp,
                    strokeWidth = 5.dp,
                    strokeWidthSelected = 12.dp
                )

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Text(
                    "Repeatable: $repeatableName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OverviewGoalItemShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth()
            .height(100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(24.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Spacer(
                        modifier = Modifier.size(4.dp)
                    )
                    Box(
                        modifier = Modifier.size(100.dp, 20.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                }

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier.size(120.dp, 24.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                    Spacer(
                        modifier = Modifier.size(2.dp)
                    )
                    Box(
                        modifier = Modifier.size(80.dp, 16.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmerEffect()
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                TodayDonutChartShimmerView(chartSize = 60.dp, strokeWidth = 5.dp)

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Box(
                    modifier = Modifier.size(70.dp, 16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }
        }
    }
}

// =========== Loan ================
@Composable
fun OverviewLoanItem(
    transaction: Transaction
) {
    if (transaction !is Transaction.Lent) return
    val color = colorResource(transaction.type.color)
    val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime

    Card(
        modifier = Modifier.width(180.dp)
            .height(170.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OverviewDonutChart(
                donutChartData = listOf(
                    DonutChartData(
                        title = "Attained",
                        amount = transaction.totalRepayment.toFloat(),
                        color = color
                    ),
                    DonutChartData(
                        title = "Remaining",
                        amount = transaction.remainingAmount.coerceAtLeast(0.0).toFloat(),
                        color = color.copy(alpha = 0.1f)
                    )
                ),
                donutChartCenterAmount = transaction.totalRepayment / transaction.amount,
                chartSize = 60.dp,
                strokeWidth = 5.dp,
                strokeWidthSelected = 12.dp
            )

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Text(
                transaction.remainingAmount.formatToAmount(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )

            Row {
                Image(
                    painter = painterResource(id = transaction.type.icon),
                    contentDescription = transaction.getLabel,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(color)
                )

                Text(
                    transaction.getLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                )
            }

            Text(
                "Remaining ${transaction.remainingAmount.formatToAmount()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OverviewLoanItemShimmer() {
    Card(
        modifier = Modifier.width(180.dp)
            .height(150.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TodayDonutChartShimmerView(
                chartSize = 60.dp,
                strokeWidth = 5.dp,
            )

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Box(
                modifier = Modifier.size(120.dp, 20.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Row {
                Box(
                    modifier = Modifier.size(24.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Box(
                    modifier = Modifier.size(100.dp, 20.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Box(
                modifier = Modifier.size(140.dp, 20.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }
    }
}

// =========== Debt ================
@Composable
fun OverviewDebtItem(
    transaction: Transaction
) {
    if (transaction !is Transaction.Debt) return
    val color = colorResource(transaction.type.color)
    val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime

    Card(
        modifier = Modifier.width(180.dp)
            .height(170.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Image(
                    painter = painterResource(id = transaction.type.icon),
                    contentDescription = transaction.getLabel,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(color)
                )
                Spacer(
                    modifier = Modifier.size(4.dp)
                )
                Text(
                    transaction.getLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                )
            }

            Text(
                transaction.remainingAmount.formatToAmount(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )

            OverviewDonutChart(
                donutChartData = listOf(
                    DonutChartData(
                        title = "Refund",
                        amount = transaction.totalRefund.toFloat(),
                        color = color
                    ),
                    DonutChartData(
                        title = "Remaining",
                        amount = transaction.remainingAmount.coerceAtLeast(0.0).toFloat(),
                        color = color.copy(alpha = 0.1f)
                    )
                ),
                donutChartCenterAmount = transaction.totalRefund / transaction.amount,
                chartSize = 60.dp,
                strokeWidth = 5.dp,
                strokeWidthSelected = 12.dp
            )

            Text(
                "Remaining ${transaction.remainingAmount.formatToAmount()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OverviewDebtItemShimmer() {
    Card(
        modifier = Modifier.width(180.dp)
            .height(170.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Box(
                    modifier = Modifier.size(24.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
                Spacer(
                    modifier = Modifier.size(4.dp)
                )
                Box(
                    modifier = Modifier.size(100.dp, 20.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Box(
                modifier = Modifier.size(120.dp, 20.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            TodayDonutChartShimmerView(
                chartSize = 60.dp,
                strokeWidth = 5.dp,
            )

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Box(
                modifier = Modifier.size(140.dp, 20.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )

            Spacer(
                modifier = Modifier.size(4.dp)
            )

            Box(
                modifier = Modifier.size(140.dp, 20.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }
    }
}