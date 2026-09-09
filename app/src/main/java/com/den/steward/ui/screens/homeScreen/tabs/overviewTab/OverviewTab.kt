package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.backend.viewModels.OverviewViewModel

@Composable
fun OverviewTab(
    padding: PaddingValues,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val overviewUiState by overviewViewModel.overviewUiState.collectAsStateWithLifecycle()
    val groupedTransactions by overviewViewModel.groupedTransactions.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        OverviewList(
            dataState = groupedTransactions
        )
    }
}