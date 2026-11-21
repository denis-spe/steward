// I Worship the LORD GOD of hosts and I bless his holy name
package com.den.steward.ui.screens.detailScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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
import io.androidpoet.drafter.gauge.GaugeChart
import io.androidpoet.drafter.gauge.GaugeChartRenderer
import io.androidpoet.drafter.gauge.model.GaugeData
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

            GoalDetailAttainLazyList(transaction)
        }
    }
}


@Composable
fun GoalDetailAttainLazyList(transaction: Transaction.Goal) {
    val attain = transaction.attain
    val achievement = transaction.achievement
    val scroll = rememberScrollState()
    val showAttain = remember { mutableStateOf(false) }
    val showAchievement = remember { mutableStateOf(false) }
    val attainExpendIcon = if (showAttain.value) Icons.Default.KeyboardArrowDown
    else Icons.AutoMirrored.Filled.KeyboardArrowRight
    val achieveExpendIcon = if (showAchievement.value) Icons.Default.KeyboardArrowDown
    else Icons.AutoMirrored.Filled.KeyboardArrowRight


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(10.dp)
    ) {
        if (attain.isNotEmpty()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GoalDetailListHeader(
                        "Attainments",
                        titleIcon = R.drawable.attainment,
                        color = colorResource(R.color.attain),
                        icon = {
                            Icon(
                                attainExpendIcon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }) {
                        showAttain.value = !showAttain.value
                    }

                    AnimatedVisibility(
                        visible = showAttain.value
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            attain.forEach { attainTransaction ->
                                GoalDetailAttainListItem(attainTransaction)
                            }
                        }
                    }
                }
            }
        }

        if (achievement.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GoalDetailListHeader(
                        "Achievement",
                        titleIcon = R.drawable.achievement_outline,
                        color = colorResource(R.color.achievement),
                        icon = {
                            Icon(
                                achieveExpendIcon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }) {
                        showAchievement.value = !showAchievement.value
                    }

                    AnimatedVisibility(
                        visible = showAchievement.value
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            achievement.forEach { achievementTransaction ->
                                GoalDetailAchievementListItem(achievementTransaction)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalDetailListHeader(
    title: String,
    icon: (@Composable () -> Unit),
    titleIcon: Int,
    color: Color,
    onClick: () -> Unit
) {
    Surface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(titleIcon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(color)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            IconButton(
                onClick = onClick
            ) {
                icon()
            }
        }
    }
}

@Composable
fun GoalDetailAttainListItem(transaction: Transaction) {
    if (transaction !is Transaction.Attain) return

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.getLabel,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = transaction.getFormattedAmountOrValue,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Text(
                text = transaction.createdAt.toLocalDateTime().formatedDateTime,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
fun GoalDetailAchievementListItem(transaction: Transaction) {
    if (transaction !is Transaction.Achievement) return

    val imgResId = if (transaction.status == GoalStatus.COMPLETED) {
        R.drawable.achievement
    } else {
        R.drawable.failed
    }
    val statusColor = colorResource(transaction.getStatusColor)
    val statusLabel = transaction.getStatus ?: ""

    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Image(
                        painter = painterResource(imgResId),
                        contentDescription = "Achievement",
                        modifier = Modifier.size(25.dp)
                    )

                    Column {
                        Text(
                            text = transaction.getLabel,
                            style = MaterialTheme.typography.labelMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = transaction.createdAt.toLocalDateTime().formatedDateTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray
                                )
                            )

                            Surface(
                                color = statusColor.copy(0.2f),
                                shape = MaterialTheme.shapes.small,
                                border = BorderStroke(1.dp, statusColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = statusLabel,
                                        color = statusColor,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = transaction.getFormattedAmountOrValue,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
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
fun GoalDetailScreenSummary(
    transaction: Transaction.Goal
) {


    val statusColor = colorResource(transaction.getStatusColor)
    val attainColor = colorResource(TransactionType.ATTAIN.color)
    val errorColor = MaterialTheme.colorScheme.error

    val state = remember(transaction) {
        val totalCount = transaction.achievement.size
        val achievedCount = transaction.achievement.count { it.status == GoalStatus.COMPLETED }
        val fieldCount = transaction.achievement.count { it.status == GoalStatus.FAILED }
        val remaining = transaction.value - transaction.totalAttain
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

                    transaction.value == remaining && transaction.status == GoalStatus.FAILED ->
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
                            append(remaining.formatToAmount())
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
