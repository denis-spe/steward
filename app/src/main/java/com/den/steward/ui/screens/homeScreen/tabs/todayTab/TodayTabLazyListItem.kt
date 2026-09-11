// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.den.steward.backend.entitles.Transaction
import com.den.steward.helper.formattedTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.componentExtenison.shimmerEffect
import com.den.steward.ui.components.SwipeDismiss
import com.den.steward.ui.components.TransactionViewDialog

@Composable
fun TodayTabLazyListItem(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colorScheme.surface,
    onUpdate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val localDateTime = remember(transaction) { transaction.createdAt.toLocalDateTime() }
    val time = localDateTime.formattedTime
    val onShow = remember { mutableStateOf(false) }
    val amount = remember(transaction) { transaction.getFormattedAmountOrValue }
    val paymentMethod = remember(transaction) { transaction.getPaymentMethodOrNull }

    SwipeDismiss(
        shape = shape,
        onUpdate = onUpdate,
        onDelete = onDelete,
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Surface(
            onClick = {
                onShow.value = true
            },
            shape = shape,
            color = color
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            time,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                            color = Color.Gray
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            transaction.getLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                        )

                        if (transaction.getAffectAmount != null) {
                            Text(
                                buildAnnotatedString {
                                    append("Affected: ")
                                    append(transaction.getAffectAmount)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                color = Color.Gray
                            )
                        }

                        if (transaction is Transaction.Goal) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(colorResource(id = transaction.status.color))
                                )
                                Text(
                                    buildAnnotatedString {
                                        append("Status: ")
                                        append(transaction.status.label)
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                    color = Color.Gray
                                )
                            }
                        }

                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            amount,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            AsyncImage(
                                model = paymentMethod?.icon,
                                contentDescription = paymentMethod?.label,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp)
                            )
                            AsyncImage(
                                model = transaction.type.icon,
                                contentDescription = stringResource(id = transaction.type.label),
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp),
                                colorFilter = ColorFilter.tint(colorResource(id = transaction.type.color))
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray
                )
            }
        }
    }

    TransactionViewDialog(
        transaction = transaction,
        onShow = onShow.value
    ) {
        onShow.value = false
    }
}


@Composable
fun TodayTabLazyListItemShimmer() {
    Surface(
        modifier = Modifier.padding(vertical = 2.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Label shimmer
                    Box(
                        modifier = Modifier
                            .size(60.dp, 16.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    // Description shimmer
                    Box(
                        modifier = Modifier
                            .size(100.dp, 16.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Amount shimmer
                    Box(
                        modifier = Modifier
                            .size(100.dp, 16.dp)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Payment Method shimmer
                        Box(
                            modifier = Modifier
                                .size(16.dp, 16.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )

                        Spacer(modifier = Modifier.size(4.dp))

                        // Transaction Type shimmer
                        Box(
                            modifier = Modifier
                                .size(16.dp, 16.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.LightGray
            )
        }
    }
}