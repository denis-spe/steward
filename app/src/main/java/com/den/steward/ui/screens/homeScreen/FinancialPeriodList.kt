// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.states.DataState

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
    ListItem(
        headlineContent = { Text(transaction.label) },
        supportingContent = { Text(transaction.note) }
    )
}