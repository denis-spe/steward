// Glory be to LORD GOD of hosts
package com.den.steward.ui.screens.homeScreen.tabs

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
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.ui.screens.homeScreen.transactionCharts.TransactionDonutChart
import com.den.steward.ui.screens.homeScreen.transactionList.FinancialPeriodList

@Composable
fun TodayTab(
    padding: PaddingValues,
    dataFetchViewModel: DataFetchViewModel,
    chartViewModel: ChartViewModel
) {
    val todayTransactions by dataFetchViewModel.todayTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TransactionDonutChart(viewModel = chartViewModel)

        FinancialPeriodList(
            modifier = Modifier.weight(1f),
            transactions = todayTransactions
        )
    }
}