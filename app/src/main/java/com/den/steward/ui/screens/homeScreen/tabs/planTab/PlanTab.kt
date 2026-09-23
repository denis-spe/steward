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
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.DataUpdateViewModel
import com.den.steward.backend.viewModels.PlanTabViewModel

@Composable
fun PlanTab(
    padding: PaddingValues,
    dataAdditionViewModel: DataAdditionViewModel,
    dataUpdateViewModel: DataUpdateViewModel,
    planTabViewModel: PlanTabViewModel = hiltViewModel()
) {
    val planTransactionsState by planTabViewModel.planTransactions.collectAsStateWithLifecycle()
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()
    val planFulfillmentState by planTabViewModel.planFulfillmentTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PlanTabLazyList(
            dataAdditionState = dataAdditionState,
            planTransactionsState = planTransactionsState,
            planFulfillmentState = planFulfillmentState,
            updateSelectedParentTransaction = dataAdditionViewModel::updateSelectedParentTransaction,
            updateSelectedFulfillmentTransactionType = dataAdditionViewModel::updateSelectedFulfillmentTransactionType,
            updateShowFulfillmentTransactionTypeBottomSheet = dataAdditionViewModel::updateShowFulfillmentTransactionTypeBottomSheet,
            updatePlanFulfillmentStatus = dataUpdateViewModel::updatePlanFulfillmentStatus,
            setSelectedTransaction = planTabViewModel::setSelectedTransaction,
            addPlanFulfillment = dataAdditionViewModel::addPlanFulfillment
        )
    }
}
