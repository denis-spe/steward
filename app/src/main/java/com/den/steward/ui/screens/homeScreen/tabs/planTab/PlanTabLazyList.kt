// Bless be the LORD of GOD of host
package com.den.steward.ui.screens.homeScreen.tabs.planTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.PlanStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.states.DataState
import com.den.steward.ui.dataAddition.PlanFulfillmentBottomDrawerSheet

@Composable
fun PlanTabLazyList(
    dataAdditionState: DataAdditionState,
    planFulfillmentState: DataState<List<Transaction>>,
    planTransactionsState: DataState<List<Transaction>>,
    updateShowFulfillmentTransactionTypeBottomSheet: (Boolean) -> Unit,
    updateSelectedParentTransaction: (Transaction?) -> Unit,
    updateSelectedFulfillmentTransactionType: (TransactionType) -> Unit,
    setSelectedTransaction: (Transaction?) -> Unit,
    updatePlanFulfillmentStatus: (String, PlanStatus, Transaction.PlanFulfillment) -> Unit,
    addPlanFulfillment: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (planTransactionsState) {
            is DataState.Success -> {
                if (planTransactionsState.data.isEmpty()) {
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
                        items(planTransactionsState.data, key = { it.id }) { transaction ->
                            PlanTabLazyListItem(
                                transaction = transaction,
                                updateShowFulfillmentTransactionTypeBottomSheet = updateShowFulfillmentTransactionTypeBottomSheet,
                                updateSelectedParentTransaction = updateSelectedParentTransaction,
                                updateSelectedFulfillmentTransactionType = updateSelectedFulfillmentTransactionType,
                                setSelectedTransaction = setSelectedTransaction,
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
                        text = "Error: ${planTransactionsState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    PlanFulfillmentBottomDrawerSheet(
        updatePlanFulfillmentStatus = updatePlanFulfillmentStatus,
        dataAdditionState = dataAdditionState,
        planFulfillmentState = planFulfillmentState,
        setSelectedTransaction =  setSelectedTransaction,
        updateSelectedParentTransaction = updateSelectedParentTransaction,
        addPlanFulfillment = addPlanFulfillment,
        updateShowFulfillmentTransactionTypeBottomSheet = updateShowFulfillmentTransactionTypeBottomSheet,
    )
}
