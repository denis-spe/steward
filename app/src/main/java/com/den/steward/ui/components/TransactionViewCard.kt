// Glory be to LORD our GOD
package com.den.steward.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.backend.dataStructure.PaymentMethod
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.helper.formatedDateTime
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
                            affectAmount = transaction.affectAmount
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    label: String,
    note: String,
    amount: Double,
    createdAt: LocalDateTime,
    paymentMethod: PaymentMethod,
    affectAmount: Boolean
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionRow(
            key = "Label",
            value = label
        )
        TransactionRow(
            key = "Note",
            value = note
        )
        TransactionRow(
            key = "Amount",
            value = amount.toString()
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
    }
}


@Composable
fun TransactionRow(
    key: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
            color = Color.Gray
        )
    }
}

