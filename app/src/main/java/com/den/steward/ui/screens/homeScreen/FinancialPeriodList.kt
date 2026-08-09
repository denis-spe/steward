// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.helper.formattedTime
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime

@Composable
fun FinancialPeriodList(transactions: DataState<List<Transaction>>) {
    Crossfade(
        targetState = transactions
    ) { state ->
        when(state) {
            is DataState.Loading -> {
                Text("Loading...")
            }
            is DataState.Success -> {
                val data = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(data.size) { index ->
                        val transaction = data[index]
                        TransactionItem(transaction = transaction)
                    }
                }
            }
            is DataState.Error -> {}
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val localDateTime = transaction.createdAt.toLocalDateTime()
    val time = localDateTime.formattedTime

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
            if (transaction.getLabel.isNotEmpty() && transaction.getNote.isNotBlank()) {
                Text(
                    transaction.getNote,
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
                transaction.getFormattedAmountOrValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
            )
            Text(
                stringResource(transaction.type.label),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                color = Color.Gray
            )
        }
    }
}