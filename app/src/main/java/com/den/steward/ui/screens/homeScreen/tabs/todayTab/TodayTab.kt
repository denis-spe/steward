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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.TodayViewModel

@Composable
fun TodayTab(
    padding: PaddingValues,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel
) {
    val todayTransactions by todayViewModel.todayTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TodayTabStatisticView(
            chartViewModel = chartViewModel,
            todayViewModel = todayViewModel
        )

        TodayTabList(
            modifier = Modifier.weight(1f),
            transactions = todayTransactions
        )
    }
}

