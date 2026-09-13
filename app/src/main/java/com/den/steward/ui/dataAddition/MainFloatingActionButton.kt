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
    val subFabSize = 48.dp

    Column(
        verticalArrangement = Arrangement.Center,
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                AddTransactionFloatingActionButton(
                    modifier = Modifier.size(subFabSize),
                    elevation = FloatingActionButtonDefaults.elevation(2.dp),
                    dataAdditionViewModel = dataAdditionViewModel
                )

                Spacer(modifier = Modifier.height(12.dp))

                AddFulfillmentTransactionFloatingActionButton(
                    modifier = Modifier.size(subFabSize),
                    elevation = FloatingActionButtonDefaults.elevation(2.dp),
                    dataAdditionViewModel = dataAdditionViewModel
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        FloatingActionButton(
            onClick = {
                dataAdditionViewModel.updateMainBottomSheetState(!isExpanded)
            },
            shape = CircleShape,
            modifier = Modifier.size(size),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isExpanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}
