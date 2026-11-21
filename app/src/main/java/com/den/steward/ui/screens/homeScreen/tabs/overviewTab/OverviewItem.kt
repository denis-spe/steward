// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.entitles.Transaction
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formattedTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.screens.screenManager.GoalDetailRouter

@Composable
fun OverviewTransactionItem(
    transaction: Transaction
) {
    val typeColor = colorResource(transaction.type.color)
    val amount = remember(transaction) { transaction.getFormattedAmountOrValue }
    val time = remember(transaction) { transaction.createdAt.toLocalDateTime().formattedTime }

    Card(
        modifier = Modifier.width(220.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(typeColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = transaction.type.icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = typeColor
                    )
                }
                Text(
                    text = transaction.getLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Column {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = typeColor
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OverviewTransactionItemShimmer() {
    Card(
        modifier = Modifier.width(220.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .shimmerEffect())
                Box(modifier = Modifier
                    .size(80.dp, 16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect())
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier
                    .size(100.dp, 24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect())
                Box(modifier = Modifier
                    .size(60.dp, 12.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect())
            }
        }
    }
}

@Composable
fun OverviewGoalItem(
    backStack: NavBackStack<NavKey>,
    transaction: Transaction
) {
    if (transaction !is Transaction.Goal) return
    val typeColor = colorResource(transaction.type.color)
    val percentage = transaction.percentage.toFloat().coerceIn(0f, 100f)
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        label = "GoalProgressAnimation"
    )

    val statusColor = colorResource(transaction.getStatusColor)
    val statusLabel = transaction.getStatus ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = {
            backStack.add(GoalDetailRouter(transaction.id))
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(typeColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = transaction.type.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = typeColor
                        )
                    }
                    Column {
                        Text(
                            text = transaction.getLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Target: ${transaction.value.formatToAmount()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${percentage.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = typeColor
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = statusColor.copy(0.2f),
                                MaterialTheme.shapes.small
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        statusColor,
                                        CircleShape
                                    )
                            )


                            Spacer(
                                modifier = Modifier.size(2.dp)
                            )

                            Text(
                                statusLabel,
                                color = statusColor,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = statusColor,
                    trackColor = typeColor.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Saved: ${transaction.totalAttain.formatToAmount()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Remaining: ${transaction.remainingValue.formatToAmount()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewGoalItemShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .shimmerEffect())
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier
                            .size(120.dp, 18.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect())
                        Box(modifier = Modifier
                            .size(80.dp, 12.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect())
                    }
                }
                Box(modifier = Modifier
                    .size(40.dp, 20.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect())
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .shimmerEffect())
        }
    }
}

@Composable
fun OverviewLoanItem(
    transaction: Transaction
) {
    if (transaction !is Transaction.Lent) return
    val typeColor = colorResource(transaction.type.color)
    val percentage = transaction.percentage.toFloat().coerceIn(0f, 100f)
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        label = "LoanProgressAnimation"
    )

    Card(
        modifier = Modifier.width(180.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = transaction.type.icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = typeColor
                )
            }

            Column {
                Text(
                    text = transaction.getLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = transaction.amount.formatToAmount(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = typeColor
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = typeColor,
                    trackColor = typeColor.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "Repaid: ${percentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OverviewLoanItemShimmer() {
    Card(
        modifier = Modifier.width(180.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .shimmerEffect())
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier
                    .size(100.dp, 16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect())
                Box(modifier = Modifier
                    .size(80.dp, 18.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect())
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .shimmerEffect())
        }
    }
}

@Composable
fun OverviewDebtItem(
    transaction: Transaction
) {
    if (transaction !is Transaction.Debt) return
    val typeColor = colorResource(transaction.type.color)
    val percentage = transaction.percentage.toFloat().coerceIn(0f, 100f)
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        label = "DebtProgressAnimation"
    )

    Card(
        modifier = Modifier.width(180.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = transaction.type.icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = typeColor
                )
            }

            Column {
                Text(
                    text = transaction.getLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = transaction.amount.formatToAmount(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = typeColor
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = typeColor,
                    trackColor = typeColor.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "Settled: ${percentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OverviewDebtItemShimmer() {
    OverviewLoanItemShimmer()
}
