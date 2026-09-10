// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState

@Composable
fun AllTabLazyList(transactions: DataState<Map<String, List<Transaction>>>) {
    Surface {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (transactions) {
                is DataState.Success -> {
                    val groupedTransactions = transactions.data

                    if (groupedTransactions.isNotEmpty()) {
                        groupedTransactions.forEach { (date, transactions) ->
                            stickyHeader {
                                AllTabLazyListStickyHeader(
                                    date = date
                                )
                            }

                            items(
                                count = transactions.size,
                                key = { index -> transactions[index].id }
                            ) { index ->
                                val transaction = transactions[index]

                                val shape = remember(index, transactions.size) {
                                    when {
                                        transactions.size == 1 -> RoundedCornerShape(16.dp)
                                        index == 0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                        index == transactions.lastIndex -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                                        else -> RectangleShape
                                    }
                                }

                                AllTabLazyListItem(
                                    modifier = Modifier
                                        .animateItem(),
                                    shape = shape,
                                    transaction = transaction
                                )
                            }
                        }
                    }
                    else {
                        item {
                            AllTabLazyListEmpty()
                        }
                    }
                }


                is DataState.Loading -> {
                    items(5) {
                        AllTabLazyListItemShimmer()
                    }
                }

                is DataState.Error -> {
                    item {
                        AllTabLazyListError(
                            message = transactions.message
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AllTabLazyListHeader() {

}

@Composable
fun AllTabLazyListEmpty() {

}

@Composable
fun AllTabLazyListError(message: String) {

}