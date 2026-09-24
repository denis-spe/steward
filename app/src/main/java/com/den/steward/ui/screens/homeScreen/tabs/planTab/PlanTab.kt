// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.planTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.viewModels.PlanTabViewModel

@Composable
fun PlanTab(
    padding: PaddingValues,
    planTabViewModel: PlanTabViewModel = hiltViewModel()
) {
    val planTransactionsState by planTabViewModel.planTransactions.collectAsStateWithLifecycle()
    val planFulfillmentState by planTabViewModel.planFulfillmentTransactions.collectAsStateWithLifecycle()
    val planTabUiState by planTabViewModel.planTabUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PlanTabLazyList(
            planTabUiState = planTabUiState,
            planTransactionsState = planTransactionsState,
            planFulfillmentState = planFulfillmentState,
            updateSelectedFulfillmentTransactionType = planTabViewModel::updateSelectedFulfillmentTransactionType,
            updateShowFulfillmentTransactionTypeBottomSheet = planTabViewModel::updateShowFulfillmentTransactionTypeBottomSheet,
            updatePlanFulfillmentStatus = planTabViewModel::updatePlanFulfillmentStatus,
            setSelectedTransaction = planTabViewModel::setSelectedTransaction,
            addPlanFulfillment = planTabViewModel::addPlanFulfillment,
            onTypeChange = planTabViewModel::updateSelectedPlanFulfillmentType,
            deleteFulfillmentPlan = planTabViewModel::deleteFulfillmentPlan
        )
    }
}
