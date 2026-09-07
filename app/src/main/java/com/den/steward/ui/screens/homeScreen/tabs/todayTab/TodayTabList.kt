// Bless be the LORD GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState

private val icon_size = 80.dp

@Composable
fun TodayTabList(
    modifier: Modifier = Modifier,
    transactions: DataState<List<Transaction>>
) {
    Crossfade(
        targetState = transactions
    ) { state ->
        when (state) {
            is DataState.Loading -> {
                TodayTabLazyListShimmer(
                    modifier = modifier,
                    numberOfShimmerItems = 5
                )
            }

            is DataState.Success -> {
                val transactions = state.data
                if (state.isEmpty) {
                    TodayTabListEmpty(
                        modifier = modifier
                    )
                } else {
                    TodayTabLazyList(
                        modifier = modifier,
                        transactions = transactions
                    )
                }
            }

            is DataState.Error -> {
                TodayTabListError()
            }
        }
    }
}

@Composable
fun TodayTabLazyList(
    modifier: Modifier = Modifier,
    transactions: List<Transaction>
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(
                transactions.size,
                key = { index -> transactions[index].id }
            ) { index ->
                val transaction = transactions[index]
                var shape = if (index == 0) {
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                } else {
                    RoundedCornerShape(0.dp)
                }

                shape = if (index == transactions.lastIndex) {
                    RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                } else {
                    RoundedCornerShape(0.dp)
                }


                TodayTabLazyListItem(
                    transaction = transaction,
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = shape
                )
            }
        }
    }
}

@Composable
fun TodayTabLazyListShimmer(
    modifier: Modifier = Modifier,
    numberOfShimmerItems: Int
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(numberOfShimmerItems) {
            TodayTabLazyListItemShimmer()
        }
    }
}


@Composable
fun TodayTabListEmpty(
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
fun TodayTabListError(
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
