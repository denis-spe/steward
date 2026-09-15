// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.Transaction
import com.den.steward.helper.formattedTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.SwipeDismiss
import com.den.steward.ui.components.TransactionViewDialog

@Composable
fun TodayTabLazyListItem(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colorScheme.surface,
    onUpdate: suspend () -> Unit = {},
    onDelete: suspend () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val localDateTime = remember(transaction) { transaction.createdAt.toLocalDateTime() }
    val time = localDateTime.formattedTime

    // Grouping UI properties that depend on 'transaction' into a single 'remember' block
    // to reduce overhead during scroll-driven recompositions.
    // Note: 'status' and 'percentage' are now derived directly to ensure they reflect the latest data.
    val uiData = remember(transaction) {
        object {
            val amount = transaction.getFormattedAmountOrValue
            val paymentMethod = transaction.getPaymentMethodOrNull
            val affectAmount = transaction.getAffectAmount
            val parent = transaction.getParentTransaction
            val typeColorRes = transaction.type.color
        }
    }

    val typeColor = colorResource(id = uiData.typeColorRes)
    val percentage = transaction.getPercentage
    val status = transaction.getStatus
    val statusColorRes = transaction.getStatusColor

    // Optimization: Ensure progress animation target is stable
    val progressTarget = remember(percentage) { (percentage?.toFloat() ?: 0f) / 100f }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        label = "ProgressAnimation"
    )

    SwipeDismiss(
        shape = shape,
        onUpdate = onUpdate,
        onDelete = onDelete,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = shape,
            color = color,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Leading Icon
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

                    // Center Content
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = transaction.getLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = time,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (uiData.affectAmount != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (uiData.affectAmount == "Yes") "Affected" else "Neutral",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (uiData.affectAmount == "Yes")
                                            typeColor
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            uiData.parent?.let { parent ->
                                val parentLabel = stringResource(id = parent.type.label)
                                val parentColor = colorResource(parent.type.color)

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(parentColor.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = parentLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = parentColor,
                                    )
                                }
                            }

                            if (status != null) {
                                val sColor = colorResource(statusColorRes)

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(sColor.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(sColor)
                                        )
                                        Text(
                                            text = status,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = sColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Trailing Content (Amount)
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = uiData.amount,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = typeColor
                        )

                        if (uiData.paymentMethod != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = uiData.paymentMethod.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    text = uiData.paymentMethod.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Progress Bar for Goals/Loans/Debts
                if (percentage != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = typeColor,
                            trackColor = typeColor.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${percentage.toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = typeColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayTabLazyListItemShimmer() {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon Shimmer
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )

                // Center Content Shimmer
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }

                // Trailing Content Shimmer
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(18.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(14.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}
