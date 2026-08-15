// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen.transactionList

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.den.steward.R
import com.den.steward.backend.dataStructure.Transaction
import com.den.steward.backend.states.DataState

private val icon_size = 80.dp

@Composable
fun FinancialPeriodList(
    modifier: Modifier = Modifier,
    transactions: DataState<List<Transaction>>
) {
    Crossfade(
        targetState = transactions
    ) { state ->
        when(state) {
            is DataState.Loading -> {
                FinancialPeriodListShimmer(
                    modifier = modifier,
                    numberOfShimmerItems = 5
                )
            }
            is DataState.Success -> {
                val transactions = state.data
                if (state.isEmpty) {
                    FinancialPeriodListEmpty(
                        modifier = modifier
                    )
                } else {
                    FinancialPeriodLazyList(
                        modifier = modifier,
                        transactions = transactions
                    )
                }
            }
            is DataState.Error -> {
                FinancialPeriodListError()
            }
        }
    }
}

@Composable
fun FinancialPeriodLazyList(
    modifier: Modifier = Modifier,
    transactions: List<Transaction>
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(
            transactions.size,
            key = { index -> transactions[index].id }
        ) { index ->
            val transaction = transactions[index]

            FinancialPeriodListItem(transaction = transaction)
        }
    }
}

@Composable
fun FinancialPeriodListShimmer(
    modifier: Modifier = Modifier,
    numberOfShimmerItems: Int
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(numberOfShimmerItems) {
            FinancialPeriodListItemShimmer()
        }
    }
}


@Composable
fun FinancialPeriodListEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.empty_list),
            contentDescription = "No Transactions",
            modifier = Modifier.size(icon_size)
        )
        Text(
            "No Transactions",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
        )
    }

}

@Composable
fun FinancialPeriodListError(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.failed),
            contentDescription = "Error",
            modifier = Modifier.size(icon_size)
        )
        Text(
            "Error",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
        )
    }
}
