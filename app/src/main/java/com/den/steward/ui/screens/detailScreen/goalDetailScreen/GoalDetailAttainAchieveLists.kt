package com.den.steward.ui.screens.detailScreen.goalDetailScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.den.steward.R
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.goalDetailScreenState.GoalData
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.SwipeDismiss

@Composable
fun GoalDetailAttainAchieveLists(
    swipedState: MutableState<SwipeToDismissBoxState?>,
    updateSelectedTransactionToDelete: (transaction: Transaction) -> Unit,
    updateSelectedTransaction: (transaction: Transaction) -> Unit,
    goalData: DataState<GoalData>,
) {
    val scroll = rememberScrollState()
    val showAttain = remember { mutableStateOf(false) }
    val showAchievement = remember { mutableStateOf(false) }
    val attainExpendIcon = if (showAttain.value) Icons.Default.KeyboardArrowDown
    else Icons.AutoMirrored.Filled.KeyboardArrowRight
    val achieveExpendIcon = if (showAchievement.value) Icons.Default.KeyboardArrowDown
    else Icons.AutoMirrored.Filled.KeyboardArrowRight

    when (goalData) {
        is DataState.Success -> {
            val attain = remember(goalData.data.attain) {
                val attn =
                    goalData.data.attain
                        .sortedBy { it.createdAt.toLocalDateTime() }
                if (attn.size > 7) {
                    attn.subList(0, 7)
                } else {
                    attn
                }
            }
            val achievement = remember(goalData.data.achieve) {
                val achieve = goalData.data.achieve
                    .sortedBy { it.createdAt.toLocalDateTime() }
                if (achieve.size > 7) {
                    achieve.subList(0, 7)
                } else {
                    achieve
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(10.dp)
                    .zIndex(0.9f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    attain.forEach { attainTransaction ->
                                        GoalDetailAttainListItem(
                                            attainTransaction,
                                            onDelete = { dismissState ->
                                                swipedState.value = dismissState
                                                updateSelectedTransactionToDelete(attainTransaction)
                                            },
                                            onUpdate = { dismissState ->
                                                swipedState.value = dismissState
                                                updateSelectedTransaction(attainTransaction)
                                            },
                                        )
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
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    achievement.forEach { achievementTransaction ->
                                        GoalDetailAchievementListItem(
                                            achievementTransaction,
                                            onDelete = { dismissState ->
                                                swipedState.value = dismissState
                                                updateSelectedTransactionToDelete(
                                                    achievementTransaction
                                                )
                                            },
                                            onUpdate = { dismissState ->
                                                swipedState.value = dismissState
                                                updateSelectedTransaction(achievementTransaction)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(70.dp))
            }
        }

        is DataState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(10.dp)
                    .zIndex(0.9f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        GoalDetailListShimmer()
                    }
                }

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
                        GoalDetailListShimmer()
                    }
                }
            }
        }

        is DataState.Error -> {
            Text(text = goalData.message)
        }
    }
}

@Composable
private fun GoalDetailListShimmer() {
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
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .size(60.dp, height = 20.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .shimmerEffect()
                )
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }
    }
}


@Composable
private fun GoalDetailListHeader(
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
private fun GoalDetailAttainListItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    onDelete: (dismissState: SwipeToDismissBoxState) -> Unit,
    onUpdate: (dismissState: SwipeToDismissBoxState) -> Unit
) {
    if (transaction !is Transaction.Attain) return

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance: Float ->
            distance * 0.5f
        }
    )

    SwipeDismiss(
        shape = shape,
        dismissState = dismissState,
        onDelete = {
            onDelete(dismissState)
        },
        onUpdate = {
            onUpdate(dismissState)
        },
        modifier = modifier.padding(vertical = 4.dp)
    ) {
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
}


@Composable
fun GoalDetailAchievementListItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    onDelete: (dismissState: SwipeToDismissBoxState) -> Unit,
    onUpdate: (dismissState: SwipeToDismissBoxState) -> Unit
) {
    if (transaction !is Transaction.Achievement) return

    val imgResId = if (transaction.status == GoalStatus.COMPLETED) {
        R.drawable.achievement
    } else {
        R.drawable.failed
    }
    val statusColor = colorResource(transaction.getStatusColor)
    val statusLabel = transaction.getStatus ?: ""
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance: Float ->
            distance * 0.5f
        }
    )

    SwipeDismiss(
        shape = shape,
        dismissState = dismissState,
        onDelete = {
            onDelete(dismissState)
        },
        onUpdate = {
            onUpdate(dismissState)
        },
        modifier = modifier.padding(vertical = 4.dp)
    ) {
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
}
