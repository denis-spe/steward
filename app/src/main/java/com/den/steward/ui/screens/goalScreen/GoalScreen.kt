// Love th LORD your GOD with all your heart and with all your soul and
// with all your might and love your neighbor as your self
package com.den.steward.ui.screens.goalScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.den.steward.backend.states.goalState.GoalDataState
import com.den.steward.backend.viewModels.GoalScreenViewModel
import com.den.steward.helper.pop
import com.den.steward.ui.components.DataDeletionDialog
import com.den.steward.ui.components.DeleteAllButton
import com.den.steward.ui.components.fields.WideSearchTextField
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalScreenViewModel = hiltViewModel(),
    backStack: NavBackStack<NavKey>
) {

    val goalTransactionDataState by viewModel.goalTransactionDataState.collectAsStateWithLifecycle()
    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val swipedState = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    val scope = rememberCoroutineScope()
    val dataUpdateState by viewModel.dataUpdateState.collectAsStateWithLifecycle()
    val goalScreenUiState by viewModel.goalScreenUiState.collectAsStateWithLifecycle()
    val transactionToBeDeleted = remember { mutableStateListOf<Transaction>() }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                GoalScreenTopAppBar(
                    searchTextState = goalScreenUiState.searchTextState,
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
                GoalScreenContent(
                    swipedState = swipedState,
                    goalTransactionDataState = goalTransactionDataState,
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
            DeleteAllButton(
                visible = transactionToBeDeleted.isNotEmpty(),
                title = "Delete All",
                subtitle = "${transactionToBeDeleted.size} transactions",
                onClick = {
                    viewModel.updateOpenDeleteAllDialog(true)
                }
            )
        }
    }

    // Show the delete dialog for single transaction
    DataDeletionDialog(
        transaction = goalScreenUiState.selectedTransactionToDelete,
        onDialogShow = goalScreenUiState.openDeleteDialog,
        onDelete = {
            viewModel.deleteTransaction(transactionToBeDeleted)
            scope.launch {
                swipedState.value?.reset()
            }
        },
    ) {
        viewModel.updateOpenDeleteDialog(false)
        scope.launch {
            swipedState.value?.reset()
        }
    }

    // Show the delete dialog for many transaction
    DataDeletionDialog(
        size = transactionToBeDeleted.size,
        onDialogShow = goalScreenUiState.openDeleteAllDialog,
        onDelete = {
            viewModel.deleteAllTransactions(transactionToBeDeleted)
        },
    ) {
        viewModel.updateOpenDeleteAllDialog(false)
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
private fun GoalScreenHeaderAction(
    onSearchClick: () -> Unit
) {
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
            onClick = onSearchClick
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
private fun GoalScreenTopAppBar(
    searchTextState: TextFieldState,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
) {
    val shadowElevation by animateDpAsState(
        targetValue = if (scrollBehavior.state.overlappedFraction > 0f) 2.dp else 0.dp,
        label = "TopAppBarShadow"
    )
    val showSearch = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    AnimatedContent(
        targetState = showSearch.value,
        modifier = Modifier.fillMaxWidth(),
    ) { state ->

        if (state) {
            Surface(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    WideSearchTextField(
                        focusRequester = focusRequester,
                        state = searchTextState,
                        onSearchClick = {}
                    ) {
                        showSearch.value = false
                    }
                }
            }
        } else {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Liabilities",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "All debt and lent transactions",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },

                actions = {
                    GoalScreenHeaderAction(
                        onSearchClick = {
                            keyboardController?.show()
                            showSearch.value = true
                        }
                    )
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
    }
}

@Composable
private fun GoalScreenContent(
    goalTransactionDataState: DataState<GoalDataState>,
    swipedState: MutableState<SwipeToDismissBoxState?>,
    transactionToBeDeleted: SnapshotStateList<Transaction>,
    updateSelectedTransaction: (Transaction) -> Unit = {},
    updateSelectedTransactionToDelete: (Transaction) -> Unit = {},
    onTransactionView: (Transaction) -> Unit = {},
) {
    when (goalTransactionDataState) {
        is DataState.Success -> {
            GoalScreenLazyList(
                swipedState = swipedState,
                goalTransactionDataState = goalTransactionDataState.data,
                updateSelectedTransaction = updateSelectedTransaction,
                updateSelectedTransactionToDelete = updateSelectedTransactionToDelete,
                transactionToBeDeleted = transactionToBeDeleted,
                onTransactionView = onTransactionView
            )
        }

        is DataState.Error -> {
            GoalScreenError(message = goalTransactionDataState.message)
        }

        is DataState.Loading -> {
            GoalScreenLoading()
        }
    }
}


@Composable
private fun GoalScreenError(message: String) {
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
private fun GoalScreenLoading() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "loading_summary") {
            GoalScreenMainSummaryShimmer()
        }

        stickyHeader(key = "loading_header") {
            GoalScreenListItemHeaderShimmer()
        }

        (0..6).forEach { idx ->
            item(key = idx) {
                GoalListItemShimmer()
            }
        }
    }
}

