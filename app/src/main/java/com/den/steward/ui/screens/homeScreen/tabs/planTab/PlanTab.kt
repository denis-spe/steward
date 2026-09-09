// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.planTab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel

@Composable
fun PlanTab(
    padding: PaddingValues,
    dataFetchViewModel: DataFetchViewModel = hiltViewModel(),
    chartViewModel: ChartViewModel = hiltViewModel()
) {
    Text("Plan Tab")
}