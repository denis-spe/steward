// Bless be the LORD of GOD of host
package com.den.steward.ui.screens.homeScreen.tabs.planTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.PlanTabViewModel
import com.den.steward.ui.dataAddition.PlanFulfillmentBottomDrawerSheet

@Composable
fun PlanTabLazyList(
    dataAdditionViewModel: DataAdditionViewModel,
    planTabViewModel: PlanTabViewModel
) {
    val planTransactionsState by planTabViewModel.planTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (val state = planTransactionsState) {
            is DataState.Success -> {
                if (state.data.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No plans yet. Start planning!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.data, key = { it.id }) { transaction ->
                            PlanTabLazyListItem(
                                transaction = transaction,
                                dataAdditionViewModel = dataAdditionViewModel,
                                planTabViewModel = planTabViewModel
                            )
                        }
                    }
                }
            }
            is DataState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading plans...", style = MaterialTheme.typography.bodyLarge)
                }
            }
            is DataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    PlanFulfillmentBottomDrawerSheet(
        planTabViewModel = planTabViewModel,
        dataAdditionViewModel = dataAdditionViewModel
    )
}
