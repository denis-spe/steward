// Glory be to LORD GOD of hosts
package com.den.steward.ui.screens.homeScreen.tabs.todayTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.backend.viewModels.TodayViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.screens.homeScreen.transactionList.FinancialPeriodList

@Composable
fun TodayTab(
    padding: PaddingValues,
    dataFetchViewModel: DataFetchViewModel,
    chartViewModel: ChartViewModel,
    todayViewModel: TodayViewModel
) {
    val todayTransactions by dataFetchViewModel.todayTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TodayStatisticView(
            chartViewModel = chartViewModel,
            todayViewModel = todayViewModel
        )

        FinancialPeriodList(
            modifier = Modifier.weight(1f),
            transactions = todayTransactions
        )
    }
}

