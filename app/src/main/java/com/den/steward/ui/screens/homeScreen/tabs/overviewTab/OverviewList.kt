// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.OverviewData
import com.den.steward.backend.states.overviewTabState.OverviewSummary
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.screens.screenManager.LiabilityRouter
import com.den.steward.ui.screens.screenManager.TransactionRouter

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
internal fun OverviewList(
    overviewDataState: DataState<OverviewData>,
    backStack: NavBackStack<NavKey>
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (overviewDataState) {
            is DataState.Success -> {
                OverviewLazyList(
                    data = overviewDataState.data.groupedTransactions,
                    backStack = backStack,
                    overviewSummary = overviewDataState.data.summary
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
internal fun OverviewLiabilityList(transactions: List<Transaction>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(transactions, key = { "overview_${it.type.name}_${it.id}" }) { transaction ->
            if (transaction is Transaction.Debt) {
                OverviewDebtItem(transaction = transaction)
            } else {
                OverviewLoanItem(transaction = transaction)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
internal fun OverviewLazyList(
    data: Map<String, List<Transaction>>,
    backStack: NavBackStack<NavKey>,
    overviewSummary: OverviewSummary
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            OverviewTabHeader()
        }

        item {
            OverviewSummarySection(
                overviewSummary = overviewSummary,
            )
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

                        FilledIconButton(
                            onClick = {
                                when (header) {
                                    "Transactions" -> {
                                        backStack.add(TransactionRouter)
                                    }
                                    "Goal" -> Filter.GOAL
                                    "Liabilities" -> {
                                        backStack.add(LiabilityRouter)
                                    }
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors()
                                .copy(
                                    containerColor = Color.LightGray.copy(
                                        alpha = 0.2f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "See All"
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

                    "Liabilities" -> {
                        item(key = "liabilities") {
                            OverviewLiabilityList(transactions = transactions)
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
    val headers = listOf("Transactions", "Goal", "Liabilities")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            OverviewTabHeader()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCardLoading(modifier = Modifier.weight(1f))
                SummaryCardLoading(modifier = Modifier.weight(1f))
            }
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
                            .size(35.dp, 35.dp)
                            .clip(CircleShape)
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

                "Liabilities" -> {
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
internal fun OverviewSummarySection(
    overviewSummary: OverviewSummary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Net Flow",
            amount = overviewSummary.netFlow,
            color = MaterialTheme.colorScheme.primary
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Liabilities",
            amount = overviewSummary.liabilities,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    color: Color
) {
    val state = remember(amount) {
        object {
            val amountAsDouble = amount.toDoubleOrNull() ?: 0.0
            val formattedAmount = amountAsDouble.formatToAmount()
        }
    }
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
                text = state.formattedAmount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (state.amountAsDouble >= 0) color else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SummaryCardLoading(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(Color.LightGray.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp, 20.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .size(50.dp, 24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
        }
    }
}
