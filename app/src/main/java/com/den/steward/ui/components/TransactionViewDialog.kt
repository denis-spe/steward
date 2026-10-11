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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.GoalType
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.limitLength
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.VerticalDivider

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
                    is Transaction.Expense,
                    is Transaction.Earnings,
                    is Transaction.Savings -> {
                        TransactionCard(
                            transaction
                        )
                    }


                    is Transaction.Goal -> {
                        TransactionGoalCard(
                            transaction = transaction,
                            onShow = onShow
                        )
                    }

                    is Transaction.Debt,
                    is Transaction.Lent -> {
                        TransactionLiabilityCard(
                            transaction = transaction,
                            onShow = onShow
                        )
                    }

                    is Transaction.Repayment,
                    is Transaction.Settlement -> {
                        TransactionLiabilityFulfillmentCard(
                            transaction = transaction
                        )
                    }

                    is Transaction.Attain -> {
                        TransactionAttainCard(
                            transaction = transaction,
                            onShow = onShow
                        )
                    }

                    is Transaction.Exchange -> {
                        TransactionExchangeCard(
                            transaction = transaction
                        )
                    }

                    is Transaction.Plan -> {
                        TransactionPlanCard(
                            transaction = transaction
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun TransactionPlanCard(
    transaction: Transaction.Plan
) {
    val state = remember(transaction) {
        object {
            val label = transaction.getLabel
            val note = transaction.getNote
            val amount = transaction.getFormattedAmountOrValue
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val transactionType = transaction.type
            val selectedIcon = transaction.getSelectedIcon
            val totalFulfillment = transaction.totalFulfillment.formatToAmount()
            val planFulfillment = transaction.transactions
        }
    }
    val transactionTypeLabel = stringResource(id = state.transactionType.label)
    val transactionTypeIcon = painterResource(state.transactionType.icon)
    val transactionTypeColor = colorResource(state.transactionType.color)

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        TransactionFulfillmentViewTitle(
            title = "Plan",
            subtitle = state.label,
            color = transactionTypeColor,
            selectedIcon = state.selectedIcon,
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
            TransactionPlanHeader(
                amount = state.amount,
                createdAt = state.createdAt,
                sumOfFulfillment = state.totalFulfillment
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.planFulfillment.size, key = { index ->
                    state.planFulfillment[index].id
                }) { index ->
                    val planFulfillment = state.planFulfillment[index]
                    val label = planFulfillment.getLabel
                    val amount = planFulfillment.getFormattedAmountOrValue
                    val color = colorResource(planFulfillment.fulfillmentType.color)
                    val icon = painterResource(planFulfillment.fulfillmentType.icon)

                    TransactionPlanFulfillmentRow(
                        key = label,
                        value = amount,
                        color = color,
                        icon = icon
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionExchangeCard(
    transaction: Transaction.Exchange
) {
    val state = remember(transaction) {
        object {
            val label = transaction.getLabel
            val note = transaction.getNote
            val amount = transaction.getFormattedAmountOrValue
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val transactionType = transaction.type
            val selectedIcon = transaction.getSelectedIcon

            val fromPaymentMethod = transaction.from
            val toPaymentMethod = transaction.to
        }
    }
    val transactionTypeLabel = stringResource(id = state.transactionType.label)
    val transactionTypeIcon = painterResource(state.transactionType.icon)
    val transactionTypeColor = colorResource(state.transactionType.color)

    Column(
        modifier = Modifier.fillMaxHeight(0.5f)
    ) {
        TransactionFulfillmentViewTitle(
            title = "Exchange",
            subtitle = state.label,
            color = transactionTypeColor,
            selectedIcon = state.selectedIcon,
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
                value = state.amount,
            )
            TransactionRow(
                key = "Created At",
                value = state.createdAt,
            )
            TransactionRow(
                key = "From",
                value = state.fromPaymentMethod.label,
            )
            TransactionRow(
                key = "To",
                value = state.toPaymentMethod.label,
            )
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: Transaction
) {
    val state = remember(transaction) {
        object {
            val label = transaction.getLabel
            val note = transaction.getNote
            val amount = transaction.getFormattedAmountOrValue
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val paymentMethod = transaction.getPaymentMethodOrNull ?: PaymentMethod.CASH
            val affectAmount = transaction.getAffectedAmount?.label ?: ""
            val transactionType = transaction.type
            val selectedIcon = transaction.getSelectedIcon
        }
    }
    val transactionTypeLabel = stringResource(id = state.transactionType.label)
    val transactionTypeIcon = painterResource(state.transactionType.icon)
    val transactionTypeColor = colorResource(state.transactionType.color)

    Column(
        modifier = Modifier.fillMaxHeight(0.5f)
    ) {
        TransactionViewTitle(
            title = state.label,
            color = transactionTypeColor,
            selectedIcon = state.selectedIcon,
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
                value = state.amount,
            )
            TransactionRow(
                key = "Created At",
                value = state.createdAt,
            )
            TransactionRow(
                key = "Payment Method",
                value = state.paymentMethod.label,
            )
            TransactionRow(
                key = "Affected Amount",
                value = state.affectAmount,
            )

            if (state.note.isNotBlank()) {
                TransactionNoteView(
                    note = state.note
                )
            }
        }
    }
}

@Composable
private fun TransactionLiabilityFulfillmentCard(
    transaction: Transaction
) {

    val parentTransaction = transaction.getParentTransaction ?: return

    val state = remember(transaction, parentTransaction) {
        object {
            val note = transaction.getNote
            val amount = transaction.getAmountOrValue ?: 0.0
            val formattedAmount = transaction.getFormattedAmountOrValue
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val paymentMethod = (transaction.getPaymentMethodOrNull
                ?: PaymentMethod.CASH).label
            val affectAmount = transaction.getAffectedAmount?.label ?: ""
            val transactionType = transaction.type
            val selectedIcon = transaction.getSelectedIcon
            val remaining = ((parentTransaction.getAmountOrValue
                ?: 0.0) - (parentTransaction.getFulfillmentTotalSum
                ?: 0.0)).formatToAmount()
            val statusColor = parentTransaction.getStatusColor
            val parentTransactionLabel = parentTransaction.getLabel
            val parentTransactionTypeName = parentTransaction.type.label
        }
    }

    val transactionTypeLabel = stringResource(id = state.transactionType.label)
    val transactionTypeIcon = painterResource(state.transactionType.icon)
    val transactionTypeColor = colorResource(state.transactionType.color)
    val parentTransactionTyeName = stringResource(state.parentTransactionTypeName)
    val statusColor = colorResource(parentTransaction.getStatusColor)

    Column(
        modifier = Modifier.fillMaxHeight(0.5f)
    ) {
        TransactionFulfillmentViewTitle(
            title = state.parentTransactionLabel,
            color = transactionTypeColor,
            selectedIcon = state.selectedIcon,
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
                value = state.formattedAmount,
            )
            TransactionRow(
                key = "Created At",
                value = state.createdAt,
            )
            TransactionRow(
                key = "Payment Method",
                value = state.paymentMethod,
            )

            TransactionRow(
                key = "Affected Amount",
                value = state.affectAmount,
            )

            TransactionRow(
                key = "Status",
                value = parentTransaction.getStatus ?: "",
                color = statusColor
            )

            TransactionRow(
                key = "Remaining",
                value = state.remaining,
            )

            if (state.note.isNotBlank()) {
                TransactionNoteView(
                    note = state.note
                )
            }
        }
    }
}


@Composable
private fun TransactionLiabilityCard(
    transaction: Transaction,
    onShow: Boolean,
) {

    val state = remember(transaction) {
        object {
            val label = transaction.getLabel
            val note = transaction.getNote
            val amount = transaction.getAmountOrValue ?: 0.0
            val formattedAmountOrValue = transaction.getFormattedAmountOrValue
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val paymentMethod = (transaction.getPaymentMethodOrNull
                ?: PaymentMethod.CASH).label
            val affectAmount = transaction.getAffectedAmount?.label ?: ""
            val transactionType = transaction.type
            val selectedIcon = transaction.getSelectedIcon
            val totalFulfillment = transaction.getFulfillmentTotalSum
            val status = transaction.getStatus ?: ""
            val statusColor = transaction.getStatusColor
        }
    }

    val transactionTypeLabel = stringResource(id = state.transactionType.label)
    val transactionTypeIcon = painterResource(state.transactionType.icon)
    val transactionTypeColor = colorResource(state.transactionType.color)
    val colorOfStatus = colorResource(state.statusColor)

    Column(
        modifier = Modifier.fillMaxHeight(0.5f)
    ) {
        TransactionViewTitle(
            title = state.label,
            color = transactionTypeColor,
            selectedIcon = state.selectedIcon,
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
                value = state.formattedAmountOrValue,
            )
            TransactionRow(
                key = "Created At",
                value = state.createdAt,
            )
            TransactionRow(
                key = "Payment Method",
                value = state.paymentMethod,
            )
            TransactionRow(
                key = "Affected Amount",
                value = state.affectAmount,
            )

            if (state.note.isNotBlank()) {
                TransactionNoteView(
                    note = state.note
                )
            }
        }

        TransactionFulfillmentStatusProgress(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            amount = state.amount,
            totalFulfillment = state.totalFulfillment,
            color = colorOfStatus,
            title = state.status,
            onShow = onShow
        )
    }
}

@Composable
private fun TransactionAttainCard(
    transaction: Transaction.Attain,
    onShow: Boolean
) {
    val state = remember(transaction) {
        object {
            val parentLabel = transaction.getParentTransaction?.getLabel ?: ""
            val transactionTypeColor = transaction.type.color
            val parentSelectedIcon =
                transaction.getParentTransaction?.getSelectedIcon ?: SelectedIcon()
            val transactionTypeIcon = transaction.type.icon
            val formattedAmountOrValue = transaction.getFormattedAmountOrValue
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val amountOrValue = transaction.goal.value

            val totalAttain = transaction.goal.totalAttain
            val status = transaction.goal.getStatus ?: ""
            val statusColor = transaction.goal.getStatusColor
            val goalType = transaction.goal.goalType
        }
    }

    val transactionTypeColor = colorResource(state.transactionTypeColor)
    val transactionTypeIcon = painterResource(state.transactionTypeIcon)
    val selectedIcon = state.parentSelectedIcon
    val subtitle = "Goal Attainment"
    val statusColor = colorResource(state.statusColor)

    Column(
        modifier = Modifier.fillMaxHeight(0.4f)
    ) {
        TransactionFulfillmentViewTitle(
            title = state.parentLabel,
            color = transactionTypeColor,
            selectedIcon = selectedIcon,
            subtitle = subtitle,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = subtitle,
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
                value = state.formattedAmountOrValue,
            )
            TransactionRow(
                key = "Created At",
                value = state.createdAt,
            )
        }

        TransactionFulfillmentStatusProgress(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            amount = state.amountOrValue,
            totalFulfillment = state.totalAttain,
            color = statusColor,
            title = state.status,
            onShow = onShow,
            isAmount = state.goalType == GoalType.AMOUNT
        )
    }
}

@Composable
private fun TransactionGoalCard(
    transaction: Transaction.Goal,
    onShow: Boolean
) {

    val state = remember(transaction) {
        object {
            val label = transaction.label
            val note = transaction.note
            val formattedAmount = transaction.getFormattedAmountOrValue
            val amount = transaction.value
            val totalFulfillment = transaction.totalAttain
            val createdAt = transaction.createdAt.toLocalDateTime().formatedDateTime
            val startDateTime = transaction.startedAt.toLocalDateTime().formatedDateTime
            val endDateTime = transaction.endAt.toLocalDateTime().formatedDateTime
            val recurrencePattern = transaction.repeatable.name
            private val achievement = transaction.achievement
            val status = transaction.status.label
            val statusColor = transaction.status.color
            val selectedIcon = transaction.getSelectedIcon
            val completedAchievementSize =
                achievement.filter { it.status == GoalStatus.COMPLETED }.size.toString()
            val failedAchievementSize =
                achievement.filter { it.status == GoalStatus.FAILED }.size.toString()
        }
    }

    val transactionTypeLabel = stringResource(id = TransactionType.GOAL.label)
    val transactionTypeIcon = painterResource(TransactionType.GOAL.icon)
    val transactionTypeColor = colorResource(TransactionType.GOAL.color)
    val achievementColor = colorResource(TransactionType.ACHIEVEMENT.color)
    val statusColor = colorResource(state.statusColor)

    Column(
        modifier = Modifier.fillMaxHeight(0.5f)
    ) {
        TransactionViewTitle(
            title = state.label,
            color = transactionTypeColor,
            selectedIcon = state.selectedIcon,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            },
        )
        TransactionColorStatusProgress(
            modifier = Modifier.fillMaxWidth(),
            amount = state.amount,
            totalFulfillment = state.totalFulfillment,
            color = transactionTypeColor,
            onShow = onShow
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TransactionRow(
                key = "Amount",
                value = state.formattedAmount,
            )
            TransactionRow(
                key = "Created At",
                value = state.createdAt,
            )
            TransactionRow(
                key = "Started At",
                value = state.startDateTime,
            )
            TransactionRow(
                key = "Deadline time",
                value = state.endDateTime,
            )

            TransactionRow(
                key = "Recurrence Pattern",
                value = state.recurrencePattern,
            )

            TransactionRow(
                key = "Status",
                value = state.status,
                color = statusColor
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
                                    state.completedAchievementSize,
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
                                    state.failedAchievementSize,
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

            if (state.note.isNotBlank()) {
                TransactionNoteView(
                    note = state.note
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
private fun TransactionPlanFulfillmentRow(
    key: String,
    value: String,
    color: Color,
    icon: Painter,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Text(
                key,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
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

@Composable
private fun TransactionPlanHeader(
    amount: String,
    createdAt: String,
    sumOfFulfillment: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Planned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                )
                Text(
                    amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Spent",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight
                )
                Text(
                    sumOfFulfillment,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Created At $createdAt",
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
private fun TransactionFulfillmentStatusProgress(
    modifier: Modifier = Modifier,
    amount: Double,
    totalFulfillment: Double?,
    color: Color,
    title: String,
    onShow: Boolean,
    isAmount: Boolean = true
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
        val result = (totalFulfillment ?: 0.0)
        if (isAmount) {
            result.formatToAmount()
        } else {
            result.toInt().toString()
        }
    }

    val remaining = remember(
        totalFulfillment,
        amount
    ) {
        val result = (amount - (totalFulfillment ?: 0.0))
        if (isAmount) {
            result.formatToAmount()
        } else {
            result.toInt().toString()
        }
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


@Composable
private fun TransactionColorStatusProgress(
    modifier: Modifier = Modifier,
    amount: Double,
    totalFulfillment: Double?,
    color: Color,
    onShow: Boolean,
    isAmount: Boolean = true
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
        val result = (totalFulfillment ?: 0.0)
        if (isAmount) {
            result.formatToAmount()
        } else {
            result.toInt().toString()
        }
    }

    val remaining = remember(
        totalFulfillment,
        amount
    ) {
        val result = (amount - (totalFulfillment ?: 0.0))
        if (isAmount) {
            result.formatToAmount()
        } else {
            result.toInt().toString()
        }
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
        LinearProgressIndicator(
            progress = { animateProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Square
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$textProgress% (${formattedTotalFulfillment})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
            )
        }
    }
}