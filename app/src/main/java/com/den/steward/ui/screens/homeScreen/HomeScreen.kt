package com.den.steward.ui.screens.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.states.HomeTab
import com.den.steward.backend.viewModels.ChartViewModel
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.ui.dataAddition.AddTransactionFloatingActionButton
import com.den.steward.ui.screens.homeScreen.tabs.AllTab
import com.den.steward.ui.screens.homeScreen.tabs.OverviewTab
import com.den.steward.ui.screens.homeScreen.tabs.PlanTab
import com.den.steward.ui.screens.homeScreen.tabs.TodayTab
import com.den.steward.ui.screens.homeScreen.tabs.YesterdayTab

@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>,
    dataAdditionViewModel: DataAdditionViewModel,
    dataFetchViewModel: DataFetchViewModel,
    chartViewModel: ChartViewModel,
    homeViewModel: HomeViewModel
) {

    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val onTabChange = remember(homeViewModel) {
        { tab: HomeTab -> homeViewModel.updateHomeTab(tab) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AddTransactionFloatingActionButton(
                dataAdditionViewModel = dataAdditionViewModel
            )
        },
        topBar = {
            HomeTopBar(
                currentTab = homeUiState.currentTab,
                onTabChange = onTabChange
            )
        }
    ) { padding ->
        when(homeUiState.currentTab) {
            HomeTab.TODAY -> TodayTab(
                padding = padding,
                dataFetchViewModel = dataFetchViewModel,
                chartViewModel = chartViewModel
            )
            HomeTab.YESTERDAY -> YesterdayTab(
                padding = padding,
                dataFetchViewModel = dataFetchViewModel,
                chartViewModel = chartViewModel
            )
            HomeTab.ALL -> AllTab(
                padding = padding,
                dataFetchViewModel = dataFetchViewModel,
                chartViewModel = chartViewModel
            )

            HomeTab.OVERVIEW -> OverviewTab(
                padding = padding,
                dataFetchViewModel = dataFetchViewModel,
                chartViewModel = chartViewModel
            )

            HomeTab.PLAN -> PlanTab(
                padding = padding,
                dataFetchViewModel = dataFetchViewModel,
                chartViewModel = chartViewModel
            )
        }
    }
}
