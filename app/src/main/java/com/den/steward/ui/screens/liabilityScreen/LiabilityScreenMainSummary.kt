// Bless be the LORD GOD of hosts
package com.den.steward.ui.screens.liabilityScreen

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.den.steward.backend.states.liabilityScreenState.LiabilityMainSummary
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.VerticalDivider

@Composable
fun LiabilityScreenMainSummaryShimmer() {
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
            }
        }
    }
}

@Composable
fun LiabilityScreenMainSummary(liabilityMainSummary: LiabilityMainSummary) {
    val green = Color(0xFF4CAF50)
    val red = Color(0xFFF44336)

    val state = remember(liabilityMainSummary) {
        object {
            val liabilityFlow = liabilityMainSummary.liabilityFlow.formatToAmount()
            val paidLiability = liabilityMainSummary.paidLiability.formatToAmount()
            val unpaidLiability = liabilityMainSummary.unpaidLiability.formatToAmount()
            val color = when {
                liabilityMainSummary.liabilityFlow == 0.0 -> Color.DarkGray
                liabilityMainSummary.liabilityFlow > 0.0 -> green
                else -> red
            }
            val icon = when {
                liabilityMainSummary.liabilityFlow == 0.0 -> Icons.Default.ExposureZero
                liabilityMainSummary.liabilityFlow > 0.0 -> Icons.Default.ArrowUpward
                else -> Icons.Default.ArrowDownward
            }

            val paidLiabilityPercent = liabilityMainSummary.paidLiabilityPercent.toInt()
            val unpaidLiabilityPercent = liabilityMainSummary.unpaidLiabilityPercent.toInt()
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
                        "Liability Flow",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    state.liabilityFlow,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
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
                        buildAnnotatedString {
                            append("(")
                            withStyle(
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                ).toSpanStyle()
                            ) {
                                append("${state.paidLiabilityPercent}%")
                            }

                            append(") Paid")
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        state.paidLiability,
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
                        buildAnnotatedString {
                            append("(")
                            withStyle(
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                ).toSpanStyle()
                            ) {
                                append("${state.unpaidLiabilityPercent}%")
                            }

                            append(") Unpaid")
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        state.unpaidLiability,
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
