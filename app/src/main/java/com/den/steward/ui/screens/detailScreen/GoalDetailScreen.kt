// I Worship the LORD GOD of hosts and I bless his holy name
package com.den.steward.ui.screens.detailScreen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.GoalDetailScreenViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.pop
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.components.BackButton

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
fun GoalDetailScreenError(message: String) {
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
fun GoalDetailScreenContent(
    backStack: NavBackStack<NavKey>,
    transaction: Transaction
) {
    if (transaction !is Transaction.Goal) return

    val typeColor = colorResource(transaction.type.color)

    val summary = remember(transaction) {
        val totalCount = transaction.achievement.size
        val achievedCount = transaction.achievement.count { it.status == GoalStatus.COMPLETED }
        val fieldCount = transaction.achievement.count { it.status == GoalStatus.FAILED }
        val remaining = transaction.value - transaction.totalAttain

        GoalDetailSummary(
            achievedCount = achievedCount,
            fieldCount = fieldCount,
            achievedCountPercent = if (totalCount > 0) (achievedCount * 100) / totalCount else 0,
            fieldCountPercent = if (totalCount > 0) (fieldCount * 100) / totalCount else 0,
            amount = transaction.getFormattedAmountOrValue,
            createdDate = transaction.createdAt.toLocalDateTime().formatedDateTime,
            remaining = when (remaining) {
                0.0 -> "You have achieved your goal"
                transaction.value -> "You haven't fulfilled your goal"
                else -> "You are currently remaining ${remaining.formatToAmount()}"
            }
        )
    }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoalDetailScreenSummary(summary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreenTopBar(backStack: NavBackStack<NavKey>, transaction: Transaction) {
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
                Text(
                    text = transaction.getLabel,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        navigationIcon = {
            BackButton {
                backStack.pop()
            }
        }
    )
}

data class GoalDetailSummary(
    val achievedCount: Int = 0,
    val fieldCount: Int = 0,
    val achievedCountPercent: Int = 0,
    val fieldCountPercent: Int = 0,
    val amount: String = "",
    val createdDate: String = "",
    val remaining: String = ""
)

@Composable
fun GoalDetailScreenSummary(
    summary: GoalDetailSummary
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
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
                    text = summary.amount,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "You completed",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${summary.achievedCount} (${summary.achievedCountPercent}%)",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "You failed",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${summary.fieldCount} (${summary.fieldCountPercent}%)",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = summary.remaining,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Created At ${summary.createdDate}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun GoalDetailScreenLoading() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
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
