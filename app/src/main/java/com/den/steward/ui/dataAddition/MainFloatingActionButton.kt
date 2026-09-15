// Glory be to name of the LORD GOD
package com.den.steward.ui.dataAddition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.viewModels.DataAdditionViewModel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MainFloatingActionButton(
    dataAdditionViewModel: DataAdditionViewModel
) {
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()
    val isExpanded = dataAdditionState.showMainBottomSheet
    
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "MainFabRotation"
    )

    val size = 56.dp
    val subFabSize = 44.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it / 2 }
            ) + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Transaction FAB with Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            dataAdditionViewModel.updateShowTransactionTypeBottomSheet(true)
                        }
                    ) {
                        Text(
                            text = "Add Transaction",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    AddTransactionFloatingActionButton(
                        modifier = Modifier.size(subFabSize),
                        elevation = FloatingActionButtonDefaults.elevation(2.dp),
                        dataAdditionViewModel = dataAdditionViewModel
                    )
                }

                // Fulfillment FAB with Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            dataAdditionViewModel.updateShowFulfillmentTransactionTypeBottomSheet(true)
                        }
                    ) {
                        Text(
                            text = "Add Fulfillment",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    AddFulfillmentTransactionFloatingActionButton(
                        modifier = Modifier.size(subFabSize),
                        elevation = FloatingActionButtonDefaults.elevation(2.dp),
                        dataAdditionViewModel = dataAdditionViewModel
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                dataAdditionViewModel.updateMainBottomSheetState(!isExpanded)
            },
            shape = CircleShape,
            modifier = Modifier.size(size),
            containerColor = MaterialTheme.colorScheme.primary.copy(0.9f).compositeOver(
                MaterialTheme.colorScheme.background
            ),
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isExpanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation).size(28.dp)
            )
        }
    }
}
