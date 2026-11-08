package com.den.steward.ui.screens.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.states.DataNotifier
import com.den.steward.backend.states.HomeTab
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.ui.dataAddition.MainFloatingActionButton
import com.den.steward.ui.screens.homeScreen.tabs.allTab.AllTab
import com.den.steward.ui.screens.homeScreen.tabs.overviewTab.OverviewTab
import com.den.steward.ui.screens.homeScreen.tabs.planTab.PlanTab
import com.den.steward.ui.screens.homeScreen.tabs.todayTab.TodayTab
import com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab.YesterdayTab
import com.den.steward.ui.screens.screenManager.SettingsRouter

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel,
    dataAdditionViewModel: DataAdditionViewModel,
) {

    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()
    val userState by homeViewModel.userState.collectAsStateWithLifecycle()
    val topDrawerState = remember { mutableStateOf(false) }

    val dataNotifier = when {
        homeUiState.dataNotifier != null -> homeUiState.dataNotifier
        dataAdditionState.dataNotifier != null -> dataAdditionState.dataNotifier
        else -> null
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeTopBar(
                    userState = userState,
                    currentTab = homeUiState.currentTab,
                    onTabChange = { homeViewModel.updateHomeTab(it) },
                    onLogoClick = {
                        topDrawerState.value = true
                    }
                )
            }
        ) { padding ->
            when (homeUiState.currentTab) {
                HomeTab.TODAY -> {
                    TodayTab(
                        padding = padding,
                        onDataNotifier = homeViewModel::updateDataNotifier
                    )
                }

                HomeTab.YESTERDAY -> {
                    YesterdayTab(
                        padding = padding,
                        onDataNotifier = homeViewModel::updateDataNotifier
                    )
                }

                HomeTab.ALL -> {
                    AllTab(
                        padding = padding,
                        homeViewModel = homeViewModel,
                        onDataNotifier = homeViewModel::updateDataNotifier
                    )
                }

                HomeTab.OVERVIEW -> {
                    OverviewTab(
                        padding = padding,
                        backStack = backStack,
                        onDataNotifier = homeViewModel::updateDataNotifier
                    )
                }

                HomeTab.PLAN -> {
                    PlanTab(
                        padding = padding,
                        onDataNotifier = homeViewModel::updateDataNotifier
                    )
                }
            }
        }

        HomeTopDrawer(
            userState = userState,
            isOpen = topDrawerState.value,
            onClose = { topDrawerState.value = false },
            onSettingsClick = {
                topDrawerState.value = false
                backStack.add(SettingsRouter)
            },
            onHomeClick = {
                topDrawerState.value = false
            }
        )

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

        Notifier(dataNotifier)

    }
}

@Composable
fun BoxScope.Notifier(dataNotifier: DataNotifier?) {
    AnimatedVisibility(
        visible = dataNotifier != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {

        val dataNotifier = dataNotifier ?: return@AnimatedVisibility

        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = colorResource(dataNotifier.color),
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Image(
                                painter = painterResource(dataNotifier.icon),
                                contentDescription = null,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            dataNotifier.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            dataNotifier.amount,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}