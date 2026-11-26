// I Worship the LORD GOD of hosts and I bless his holy name
package com.den.steward.ui.screens.detailScreen.goalDetailScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Recurrence
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.GoalDetailScreenViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.formattedDate
import com.den.steward.helper.formattedTime
import com.den.steward.helper.getStartOfDayMillis
import com.den.steward.helper.pop
import com.den.steward.helper.title
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.BackButton
import com.den.steward.ui.components.DataDeletionDialog
import com.den.steward.ui.dataUpdate.UpdateTransactionBottomDrawerSheet
import io.androidpoet.drafter.gauge.GaugeChart
import io.androidpoet.drafter.gauge.GaugeChartRenderer
import io.androidpoet.drafter.gauge.model.GaugeData
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit

@Composable
fun GoalDetailScreen(
    transactionId: String,
    viewModel: GoalDetailScreenViewModel,
    backStack: NavBackStack<NavKey>
) {
    val goalTransactionFlow = remember(transactionId) {
        viewModel.getTransaction(transactionId)
    }
    val goalTransaction by goalTransactionFlow.collectAsStateWithLifecycle()

    when (val state = goalTransaction) {
        is DataState.Loading -> {
            GoalDetailScreenLoading()
        }

        is DataState.Success -> {
            GoalDetailScreenContent(
                viewModel = viewModel,
                backStack = backStack,
                transaction = state.data
            )
        }

        is DataState.Error -> {
            GoalDetailScreenError(state.message)
        }
    }
}

@Composable
private fun GoalDetailScreenError(message: String) {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.circle_error),
                    contentDescription = "Error",
                    modifier = Modifier.size(100.dp)
                )

                Text(
                    text = message,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GoalDetailScreenContent(
    backStack: NavBackStack<NavKey>,
    transaction: Transaction,
    viewModel: GoalDetailScreenViewModel
) {
    if (transaction !is Transaction.Goal) return

    val swipedState = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }
    val scope = rememberCoroutineScope()
    val dataUpdateState by viewModel.dataUpdateState.collectAsStateWithLifecycle()
    val goalDetailUiState by viewModel.goalDetailUiState.collectAsStateWithLifecycle()
    val transactionToBeDeleted = remember { mutableStateListOf<Transaction>() }

    val goalDataFlow = remember(transaction) {
        viewModel.fetchFulfillmentTransactions(transaction)
    }
    val goalData by goalDataFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            GoalDetailScreenTopBar(
                backStack = backStack,
                transaction = transaction
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GoalDetailScreenSummary(transaction)

            GoalDetailAttainAchieveLists(
                goalData = goalData,
                swipedState = swipedState,
                updateSelectedTransaction = viewModel.updateTransaction::updateSelectedTransaction,
                updateSelectedTransactionToDelete = viewModel::updateSelectedTransactionToDelete,
            )
        }
    }

    // Show the delete dialog for single transaction
    DataDeletionDialog(
        transaction = goalDetailUiState.selectedTransactionToDelete,
        onDialogShow = goalDetailUiState.openDeleteDialog,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDetailScreenTopBar(backStack: NavBackStack<NavKey>, transaction: Transaction) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = colorResource(transaction.type.color).copy(0.4f),
                            shape = CircleShape
                        )
                        .border(
                            2.dp,
                            Color.Black.copy(0.4f),
                            CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = transaction.getSelectedIcon.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(7.dp)
                            .size(25.dp)
                    )
                }

                Column {
                    Text(
                        text = transaction.getLabel,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        navigationIcon = {
            BackButton {
                backStack.pop()
            }
        }
    )
}

@Composable
private fun GoalDetailScreenSummary(
    transaction: Transaction.Goal
) {


    val statusColor = colorResource(transaction.getStatusColor)
    val attainColor = colorResource(TransactionType.ATTAIN.color)
    val errorColor = MaterialTheme.colorScheme.error

    val state = remember(transaction) {
        val totalCount = transaction.achievement.size
        val achievedCount = transaction.achievement.count { it.status == GoalStatus.COMPLETED }
        val fieldCount = transaction.achievement.count { it.status == GoalStatus.FAILED }
        val remaining = transaction.remainingValue
        val statusLabel = transaction.getStatus ?: ""
        val durationDate = transaction.endAt.toLocalDateTime()
        val startOfYesterday = getStartOfDayMillis(-1)
        val startOfToday = getStartOfDayMillis(0)
        val startOfTomorrow = getStartOfDayMillis(1)
        val startOfDayAfterTomorrow = getStartOfDayMillis(2)

        val isToday = transaction.endAt in startOfToday until startOfTomorrow
        val isTomorrow = transaction.endAt in startOfTomorrow until startOfDayAfterTomorrow
        val isYesterday = transaction.endAt in startOfYesterday until startOfToday

        val datePrefix = when {
            isToday -> "Today"
            isTomorrow -> "Tomorrow"
            isYesterday -> "Yesterday"
            else -> durationDate.formattedDate
        }

        val formattedTimeWithDay = "$datePrefix At ${durationDate.formattedTime}"

        object {
            val percentage = transaction.percentage
            val statusColor = statusColor
            val statusLabel = statusLabel
            val achievedCount = achievedCount
            val fieldCount = fieldCount
            val achievedCountPercent = if (totalCount > 0) (achievedCount * 100) / totalCount else 0
            val fieldCountPercent = if (totalCount > 0) (fieldCount * 100) / totalCount else 0
            val amount = transaction.getFormattedAmountOrValue
            val createdDate = transaction.createdAt.toLocalDateTime().formatedDateTime
            val remaining = buildAnnotatedString {
                when {
                    0.0 == remaining -> append("You have achieved your goal")
                    transaction.value == remaining && transaction.status == GoalStatus.IN_PROGRESS ->
                        append("You haven't fulfilled your goal")

                    transaction.value == remaining || transaction.status == GoalStatus.FAILED ->
                        withStyle(
                            style = SpanStyle(
                                color = errorColor,
                            )
                        ) {
                            append("You have failed your goal")
                        }

                    else -> {
                        append("You are currently remaining ")
                        withStyle(
                            style = SpanStyle(
                                color = attainColor,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append((remaining ?: 0.0).formatToAmount())
                        }
                    }
                }
            }

            val scheduleType = transaction.repeatable.name
            val duration = when (val rep = transaction.repeatable) {
                is Recurrence.OnDays -> "${durationDate.dayOfWeek.name.title} At ${durationDate.formattedTime}"

                is Recurrence.Every -> {
                    when (rep.unit) {
                        ChronoUnit.DAYS -> formattedTimeWithDay
                        ChronoUnit.WEEKS -> durationDate.formattedDate
                        ChronoUnit.MONTHS -> durationDate.month.name.title
                        ChronoUnit.YEARS -> durationDate.year.toString()
                        else -> formattedTimeWithDay
                    }
                }

                Recurrence.Once -> durationDate.formatedDateTime
            }
        }
    }

    val percentage = state.percentage.toFloat().coerceIn(0f, 100f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .zIndex(1f),
        shadowElevation = 10.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Your Objective",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = state.amount,
                    style = MaterialTheme.typography.titleLarge
                )

                Box(
                    modifier = Modifier
                        .border(
                            1.dp, state.statusColor,
                            MaterialTheme.shapes.small
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Status",
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(
                            modifier = Modifier.size(2.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(
                                    state.statusColor,
                                    CircleShape
                                )
                        )


                        Spacer(
                            modifier = Modifier.size(2.dp)
                        )

                        Text(
                            state.statusLabel,
                            color = state.statusColor,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GaugeChart(
                    GaugeChartRenderer(
                        GaugeData(
                            value = percentage,
                            label = "Attain (%)"
                        )
                    ),
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "You completed",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${state.achievedCount} (${state.achievedCountPercent}%)",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "You failed",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${state.fieldCount} (${state.fieldCountPercent}%)",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Schedule Type",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = state.scheduleType,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Next Duration",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = state.duration,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.remaining,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Created At ${state.createdDate}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun GoalDetailScreenLoading() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator()

                Text(
                    text = "Loading your goal",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
