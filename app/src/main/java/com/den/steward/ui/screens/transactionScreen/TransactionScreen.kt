// Glory be to the LORD GOD of hosts
package com.den.steward.ui.screens.transactionScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.TransactionScreenViewModel
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.pop
import com.den.steward.helper.toLocalDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionScreenViewModel = hiltViewModel(),
    backStack: NavBackStack<NavKey>
) {

    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        topBar = {
            TransactionScreenHeader(
                scrollBehavior = scrollBehavior
            ) {
                backStack.pop()
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            TransactionScreenContent(
                transactions = transactions
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenHeader(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
) {
    LargeTopAppBar(
        title = {

        },

        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                FilledIconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Transactions"
                )
            }
        },

        scrollBehavior = scrollBehavior
    )
}

@Composable
fun TransactionScreenContent(transactions: DataState<List<Transaction>>) {
    when (transactions) {
        is DataState.Success -> {
            val data = transactions.data
            if (data.isEmpty()) {
                TransactionScreenEmptyList()
            } else {
                TransactionScreenLazyList(
                    transactions = data.toImmutableList()
                )
            }
        }

        is DataState.Error -> {
            TransactionScreenError(message = transactions.message)
        }

        is DataState.Loading -> {
            TransactionScreenLoading(nShimmerItem = 5)
        }
    }
}

@Composable
fun TransactionScreenLazyList(
    transactions: ImmutableList<Transaction>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(transactions.size) { index ->
            val transaction = transactions[index]
            TransactionListItem(transaction = transaction)
        }
    }
}

@Composable
fun TransactionListItem(transaction: Transaction) {
    val state = remember {
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

    val icon = painterResource(id = state.icon)
    val typeColor = colorResource(id = state.typeColor)
    val typeIcon = painterResource(id = state.typeIcon)

    Surface(
        shape = MaterialTheme.shapes.small,
        color = typeColor.copy(0.1f),
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
                    Image(
                        painter = typeIcon,
                        contentDescription = "Transaction Type Icon",
                        modifier = Modifier.size(15.dp),
                        colorFilter = ColorFilter.tint(typeColor)
                    )

                    Image(
                        painter = painterResource(state.paymentMethod.icon),
                        contentDescription = "Payment Method Icon",
                        modifier = Modifier.size(15.dp)
                    )

                    Text(
                        text = state.paymentMethod.label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionScreenEmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_transactions),
                contentDescription = "Empty List",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "No transaction available.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


@Composable
fun TransactionScreenError(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.circle_error),
                contentDescription = "Error",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TransactionScreenLoading(nShimmerItem: Int) {
}


