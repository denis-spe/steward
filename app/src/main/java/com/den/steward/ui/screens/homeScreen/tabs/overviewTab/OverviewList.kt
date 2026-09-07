// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
internal fun OverviewList(
    dataState: DataState<Map<String, List<Transaction>>>
) {
    when(dataState) {
        is DataState.Success -> {
            val data = dataState.data
            OverviewLazyList(data = data)
        }
        is DataState.Loading -> {
            OverviewListLoading()
        }
        is DataState.Error -> {
            OverviewListError()
        }
    }
}

@Composable
internal fun OverviewTabHeader() {

}

@Composable
internal fun OverviewTransactionList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 3.dp)
    ) {
        items(transactions) { transaction ->
            OverviewTransactionItem(transaction = transaction)
        }
    }
}

@Composable
internal fun OverviewGoalList(transactions: List<Transaction>) {
    transactions.forEach {
        OverviewGoalItem(transaction = it)
    }
}

@Composable
internal fun OverviewDebtList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(transactions, key = { it.id }) { transaction ->
            OverviewDebtItem(transaction = transaction)
        }
    }
}

@Composable
internal fun OverviewLoanList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 3.dp)
    ) {
        items(transactions, key = { it.id }) { transaction ->
            OverviewLoanItem(transaction = transaction)
        }
    }
}


@Composable
internal fun OverviewLazyList(
    data: Map<String, List<Transaction>>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        data.forEach { (header, transactions) ->
            if (transactions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        TextButton(
                            onClick = { /* Handle view more click */ }
                        ) {
                            Text(
                                text = "View more",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                when (header) {
                    "Transactions" -> {
                        item(key = "transactions") {
                            OverviewTransactionList(transactions = transactions)
                        }
                    }

                    "Goal" -> {
                        item(key = "goal") {
                            OverviewGoalList(transactions = transactions)
                        }
                    }

                    "Debt" -> {
                        item(key = "debt") {
                            OverviewDebtList(transactions = transactions)
                        }
                    }

                    "Lent" -> {
                        item(key = "lent") {
                            OverviewLoanList(transactions = transactions)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OverviewListLoading() {
    val headers = listOf("Transactions", "Goal", "Debt", "Lent")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        headers.forEach { header ->
            item {
                Text(
                    text = header,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            when (header) {
                "Transactions" -> {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 3.dp)
                        ) {
                            items(5) {
                                OverviewTransactionItemShimmer()
                            }
                        }
                    }
                }

                "Goal" -> {
                    items(3) {
                        OverviewGoalItemShimmer()
                    }
                }

                "Debt" -> {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 3.dp)
                        ) {
                            items(3) {
                                OverviewDebtItemShimmer()
                            }
                        }
                    }
                }

                "Lent" -> {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 3.dp)
                        ) {
                            items(3) {
                                OverviewLoanItemShimmer()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OverviewListError() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Failed to load overview data.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

