package com.den.steward.ui.screens.homeScreen.tabs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel

@Composable
fun OverviewTab(
    padding: PaddingValues,
    dataFetchViewModel: DataFetchViewModel,
    chartViewModel: ChartViewModel
) {
    Text("Overview Tab")
}