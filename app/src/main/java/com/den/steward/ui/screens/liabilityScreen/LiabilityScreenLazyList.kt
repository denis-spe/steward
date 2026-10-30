// For Law was given through Moses, Grace and came through JESUS Christ our LORD
package com.den.steward.ui.screens.liabilityScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.liabilityScreenState.LiabilityDataState
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.SwipeDismiss

@Composable
fun LiabilityScreenLazyList(
    liabilityDataState: LiabilityDataState,
    transactionToBeDeleted: SnapshotStateList<Transaction>,
    swipedState: MutableState<SwipeToDismissBoxState?>,
    updateSelectedTransactionToDelete: (Transaction) -> Unit,
    updateSelectedTransaction: (Transaction) -> Unit,
    onTransactionView: (Transaction) -> Unit
) {
    val (groupedTransactions, transactionMainSummary) = liabilityDataState
    val transactionIds = transactionToBeDeleted.map { it.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "header_summary") {
            LiabilityScreenMainSummary(
                liabilityMainSummary = transactionMainSummary
            )
        }

        if (groupedTransactions.isEmpty()) {
            item(key = "empty_transactions") {
                LiabilityScreenEmpty()
            }
        } else {
            groupedTransactions.forEach { (date, transactions) ->
                stickyHeader(key = date) {
                    LiabilityScreenListItemHeader(date = date)
                }

                items(transactions.size, key = { transactions[it].id }) { index ->
                    val transaction = transactions[index]

                    LiabilityListItem(
                        transaction = transaction,
                        selected = transaction.id in transactionIds,
                        onDelete = { dismissState ->
                            swipedState.value = dismissState
                            updateSelectedTransactionToDelete(transaction)
                        },
                        onUpdate = { dismissState ->
                            swipedState.value = dismissState
                            updateSelectedTransaction(transaction)
                        },
                        onLongClick = {
                            if (transaction.id !in transactionIds) {
                                transactionToBeDeleted.add(transaction)
                            } else {
                                transactionToBeDeleted.removeIf { it.id == transaction.id }
                            }
                        },
                        onClick = {
                            if (transaction.id in transactionIds) {
                                transactionToBeDeleted.removeIf { it.id == transaction.id }
                            } else if (transactionToBeDeleted.isNotEmpty()) {
                                transactionToBeDeleted.add(transaction)
                            } else {
                                onTransactionView(transaction)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LiabilityScreenListItemHeader(
    date: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background.copy(0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun LiabilityScreenListItemHeaderShimmer() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background.copy(0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp, 26.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun LiabilityListItem(
    transaction: Transaction,
    selected: Boolean = false,
    onLongClick: (transaction: Transaction) -> Unit = {},
    onClick: (transaction: Transaction) -> Unit = {},
    onDelete: (dismissState: SwipeToDismissBoxState) -> Unit,
    onUpdate: (dismissState: SwipeToDismissBoxState) -> Unit
) {
    val state = remember(transaction) {
        object {
            val title = transaction.getLabel
            val icon = transaction.getIcon
            val amount = transaction.getFormattedAmountOrValue
            val paymentMethod = transaction.getPaymentMethodOrNull ?: PaymentMethod.CASH
            val typeColor = transaction.type.color
            val date = transaction.createdAt.toLocalDateTime().formatedDateTime
            val typeIcon = transaction.type.icon
        }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance: Float ->
            distance * 0.5f
        }
    )

    val icon = painterResource(id = state.icon)
    val red = Color(0xFFF44336)
    val typeColor = colorResource(id = state.typeColor)
    val typeIcon = painterResource(id = state.typeIcon)
    val background = if (selected) Color.LightGray.copy(0.08f)
    else typeColor.copy(0.1f)

    SwipeDismiss(
        dismissState = dismissState,
        shape = MaterialTheme.shapes.small,
        onDelete = {
            onDelete(dismissState)
        },
        onUpdate = {
            onUpdate(dismissState)
        }
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = background.compositeOver(
                MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.combinedClickable(
                onClick = {
                    onClick(transaction)
                },
                onLongClick = {
                    onLongClick(transaction)
                }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = typeColor.copy(0.5f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .border(
                                    1.dp, typeColor,
                                    MaterialTheme.shapes.small
                                )
                                .size(60.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = icon,
                                contentDescription = "Transaction Icon"
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = state.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = state.date,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(end = 10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = state.amount,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = state.paymentMethod.label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Image(
                                painter = painterResource(state.paymentMethod.icon),
                                contentDescription = "Payment Method Icon",
                                modifier = Modifier.size(15.dp)
                            )
                            Image(
                                painter = typeIcon,
                                contentDescription = "Transaction Type Icon",
                                modifier = Modifier.size(15.dp),
                                colorFilter = ColorFilter.tint(typeColor)
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = selected,
                    modifier = Modifier.weight(0.2f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Transaction Icon",
                            modifier = Modifier.size(20.dp),
                            tint = red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiabilityListItemShimmer() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = Color.LightGray.copy(0.1f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(
                            1.dp, Color.LightGray,
                            MaterialTheme.shapes.small
                        )
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect()
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp, 20.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(68.dp, 16.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                }
            }

            Column(
                modifier = Modifier.padding(end = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp, 20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(43.dp, 20.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )

                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .border(
                                1.dp, Color.LightGray,
                                MaterialTheme.shapes.small
                            )
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .border(
                                1.dp, Color.LightGray,
                                MaterialTheme.shapes.small
                            )
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
private fun LiabilityScreenEmpty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_empty_transactions),
                contentDescription = "No Transactions",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No transaction available.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                "Go back and add some transactions.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}