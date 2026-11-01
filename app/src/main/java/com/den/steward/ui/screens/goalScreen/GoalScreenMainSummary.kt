// Bless be the LORD GOD of hosts
package com.den.steward.ui.screens.goalScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExposureZero
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.states.goalState.GoalMainSummary
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.VerticalDivider

@Composable
fun GoalScreenMainSummaryShimmer() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(
            bottomStart = 20.dp,
            bottomEnd = 20.dp
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .shimmerEffect(),
                    )

                    Spacer(
                        modifier = Modifier.width(3.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(45.dp, 18.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect(),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(45.dp, 30.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect(),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp, 17.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect(),
                    )

                    Box(
                        modifier = Modifier
                            .size(35.dp, 18.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect(),
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(horizontal = 10.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp, 17.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect(),
                    )

                    Box(
                        modifier = Modifier
                            .size(50.dp, 18.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect(),
                    )
                }
            }
        }
    }
}

@Composable
fun GoalScreenMainSummary(goalMainSummary: GoalMainSummary) {
    val green = Color(0xFF016C62)
    val red = Color(0xFF03A9F4)

    val state = remember(goalMainSummary) {
        object {
            val rate = goalMainSummary.rate.toInt()
            val totalFailedAchievement = goalMainSummary.totalFailedAchievement
            val totalCompletedAchievement = goalMainSummary.totalCompletedAchievement

            val totalFailedAchievementPercent = goalMainSummary.totalFailedAchievementPercent
            val totalCompletedAchievementPercent = goalMainSummary.totalCompletedAchievementPercent
            val color = when {
                goalMainSummary.rate == 0.0 -> Color.DarkGray
                goalMainSummary.rate > 0.0 -> green
                else -> red
            }
            val icon = when {
                goalMainSummary.rate == 0.0 -> Icons.Default.ExposureZero
                goalMainSummary.rate > 0.0 -> Icons.Default.ArrowUpward
                else -> Icons.Default.ArrowDownward
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(
            bottomStart = 20.dp,
            bottomEnd = 20.dp
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(state.color.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = state.icon,
                            contentDescription = "Arrow",
                            tint = state.color
                        )
                    }

                    Text(
                        "Rate",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    "${state.rate}%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = state.color
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "(${state.totalCompletedAchievementPercent}%) Completed",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        "${state.totalCompletedAchievement}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = green
                        )
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(horizontal = 10.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "(${state.totalFailedAchievementPercent}%) Failed",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        state.totalFailedAchievement.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = red
                        )
                    )
                }
            }
        }
    }
}
