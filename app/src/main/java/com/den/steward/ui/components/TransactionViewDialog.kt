// Glory be to LORD our GOD
package com.den.steward.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
                            affectAmount = transaction.affectAmount,
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
                            affectAmount = transaction.affectAmount,
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
                            affectAmount = transaction.affectAmount,
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
    affectAmount: Boolean,
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
                value = amount.formatToAmount()
            )
            TransactionRow(
                key = "Created At",
                value = createdAt.formatedDateTime
            )
            TransactionRow(
                key = "Payment Method",
                value = paymentMethod.toString()
            )
            TransactionRow(
                key = "Affected Amount",
                value = if (affectAmount) "Yes" else "No"
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
                value = amount.formatToAmount()
            )
            TransactionRow(
                key = "Created At",
                value = createdAt.formatedDateTime
            )
            TransactionRow(
                key = "Started At",
                value = startDateTime.formatedDateTime
            )
            TransactionRow(
                key = "Deadline time",
                value = endDateTime.formatedDateTime
            )

            TransactionRow(
                key = "Recurrence Pattern",
                value = recurrencePattern.name
            )

            TransactionRow(
                key = "Status",
                value = status.label
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
        Text(
            value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
            color = Color.Gray
        )
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