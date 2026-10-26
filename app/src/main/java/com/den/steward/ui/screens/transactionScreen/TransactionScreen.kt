// Glory be to the LORD GOD of hosts
package com.den.steward.ui.screens.transactionScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.transactionScreenState.TransactionDataState
import com.den.steward.backend.viewModels.TransactionScreenViewModel
import com.den.steward.helper.pop
import com.den.steward.ui.components.DataDeletionDialog
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionScreenViewModel = hiltViewModel(),
    backStack: NavBackStack<NavKey>
) {

    val transactionDataState by viewModel.transactionDataState.collectAsStateWithLifecycle()
    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val swipedState = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    val scope = rememberCoroutineScope()
    val dataUpdateState by viewModel.dataUpdateState.collectAsStateWithLifecycle()
    val transactionScreenUiState by viewModel.transactionScreenUiState.collectAsStateWithLifecycle()
    val transactionToBeDeleted = remember { mutableStateListOf<Transaction>() }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TransactionScreenTopAppBar(
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
                    swipedState = swipedState,
                    transactionDataState = transactionDataState,
                    updateSelectedTransaction = viewModel.updateTransaction::updateSelectedTransaction,
                    updateSelectedTransactionToDelete = viewModel::updateSelectedTransactionToDelete,
                    transactionToBeDeleted = transactionToBeDeleted,
                    onTransactionView = {

                    }
                )
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            AnimatedVisibility(
                visible = transactionToBeDeleted.isNotEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors()
                        .copy(
                            containerColor = Color(0xFFF62229).copy(0.2f).compositeOver(
                                MaterialTheme.colorScheme.background
                            ),
                            contentColor = Color(0xFFF62229)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "DeleteForever"
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Delete All",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFF62229)
                            )
                        )
                        Text(
                            text = "${transactionToBeDeleted.size} Transactions",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    DataDeletionDialog(
        transaction = transactionScreenUiState.selectedTransactionToDelete,
        onDialogShow = transactionScreenUiState.openDeleteDialog,
        onDelete = viewModel::deleteTransaction,
    ) {
        viewModel.updateOpenDeleteDialog(false)
        scope.launch {
            swipedState.value?.reset()
        }
    }

    UpdateTransactionBottomDrawerSheet(
        dataUpdateState = dataUpdateState,
        updateCorrectNote = viewModel.updateTransaction::updateCurrentNote,
        updateCorrectLabel = viewModel.updateTransaction::updateCurrentLabel,
        updateSelectedIcon = viewModel.updateTransaction::updateSelectedIcon,
        updatePaymentMethod = viewModel.updateTransaction::updatePaymentMethod,
        updateCorrectAmount = viewModel.updateTransaction::updateCurrentAmount,
        updateIsLabelCorrect = viewModel.updateTransaction::updateIsLabelCorrect,
        updateIsAmountCorrect = viewModel.updateTransaction::updateIsAmountCorrect,
        updateIsAffectingAmount = viewModel.updateTransaction::updateIsAffectingAmount,
        onLocalTimeChange = viewModel.updateTransaction::onLocalTimeChangeUpdate,
        onLocalDateChange = viewModel.updateTransaction::onLocalDateChangeUpdate,
        onExchange = viewModel.updateTransaction::onExchange,
        onTransactionUpdate = {
            viewModel.onUpdateTransaction()
            scope.launch {
                swipedState.value?.reset()
            }
        },
        reset = {
            viewModel.updateTransaction.onReset()
            scope.launch {
                swipedState.value?.reset()
            }
        }
    )
}

@Composable
private fun TransactionScreenHeaderAction() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort,
                contentDescription = "Analytics"
            )
        }

        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Outlined.Analytics,
                contentDescription = "Analytics"
            )
        }

        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionScreenTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
) {
    val shadowElevation by animateDpAsState(
        targetValue = if (scrollBehavior.state.overlappedFraction > 0f) 2.dp else 0.dp,
        label = "TopAppBarShadow"
    )

    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "View all your transactions",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },

        actions = {
            TransactionScreenHeaderAction()
        },

        navigationIcon = {
            FilledIconButton(
                onClick = onBackClick,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = Color.LightGray.copy(0.2f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },

        modifier = Modifier.shadow(shadowElevation),

        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun TransactionScreenContent(
    transactionDataState: DataState<TransactionDataState>,
    swipedState: MutableState<SwipeToDismissBoxState?>,
    transactionToBeDeleted: SnapshotStateList<Transaction>,
    updateSelectedTransaction: (transaction: Transaction) -> Unit = {},
    updateSelectedTransactionToDelete: (transaction: Transaction) -> Unit = {},
    onTransactionView: (transaction: Transaction) -> Unit = {},
) {
    when (transactionDataState) {
        is DataState.Success -> {
            val transactionDataState = transactionDataState.data
            if (transactionDataState.transactions.isEmpty()) {
                TransactionScreenEmpty()
            } else {
                TransactionScreenLazyList(
                    swipedState = swipedState,
                    transactionDataState = transactionDataState,
                    updateSelectedTransaction = updateSelectedTransaction,
                    updateSelectedTransactionToDelete = updateSelectedTransactionToDelete,
                    transactionToBeDeleted = transactionToBeDeleted,
                    onTransactionView = onTransactionView
                )
            }
        }

        is DataState.Error -> {
            TransactionScreenError(message = transactionDataState.message)
        }

        is DataState.Loading -> {
            TransactionScreenLoading()
        }
    }
}
@Composable
private fun TransactionScreenEmpty() {
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
private fun TransactionScreenError(message: String) {
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
private fun TransactionScreenLoading() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "loading_summary") {
            TransactionScreenMainSummaryShimmer()
        }

        (0..6).forEach { idx ->
            item(key = idx) {
                TransactionListItemShimmer()
            }
        }
    }
}


