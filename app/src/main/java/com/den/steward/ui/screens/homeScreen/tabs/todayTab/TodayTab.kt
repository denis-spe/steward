// Glory be to LORD GOD of hosts
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.ui.components.FilterBottomSheet

@Composable
fun TodayTab(
    padding: PaddingValues,
    chartViewModel: ChartViewModel = hiltViewModel(),
    todayViewModel: TodayViewModel = hiltViewModel(),
    dataDeletionViewModel: DataDeletionViewModel = hiltViewModel()
) {

    val todayUiState by todayViewModel.todayUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TodayTabList(
            modifier = Modifier.fillMaxSize(),
            chartViewModel = chartViewModel,
            todayViewModel = todayViewModel,
            dataDeletionViewModel = dataDeletionViewModel
        )
    }

    FilterBottomSheet(
        isExpanded = todayUiState.isFilterExpanded,
        selected = todayUiState.filter,
        onFilterSelected = todayViewModel::updateFilter,
        onDismiss = {
            todayViewModel.updateIsFilterExpanded(false)
        }
    )
}

