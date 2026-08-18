// Glory be to LORD our GOD
package com.den.steward.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.backend.dataStructure.GoalStatus
import com.den.steward.backend.dataStructure.PaymentMethod
import com.den.steward.backend.dataStructure.RecurrencePattern
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.limitLength
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionViewCard(
    transaction: Transaction,
    onShow: Boolean,
    onDismissRequest: () -> Unit
) {
    if (onShow) {
        Dialog (
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

                when(transaction) {
                    is Transaction.Expense -> {
                        TransactionCard(
                            label = transaction.label,
                            note = transaction.note,
                            amount = transaction.amount,
                            createdAt = transaction.createdAt.toLocalDateTime(),
                            paymentMethod = transaction.paymentMethod,
                            transactionType = transaction.type,
                            affectAmount = transaction.affectAmount
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
                            transactionType = transaction.type
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
                            transactionType = transaction.type
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
                            status = transaction.status
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
    transactionType: TransactionType
) {
    val transactionTypeLabel = stringResource(id = transactionType.label)
    val transactionTypeIcon = painterResource(transactionType.icon)
    val transactionTypeColor = colorResource(transactionType.color)

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        TransactionViewTitle(
            title = label,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            }
        )

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
            key = "Affect Amount",
            value = if (affectAmount) "Yes" else "No"
        )

        if (note.isNotBlank()) {
            TransactionNoteView(
                note = note
            )
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
    achievement: List<Transaction.Achievement>
) {
    val transactionTypeLabel = stringResource(id = TransactionType.GOAL.label)
    val transactionTypeIcon = painterResource(TransactionType.GOAL.icon)
    val transactionTypeColor = colorResource(TransactionType.GOAL.color)

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        TransactionViewTitle(
            title = label,
            icon = {
                Icon(
                    painter = transactionTypeIcon,
                    contentDescription = transactionTypeLabel,
                    tint = transactionTypeColor
                )
            }
        )

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

        if (note.isNotBlank()) {
            TransactionNoteView(
                note = note
            )
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
    icon: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(
                title.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
        }

        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}