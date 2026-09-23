// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect

import com.den.steward.backend.states.HomeTab
import com.den.steward.backend.states.Filter

@Composable
internal fun OverviewList(
    dataState: DataState<Map<String, List<Transaction>>>,
    onTabChange: (HomeTab, Filter?) -> Unit = { _, _ -> }
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when(dataState) {
            is DataState.Success -> {
                OverviewLazyList(
                    data = dataState.data,
                    onTabChange = onTabChange
                )
            }
            is DataState.Loading -> {
                OverviewListLoading()
            }
            is DataState.Error -> {
                OverviewListError()
            }
        }
    }
}

@Composable
internal fun OverviewTabHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 4.dp)
    ) {
        Text(
            text = "Financial Overview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Your financial health at a glance",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
internal fun OverviewTransactionList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(transactions, key = { "overview_tx_${it.id}" }) { transaction ->
            OverviewTransactionItem(transaction = transaction)
        }
    }
}

@Composable
internal fun OverviewGoalList(transactions: List<Transaction>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        transactions.forEach {
            OverviewGoalItem(transaction = it)
        }
    }
}

@Composable
internal fun OverviewDebtList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(transactions, key = { "overview_debt_${it.id}" }) { transaction ->
            OverviewDebtItem(transaction = transaction)
        }
    }
}

@Composable
internal fun OverviewLoanList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(transactions, key = { "overview_loan_${it.id}" }) { transaction ->
            OverviewLoanItem(transaction = transaction)
        }
    }
}


@Composable
internal fun OverviewLazyList(
    data: Map<String, List<Transaction>>,
    onTabChange: (HomeTab, Filter?) -> Unit
) {
    val allTransactions = data.values.flatten()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            OverviewTabHeader()
        }

        item {
            OverviewSummarySection(transactions = allTransactions)
        }

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
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )

                        TextButton(
                            onClick = {
                                val filter = when(header) {
                                    "Transactions" -> Filter.ALL
                                    "Goal" -> Filter.GOAL
                                    "Debt" -> Filter.DEBT
                                    "Lent" -> Filter.LENT
                                    else -> Filter.ALL
                                }
                                onTabChange(HomeTab.ALL, filter)
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "View all",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
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

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
        }
    }
}

@Composable
internal fun OverviewListLoading() {
    val headers = listOf("Transactions", "Goal", "Debt", "Lent")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            OverviewTabHeader()
        }

        headers.forEach { header ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )

                    Box(
                        modifier = Modifier
                            .size(60.dp, 20.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                }
            }

            when (header) {
                "Transactions" -> {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(5) {
                                OverviewTransactionItemShimmer()
                            }
                        }
                    }
                }

                "Goal" -> {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                OverviewGoalItemShimmer()
                            }
                        }
                    }
                }

                "Debt" -> {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
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

@Composable
internal fun OverviewSummarySection(transactions: List<Transaction>) {
    val totalEarnings = transactions.filter { it.type == TransactionType.EARNINGS }.sumOf { it.getAmountOrValue ?: 0.0 }
    val totalExpenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.getAmountOrValue ?: 0.0 }
    val totalLent = transactions.filterIsInstance<Transaction.Lent>().sumOf { it.amount }
    val totalDebt = transactions.filterIsInstance<Transaction.Debt>().sumOf { it.amount }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Net Flow",
            amount = totalEarnings - totalExpenses,
            color = MaterialTheme.colorScheme.primary
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Liabilities",
            amount = totalDebt - totalLent,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = amount.formatToAmount(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (amount >= 0) color else MaterialTheme.colorScheme.error
            )
        }
    }
}
