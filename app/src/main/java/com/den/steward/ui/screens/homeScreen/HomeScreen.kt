package com.den.steward.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.states.HomeTab
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.DataUpdateViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.ui.dataAddition.MainFloatingActionButton
import com.den.steward.ui.screens.homeScreen.tabs.allTab.AllTab
import com.den.steward.ui.screens.homeScreen.tabs.overviewTab.OverviewTab
import com.den.steward.ui.screens.homeScreen.tabs.planTab.PlanTab
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTab
import com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab.YesterdayTab

@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel,
    dataAdditionViewModel: DataAdditionViewModel,
    dataUpdateViewModel: DataUpdateViewModel
) {

    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeTopBar(
                    currentTab = homeUiState.currentTab,
                    onTabChange = { homeViewModel.updateHomeTab(it) }
                )
            }
        ) { padding ->
            when (homeUiState.currentTab) {
                HomeTab.TODAY -> {
                    TodayTab(
                        padding = padding
                    )
                }

                HomeTab.YESTERDAY -> {
                    YesterdayTab(
                        padding = padding
                    )
                }

                HomeTab.ALL -> {
                    AllTab(
                        padding = padding,
                        homeViewModel = homeViewModel
                    )
                }

                HomeTab.OVERVIEW -> {
                    OverviewTab(
                        padding = padding,
                        onTabChange = { tab, filter ->
                            homeViewModel.updateHomeTab(tab, filter)
                        }
                    )
                }

                HomeTab.PLAN -> {
                    PlanTab(
                        padding = padding,
                        dataAdditionViewModel = dataAdditionViewModel,
                        dataUpdateViewModel = dataUpdateViewModel
                    )
                }
            }
        }

        if (dataAdditionState.showMainBottomSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            dataAdditionViewModel.updateMainBottomSheetState(false)
                        }
                    )
            )
        }

        // Place the FAB at the end so it's above both the Scaffold and the Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            MainFloatingActionButton(
                dataAdditionViewModel = dataAdditionViewModel
            )
        }
    }
}
