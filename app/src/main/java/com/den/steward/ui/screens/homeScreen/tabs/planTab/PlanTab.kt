// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.planTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.backend.viewModels.PlanTabViewModel
import com.den.steward.ui.screens.homeScreen.tabs.allTab.AllTabLazyListItem

@Composable
fun PlanTab(
    padding: PaddingValues,
    planTabViewModel: PlanTabViewModel = hiltViewModel(),
    chartViewModel: ChartViewModel = hiltViewModel()
) {
    val planTransactionsState by planTabViewModel.planTransactions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PlanTabLazyList(planTransactionsState)
    }
}
