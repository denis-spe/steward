// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.transactionList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.helper.formattedTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.TransactionViewCard

@Composable
fun FinancialPeriodListItem(transaction: Transaction) {
    val localDateTime = remember(transaction) { transaction.createdAt.toLocalDateTime() }
    val time = localDateTime.formattedTime
    val onShow = remember { mutableStateOf(false) }
    val amount = remember(transaction) {  transaction.getFormattedAmountOrValue }
    val paymentMethod = remember(transaction) { transaction.getPaymentMethodOrNull }

    Surface(
        onClick = {
            onShow.value = true
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    time,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    transaction.getLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                )

                if (transaction.getAffectAmount != null) {
                    Text(
                        buildAnnotatedString {
                            append("Affected: ")
                            append(transaction.getAffectAmount)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                        color = Color.Gray
                    )
                }

            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    amount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    AsyncImage(
                        model = paymentMethod?.icon,
                        contentDescription = paymentMethod?.label,
                        modifier = Modifier.size(18.dp).padding(end = 4.dp)
                    )
                    AsyncImage(
                        model = transaction.type.icon,
                        contentDescription = stringResource(id = transaction.type.label),
                        modifier = Modifier.size(18.dp).padding(end = 4.dp),
                        colorFilter = ColorFilter.tint(colorResource(id = transaction.type.color))
                    )
                }
            }
        }
    }

    TransactionViewCard(
        transaction = transaction,
        onShow = onShow.value
    ) {
        onShow.value = false
    }
}