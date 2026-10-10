// Glory be to LORD our GOD
package com.den.steward.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.limitLength
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.VerticalDivider
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionViewDialog(
    transaction: Transaction,
    onShow: Boolean,
    onDismissRequest: () -> Unit
) {
    if (onShow) {
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {

                when (transaction) {
                    is Transaction.Expense -> {
                        TransactionCard(
                            label = transaction.label,
                            note = transaction.note,
                            amount = transaction.amount,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            paymentMethod = transaction.paymentMethod,
                            transactionType = transaction.type,
                            affectAmount = transaction.getAffectedAmount?.label ?: "",
                            selectedIcon = transaction.getSelectedIcon
                        )
                    }

                    is Transaction.Earnings -> {
                        TransactionCard(
                            label = transaction.label,
                            note = transaction.note,
                            amount = transaction.amount,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            paymentMethod = transaction.paymentMethod,
                            affectAmount = transaction.getAffectedAmount?.label ?: "",
                            transactionType = transaction.type,
                            selectedIcon = transaction.getSelectedIcon
                        )
                    }

                    is Transaction.Savings -> {
                        TransactionCard(
                            label = transaction.label,
                            note = transaction.note,
                            amount = transaction.amount,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            paymentMethod = transaction.paymentMethod,
                            affectAmount = transaction.getAffectedAmount?.label ?: "",
                            transactionType = transaction.type,
                            selectedIcon = transaction.getSelectedIcon
                        )
                    }

                    is Transaction.Goal -> {
                        TransactionGoalCard(
                            label = transaction.label,
                            note = transaction.note,
                            amount = transaction.value,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            startDateTime = transaction.startedAt.toLocalDateTime(),
                            endDateTime = transaction.endAt.toLocalDateTime(),
                            recurrencePattern = transaction.repeatable,
                            achievement = transaction.achievement,
                            status = transaction.status,
                            selectedIcon = transaction.getSelectedIcon
                        )
                    }

                    is Transaction.Debt,
                    is Transaction.Lent -> {
                        TransactionLiabilityCard(
                            label = transaction.getLabel,
                            note = transaction.getNote,
                            amount = transaction.getAmountOrValue ?: 0.0,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            paymentMethod = transaction.getPaymentMethodOrNull
                                ?: PaymentMethod.CASH,
                            affectAmount = transaction.getAffectedAmount?.label ?: "",
                            transactionType = transaction.type,
                            selectedIcon = transaction.getSelectedIcon,
                            totalFulfillment = transaction.getFulfillmentTotalSum,
                            status = transaction.getStatus,
                            statusColor = transaction.getStatusColor,
                            onShow = onShow
                        )
                    }

                    is Transaction.Repayment,
                    is Transaction.Settlement -> {
                        TransactionLiabilityFulfillmentCard(
                            note = transaction.getNote,
                            amount = transaction.getAmountOrValue ?: 0.0,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            paymentMethod = transaction.getPaymentMethodOrNull
                                ?: PaymentMethod.CASH,
                            affectAmount = transaction.getAffectedAmount?.label ?: "",
                            transactionType = transaction.type,
                            parentTransaction = transaction.getParentTransaction,
                            selectedIcon = transaction.getSelectedIcon
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(
    label: String,
    note: String,
    amount: Double,
    createdAt: LocalDateTime,
    paymentMethod: PaymentMethod,
    affectAmount: String,
    selectedIcon: SelectedIcon,
    transactionType: TransactionType
) {
    val transactionTypeLabel = stringResource(id = transactionType.label)
    val transactionTypeIcon = painterResource(transactionType.icon)
    val transactionTypeColor = colorResource(transactionType.color)

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        TransactionViewTitle(
            title = label,
            color = transactionTypeColor,
            selectedIcon = selectedIcon,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            }
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TransactionRow(
                key = "Amount",
                value = amount.formatToAmount(),
            )
            TransactionRow(
                key = "Created At",
                value = createdAt.formatedDateTime,
            )
            TransactionRow(
                key = "Payment Method",
                value = paymentMethod.label,
            )
            TransactionRow(
                key = "Affected Amount",
                value = affectAmount,
            )

            if (note.isNotBlank()) {
                TransactionNoteView(
                    note = note
                )
            }
        }
    }
}

@Composable
private fun TransactionLiabilityFulfillmentCard(
    note: String,
    amount: Double,
    createdAt: LocalDateTime,
    paymentMethod: PaymentMethod,
    affectAmount: String,
    selectedIcon: SelectedIcon,
    parentTransaction: Transaction?,
    transactionType: TransactionType
) {

    if (parentTransaction == null) return

    val transactionTypeLabel = stringResource(id = transactionType.label)
    val transactionTypeIcon = painterResource(transactionType.icon)
    val transactionTypeColor = colorResource(transactionType.color)
    val parentTransactionLabel = parentTransaction.getLabel
    val parentTransactionTyeName = stringResource(parentTransaction.type.label)
    val remaining = remember(
        parentTransaction.getFulfillmentTotalSum,
        amount
    ) {
        ((parentTransaction.getAmountOrValue ?: 0.0) - (parentTransaction.getFulfillmentTotalSum
            ?: 0.0)).formatToAmount()
    }
    val statusColor = colorResource(parentTransaction.getStatusColor)

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        TransactionFulfillmentViewTitle(
            title = parentTransactionLabel,
            color = transactionTypeColor,
            selectedIcon = selectedIcon,
            subtitle = "$parentTransactionTyeName $transactionTypeLabel",
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            }
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TransactionRow(
                key = "Amount",
                value = amount.formatToAmount(),
            )
            TransactionRow(
                key = "Created At",
                value = createdAt.formatedDateTime,
            )
            TransactionRow(
                key = "Payment Method",
                value = paymentMethod.label,
            )

            TransactionRow(
                key = "Affected Amount",
                value = affectAmount,
            )

            TransactionRow(
                key = "Status",
                value = parentTransaction.getStatus ?: "",
                color = statusColor
            )

            TransactionRow(
                key = "Remaining",
                value = remaining,
            )

            if (note.isNotBlank()) {
                TransactionNoteView(
                    note = note
                )
            }
        }
    }
}


@Composable
private fun TransactionLiabilityCard(
    label: String,
    note: String,
    amount: Double,
    createdAt: LocalDateTime,
    paymentMethod: PaymentMethod,
    affectAmount: String,
    selectedIcon: SelectedIcon,
    status: String?,
    transactionType: TransactionType,
    totalFulfillment: Double?,
    onShow: Boolean,
    statusColor: Int
) {
    val transactionTypeLabel = stringResource(id = transactionType.label)
    val transactionTypeIcon = painterResource(transactionType.icon)
    val transactionTypeColor = colorResource(transactionType.color)
    val colorOfStatus = colorResource(statusColor)

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        TransactionViewTitle(
            title = label,
            color = transactionTypeColor,
            selectedIcon = selectedIcon,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            }
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TransactionRow(
                key = "Amount",
                value = amount.formatToAmount(),
            )
            TransactionRow(
                key = "Created At",
                value = createdAt.formatedDateTime,
            )
            TransactionRow(
                key = "Payment Method",
                value = paymentMethod.label,
            )
            TransactionRow(
                key = "Affected Amount",
                value = affectAmount,
            )

            if (note.isNotBlank()) {
                TransactionNoteView(
                    note = note
                )
            }
        }

        TransactionLiabilityStatusProgress(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            amount = amount,
            totalFulfillment = totalFulfillment,
            color = colorOfStatus,
            title = status ?: "",
            onShow = onShow
        )
    }
}

@Composable
private fun TransactionGoalCard(
    label: String,
    note: String,
    amount: Double,
    createdAt: LocalDateTime,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime,
    recurrencePattern: RecurrencePattern,
    status: GoalStatus,
    achievement: List<Transaction.Achievement>,
    selectedIcon: SelectedIcon
) {
    val transactionTypeLabel = stringResource(id = TransactionType.GOAL.label)
    val transactionTypeIcon = painterResource(TransactionType.GOAL.icon)
    val transactionTypeColor = colorResource(TransactionType.GOAL.color)

    val counts = remember(achievement) {
        object {
            val completedAchievementSize =
                achievement.filter { it.status == GoalStatus.COMPLETED }.size
            val failedAchievementSize = achievement.filter { it.status == GoalStatus.FAILED }.size
        }
    }

    val achievementColor = colorResource(TransactionType.ACHIEVEMENT.color)

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        TransactionViewTitle(
            title = label,
            color = transactionTypeColor,
            selectedIcon = selectedIcon,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            },
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TransactionRow(
                key = "Amount",
                value = amount.formatToAmount(),
            )
            TransactionRow(
                key = "Created At",
                value = createdAt.formatedDateTime,
            )
            TransactionRow(
                key = "Started At",
                value = startDateTime.formatedDateTime,
            )
            TransactionRow(
                key = "Deadline time",
                value = endDateTime.formatedDateTime,
            )

            TransactionRow(
                key = "Recurrence Pattern",
                value = recurrencePattern.name,
            )

            TransactionRow(
                key = "Status",
                value = status.label,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = achievementColor.copy(0.4f),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    counts.completedAchievementSize.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    color = achievementColor
                                )
                            }
                        }
                        Text(
                            "Achieved",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = MaterialTheme.typography.labelMedium.fontWeight
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = Color.LightGray
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(0.4f),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    counts.failedAchievementSize.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Text(
                            "Failed",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = MaterialTheme.typography.labelMedium.fontWeight
                        )
                    }
                }
            }

            if (note.isNotBlank()) {
                TransactionNoteView(
                    note = note
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(
    key: String,
    value: String,
    color: Color? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            key,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (color != null) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }

            Text(
                value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun TransactionNoteView(
    note: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Note",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            textDecoration = MaterialTheme.typography.titleMedium.textDecoration
        )
        Text(
            note.limitLength(210),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
private fun TransactionViewTitle(
    title: String,
    color: Color,
    icon: @Composable () -> Unit,
    selectedIcon: SelectedIcon
) {

    val iconBackgroundColor = color.copy(0.4f).compositeOver(MaterialTheme.colorScheme.background)
    val transactionSelectedIcon = painterResource(selectedIcon.icon)

    Surface(
        color = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .width(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = transactionSelectedIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                title.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                color = Color.White
            )
        }
    }
}


@Composable
private fun TransactionFulfillmentViewTitle(
    title: String,
    subtitle: String,
    color: Color,
    icon: @Composable () -> Unit,
    selectedIcon: SelectedIcon
) {

    val iconBackgroundColor = color.copy(0.4f).compositeOver(MaterialTheme.colorScheme.background)
    val transactionSelectedIcon = painterResource(selectedIcon.icon)

    Surface(
        color = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .width(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = transactionSelectedIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    color = Color.White
                )

                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TransactionLiabilityStatusProgress(
    modifier: Modifier = Modifier,
    amount: Double,
    totalFulfillment: Double?,
    color: Color,
    title: String,
    onShow: Boolean
) {

    // Recalculate when amount or totalFulfillment changes
    val progress = remember(totalFulfillment, amount) {
        if (totalFulfillment == null || amount <= 0) {
            0.0f
        } else if (totalFulfillment > amount) {
            1.0f
        } else {
            (totalFulfillment / amount).toFloat()
        }
    }

    val formattedTotalFulfillment = remember(totalFulfillment) {
        (totalFulfillment ?: 0.0).formatToAmount()
    }

    val remaining = remember(
        totalFulfillment,
        amount
    ) {
        (amount - (totalFulfillment ?: 0.0)).formatToAmount()
    }

    // Trigger targetValue transition after initial composition
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val animateProgress by animateFloatAsState(
        targetValue = if (startAnimation && onShow) progress else 0f,
        animationSpec =
            tween(durationMillis = 800),
        label = "ProgressAnimation"
    )

    val textProgress = (animateProgress * 100).toInt()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )

                Text(
                    title.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Remaining:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                    color = Color.Gray
                )

                Text(
                    remaining,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                    color = Color.Gray
                )
            }
        }

        LinearProgressIndicator(
            progress = { animateProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .height(6.dp),
            color = color,
            trackColor = color.copy(alpha = 0.1f),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "0%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
            )

            Text(
                "$textProgress% (${formattedTotalFulfillment})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
            )

            Text(
                "100%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
            )
        }
    }
}